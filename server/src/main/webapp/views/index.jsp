<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/WEB-INF/layouts/base.jsp" %>
<title>monitor监控系统</title>

<script type="text/javascript">
var columStyle1 =
	[  
		{id:'1',text:'名称',name:"appellation",index:'1',align:''},
		{id:'2',text:'可用性',name:"appellation",index:'1',align:'',sort:'disable'},
		{id:'3',text:'健康状态',name:"appellation",index:'1',align:'',sort:'disable'},
        {id:'4',text:'统计信息',name:"appellation",index:'1',align:'',sort:'disable'}
	];
var columStyle2 = 
	[  
		{id:'1',text:'状态',name:"appellation",index:'1',align:'',width:'52',sort:'disable'},
		{id:'2',text:'消息',name:"appellation",index:'1',align:''},
		{id:'3',text:'名称',name:"appellation",index:'1',align:''},
		{id:'4',text:'类型',name:"appellation",index:'1',align:''},
		{id:'5',text:'时间',name:"appellation",index:'1',align:''}
	];

// 	URL 	访问次数 	平均相应时间 	告警次数
//var columStyle3 =
//    [
//        {id:'1',text:'URL',name:"appellation",index:'1',align:''},
//        {id:'2',text:'访问次数',name:"appellation",index:'1',align:'',sort:'disable'},
//        {id:'3',text:'平均相应时间',name:"appellation",index:'1',align:'',sort:'disable'},
//        {id:'4',text:'告警次数',name:"appellation",index:'1',align:'',sort:'disable'}
//    ]
function refreshGrid(_data){

    $("#statistics_grid").html("");
    var title = '<tr>'+
            '<td align="left" width="55"><div class="gird_head_column cols col_2 th_change">URL<div></td>'+
            '<td align="left" width="13"><div class="gird_head_column cols col_2 th_change">访问次数<div></td>'+
            '<td align="left" width="18"><div class="gird_head_column cols col_2 th_change">平均响应时间<div></td>'+
            '<td align="left" width="13"><div class="gird_head_column cols col_2 th_change">告警次数<div></td>'+
            '</tr>';
    if(_data.length<= 0 ){
        var row_1 =  '<tr>'+
                '<td align="left" width="66%">暂无数据</td>'+
                '<td align="left" width="10%">暂无数据</td>'+
                '<td align="left" width="14%">暂无数据</td>'+
                '<td align="left" width="10%">暂无数据</td>'+
                '</tr>';
        $("#statistics_grid").append(title);
//                $("#statistics_grid").append(row_1);
        return ;
    }
    $("#statistics_grid").append(title);
    for(var i in _data){
        var rowHtml =  '<tr>'+
                '<td align="left" width="66%">'+_data[i].url+'</td>'+
                '<td align="center" width="10%">'+_data[i].visit+'</td>'+
                '<td align="center" width="14%">'+_data[i].responseTime+'</td>'+
                '<td align="center" width="10%">'+_data[i].alarm+'</td>'+
                '</tr>';
        $("#statistics_grid").append(rowHtml);
    }
}

function showStatisticDetail(_applicationId){
    var startTime = '2000-01-01 00:00:00';
    var endTime = '2100-01-01 00:00:00';
    $.ajaxSetup({cache:false});
    $.ajax({
        type: "GET",
        url: "${ctx}/export/application/"+_applicationId+"/"+startTime+"/"+endTime,
        dataType: "json",
        success: function (resData) {
            refreshGrid(resData.gridData);
        },
        error: function (data) {
            msgAlert("", "数据加载异常...");
        }
    });
}
$(function(){
	$("body").layout({
		top:{topHeight:100},
		bottom:{bottomHeight:30}
	});
    if($.browser.msie && ($.browser.version == "7.0")){
        var center = $("#layout_center");
        $("#main").width(center.width() - 31).height(center.height() - 30);
    }


    //thresholdList emergencyList systemList oracleList
	var gridList = new Array();
	
	gridList.push({"renderId":"thresholdList","url":rootPath+"/applicationList?time=" + new Date().getTime(), "columStyle":columStyle1});
	gridList.push({"renderId":"emergencyList","url":rootPath+"/alarmList?time=" + new Date().getTime(), "columStyle":columStyle2});
//	gridList.push({"renderId":"systemList","url":rootPath+"/os/systemList?time=" + new Date().getTime(), "columStyle":columStyle1});
//	gridList.push({"renderId":"oracleList","url":rootPath+"/db/oracle/thresholdList?time=" + new Date().getTime(), "columStyle":columStyle1});
//    gridList.push({"renderId":"statisticsList","url":rootPath+"/export/application/statisticsList/{startTime}/{endTime}", "columStyle":columStyle3});
	
	$(gridList).each(function(i, d){
		$("#"+d.renderId).Grid({
			url : d.url,  
			dataType: "json",
			colDisplay: false,  
			clickSelect: true,
			draggable:false,
			colums: d.columStyle,
			rowNum:9999,
			pager : false,
			number:false,  
			multiselect: false  
		});
	});
	$("#myDesk").height($("#layout_center").height());
});

</script>
</head>

<body>
<%@include file="/WEB-INF/layouts/menu.jsp" %>
<div id="layout_center">
	<div class="main" id="main">
		<ul class="crumbs">
            <li><b>首页</b></li>
        </ul>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	      <tr>
	        <td width="48%" rowspan="3" style="vertical-align:top">
	            <div class="threshold_file">
	                <h3 class="title3">应用系统：</h3>
	                <div id="thresholdList"></div>
	            </div>
	            <br/>
	        	<div class="threshold_file">
	                <h3 class="title3">告警信息：</h3>
	                <div id="emergencyList"></div>
	            </div>
	        </td>
	        <td width="4%">&nbsp;</td>
	        <td rowspan="3" style="vertical-align:top">
	        	<%--<div class="threshold_file">--%>
	                <%--<h3 class="title3">操作系统：</h3>--%>
	                <%--<div id="systemList"></div>--%>
	            <%--</div>--%>
	            <%--<br />--%>
                    <%--去除数据库信息显示--%>
	        	<%--<div class="threshold_file">--%>
	                <%--<h3 class="title3">数据库：</h3>--%>
	                <%--<div id="oracleList"></div>--%>
	            <%--</div>--%>

                <div class="threshold_file">
                    <h3 class="title3">应用系统统计信息列表：</h3>
                    <table id='statistics_grid' width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td align="left" width="66%">URL</td>
                            <td align="left" width="10%">访问次数</td>
                            <td align="left" width="14%">平均响应时间</td>
                            <td align="left" width="10%">告警次数</td>
                        </tr>
                    </table>
                </div>
	        </td>
	      </tr>
	      <tr>
	        <td>&nbsp;</td>
	      </tr>
	      <tr>
	        <td>&nbsp;</td>
	      </tr>
	    </table>
	</div>
</div>
<div id="layout_bottom">
	<p class="footer">Copyright &copy; 2013 Sinosoft Co.,Lt</p>
</div>
</body>
</html>
