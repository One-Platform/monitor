package com.sinosoft.monitor.agent.trackers;

import com.sinosoft.monitor.agent.JavaAgent;
import com.sinosoft.monitor.agent.instrumentation.interceptor.ComponentNames;
import com.sinosoft.monitor.agent.trackers.method.arguments.ArgsPropertyFilter;
import com.sinosoft.monitor.agent.trackers.store.InvalidTypeStore;
import com.sinosoft.monitor.com.alibaba.fastjson.JSONArray;
import com.sinosoft.monitor.com.alibaba.fastjson.serializer.SerializerFeature;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultTracker extends AbstractTracker
		implements ComponentNames {

	protected long startTime;
	protected int duration;
	protected long endTime;

	protected String interceptedClassName;
	protected String interceptedMethodName;
	protected String methodParams;
	protected String returnValue;
	protected String componentName;

	protected String exceptionDescription;
	protected String exceptionStackTrace;

	/**
	 * 为除去filter，servlet，jsp等方法调用的构造方法
	 * @param className
	 * @param methodName
	 * @param thiz
	 * @param args
	 * @param isNeedMethodParams
	 */
	public DefaultTracker(String className, String methodName, Object thiz,
	                      Object[] args, boolean isNeedMethodParams) {
		this.interceptedClassName = className;
		this.interceptedMethodName = methodName;
		this.startTime = System.currentTimeMillis();
		if(isNeedMethodParams) {
            try {
                this.methodParams = args != null ? JSONArray.toJSONString(args,
                        ArgsPropertyFilter.getInstence(),
                        SerializerFeature.QuoteFieldNames,
                        SerializerFeature.SkipTransientField) : "";
                JavaAgent.logger.info(MessageFormat.format("[methodParams:{0}]",this.methodParams));
            }catch (Throwable throwable){
                JavaAgent.logger.severe("agent准备据传输，方法参数转JSON出错..");
                InvalidTypeStore.synchronizedLocalFile();
            }

//			this.methodParams = this.methodParams
//					.replaceAll("\\[","")
//					.replaceAll("\\]","")
//					.replaceAll("\\{","")
//					.replaceAll("\\}","")
//					.replaceAll("'","")
//					.replaceAll(":","")
//					.replaceAll(",","")
//					.replaceAll("\"","")
//					.replaceAll("<","")
//					.replaceAll("/","")
//					.replaceAll(">","")
//					.replaceAll("\\s","");
//			System.out.println(this.methodParams);
		}


	}

	public DefaultTracker(String className, String methodName, Object thiz, Object[] args) {
		this.interceptedClassName = className;
		this.interceptedMethodName = methodName;
		this.startTime = System.currentTimeMillis();
		this.methodParams = null;
	}



	public long getStartTime() {
		return this.startTime;
	}

	@Override
	public long getEndTime() {
		return this.endTime;
	}

	public int getDuration() {
		return this.duration;
	}

	public String getInterceptedClassName() {
		return this.interceptedClassName;
	}

	public String getInterceptedMethodName() {
		return this.interceptedMethodName;
	}

	public void quit(int opcode, Object returnValue) {

		quit(returnValue);
	}

	public void quit(Throwable th) {
		handleException(th);
		quit();
	}

	protected void quit(){
		this.endTime = System.currentTimeMillis();
		this.duration = ((int) (this.endTime - this.startTime));
	}

	protected void quit(Object returnValue) {
//		this.returnValue = new JSONArray(Arrays.asList(returnValue));
        try {
            this.returnValue = returnValue != null ? JSONArray.toJSONString(returnValue,
                    ArgsPropertyFilter.getInstence(),
                    SerializerFeature.QuoteFieldNames,
                    SerializerFeature.SkipTransientField) : "";
            if (!"".equals(this.returnValue)){
                JavaAgent.logger.info(MessageFormat.format("[methodReturns:{0}]",this.returnValue));
            }
        } catch (Throwable t){
            JavaAgent.logger.severe("agent准备据传输，方法返回值转JSON出错..");
            InvalidTypeStore.synchronizedLocalFile();
        }
        this.endTime = System.currentTimeMillis();
		this.duration = ((int) (this.endTime - this.startTime));
	}

	public String getTrackerSignature() {
		return this.interceptedClassName + "." + this.interceptedMethodName + "()";
	}

	@Override
	public boolean generateUrlTraceLog(String paramString) {
		return isUrlTraceHolded();
	}

	@Override
	public boolean isUrlTraceHolded() {
		return false;
	}

	public String getComponentName() {
		return this.componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getExceptionDescription() {
		return exceptionDescription;
	}

	public String getExceptionStackTrace() {
		return exceptionStackTrace;
	}

	public String getMethodParams() {
		return methodParams;
	}

	public String getReturnValue() {
		return returnValue;
	}

	private void handleException(Throwable th) {
		if(th != null) {
			exceptionDescription = th.getClass().getName();
			StringBuilder stackTrace = new StringBuilder(th.getMessage() == null ?
					"" : th.getMessage()).append("\n");
			for( StackTraceElement element : th.getStackTrace() ) {
				stackTrace.append(element.toString()).append("\n\t");
			}
			exceptionStackTrace = stackTrace.substring(0,stackTrace.length() - 1);
		}
	}
}
