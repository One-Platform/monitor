package com.sinosoft.one.monitor.export.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: 韩春亮
 * Date: 14-1-23
 * Time: 上午10:59
 */
public class ExportViewModel {

    /**
     * 格式[{name:"",data:[2]},...]
     */
    private List<Map<String,Object>> visitData;
    private List<Map<String,Object>> alarmData;
    private List<Map<String,Object>> responseTimeData;
    private List<Map<String,Object>> exceptionData;
    /**
     * 格式[{url:"",visit:5,responseTime:4,alarm:3}...]
     */
    private List<Map<String,Object>> gridData;

    public void setVisitData(List<Map<String, Object>> visitData) {
        this.visitData = visitData;
    }

    public void setAlarmData(List<Map<String, Object>> alarmData) {
        this.alarmData = alarmData;
    }

    public void setResponseTimeData(List<Map<String, Object>> responseTimeData) {
        this.responseTimeData = responseTimeData;
    }

    public void setExceptionData(List<Map<String, Object>> exceptionData) {
        this.exceptionData = exceptionData;
    }

    public void setGridData(List<Map<String, Object>> gridData) {
        this.gridData = gridData;
    }

    public List<Map<String, Object>> getVisitData() {
        return visitData;
    }

    public void initVisitData(List<UrlStatisticsModel> urlStatisticsModels) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (UrlStatisticsModel urlStatisticsModel:urlStatisticsModels){
            Map<String,Object> row = new HashMap<String, Object>() ;
            row.put("name",urlStatisticsModel.getUrlName());
            row.put("data",new Long[]{urlStatisticsModel.getVisitCount()});
            rows.add(row);
        }
        this.visitData = rows;
    }

    public List<Map<String, Object>> getAlarmData() {
        return alarmData;
    }

    public void initAlarmData(List<UrlStatisticsModel> urlStatisticsModels) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (UrlStatisticsModel urlStatisticsModel:urlStatisticsModels){
            Map<String,Object> row = new HashMap<String, Object>() ;
            row.put("name",urlStatisticsModel.getUrlName());
            row.put("data",new Long[]{urlStatisticsModel.getAlarmCount()});
            rows.add(row);
        }
        this.alarmData = rows;
    }

    public List<Map<String, Object>> getResponseTimeData() {
        return responseTimeData;
    }

    public void initResponseTimeData(List<UrlStatisticsModel> urlStatisticsModels) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (UrlStatisticsModel urlStatisticsModel:urlStatisticsModels){
            Map<String,Object> row = new HashMap<String, Object>() ;
            row.put("name",urlStatisticsModel.getUrlName());
            row.put("data",new Long[]{urlStatisticsModel.getResponseTime()});
            rows.add(row);
        }
        this.responseTimeData = rows;
    }

    public List<Map<String, Object>> getExceptionData() {
        return exceptionData;
    }

    public void initExceptionData(List<UrlStatisticsModel> urlStatisticsModels) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (UrlStatisticsModel urlStatisticsModel:urlStatisticsModels){
            Map<String,Object> row = new HashMap<String, Object>() ;
            row.put("name",urlStatisticsModel.getUrlName());
            row.put("data",new Long[]{urlStatisticsModel.getExceptionCount()});
            rows.add(row);
        }
        this.exceptionData = rows;
    }

    public List<Map<String, Object>> getGridData() {
        return gridData;
    }

    public void initGridData(List<UrlStatisticsModel> urlStatisticsModels) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (UrlStatisticsModel urlStatisticsModel:urlStatisticsModels){
            Map<String,Object> row = new HashMap<String, Object>() ;
            row.put("index",urlStatisticsModel.getIndex());
            row.put("url",urlStatisticsModel.getUrlName());
            row.put("visit",urlStatisticsModel.getVisitCount());
            row.put("responseTime",urlStatisticsModel.getResponseTime());
            row.put("alarm",urlStatisticsModel.getAlarmCount());
            rows.add(row);
        }
        this.gridData = rows;
    }
}
