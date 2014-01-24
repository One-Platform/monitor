package com.sinosoft.one.monitor.export.domain;

import com.sinosoft.one.monitor.application.model.Application;
import com.sinosoft.one.monitor.application.repository.ApplicationRepository;
import com.sinosoft.one.monitor.export.repository.UrlExportStatisticsRepository;
import com.sinosoft.one.monitor.export.model.ExportConditionsModel;
import com.sinosoft.one.monitor.export.model.ExportModel;
import com.sinosoft.one.monitor.export.model.UrlStatisticsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: 韩春亮
 * Date: 14-1-21
 * Time: 下午2:23
 */
@Component
public class ApplicationUrlExportService {
    @Autowired
    UrlExportStatisticsRepository urlExportStatisticsRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    public List<Application> listValidApplication(){
        List<Application> applicationList = applicationRepository.findAllActiveApplication();
        return  applicationList;
    }

    public ExportModel createExportModel(ExportConditionsModel condition){
        ExportModel exportModel = new ExportModel();
        Date dateStart = condition.getStartTime();
        Date dateEnd = condition.getEndTime();
        boolean isMinTimeUnit = !(dateStart.getTime()%(3600*1000)==0 && dateEnd.getTime()%(3600*1000)==0);
        //访问量统计
        List<UrlStatisticsModel> visitList =  findMaxVisitUrls(condition,isMinTimeUnit);
        exportModel.setVisitStatisticsList(visitList);
        //告警统计
        List<UrlStatisticsModel> alarmList = findMaxAlarmUrls(condition);
        exportModel.setAlarmStatisticsList(alarmList);
        //响应时间统计
        List<UrlStatisticsModel> responseTimeList = findMaxTimeUrls(condition,isMinTimeUnit);
        exportModel.setResponseTimeStatisticsList(responseTimeList);
        //异常统计
        List<UrlStatisticsModel> exceptionList = findMaxExceptionUrls(condition) ;
        exportModel.setExceptionStatisticsList(exceptionList);
        //解析URL集合
        Set<String> urlSet = new HashSet<String>();
        for (UrlStatisticsModel urlStatisticsModel:visitList){
            urlSet.add(urlStatisticsModel.getUrlName());
        }
        for (UrlStatisticsModel urlStatisticsModel:alarmList){
            urlSet.add(urlStatisticsModel.getUrlName());
        }
        for (UrlStatisticsModel urlStatisticsModel:responseTimeList){
            urlSet.add(urlStatisticsModel.getUrlName());
        }
        for (UrlStatisticsModel urlStatisticsModel:exceptionList){
            urlSet.add(urlStatisticsModel.getUrlName());
        }
        //综合数据
        List<UrlStatisticsModel> comprehensiveList = null;
        Map<String,Long> alarmMap = new HashMap<String,Long>();
        if (urlSet.isEmpty()){
            comprehensiveList = Collections.EMPTY_LIST;
        } else {
            //访问量，相应时间
            if (isMinTimeUnit){
                comprehensiveList = urlExportStatisticsRepository.findUrlStatisticsAllByUrls(urlSet, condition);
            } else {
                comprehensiveList = urlExportStatisticsRepository.findUrlStatisticsByUrls(urlSet, condition);
            }
            //告警
            List<UrlStatisticsModel> alarmCountList =  urlExportStatisticsRepository.findUrlStatisticsAlarmByUrls(urlSet,condition);
            toAlarmMap(alarmMap,alarmCountList);
        }
        int index=0;
        for(UrlStatisticsModel urlStatisticsModel:comprehensiveList){
            long visitCount = urlStatisticsModel.getVisitCount();
            long totalTime = urlStatisticsModel.getResponseTime();
            long avgResponseTime = 0L;
            if (visitCount!=0) {
                avgResponseTime = totalTime/visitCount;
            }
            urlStatisticsModel.setResponseTime(avgResponseTime);
            long alarmCount = urlStatisticsModel.getAlarmCount();
            urlStatisticsModel.setAlarmCount(alarmCount);
            urlStatisticsModel.setIndex(index++);
        }
        exportModel.setComprehensiveList(comprehensiveList);
        return exportModel;
    }
    private void toAlarmMap(Map<String,Long> alarmMap,List<UrlStatisticsModel> alarmCountList){
        for(UrlStatisticsModel urlStatisticsModel:alarmCountList){
            alarmMap.put(urlStatisticsModel.getUrlName(),urlStatisticsModel.getAlarmCount());
        }
    }
    List<UrlStatisticsModel> findMaxVisitUrls(ExportConditionsModel condition,boolean minTimeUnit) {
        Pageable pageable = new PageRequest(0,condition.getCount(),new Sort(Sort.Direction.DESC,"visitCount"));
        try{
            Page<UrlStatisticsModel> urlStatisticsModelPage;
            if(minTimeUnit){
                urlStatisticsModelPage = urlExportStatisticsRepository.findUrlStatisticsLimitByVisitCountInAll(pageable,condition) ;
            }else{
                urlStatisticsModelPage = urlExportStatisticsRepository.findUrlStatisticsLimitByVisitCount(pageable,condition) ;
            }
            List<UrlStatisticsModel> urlStatisticsModels = urlStatisticsModelPage.getContent();
            return urlStatisticsModels;
        } catch (Throwable t){
            return Collections.EMPTY_LIST;
        }
    }

    List<UrlStatisticsModel> findMaxTimeUrls(ExportConditionsModel condition,boolean minTimeUnit) {
        Pageable pageable = new PageRequest(0,condition.getCount(),new Sort(Sort.Direction.DESC,"responseTime"));
        try{
            Page<UrlStatisticsModel> urlStatisticsModelPage;
            if(minTimeUnit){
                urlStatisticsModelPage = urlExportStatisticsRepository.findUrlStatisticsLimitByResponseTimeInAll(pageable, condition) ;
            }else{
                urlStatisticsModelPage = urlExportStatisticsRepository.findUrlStatisticsLimitByResponseTime(pageable, condition) ;
            }
            List<UrlStatisticsModel> urlStatisticsModels = urlStatisticsModelPage.getContent();
            return urlStatisticsModels;
        } catch (Throwable t){
            return Collections.EMPTY_LIST;
        }
    }

    List<UrlStatisticsModel> findMaxAlarmUrls(ExportConditionsModel condition) {
        try {
            Pageable pageable = new PageRequest(0,condition.getCount(),new Sort(Sort.Direction.DESC,"alarmCount"));
            Page<UrlStatisticsModel> urlStatisticsModelPage = urlExportStatisticsRepository.findUrlStatisticsLimitByAlarmCount(pageable, condition) ;
            List<UrlStatisticsModel> urlStatisticsModels = urlStatisticsModelPage.getContent();
            return urlStatisticsModels;
        } catch (Throwable t){
            return Collections.EMPTY_LIST;
        }
    }

    List<UrlStatisticsModel> findMaxExceptionUrls(ExportConditionsModel condition) {
        try {
            Pageable pageable = new PageRequest(0,condition.getCount(),new Sort(Sort.Direction.DESC,"exceptionCount"));
            Page<UrlStatisticsModel> urlStatisticsModelPage = urlExportStatisticsRepository.findUrlStatisticsLimitByExceptionCount(pageable, condition) ;
            List<UrlStatisticsModel> urlStatisticsModels = urlStatisticsModelPage.getContent();
            return urlStatisticsModels;
        } catch (Throwable t){
            return Collections.EMPTY_LIST;
        }
    }
}
