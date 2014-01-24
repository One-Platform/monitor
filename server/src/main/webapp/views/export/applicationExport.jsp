<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>monitor监控系统-报表统计</title>
    <%@ include file="/WEB-INF/layouts/base.jsp" %>
    <link href="${ctx }/global/css/sinosoft.tabs.css" rel="stylesheet" type="text/css" />
    <link type="text/css" href="${ctx }/global/css/jquery-ui-1.8.17.custom.css" rel="stylesheet" />
    <link type="text/css" href="${ctx }/global/css/jquery-ui-timepicker-addon.css" rel="stylesheet" />
    <script type="text/javascript" src="${ctx }/global/js/jquery-ui-1.8.17.custom.min.js"></script>
    <script type="text/javascript" src="${ctx }/global/js/jquery-ui-timepicker-addon.js"></script>
    <script type="text/javascript" src="${ctx }/global/js/jquery-ui-timepicker-zh-CN.js"></script>

    <script language="javascript" src="${ctx }/global/js/sinosoft.tabs.js"></script>
    <script language="javascript" src="${ctx }/global/js/highcharts.src.js"></script>
    <script language="javascript" src="${ctx }/global/js/oracleMonitor.js"></script>

    <script type="text/javascript">
        <%--jqery时间戳控件--%>
//        $(function () {
//            $(".ui_timepicker").datetimepicker({
//                //showOn: "button",
//                //buttonImage: "./css/images/icon_calendar.gif",
//                //buttonImageOnly: true,
//                showSecond: true,
//                timeFormat: 'hh:mm:ss',
//                stepHour: 1,
//                stepMinute: 1,
//                stepSecond: 1
//            })
//        })
        function refreshBar(_id,_title,_yAxisTitle,_valueSuffix,_data){
            if(_data.length<=0){
                return  $('#'+_id).html("暂无数据");
            }
            $('#'+_id).html("");
            new Highcharts.Chart({
                chart: {
                    renderTo:_id,
                    type: 'bar',
//                    height:450,
                    with:500
                },
                title: {
                    text: _title
                },
                xAxis: {
                    categories: ['URL'],
                    title: {
                        text: null
                    }
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: _yAxisTitle,
                        align: 'high'
                    },
                    labels: {
                        overflow: 'justify'
                    }
                },
                tooltip: {
                    valueSuffix: _valueSuffix
                },
                plotOptions: {
                    bar: {
                        dataLabels: {
                            enabled: true
                        }
                    }
                },
                legend: {
                    layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'top',
                    floating: false,
                    borderWidth: 1,
                    backgroundColor: '#FFFFFF',
                    shadow: true
                },
                credits: {
                    enabled: false
                },
                series: _data
            });
        }
        function calibrationSearchData(applicationId,startTime,endTime){
            var timeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;
            if(applicationId==-1){
                return false;
            }else{
                if(!timeRegex.test(startTime)){
                    msgAlert("","请选择统计开始时间！")
                    return false;
                }
                else if(!timeRegex.test(endTime)){
                    msgAlert("","请选择统计截止时间！")
                    return false;
                }
            }
            return true;
        }
        function search(){
            var applicationId = $("#applicationId").val();
            var startTime = $("#startTime").val();
            var endTime = $("#endTime").val();
            var res = calibrationSearchData(applicationId,startTime,endTime);
            if(res==false){
                $("#urlVisitBar").html("暂无数据");
                $("#urlResponseBar").html("暂无数据");
                $("#urlExceptionBar").html("暂无数据");
                $("#urlAlarmBar").html("暂无数据");
                return;
            }
            $.ajaxSetup({cache:false});
            $.ajax({
                type: "GET",
                url: "${ctx}/export/application/"+applicationId+"/"+startTime+"/"+endTime,
                dataType: "json",
                success: function (resData) {
                    freshExportPage(resData);
                },
                error: function (data) {
                    msgAlert("", "数据加载异常...");
                }
            });
        }
        function freshExportPage(_data){
            var visitBarData =  _data.visitData;
            var responseBarData =  _data.responseTimeData;
            var exceptionBarData =  _data.exceptionData;
            var alarmBarData =  _data.alarmData;
            refreshBar('urlVisitBar','Top20 URL访问次数','访问数（次）',' 次',visitBarData);
            refreshBar('urlResponseBar','Top20 URL最慢相应时间','相应时间（ms）',' ms',responseBarData);
            refreshBar('urlExceptionBar','Top20 URL异常次数','异常数（次）',' 次',exceptionBarData);
            refreshBar('urlAlarmBar','Top20 URL告警次数','告警数（次）',' 次',alarmBarData);
            //刷啊新grade
            refreshGrid(_data.gridData);
        }
        function freshApplicationList(){
            $.ajaxSetup({cache:false});
            $.ajax({
                type: "GET",
                url: "${ctx}/export/application/list",
                dataType: "json",
                success: function (resData) {
                    refreshApplicationSelect(resData);
                },
                error: function (data) {
                    msgAlert("", "数据加载异常...");
                }
            });
        }
        $(
            function () {
                $("#startTime").datetimepicker({
                    showSecond: true,
                    timeFormat: 'hh:mm:ss',
                    stepHour: 1,
                    stepMinute: 1,
                    stepSecond: 1
                })
                $("#endTime").datetimepicker({
                    showSecond: true,
                    timeFormat: 'hh:mm:ss',
                    stepHour: 1,
                    stepMinute: 1,
                    stepSecond: 1
                })
                search();
            }
        );
        function refreshApplicationSelect(_data){
            if(_data.length>0){
                $("#applicationId").html("");
                for(var i in _data){
                    var app_id = _data[i].id;
                    var app_name = _data[i].applicationName;
                    var option = '<option value="'+app_id+'">'+app_name+'</option>';
                    $("#applicationId").append(option);
                }
            }
        }
        //格式[{url:"",visit:5,responseTime:4,alarm:3}...]
        function refreshGrid(_data){

            $("#statistics_grid").html("");
            var title = '<tr>'+
                    '<td align="center" width="5">序号</td>'+
                    '<td align="left" width="65">URL</td>'+
                    '<td align="left" width="9">访问次数</td>'+
                    '<td align="left" width="9">平均相应时间</td>'+
                    '<td align="left" width="9">告警次数</td>'+
                    '</tr>';
            if(_data.length<= 0 ){
                var row_1 =  '<tr>'+
                        '<td align="center" width="5">暂无数据</td>'+
                        '<td align="left" width="65">暂无数据</td>'+
                        '<td align="left" width="9">暂无数据</td>'+
                        '<td align="left" width="9">暂无数据</td>'+
                        '<td align="left" width="9">暂无数据</td>'+
                        '</tr>';
                $("#statistics_grid").append(title);
//                $("#statistics_grid").append(row_1);
                return ;
            }
            $("#statistics_grid").append(title);
            for(var i in _data){
                var rowHtml =  '<tr>'+
                        '<td align="center" width="5">'+_data[i].index+'</td>'+
                        '<td align="left" width="65">'+_data[i].url+'</td>'+
                        '<td align="left" width="9">'+_data[i].visit+'</td>'+
                        '<td align="left" width="9">'+_data[i].responseTime+'</td>'+
                        '<td align="left" width="9">'+_data[i].alarm+'</td>'+
                        '</tr>';
                $("#statistics_grid").append(rowHtml);
            }
        }
    </script>
