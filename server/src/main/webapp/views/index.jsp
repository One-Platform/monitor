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
		{id:'3',text:'健康状态',name:"appellation",index:'1',align:'',sort:'disable'}
	];
var columStyle2 = 
	[  
		{id:'1',text:'状态',name:"appellation",index:'1',align:'',width:'52',sort:'disable'},
		{id:'2',text:'消息',name:"appellation",index:'1',align:''},
		{id:'3',text:'名称',name:"appellation",index:'1',align:''},
		{id:'4',text:'类型',name:"appellation",index:'1',align:''},
		{id:'5',text:'时间',name:"appellation",index:'1',align:''}
	];
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
	gridList.push({"renderId":"systemList","url":rootPath+"/os/systemList?time=" + new Date().getTime(), "columStyle":columStyle1});
	gridList.push({"renderId":"oracleList","url":rootPath+"/db/oracle/thresholdList?time=" + new Date().getTime(), "columStyle":columStyle1});
	
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
	        	<div class="threshold_file">
	                <h3 class="title3">数据库：</h3>
	                <div id="oracleList"></div>
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
