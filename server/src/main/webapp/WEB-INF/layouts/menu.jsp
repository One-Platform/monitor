<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<script type="text/javascript">
    $(function () {
        $("body").layout({
            top:{topHeight:100},
            bottom:{bottomHeight:30}
        });
        $("#myDesk").height($("#layout_center").height());
        $("#nav").delegate('li', 'mouseover mouseout', navHover);
        $("#nav,#menu").delegate('li', 'click', navClick);
    });
    function navHover() {
        $(this).toggleClass("hover")
    }
    function navClick() {
        $(this).addClass("seleck").siblings().removeClass("seleck");
        if ($(this).hasClass('has_sub')) {
            var subMav = $(this).children("ul.add_sub_menu");
            var isAdd = false;
            if ($(this).parent().attr("id") == "menu") {
                isAdd = true;
            }
            ;
            subMav.slideDown('fast', function () {
                $(document).bind('click', {dom:subMav, add:isAdd}, hideNav);
                return false;
            });
        }
        ;
    }
    function hideNav(e) {
        var subMenu = e.data.dom;
        var isAdd = e.data.add;
        subMenu.slideUp('fast', function () {
            if (isAdd) {
                subMenu.parent().removeClass('seleck');
            }
            ;
        });
        $(document).unbind();
    }
    function save() {
        msgSuccess("系统消息", "操作成功，监视器已保存！");
    }
    function rowsTogle() {
        var rows = $("#threshold tr.hideRows");
        if (rows.eq(0).is(':hidden')) {
            rows.show();
        } else {
            rows.hide();
        }
        ;
        return false;
    }
</script>
<div id="layout_top">
    <div class="header">
    	<shiro:hasPermission name="admin">
        <p class="user">您好,系统管理员 <span>|</span> <a href="${ctx}/login">退出系统</a></p>
        </shiro:hasPermission>
        <shiro:lacksPermission name="admin">
        <p class="user">您好,用户${sessionScope.loginUserName} <span>|</span> <a href="${ctx}/login">退出系统</a></p>
        </shiro:lacksPermission>
        <div class="menu_box">
            <ul class="nav" id="nav">
                <li><a href="${ctx }/index">首页</a></li>
                <%--<li class="has_sub">--%>
                    <%--<a href="javascript:viod(0)">监视器</a><span class="show_sub_anv"></span>--%>
                    <%--<ul class="add_sub_menu" id="subNav">--%>
                        <%--&lt;%&ndash;<li class="action"><span class="sever">操作系统</span>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<ul class="list">&ndash;%&gt;--%>
                                <%--&lt;%&ndash;<li><a href="${ctx}/os/toSystemMonitor">Linux</a></li>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;</ul>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</li>&ndash;%&gt;--%>
                        <%--<li class="action"><span class="system">应用系统</span>--%>
                            <%--<ul class="list">--%>
                                <%--<li><a href="${ctx}/application/manager/appmanager/applist/1">应用系统</a></li>--%>
                            <%--</ul>--%>
                        <%--</li>--%>
                        <%--<li class="action" style="border:none"><span>数据库</span>--%>
                            <%--<ul class="list">--%>
                                <%--<li><a href="${ctx }/db/oracle/oracleMonitor">oracle</a></li>--%>
                            <%--</ul>--%>
                        <%--</li>--%>
                        <%--<li class="clear"></li>--%>
                    <%--</ul>--%>
                <%--</li>--%>
                <li><a href="${ctx}/application/manager/appmanager/applist/1">应用系统</a></li>
                <li><a href="${ctx}/alarm/manager/alarmmanager/list">告警</a></li>
                <shiro:hasPermission name="admin">
                <li><a href="${ctx}/account/user/list">用户管理</a></li>
                </shiro:hasPermission>
                <li><a href="${ctx}/export/application/view">报表统计</a></li>
            </ul>
        </div>
        <ul class="add_menu" id="menu">
            <shiro:hasPermission name="admin">
            <li><a href="${ctx}/addmonitor/list">新建监视器</a></li>
            </shiro:hasPermission>
            <li class="has_sub">
                <a ><span>阈值配置文件</span></a>
                <ul class="add_sub_menu">
                	<shiro:hasPermission name="admin">
                    <li><a class="addThreshold" href="${ctx}/threshold/create">新建阈值文件</a></li>
                    </shiro:hasPermission>
                    <li><a class="thresholdFile" href="${ctx}/threshold/list">查看阈值配置文件</a></li>
                </ul>
            </li>
            <li class="has_sub">
                <a><span>动作</span></a>
                <ul class="add_sub_menu">
                    <li class="title"><a href="${ctx}/action/email/list">显示动作</a></li>
                    <shiro:hasPermission name="admin">
                    <li class="action">创建新动作</li>
                    <li><a class="email" href="${ctx}/action/email/create">邮件</a></li>
                    </shiro:hasPermission>
                </ul>
            </li>
            <shiro:hasPermission name="admin">
            <li><a href="${ctx}/alarm/manager/configemergency/config">配置告警</a></li>
            </shiro:hasPermission>
        </ul>
    </div>
</div>