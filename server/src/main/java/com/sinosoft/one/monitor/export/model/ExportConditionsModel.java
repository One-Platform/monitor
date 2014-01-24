package com.sinosoft.one.monitor.export.model;

import java.util.Date;

/**
 * User: 韩春亮
 * Date: 14-1-21
 * Time: 下午3:01
 */
public class ExportConditionsModel {
    /**
     * 应用系统ID
     */
    private String applicationId;
    /**
     * 统计起始时间
     */
    private Date startTime;
    /**
     * 统计结束时间
     */
    private Date endTime;
    /**
     * 统计条目数
     */
    private int count;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
