<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<form id="pagerForm" method="post" action="quartz/listScheduler">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${model.numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<div class="pageHeader">
	<form rel="pagerForm" onsubmit="return navTabSearch(this);" action="demo_page1.html" method="post">
	<div class="searchBar">
		<ul class="searchContent">
			<li>
				Quartz监控管理工具
			</li>
		</ul>
	</div>
	</form>
</div>

<div class="pageContent">
<!-- 	<div class="panelBar"> -->
<!-- 		<ul class="toolBar"> -->
<%-- 			<li><a class="add" href="addInstance.jsp" target="dialog" rel="addInstance"><span>添加</span></a></li> --%>
<%-- 			<li><a class="delete" href="<%=request.getContextPath()%>/quartz/delete.action?uuid={sid_user}" target="ajaxTodo" title="确定要删除吗？" warn="请选择一个用户"><span>删除</span></a></li> --%>
<%-- 			<li><a class="edit" href="<%=request.getContextPath()%>/quartz/show.action?uuid={sid_user}" target="dialog" warn="请选择一记录"><span>修改</span></a></li> --%>
<!-- 			<li class="line">line</li> -->
<%-- 			<li><a class="icon" href="demo/common/dwz-team.xls" target="dwzExport" targetType="navTab" title="实要导出这些记录吗?"><span>导出EXCEL</span></a></li> --%>
<%-- 			<li><a class="icon" href="javascript:$.printBox('w_list_print')"><span>打印</span></a></li> --%>
<!-- 		</ul> -->
<!-- 	</div> -->

	<div id="w_list_print">
	<table class="list" width="100%" targetType="navTab" asc="asc" desc="desc" layoutH="116">
		<thead>
			<tr>
				<th width="100" align="middle">远程地址</th>
				<th width="100" align="middle">调度器名称</th>
				<th width="100" align="middle">JMX MBean名称</th>
				<th width="100" align="middle">是否关闭</th>
				<th width="100" align="middle">待机模式</th>
				<th width="100" align="middle">存储器类名</th>
				<th width="100" align="middle">线程池类名</th>
				<th width="100" align="middle">线程池大小</th>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="schedulerList" id="sch">
			<tr target="sid_user" rel="<s:property value="#sch.quartzInstanceUUID"/>">
				<td align="middle"><s:property value="#sch.config.host"/>:<s:property value="#sch.config.port"/></td>
				<td align="middle"><s:property value="#sch.name"/></td>
				<td align="middle"><s:property value="#sch.objectName"/></td>
				<td align="middle"><s:property value="#sch.shutdown"/></td>
				<td align="middle"><s:property value="#sch.standByMode"/></td>
				<td align="middle"><s:property value="#sch.jobStoreClassName"/></td>
				<td align="middle"><s:property value="#sch.threadPoolClassName"/></td>
				<td align="middle"><s:property value="#sch.threadPoolSize"/></td>
			</tr>
			</s:iterator>
		</tbody>
	</table>
	</div>
</div>