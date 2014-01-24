package com.sinosoft.one.monitor.application.repository;
// Generated 2013-3-4 15:45:31 by One Data Tools 1.0.0

import com.sinosoft.one.data.jade.annotation.SQL;
import com.sinosoft.one.monitor.application.model.EumUrl;
import com.sinosoft.one.monitor.application.model.EumUrlAvaSta;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;


public interface EumUrlAvaStaRepository extends PagingAndSortingRepository<EumUrlAvaSta, String> {

    /**
     * 根据日期与仿真URL查询当天统计数据
     * @param Date
     * @param eumUrlId
     * @return
     */
    @SQL("select a.* from Ge_Monitor_Eum_Url_Ava_sta a where a.eum_url_id=?2 and a.record_time=?1")
    List<EumUrlAvaSta> findByRecordTimeAndEumUrlId(Date Date,String eumUrlId);

    @SQL("select t.* from Ge_Monitor_Eum_Url_Ava_sta t " +
            " where t.eum_url_id=?1" +
            "   and to_char(t.record_time,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') " +
            " order by t.record_time desc")
    EumUrlAvaSta findByTodayAndApplicationId(String applicationId);
}

