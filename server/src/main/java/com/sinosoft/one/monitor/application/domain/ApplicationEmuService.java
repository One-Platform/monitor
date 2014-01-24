package com.sinosoft.one.monitor.application.domain;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sinosoft.one.monitor.application.model.EumUrl;
import com.sinosoft.one.monitor.application.model.EumUrlAva;
import com.sinosoft.one.monitor.application.model.EumUrlAvaSta;
import com.sinosoft.one.monitor.application.repository.EumUrlAvaRepository;
import com.sinosoft.one.monitor.application.repository.EumUrlAvaStaRepository;
import com.sinosoft.one.monitor.application.repository.EumUrlRepository;
import com.sinosoft.one.monitor.common.Trend;

/**
 * 仿真URL服务对象
 * User: cq
 * Date: 13-3-6
 * Time: AM11:37
 */
@Service
@Lazy(false)
public class ApplicationEmuService {

    @Autowired
    private EumUrlRepository eumUrlRepository;

    @Autowired
    private EumUrlAvaRepository eumUrlAvaRepository;

    @Autowired
    private EumUrlAvaStaRepository eumUrlAvaStaRepository;

    private Logger logger = LoggerFactory.getLogger(ApplicationEmuService.class);

    /**
     * enumUrl缓存
     */
    private LoadingCache<String,EumUrl> enumUrlCache = CacheBuilder.newBuilder().build(new CacheLoader<String,EumUrl>(){
        @Override
        public EumUrl load(String urlId) throws Exception {
            List<EumUrl> eumUrls = eumUrlRepository.findByUrlId(urlId);
            if (eumUrls.isEmpty())
            {
                return null;
            }
            if (eumUrls.size() > 0)
            {
                return eumUrls.get(0);
            }
            return eumUrls.get(0);
        }
    });

    EumUrlAvaSta getEumUrlStatisticsByEnumIdAndDate(String eumUrlId,Date date){
        List<EumUrlAvaSta> eumUrlAvaStas = eumUrlAvaStaRepository.findByRecordTimeAndEumUrlId(date,eumUrlId);
        return eumUrlAvaStas.isEmpty()?newEumUrlAvaSta():eumUrlAvaStas.get(0);
    }

    public EumUrlAva getTodayLatestEumUrlAva(String eumUrlId){
        Assert.hasText(eumUrlId);
        Sort desc = new Sort(Sort.Direction.DESC,"recordTime");
        Pageable pageDesc = new PageRequest(0,1,desc);
        List<EumUrlAva> eumUrlAvas = eumUrlAvaRepository.findByEumUrlId(eumUrlId, pageDesc).getContent();
        if(eumUrlAvas.isEmpty())
            return  null;
        return eumUrlAvas.get(0);
    }

    private EumUrlAvaSta newEumUrlAvaSta(){
        EumUrlAvaSta eumUrlAvaSta = new EumUrlAvaSta();
        eumUrlAvaSta.setRecordTime(new Date());
        eumUrlAvaSta.setNormalRuntime(BigDecimal.ZERO);
        eumUrlAvaSta.setTotalFailureTime(BigDecimal.ZERO);
        eumUrlAvaSta.setFailureCount(BigDecimal.ZERO);
        return eumUrlAvaSta;
    }

    public ApplicationAvailableInf getApplicationAvailableToday(String applicationId) throws EumUrlsNotFoundException {
        ApplicationAvailableInf applicationAvailableInf = eumUrlAvaRepository.staticsAvailableCount(applicationId);
        applicationAvailableInf.setTrend(calTrend(applicationId));
        return applicationAvailableInf;
    }
    public ApplicationAvailableInf getAppAvailableToday(String applicationId) throws EumUrlsNotFoundException {
        Assert.hasText(applicationId);
        ApplicationAvailableInf availableInf = getApplicationAvailableToday(applicationId);
        return availableInf;
    }

    public Trend urlAvaTrendByUrlId(String applicationId){
        return calTrend(applicationId);
    }


