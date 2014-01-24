package com.sinosoft.one.monitor.application.repository;
// Generated 2013-3-4 15:45:31 by One Data Tools 1.0.0

import com.sinosoft.one.data.jade.annotation.SQL;
import com.sinosoft.one.monitor.application.domain.ApplicationAvailableInf;
import com.sinosoft.one.monitor.application.domain.TimeQuantumAvailableStatistics;
import com.sinosoft.one.monitor.application.model.EumUrlAva;
import com.sinosoft.one.monitor.utils.AvailableCalculate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;


public interface EumUrlAvaRepository extends PagingAndSortingRepository<EumUrlAva, String> {

     public Page<EumUrlAva> findByEumUrlId(String eumUrlId,Pageable pageable);

     public List<EumUrlAva> findByEumUrlIdAndState(String eumUrlId,String state);

     public Page<EumUrlAva> findByEumUrlIdAndState(String eumUrlId,String state,Pageable pageable);

     @SQL("select count(1) as count ,interval from GE_MONITOR_EUM_URL_AVA where eum_url_id=?1 and state =?2 and to_char(t.record_time,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') GROUP BY INTERVAL")
     public List<AvailableCalculate.AvailableCountsGroupByInterval> countsGroupByInterval(String eumUrlId,String state);

     @SQL("select count(1) from GE_MONITOR_EUM_URL_AVA where eum_url_id=?1")
     public int countByEmuId(String applicationId);

     @SQL("SELECT count(1) from GE_MONITOR_EUM_URL_AVA where eum_url_id=?1 and STATE = ?2")
     public int countByEmuIdAndStatus(String applicationId,String status);

     @SQL("select to_char(a.record_time, 'yyyy-MM-dd HH24') as timeQuantum, state as status, count(1) as count" +
             "  from ge_monitor_eum_url_ava a" +
             "  where a.eum_url_id=?1 and a.record_time between ?2 and ?3 " +
             " group by to_char(a.record_time, 'yyyy-MM-dd HH24'), state")
     public List<TimeQuantumAvailableStatistics> statisticsByEumUrlIdAndRecordTime(String eumUrlId,Date start,Date end);

	@SQL("delete ge_monitor_eum_url_ava a where a.record_time < ?1")
	 void deleteByLessThanDate(Date date);

    @SQL("select sum(a.state) as availableCount,count(1) as count from GE_MONITOR_EUM_URL_AVA a " +
            " where a.eum_url_id=?1" +
            "   and to_char(a.record_time,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')")
    ApplicationAvailableInf staticsAvailableCount(String applicationId);

    @SQL("select t.state from GE_MONITOR_EUM_URL_AVA t " +
            " where t.eum_url_id=?1" +
            "   and to_char(t.record_time,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') " +
            " order by t.record_time desc")
    List<String> findAvailableStates(String applicationId);

    @SQL("select t.* from GE_MONITOR_EUM_URL_AVA t " +
            " where t.eum_url_id=?1" +
            "   and to_char(t.record_time,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') " +
            " order by t.record_time asc")
    List<EumUrlAva> findTodayEumUrlAvaList(String id);
}

