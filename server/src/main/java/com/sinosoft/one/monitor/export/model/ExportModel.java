package com.sinosoft.one.monitor.export.model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-1-23
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
public class ExportModel {
    /**
     * 访问次数
     */
    private List<UrlStatisticsModel> visitStatisticsList;
    /**
     * 响应时间
     */
    private List<UrlStatisticsModel> responseTimeStatisticsList;
    /**
     * 异常
     */
    private List<UrlStatisticsModel> exceptionStatisticsList;
    /**
     * 告警
     */
    private List<UrlStatisticsModel> alarmStatisticsList;

    /**
     * 综合列表
     */
    private List<UrlStatisticsModel> comprehensiveList;

    public List<UrlStatisticsModel> getComprehensiveList() {
        return comprehensiveList;
    }

    public void setComprehensiveList(List<UrlStatisticsModel> comprehensiveList) {
        this.comprehensiveList = comprehensiveList;
    }

    public List<UrlStatisticsModel> getVisitStatisticsList() {
        return visitStatisticsList;
    }

    public void setVisitStatisticsList(List<UrlStatisticsModel> visitStatisticsList) {
        this.visitStatisticsList = visitStatisticsList;
    }

    public List<UrlStatisticsModel> getResponseTimeStatisticsList() {
        return responseTimeStatisticsList;
    }

    public void setResponseTimeStatisticsList(List<UrlStatisticsModel> responseTimeStatisticsList) {
        this.responseTimeStatisticsList = responseTimeStatisticsList;
    }

    public List<UrlStatisticsModel> getExceptionStatisticsList() {
        return exceptionStatisticsList;
    }

    public void setExceptionStatisticsList(List<UrlStatisticsModel> exceptionStatisticsList) {
        this.exceptionStatisticsList = exceptionStatisticsList;
    }

    public List<UrlStatisticsModel> getAlarmStatisticsList() {
        return alarmStatisticsList;
    }

    public void setAlarmStatisticsList(List<UrlStatisticsModel> alarmStatisticsList) {
        this.alarmStatisticsList = alarmStatisticsList;
    }
}
