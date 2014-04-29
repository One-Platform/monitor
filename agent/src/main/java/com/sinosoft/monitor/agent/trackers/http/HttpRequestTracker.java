package com.sinosoft.monitor.agent.trackers.http;

import com.sinosoft.monitor.agent.JavaAgent;
import com.sinosoft.monitor.agent.config.JavaAgentConfig;
import com.sinosoft.monitor.agent.store.AgentTraceStore;
import com.sinosoft.monitor.agent.store.model.exception.ExceptionInfo;
import com.sinosoft.monitor.agent.store.model.url.MethodTraceLog;
import com.sinosoft.monitor.agent.store.model.url.UrlTraceLog;
import com.sinosoft.monitor.agent.trackers.AbstractRootTracker;
import com.sinosoft.monitor.agent.trackers.DefaultTracker;
import com.sinosoft.monitor.agent.trackers.Tracker;
import com.sinosoft.monitor.agent.util.AgentKeyUtil;
import com.sinosoft.monitor.agent.util.SequenceURINormalizer;
import com.sinosoft.monitor.agent.util.UUIDUtil;
import com.sinosoft.monitor.com.alibaba.fastjson.JSON;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public class HttpRequestTracker extends AbstractRootTracker {
	private boolean hasUrlTrace;
	protected Object[] args;
	private String requestMethod;
	private Map<String, Object> requestParamMap;
    private String requestParamsString;
	private String requestIp;
	private String sessionId;

//	protected

	public HttpRequestTracker(String className, String methodName, Object thisObj, Object[] args) {
		super(className, methodName, thisObj, args);

		this.args = args;
		String uri;
		try {
			HttpRequest request = new HttpRequest(this.args[0]);
			requestMethod = request.getMethod();

			requestParamMap = request.getParameterMap();

			requestIp = request.getRealIP();

			sessionId = request.getSessionId();

			uri = SequenceURINormalizer.normalizeURI(request.getRequestURI());

            //rootTracker 创建ONE_M_KEY
            String oneMAgentKey = request.getHeader(AgentKeyUtil.ONE_M_AGENT_KEY);

            JavaAgent.logger.log(Level.INFO,"Request received _one_agent_key_:{0}",oneMAgentKey);

            oneMAgentKey = genericOneMAgentKey(oneMAgentKey);

            setOneMAgentKey(oneMAgentKey);

		} catch (Exception ex) {
			uri = "unknown";
			JavaAgent.logger.log(Level.WARNING, "Exception while normalizing the url {0}", ex.getMessage());
		}
		this.seqName = ("" + uri);
	}
    private String genericOneMAgentKey(String oneMAgentKey){
        //通过http获取oneMAgentKey
        if (oneMAgentKey==null||"".equals(oneMAgentKey)){
            oneMAgentKey = AgentKeyUtil.createOneMAgentKey();
        } else {
            AgentKeyUtil.setAgentKey(oneMAgentKey);
        }
        return oneMAgentKey;
    }
    public void parseEncoding(String charSet){
        try{
            //创建一个临时Map ,用于装载转码后的字符串
            Map<String,Object> tempMap = new HashMap<String, Object>(requestParamMap.size());
            Set<String> keySet = requestParamMap.keySet();

            for(String key:keySet){

                Object objValue = requestParamMap.get(key);
                //如果value值不是字符串类型
                if (objValue.getClass()!=String.class){
                    //如果value值是字符串数组类型
                    if(objValue.getClass()==String[].class){
                        String[] values = (String[])objValue;
                        int len = values.length;
                        //创建一个新的字符串数组用来复制转码后的原有数组
                        String[] newValues = new String[len];
                        for(int i=0;i<len;i++){
                            String value = values[i];
                            String rs =  new String(value.getBytes("iso8859-1"), charSet);
                            JavaAgent.logger.info(MessageFormat.format("-i[rs:{0}]", rs));
                            newValues[i] = rs;
                        }
                        //向临时Map中装载数据
                        tempMap.put(key,newValues);
                    }
                }
                //如果value值是字符串类型
                else {
                    String value = (String)objValue;
                    String rs =  new String(value.getBytes("iso8859-1"), charSet);
                    JavaAgent.logger.info(MessageFormat.format("[rs:{0}]", rs));
                    //向临时Map中装载数据
                    tempMap.put(key,rs);
                }
            }
            //将参数序列化成JSON格式然后赋值给requestParamsString
            requestParamsString = JSON.toJSONString(tempMap);
        } catch (Throwable t){
            JavaAgent.logger.log(Level.SEVERE,this.getClass().getName(),t);
        }
    }
	public String assignSequenceName() {

		this.hasUrlTrace = true;
		//??
		this.args = null;
		return this.seqName;
	}

	public boolean isMetricHolded() {
		return this.hasUrlTrace;
	}


	public void quit(int opcode, Object returnValue) {
		quit(returnValue);
	}

	public void quit(Throwable th) {
		super.quit(th);
		quit();
	}

	@Override
	public boolean generateUrlTraceLog(String seqName) {
		if (this.hasUrlTrace) {
//			UrlTraceStore urlStore = UrlTraceStoreController.getUrlTraceStore();
//			List<UrlTraceLog> urlTraces = urlStore.getUrlTrace(seqName);
			UrlTraceLog urlTrace = new UrlTraceLog();
            //增加监控生产戳
            urlTrace.setOneMAgentKey(this.getOneMAgentKey());
			urlTrace.setConsumeTime(getDuration());
			urlTrace.setBeginTime(new Date(getStartTime()));
			urlTrace.setEndTime(new Date(getEndTime()));
			urlTrace.setRecordTime(new Date());
			urlTrace.setUrl(seqName);
			urlTrace.setId(UUIDUtil.getUUID());
			urlTrace.setApplicationId(JavaAgentConfig.getAgentInstanceId());
            String jsonStr = requestParamsString;
            if (requestParamsString==null){
                jsonStr = JSON.toJSONString(requestParamMap);
            }
			urlTrace.setRequestParams(jsonStr);
            JavaAgent.logger.info(MessageFormat.format("[url:{0},param:{1},setOneMAgentKey:{2}]",seqName,jsonStr,this.getOneMAgentKey()));
			urlTrace.setSessionId(sessionId);
			urlTrace.setUserIp(requestIp);

			//handle exception
			if(getExceptionDescription() != null) {
				urlTrace.setHasException(true);
				urlTrace.setAlarmId(UUIDUtil.getUUID());
//				List<ExceptionInfo> exceptions = urlStore.getExceptionInfo(seqName);
				ExceptionInfo info = new ExceptionInfo();
				info.setUrlTraceId(urlTrace.getId());
				info.setAlarmId(urlTrace.getAlarmId());
				info.setId(UUIDUtil.getUUID());
				info.setRequestParams(urlTrace.getRequestParams());
				info.setUrl(urlTrace.getUrl());
				info.setExceptionDescription(getExceptionDescription());
				info.setExceptionStackTrace(getExceptionStackTrace());
				info.setRecordTime(new Date());
				info.setApplicationId(urlTrace.getApplicationId());
//				exceptions.add(info);
                AgentTraceStore.offerExceptionInfo(info);
			}
			generateMethodTraceLogs(urlTrace);
//			urlTraces.add(urlTrace);
            AgentTraceStore.offerUrlTraceLog(urlTrace);
		}
		return this.hasUrlTrace;
	}

	protected void quit(Object returnValue) {
		super.quit(returnValue);
		this.args = null;
	}

	private List<MethodTraceLog> generateMethodTraceLogs(UrlTraceLog urlTraceLog) {

		List<MethodTraceLog> methodTraceLogs = urlTraceLog.getMethodTraceLogList();
		if(methodTraceLogs != null) {

			methodTraceLogs.add(generateMethodTraceLog(urlTraceLog,this));

			List<Tracker> childs = getChildTrackers();
			if( childs != null && childs.size() > 0 ) {
				for( Tracker tracker : childs ) {
					if( tracker instanceof DefaultTracker ) {

						generateChildsMethodTraceLog((DefaultTracker) tracker,
								methodTraceLogs, urlTraceLog);

					}
				}
			}
		}
		return methodTraceLogs;
	}

	private void generateChildsMethodTraceLog(DefaultTracker childTracker,
	                                          List<MethodTraceLog> methodTraceLogs,
	                                          UrlTraceLog urlTraceLog) {
		methodTraceLogs.add(generateMethodTraceLog(urlTraceLog,childTracker));
		if(childTracker.getChildTrackers() != null) {
			for( Tracker tracker : childTracker.getChildTrackers() ) {
				if( tracker instanceof DefaultTracker ) {
					generateChildsMethodTraceLog((DefaultTracker) tracker, methodTraceLogs, urlTraceLog);
				}
			}
		}
	}

	private MethodTraceLog generateMethodTraceLog(UrlTraceLog urlTraceLog,
	                                              DefaultTracker defaultTracker) {
		MethodTraceLog methodTraceLog = new MethodTraceLog();
		methodTraceLog.setId(UUIDUtil.getUUID());
		methodTraceLog.setBeginTime(new Date(defaultTracker.getStartTime()));
		methodTraceLog.setEndTime(new Date(defaultTracker.getEndTime()));
		methodTraceLog.setConsumeTime(defaultTracker.getDuration());
		methodTraceLog.setClassName(defaultTracker.getInterceptedClassName() != null?
				defaultTracker.getInterceptedClassName().replaceAll("/",".") : "");
		methodTraceLog.setMethodName(defaultTracker.getInterceptedMethodName());
		methodTraceLog.setUrlTraceLogId(urlTraceLog.getId());
		methodTraceLog.setRecordTime(new Date());
		methodTraceLog.setInParam(defaultTracker.getMethodParams());
		methodTraceLog.setOutParam(defaultTracker.getReturnValue());
		//JavaAgent.logger.info("generateMethodTraceLog :"+methodTraceLog.getFullMethodName());
		return methodTraceLog;
	}

}
