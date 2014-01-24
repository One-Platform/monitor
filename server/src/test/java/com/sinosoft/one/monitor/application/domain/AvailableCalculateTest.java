package com.sinosoft.one.monitor.application.domain;


import com.sinosoft.one.monitor.utils.AvailableCalculate;
import com.sinosoft.one.util.test.SpringTxTestCase;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DirtiesContext
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class AvailableCalculateTest extends   SpringTxTestCase{

    private int dayMinute;
    @Test
    public void test(){
    
    }
    @Before
    public void init(){
        LocalTime localTime = LocalTime.now();
        this.dayMinute = localTime.getHourOfDay()*60+localTime.getMinuteOfHour();
    }
    

    /**
     * 测试今天还无数据的情况，并第一次的结果为true
     */
    @Test
    public void testFirstRecordAndResultIsTrue(){
        List<AvailableCalculate.AvailableCountsGroupByInterval> avCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        List<AvailableCalculate.AvailableCountsGroupByInterval> unavCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        AvailableCalculate availableCalculate =   AvailableCalculate.calculate(
            new AvailableCalculate.AvailableCalculateParam(
                    new AvailableCalculate.AvailableStatistics(0l,0l,0),
                    avCount,
                    unavCount,
                    5,
                    true,
                    null
            )
        );
        Assert.assertEquals(new BigDecimal(5),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(0),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(0),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-5),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(5),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(0),availableCalculate.getTimeToRepair());
    }


    /**
     * 测试今天还无数据的情况，并第一次的结果为false
     */
    @Test
    public void testFirstRecordAndResultIsFalse(){
        List<AvailableCalculate.AvailableCountsGroupByInterval> avCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        List<AvailableCalculate.AvailableCountsGroupByInterval> unavCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        AvailableCalculate availableCalculate =   AvailableCalculate.calculate(
                new AvailableCalculate.AvailableCalculateParam(
                        new AvailableCalculate.AvailableStatistics(0l,0l,0),
                        avCount,
                        unavCount,
                        5,
                        false,
                        null
                )
        );
        Assert.assertEquals(new BigDecimal(0),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(1),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(5),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-5),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(0),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(5),availableCalculate.getTimeToRepair());
    }


    /**
     * //以前的运行时间与不正确时间无意义，失败次数有实际意义
     * 测试当天已经有数据，此次间隔数为2，结果为正确。之前统计了不正确的时间
     * 今天已经出现了5条轮询时间为5且结果为真以及3条间隔为2且结果为真的数据
     *             4条轮询时间为5且结果为假的数据。
     *
     */
    @Test
    public void testHadStaticsDataAndCurrentResultIsTrue(){
        List<AvailableCalculate.AvailableCountsGroupByInterval> avCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        List<AvailableCalculate.AvailableCountsGroupByInterval> unavCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        AvailableCalculate.AvailableCountsGroupByInterval aCount = new AvailableCalculate.AvailableCountsGroupByInterval();
        aCount.setCount(5);
        aCount.setInterval(5);
        AvailableCalculate.AvailableCountsGroupByInterval aCount2 = new AvailableCalculate.AvailableCountsGroupByInterval();
        aCount2.setCount(3);
        aCount2.setInterval(2);
        avCount.add(aCount);
        avCount.add(aCount2);
        AvailableCalculate.AvailableCountsGroupByInterval fCount1 = new AvailableCalculate.AvailableCountsGroupByInterval();
        fCount1.setCount(4);
        fCount1.setInterval(5);
        unavCount.add(fCount1);
        AvailableCalculate availableCalculate  =   AvailableCalculate.calculate(
                new AvailableCalculate.AvailableCalculateParam(
                        new AvailableCalculate.AvailableStatistics(5l,0l,1),
                        avCount,
                        unavCount,
                        5,
                        true,
                        new AvailableCalculate.AvailableInf(DateTime.now().minusMinutes(5).toDate(),true,5)
                )
        );
        Assert.assertEquals(new BigDecimal(36),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(1),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(20),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-20-36),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(18),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(20),availableCalculate.getTimeToRepair());
        //上次数据为false
        availableCalculate  =   AvailableCalculate.calculate(
                new AvailableCalculate.AvailableCalculateParam(
                        new AvailableCalculate.AvailableStatistics(5l,0l,1),
                        avCount,
                        unavCount,
                        5,
                        true,
                        new AvailableCalculate.AvailableInf(DateTime.now().minusMinutes(5).toDate(),false,5)
                )
        );
        Assert.assertEquals(new BigDecimal(36),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(1),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(20),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-20-36),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(18),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(20),availableCalculate.getTimeToRepair());
        //上次数据为false
        availableCalculate  =   AvailableCalculate.calculate(
                new AvailableCalculate.AvailableCalculateParam(
                        new AvailableCalculate.AvailableStatistics(5l,0l,1),
                        avCount,
                        unavCount,
                        5,
                        true,
                        new AvailableCalculate.AvailableInf(DateTime.now().minusMinutes(10).toDate(),true,5)
                )
        );
        Assert.assertEquals(new BigDecimal(36),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(2),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(20),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-20-36),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(36/(2+1)),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(20/2),availableCalculate.getTimeToRepair());
    }


    /**
     * //以前的运行时间与不正确时间无意义，失败次数有实际意义
     * 测试当天已经有数据，此次间隔数为2，结果为错误。之前统计了不正确的时间
     * 今天已经出现了5条轮询时间为5且结果为真以及3条间隔为2且结果为真的数据
     *             4条轮询时间为5且结果为假的数据。
     *
     */
    @Test
    public void testHadStaticsDataAndCurrentResultIsFalse(){
        List<AvailableCalculate.AvailableCountsGroupByInterval> avCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        List<AvailableCalculate.AvailableCountsGroupByInterval> unavCount  = new ArrayList<AvailableCalculate.AvailableCountsGroupByInterval>(0);
        AvailableCalculate.AvailableCountsGroupByInterval aCount = new AvailableCalculate.AvailableCountsGroupByInterval();
        aCount.setCount(5);
        aCount.setInterval(5);
        AvailableCalculate.AvailableCountsGroupByInterval aCount2 = new AvailableCalculate.AvailableCountsGroupByInterval();
        aCount2.setCount(3);
        aCount2.setInterval(2);
        avCount.add(aCount);
        avCount.add(aCount2);
        AvailableCalculate.AvailableCountsGroupByInterval fCount1 = new AvailableCalculate.AvailableCountsGroupByInterval();
        fCount1.setCount(4);
        fCount1.setInterval(5);
        unavCount.add(fCount1);
        AvailableCalculate availableCalculate  =   AvailableCalculate.calculate(
                new AvailableCalculate.AvailableCalculateParam(
                        new AvailableCalculate.AvailableStatistics(5l,0l,1),
                        avCount,
                        unavCount,
                        2,
                        false,
                        new AvailableCalculate.AvailableInf(DateTime.now().minusMinutes(5).toDate(),true,5)
                )
        );
        Assert.assertEquals(new BigDecimal(31),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(2),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(22),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-31-22),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(31/3),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(11),availableCalculate.getTimeToRepair());

        //上次记录为false，且记录时间满足与间隔数一致的情况
        availableCalculate  =   AvailableCalculate.calculate(
                new AvailableCalculate.AvailableCalculateParam(
                        new AvailableCalculate.AvailableStatistics(5l,0l,1),
                        avCount,
                        unavCount,
                        2,
                        false,
                        new AvailableCalculate.AvailableInf(DateTime.now().minusMinutes(5).toDate(),false,5)
                )
        );
        Assert.assertEquals(new BigDecimal(31),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(1),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(22),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-31-22),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(31/2),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(22),availableCalculate.getTimeToRepair());

        //上次记录为false，且记录时间满足与间隔数一致的情况
        availableCalculate  =   AvailableCalculate.calculate(
                new AvailableCalculate.AvailableCalculateParam(
                        new AvailableCalculate.AvailableStatistics(5l,0l,1),
                        avCount,
                        unavCount,
                        2,
                        false,
                        new AvailableCalculate.AvailableInf(DateTime.now().minusMinutes(10).toDate(),false,5)
                )
        );
        Assert.assertEquals(new BigDecimal(31),availableCalculate.getAliveTime());
        Assert.assertEquals(new BigDecimal(1),availableCalculate.getFalseCount());
        Assert.assertEquals(new BigDecimal(22),availableCalculate.getStopTime());
        Assert.assertEquals(new BigDecimal(dayMinute-31-22),availableCalculate.getUnknownTime());
        Assert.assertEquals(new BigDecimal(31/2),availableCalculate.getTimeBetweenFailures());
        Assert.assertEquals(new BigDecimal(22),availableCalculate.getTimeToRepair());

    }
}
