package com.sinosoft.one.monitor.common;


import com.sinosoft.one.monitor.utils.ApplicationConfig;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 告警信息接口.
 * User: carvin
 * Date: 13-3-1
 * Time: 下午2:24
 */
public class MessageBase {
	private String resourceId;
	private List<AlarmAttribute> alarmAttributes;
	private AlarmSource alarmSource;
	private ResourceType subResourceType;
	private String subResourceId;
	private MessageBaseEventSupport messageBaseEventSupport;
	private String alarmId;
    private String urlId;
    private String urlTraceLogId;
    private String applicationId;
    public MessageBase setLinkIds(String applicationId,String urlTraceLogId,String urlId){
        this.urlId = urlId;
        this.urlTraceLogId = urlTraceLogId;
        this.applicationId = applicationId;
        return this;
    }

    /**
     * 链接模板，链接到指定的应用页面。
     */
    private LinkTemplate linkTemplate = new LinkTemplate();

    /**
     * 是否由链接
     */
    private boolean isLink = false;

    public boolean hasLink(){
        return isLink;
    }

    /**
     * 如果需要连接则传入LINK_TYPE,@see LinkTemplate
     * @param linkType
     * @return
     */
    public MessageBase setLink(int linkType){
        linkTemplate.setLinkType(linkType);
        isLink = true;
        return this;
    }

	MessageBase(String resourceId) {
		Assert.hasText(resourceId);
		this.resourceId = resourceId;
		alarmAttributes = new ArrayList<AlarmAttribute>();
	}

	void messageBaseEventSupport(MessageBaseEventSupport messageBaseEventSupport) {
		this.messageBaseEventSupport = messageBaseEventSupport;
	}

	public String getResourceId() {
		return resourceId;
	}

	public List<AlarmAttribute> getAlarmAttributes() {
		return alarmAttributes;
	}

	public AlarmSource getAlarmSource() {
		return alarmSource;
	}

	public MessageBase alarmSource(AlarmSource alarmSource) {
		this.alarmSource = alarmSource;
		return this;
	}

	public ResourceType getSubResourceType() {
		return subResourceType;
	}

	public MessageBase subResourceType(ResourceType subResourceType) {
		this.subResourceType = subResourceType;
		return this;
	}

	public String getSubResourceId() {
		return subResourceId;
	}

	public MessageBase subResourceId(String subResourceId) {
		this.subResourceId = subResourceId;
		return this;
	}

	public String getAlarmId() {
		return alarmId;
	}

	public MessageBase alarmId(String alarmId) {
		this.alarmId = alarmId;
		return this;
	}

	public MessageBase addAlarmAttribute(AttributeName attributeName, String attributeValue) {
		Assert.notNull(attributeName);
		Assert.hasText(attributeValue);
		alarmAttributes.add(AlarmAttribute.valueOf(attributeName, attributeValue));
		return this;
	}

	public void alarm() {
		messageBaseEventSupport.doMessageBase(this);
	}
    public String getLink(){
        return linkTemplate.createLink();
    }
    public class LinkTemplate{
        private int linkType;
        public final static int LOG=1;
        public final static int EXCEPTION=2;
        public final static int EUM=3;
        private String ip = ApplicationConfig.getMonitorIp();
        private String port = ApplicationConfig.getMonitorPort();
        private String linkUrl;
        public static final  String ALARM_LINK_TEMPLATE = "http://${ip}:${port}/monitor/alarm/manager/" +
                "alarmmanager/viewLogDetail/${applicationId}/${urlId}/${traceId}?alarm_id=${alarmId}";
        //http://localhost:8082/monitor/application/manager/detail/main/402881f140cec83e0140cec9afb10000
        public static final String EUM_LINK_TEMPLATE = "http://${ip}:${port}/monitor/application/manager" +
                "/detail/main/${applicationId}";

        public void setLinkType(int linkType){
            this.linkType = linkType;
        }
        public String createLink(){
            if (linkUrl!=null)
                return linkUrl;
            switch (linkType){
                case LOG:
                    linkUrl = createAlarmLink();
                    break;
                case EXCEPTION:
                    linkUrl = createAlarmLink();
                    break;
                case EUM:
                    linkUrl = createEumLink();
                    break;
            }
            return linkUrl;
        }
        String createAlarmLink(){
            return format(ALARM_LINK_TEMPLATE,ip,port,applicationId,urlId,urlTraceLogId,alarmId);
        }
        //暂时不提供EUM类型的链接
        String createEumLink(){
            return format(EUM_LINK_TEMPLATE,ip,port,applicationId);
        }
    }
    public static String format(String template,Object... args){
        if(args!=null){
            for (Object arg:args){
                template = template.replaceFirst("\\$\\{[^}]*\\}", arg.toString());
            }
        }
        return template;
    }
}
