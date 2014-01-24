package com.sinosoft.one.monitor.application.domain;

import com.sinosoft.one.monitor.common.Trend;

/**
 * 应用可用性
 * User: ChengQi
 * Date: 13-3-8
 * Time: AM1:01
 */
public class ApplicationAvailableInf {

    private Trend trend;

    private Integer count;

    private Integer availableCount;

    public ApplicationAvailableInf(Trend trend, int count, int availableCount) {
        this.trend = trend;
        this.count = count;
        this.availableCount = availableCount;
    }

    public ApplicationAvailableInf(){}

    public void setTrend(Trend trend){
        this.trend = trend;
    }

    public Trend getTrend() {
        return trend;
    }

    public Integer getCount() {
        if(count==null){
            count=0;
        }
        return count;
    }

    public void setCount(Integer count){
        this.count = count;
    }

    public Integer getAvailableCount() {
        if(availableCount==null){
            availableCount=0;
        }
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount){
        this.availableCount = availableCount;
    }

}
