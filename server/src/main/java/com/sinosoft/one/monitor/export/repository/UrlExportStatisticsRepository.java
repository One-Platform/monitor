package com.sinosoft.one.monitor.export.repository;

import com.sinosoft.one.data.jade.annotation.SQL;
import com.sinosoft.one.monitor.application.model.Url;
import com.sinosoft.one.monitor.export.model.ExportConditionsModel;
import com.sinosoft.one.monitor.export.model.UrlStatisticsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-1-23
 * Time: 下午12:56
 * To change this template use File | Settings | File Templates.
 */
public interface UrlExportStatisticsRepository extends PagingAndSortingRepository<Url, String> {
    //查询最大访问数前n条
    @SQL("select sum(a.total_count) as visitCount,a.url as urlName" +
            "  from ge_monitor_url_response_time a" +
            " where a.application_id=?2.applicationId" +
            "   and a.total_count>0" +
            "   and a.record_time between ?2.startTime and ?2.endTime" +
            " group by a.url")
    Page<UrlStatisticsModel> findUrlStatisticsLimitByVisitCount(Pageable pageable, ExportConditionsModel exportConditionsModel);

    //查询最大访问数前n条,统计最小粒度的
    @SQL("select count(a.url) as visitCount,a.url as urlName" +
            "  from ge_monitor_url_trace_log a" +
            " where a.application_id=?2.applicationId" +
            "   and a.record_time between ?2.startTime and ?2.endTime" +
            " group by a.url")
    Page<UrlStatisticsModel> findUrlStatisticsLimitByVisitCountInAll(Pageable pageable, ExportConditionsModel exportConditionsModel);

    //综合查询
    @SQL("select sum(a.total_count) as visitCount,sum(a.total_response_time) as responseTime,a.url as urlName" +
            "  from ge_monitor_url_response_time a" +
            " where a.application_id=?2.applicationId" +
            "   and a.url in (?1)" +
            "   and a.total_count>0" +
            "   and a.record_time between ?2.startTime and ?2.endTime" +
            " group by a.url" +
            " order by visitCount desc,responseTime desc")
    List<UrlStatisticsModel> findUrlStatisticsByUrls(Set<String> urlSet, ExportConditionsModel exportConditionsModel);

    //综合查询,统计最小粒度的
    @SQL("select count(a.url) as visitCount,sum(a.consume_time) as responseTime,a.url as urlName" +
            "  from ge_monitor_url_trace_log a" +
            " where a.application_id=?2.applicationId" +
            "   and a.url in (?1)" +
            "   and a.record_time between ?2.startTime and ?2.endTime" +
            " group by a.url" +
            " order by visitCount desc,responseTime desc")
    List<UrlStatisticsModel> findUrlStatisticsAllByUrls(Set<String> urlSet, ExportConditionsModel exportConditionsModel);

    //查询最大相应时间前n条
    @SQL("select max(a.max_response_time) as responseTime,a.url as urlName" +
            "  from ge_monitor_url_response_time a" +
            " where a.application_id=?2.applicationId" +
            "   and a.total_count>0" +
            "   and a.record_time between ?2.startTime and ?2.endTime" +
            " group by a.url")
    Page<UrlStatisticsModel> findUrlStatisticsLimitByResponseTime(Pageable pageable, ExportConditionsModel exportConditionsModel);

    @SQL("select max(b.consume_time) as responseTime,b.url as urlName" +
            "   from ge_monitor_url_trace_log b" +
            "  where b.application_id=?2.applicationId" +
            "    and b.consume_time>0" +
            "    and b.record_time between ?2.startTime and ?2.endTime" +
            "  group by b.url")
    Page<UrlStatisticsModel> findUrlStatisticsLimitByResponseTimeInAll(Pageable pageable, ExportConditionsModel exportConditionsModel);

    //查询最多异常数前n条
    @SQL("select count(a.url) as exceptionCount, a.url as urlName" +
            "  from ge_monitor_exception_info a" +
            " where a.application_id=?2.applicationId" +
            "   and a.record_time between ?2.startTime and ?2.endTime"+
            " group by a.url")
    Page<UrlStatisticsModel> findUrlStatisticsLimitByExceptionCount(Pageable pageable,ExportConditionsModel exportConditionsModel);

    //查询最多告警数前n条
    @SQL("select t.id as urlId,t.url as urlName,s.alarmCount as alarmCount" +
            " from ge_monitor_url t ," +
            "(" +
            "     select count(b.severity) as alarmCount,b.sub_resource_id as urlId from ge_monitor_alarm b " +
            "     where b.monitor_id=?2.applicationId" +
            "     and b.sub_resource_type = 'APPLICATION_SCENARIO_URL' " +
            "     and b.monitor_type='APPLICATION'" +
            "     and b.severity='CRITICAL'" +
            "     and b.create_time  between ?2.startTime and ?2.endTime" +
            "     group by b.sub_resource_id" +
            ") s" +
            " where s.urlId = t.id")
    Page<UrlStatisticsModel> findUrlStatisticsLimitByAlarmCount(Pageable pageable, ExportConditionsModel exportConditionsModel);
    //统计URL告警数
    @SQL("select t.id as urlId,t.url as urlName,s.alarmCount as alarmCount" +
            " from ge_monitor_url t ," +
            "(" +
            "     select count(b.severity) as alarmCount,b.sub_resource_id as urlId from ge_monitor_alarm b " +
            "     where b.monitor_id=?2.applicationId" +
            "     and b.sub_resource_type = 'APPLICATION_SCENARIO_URL' " +
            "     and b.monitor_type='APPLICATION'" +
            "     and b.severity='CRITICAL'" +
            "     and b.create_time  between ?2.startTime and ?2.endTime" +
            "     group by b.sub_resource_id" +
            ") s" +
            " where s.urlId = t.id and t.url in(?1)")
    List<UrlStatisticsModel> findUrlStatisticsAlarmByUrls(Set<String> urlSet,ExportConditionsModel exportConditionsModel);
}