    public UrlAvailableInf getUrlAvailableToday(String applicationId,String urlId){
        EumUrl eumUrl = getEumUrlByUrlId(urlId);
//        String applicationId = eumUrl.getApplication().getId();
        List<EumUrlAva> urlAvaList = eumUrlAvaRepository.findTodayEumUrlAvaList(applicationId);
        EumUrlAva firstEumUrlAva = null;
        EumUrlAva lastEumUrlAva = null;
        if (urlAvaList!=null && urlAvaList.size()>0){
            firstEumUrlAva = urlAvaList.get(0);
            lastEumUrlAva = urlAvaList.get(urlAvaList.size()-1);
        }
        EumUrlAva latestErrorEumUrlAva = getTodayLatestEumUrlAva(urlAvaList);
        int availableCount = 0;
        for (EumUrlAva eumUrlAva:urlAvaList){
            if ("1".equals(eumUrlAva.getState()))
                availableCount++;
        }
        Interval interval = new Interval(
                new DateTime(
                        firstEumUrlAva == null ? LocalDate.now().toDate(): firstEumUrlAva.getRecordTime()
                ),
                new DateTime(
                        lastEumUrlAva == null ? LocalDate.now().toDate(): lastEumUrlAva.getRecordTime()
                )
        );
        return new UrlAvailableInf(urlAvaTrendByUrlId(applicationId),
                urlAvaList.size(),
                availableCount,
                interval.toPeriod(),
                latestErrorEumUrlAva == null ? null : latestErrorEumUrlAva.getRecordTime(),
                eumUrl.getUrl());

    }

    /**
     * 返回null则认为没有故障
     * 寻找最后一次故障的记录。
     * 最后一次状态连续为0的记录段的起始位置为最后故障时间
     */
    private EumUrlAva getTodayLatestEumUrlAva(List<EumUrlAva> urlAvaList){
        EumUrlAva eumUrlAva = null;
        List<EumUrlAva> tempList = new ArrayList<EumUrlAva>();
        if (urlAvaList!=null && urlAvaList.size()>0){
            boolean flag = true;
            for (EumUrlAva var:urlAvaList){
                if (flag){
                    //如果状态不可用记录一次，将flag设置为false,从而如果再不做记录
                    if ("0".equals(var.getState())){
                        tempList.add(var);
                        flag = false;
                    }
                }
                //如果状态为可用，则将flag设置为true,然后准备对状态为不可用的做记录
                if ("1".equals(var.getState())){
                    flag = true;
                }
            }
        }
        if (tempList.size()>0){
            eumUrlAva = tempList.get(tempList.size()-1);
        }
        return eumUrlAva;
    }