</head>

<body>
<%@include file="/WEB-INF/layouts/menu.jsp" %>
<div id="layout_center">
    <div class="main" id="main">
        <div class="threshold_file">
            <h2 class="title2">
            <strong class="right">
                筛选条件：
                <select id="applicationId" onfocus="freshApplicationList();" class="diySelect">
                    <option value="-1" selected="selected">应用系统</option>
                </select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <input id="startTime" type="text" value="" class="ui_timepicker" />&nbsp;至&nbsp;
                <input id="endTime" type="text" value=""/>
                <input onclick="search();" type="button" value="查询" class="ui_timepicker"/>
                <input type="button" value="导出PDF"/>
            </strong>
                报表统计
            </h2>
        </div>

        <br>
        <div class="threshold_file">
            <h3 class="title3">Top20 URL访问次数</h3>
            <div id="urlVisitBar" ></div>
        </div>
        <br>
        <div class="threshold_file">
            <h3 class="title3">Top20 URL最慢响应时间</h3>
            <div id="urlResponseBar"></div>
        </div>
        <br>
        <div class="threshold_file">
            <h3 class="title3">Top20 URL异常次数</h3>
            <div id="urlExceptionBar" ></div>
        </div>
        <br>
        <div class="threshold_file">
            <h3 class="title3">Top20 URL告警次数</h3>
            <div id="urlAlarmBar"></div>
        </div>
        <br>
        <div class="threshold_file">
            <h3 class="title3">统计列表</h3>
            <table id='statistics_grid' width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td align="center" width="5">序号</td>
                    <td align="left" width="65">URL</td>
                    <td align="left" width="9">访问次数</td>
                    <td align="left" width="9">平均相应时间</td>
                    <td align="left" width="9">告警次数</td>
                </tr>
            </table>
        </div>
    </div>
</div>
<div id="layout_bottom">
    <p class="footer">Copyright &copy; 2013 Sinosoft Co.,Lt</p>
</div>
</body>
</html>
