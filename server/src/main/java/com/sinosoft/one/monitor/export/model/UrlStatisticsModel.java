package com.sinosoft.one.monitor.export.model;

/**
 * User: 韩春亮
 * Date: 14-1-21
 * Time: 下午3:31
 */
public class UrlStatisticsModel {
    private String urlId;
    private String urlName;
    private Long visitCount;
    private Long responseTime;
    private Long exceptionCount;
    private Long alarmCount;
    private Integer index;

    public Integer getIndex() {
        if (index==null)
            return 0;
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public Long getVisitCount() {
        if (visitCount == null)
            visitCount = 0L;
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }

    public Long getResponseTime() {
        if (responseTime == null)
            responseTime = 0L;
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public Long getExceptionCount() {
        if (exceptionCount == null)
            exceptionCount = 0L;
        return exceptionCount;
    }

    public void setExceptionCount(Long exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    public Long getAlarmCount() {
        if (alarmCount == null)
            alarmCount = 0L;
        return alarmCount;
    }

    public void setAlarmCount(Long alarmCount) {
        this.alarmCount = alarmCount;
    }
}