    EumUrl getEumUrlByUrlId(String urlId) {
        Assert.hasText(urlId);
        try {
            return enumUrlCache.get(urlId);
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
    }

    List<TimeQuantumAvailableInfo> findAvailableStatisticsByUrlId(String urlId) {
        Assert.hasText(urlId);
//        DateTime now = DateTime.now().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime now = DateTime.now();
        DateTime prev = now.minusHours(6);
        // modified urlId to applicationId
        List<TimeQuantumAvailableStatistics>  list = eumUrlAvaRepository.statisticsByEumUrlIdAndRecordTime(getEumUrlByUrlId(urlId).getApplication().getId(),prev.toDate(),now.toDate());
        Map<String,TimeQuantumAvailableInfo> map = Maps.newHashMap();
        for(TimeQuantumAvailableStatistics statistics:list){
            TimeQuantumAvailableInfo timeQuantumAvailableInfo = null;
            if(map.get(statistics.getTimeQuantum())==null){
                 timeQuantumAvailableInfo = new TimeQuantumAvailableInfo();
                if(statistics.getStatus().equals("1")){
                    timeQuantumAvailableInfo.setAvaCount(statistics.getCount());
                }
                timeQuantumAvailableInfo.setCount(statistics.getCount());
	            timeQuantumAvailableInfo.setTimeQuantum(statistics.getTimeQuantum());
                map.put(statistics.getTimeQuantum(),timeQuantumAvailableInfo);
            }else{
                timeQuantumAvailableInfo =map.get(statistics.getTimeQuantum());
                if(statistics.getStatus().equals("1")){
                    timeQuantumAvailableInfo.setAvaCount(timeQuantumAvailableInfo.getAvaCount() + statistics.getCount());
                }
                int count = timeQuantumAvailableInfo.getCount()+statistics.getCount();
                timeQuantumAvailableInfo.setCount(count);
                timeQuantumAvailableInfo.setTimeQuantum(statistics.getTimeQuantum());
            }
        }
        return Lists.newArrayList(map.values());
    }


    void saveEnumUrlAvailableDetail(String applicationId,boolean result,BigDecimal interval,Date recordTime){
        deleteEnumUrlAvaData(applicationId);
        EumUrlAva eumUrlAva = new EumUrlAva();
        eumUrlAva.setEumUrlId(applicationId);
        eumUrlAva.setInterval(interval);
        //eumUrlAva.setRecordTime(DateTime.now().toDate());
        eumUrlAva.setRecordTime(recordTime);
        eumUrlAva.setState(result?"1":"0");
        eumUrlAvaRepository.save(eumUrlAva);
    }

    void deleteEnumUrlAvaData(String eumUrlId){
        EumUrlAva eumAvaLast = getTodayLatestEumUrlAva(eumUrlId);
        if(eumAvaLast!=null){
            LocalDate prevDate = new LocalDate(eumAvaLast.getRecordTime());
            if(prevDate.compareTo(LocalDate.now())<0){
                eumUrlAvaRepository.deleteByLessThanDate(LocalDate.now().minusDays(1).toDate());
            }
        }
    }

    List<EumUrl> findEumUrlByApplicationId(String applicationId){
        return  eumUrlRepository.findByApplication_Id(applicationId);
    }


    private Trend calTrend(String applicationId){
        List<String> states = eumUrlAvaRepository.findAvailableStates(applicationId);
        if (states!=null && states.size()>0){
            if(StringUtils.equals("0",states.get(0))){
                return Trend.DROP;
            }
        } else {
            return Trend.DROP;
        }
        return Trend.RISE;
    }


    public void saveEnumUrlAvailableStatistics(String applicationId, EumUrlAvaSta eumUrlAvaSta, boolean result,BigDecimal interval) {
        Assert.hasText(applicationId);
        Assert.notNull(interval);
        //根据应用ID获取当前所有可用性，根据时间正序
        List<EumUrlAva> eumUrlAvas = eumUrlAvaRepository.findTodayEumUrlAvaList(applicationId);
        //总停机时间
        BigDecimal totalFailureTime = new BigDecimal(0);
        //正常运行时间
        BigDecimal normalRuntime = new BigDecimal(0);
        int totalfailueCountLine = 0;
        int flag = 0;
        for (int i=0;i<eumUrlAvas.size();i++){
            EumUrlAva eumUrlAva = eumUrlAvas.get(i);
            if ("1".equals(eumUrlAva.getState())){
                normalRuntime = normalRuntime.add(eumUrlAva.getInterval());
                flag = 0;
            } else {
                totalFailureTime = totalFailureTime.add(eumUrlAva.getInterval());
                if (flag==0){
                    totalfailueCountLine++;
                    flag = 1;
                }
            }
        }
        //总停机次数
        BigDecimal failureCount  = new BigDecimal(totalfailueCountLine);
        //平均停机时间=总停机时间/停机次数
        BigDecimal avgFailureTime = totalFailureTime;
        if (totalfailueCountLine>0){
            avgFailureTime = new BigDecimal(totalFailureTime.longValue()/totalfailueCountLine);
        }
        eumUrlAvaSta.setTotalFailureTime(totalFailureTime);
        eumUrlAvaSta.setFailureCount(failureCount);
        eumUrlAvaSta.setAvgFailureTime(avgFailureTime);
        eumUrlAvaSta.setNormalRuntime(normalRuntime);
        eumUrlAvaSta.setEumUrlId(applicationId);

        eumUrlAvaStaRepository.save(eumUrlAvaSta);
    }
}
