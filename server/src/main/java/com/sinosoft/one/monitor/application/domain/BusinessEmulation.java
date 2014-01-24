package com.sinosoft.one.monitor.application.domain;

import com.google.common.collect.MapMaker;
import com.sinosoft.one.monitor.application.model.*;
import com.sinosoft.one.monitor.application.repository.EumUrlAvaRepository;
import com.sinosoft.one.monitor.application.repository.EumUrlAvaStaRepository;
import com.sinosoft.one.monitor.common.*;
import com.sinosoft.one.monitor.db.oracle.domain.StaTimeEnum;
import com.sinosoft.one.monitor.utils.AvailableCalculate;
import com.sinosoft.one.monitor.utils.ResponseUtil;
import com.sinosoft.one.util.thread.ThreadUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/**
 * 业务仿真
 * User: ChengQi
 * Date: 13-3-4
 * Time: PM3:44
 */
@Component
@Lazy(false)
public class BusinessEmulation {

    private static Logger logger = LoggerFactory.getLogger(BusinessEmulation.class);

    private static final int CORE_POOL_SIZE = 200;

    //default_interval set is 5m*60sec
    private static final int DEFAULT_INTERVAL = 5*60;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(CORE_POOL_SIZE, new ThreadUtils.CustomizableThreadFactory("appEMU"));

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationEmuService applicationEmuService;

	@Autowired
	private AlarmMessageBuilder alarmMessageBuilder;

    private static ConcurrentMap<String, Investigation> holders = new MapMaker().concurrencyLevel(32).makeMap();//监控站点线程


    /**
     * 服务器启动后开使应用系统可用性统计作业
     */
    @PostConstruct
    public void init(){
        List<Application> applications =  applicationService.findValidateApplication();
        logger.info("{}:,  {}",BusinessEmulation.class.toString(),applications.size());
        for(Application application:applications){
            eum(application);
        }
    }

    /**
     * 为给定的应用系统可用性统计作业
     * @param application
     */
    private void eum(Application application){
        application.setEnumUrls(applicationEmuService.findEumUrlByApplicationId(application.getId()));
        Investigation investigation =  new Investigation(application);
        long  interval =interval(application.getInterval());
        //延时时间按照
        ScheduledFuture<?>  scheduledFuture = executorService.scheduleAtFixedRate(investigation, interval,
                interval , TimeUnit.SECONDS);
        investigation.setScheduledFuture(scheduledFuture);
        holders.put(application.getId(),investigation);
    }

    @Transactional(readOnly = false)
    private void recordEnum( Application application, boolean result ){
        BigDecimal interval = application.getInterval();
        //记录可用性状态
        AvailabilityStatus availabilityStatus;
        //如果可用availabilityStatus = AvailabilityStatus.NORMAL，
        //  不可用availabilityStatus = AvailabilityStatus.ERROR
        if(!result) {
            availabilityStatus = AvailabilityStatus.ERROR;
        } else {
            availabilityStatus = AvailabilityStatus.NORMAL;
        }
        //创建告警信息并告警
        alarmMessageBuilder.newMessageBase(application.getId())
                .alarmSource(AlarmSource.EUM)
                .addAlarmAttribute(AttributeName.Availability, availabilityStatus.value())
                .subResourceType(ResourceType.APPLICATION)
                .subResourceId(application.getId())
                .setLink(MessageBase.LinkTemplate.EUM)
                .setLinkIds(application.getId(),null,null)
                .alarm();
        //获取当前时间
        Date date = new Date();
//	    Date now = LocalDate.now().toDate();
        //获取当天日期
        Date now = StaTimeEnum.getTime(StaTimeEnum.TODAY,date);
        //通过applicationId获取当天EumUrl统计对象
        EumUrlAvaSta eumUrlAvaSta = applicationEmuService.getEumUrlStatisticsByEnumIdAndDate(application.getId(), now);
        eumUrlAvaSta.setRecordTime(now);
        BigDecimal newInterval = interval;
        // 调整Interval
        if(eumUrlAvaSta.getId() == null) {
//            int minutes = DateTime.now().getMinuteOfHour();
            int minutes = date.getMinutes();
            if(minutes < interval.intValue()) {
                newInterval = BigDecimal.valueOf(minutes);
            }
        }
        //记录至今天访问明细
        applicationEmuService.saveEnumUrlAvailableDetail(application.getId(),result, newInterval,date);
        //记录当天的统计信息
        applicationEmuService.saveEnumUrlAvailableStatistics(application.getId(), eumUrlAvaSta, result, newInterval);
    }

    public void start(String applicationId){
        Application application = applicationService.findApplication(applicationId);
        eum(application);
    }

    public void stop(String applicationId){
        if(holders.get(applicationId)!=null){
            holders.get(applicationId).stop();
            holders.remove(applicationId);
        }
    }

    public void restart(String applicationId){
        stop(applicationId);
        start(applicationId);
    }

    private long interval(BigDecimal interval){
        if(interval==null)
            return DEFAULT_INTERVAL;

       return (interval.equals(BigDecimal.ZERO)?DEFAULT_INTERVAL:interval.longValue())*60;
    }

    private class Investigation implements Runnable {

        private Logger loggerInv = LoggerFactory.getLogger(Investigation.class);

        private final Application application;

        private ScheduledFuture<?>  scheduledFuture;

        public Investigation(final Application application){
            this.application = application;
        }

        //modified by hanchunliang 2013-12-27
        @Override
        public void run() {
            try {
                //获取可用性结果
                boolean result = ResponseUtil.getResponseCode(createHttpUrl())!= 404;
                //可用性记录
                recordEnum(application,result);
            } catch (Throwable t){
                logger.error("[扫描URL出错] there is an error occurred on Scanning URL or recordEnum...");
            }

        }
        public void stop(){
            this.scheduledFuture.cancel(true);
        }
        public String createHttpUrl(){
            StringBuilder str = new  StringBuilder()
                    .append("http://")
                    .append(application.getApplicationIp())
                    .append(":")
                    .append(application.getApplicationPort())
                    .append("/")
                    .append(application.getApplicationName());
            return str.toString();
        }
        public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
    }
}
