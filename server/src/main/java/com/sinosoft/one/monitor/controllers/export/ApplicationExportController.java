package com.sinosoft.one.monitor.controllers.export;

import com.alibaba.fastjson.JSON;
import com.sinosoft.one.monitor.application.model.Application;
import com.sinosoft.one.monitor.export.domain.ApplicationUrlExportService;
import com.sinosoft.one.monitor.export.model.ExportConditionsModel;
import com.sinosoft.one.monitor.export.model.ExportModel;
import com.sinosoft.one.monitor.export.model.ExportViewModel;
import com.sinosoft.one.mvc.web.annotation.Param;
import com.sinosoft.one.mvc.web.annotation.Path;
import com.sinosoft.one.mvc.web.annotation.rest.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-1-22
 * Time: 下午2:28
 * To change this template use File | Settings | File Templates.
 */
@Path("application")
public class ApplicationExportController {
    @Autowired
    ApplicationUrlExportService applicationUrlExportService;
    Logger logger = LoggerFactory.getLogger(ApplicationExportController.class);
    @Get("view")
    public String viewExport(){
        return "applicationExport";
    }

    @Get("{applicationId}/{startTime}/{endTime}")
    public String getExportData(@Param("applicationId") String applicationId,
                                @Param("startTime")String startTime,
                                @Param("endTime")String endTime){
        ExportConditionsModel conditionsModel = new ExportConditionsModel();
        conditionsModel.setApplicationId(applicationId);
        conditionsModel.setCount(20);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            conditionsModel.setStartTime(sdf.parse(startTime));
            conditionsModel.setEndTime(sdf.parse(endTime));
        } catch (ParseException e) {
            logger.error("parse timeString error in ApplicationExportController.getExportData",e);
        }
        ExportModel exportModel = applicationUrlExportService.createExportModel(conditionsModel);
        ExportViewModel exportViewModel = new ExportViewModel();
        exportViewModel.initAlarmData(exportModel.getAlarmStatisticsList());
        exportViewModel.initExceptionData(exportModel.getExceptionStatisticsList());
        exportViewModel.initResponseTimeData(exportModel.getResponseTimeStatisticsList());
        exportViewModel.initVisitData(exportModel.getVisitStatisticsList());
        exportViewModel.initGridData(exportModel.getComprehensiveList());
        return "@"+ JSON.toJSONString(exportViewModel);
    }

    @Get("list")
    public String listApplication(){
        List<Application> applications = applicationUrlExportService.listValidApplication();
        return "@"+ JSON.toJSONString(applications);
    }
}
