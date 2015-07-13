<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<title>作业管理</title>
	<%@ include file="include.jsp"%>
<style>
.grid_5 p input+input {
	margin-left: 10px;
}
</style>
<script src="<%=path %>/js/jquery.js"></script>
<link rel="stylesheet" href="<%=path %>/css/ui-dialog.css">
<script src="<%=path %>/js/dialog-min.js"></script>
<script src="<%=path %>/js/Date.js"></script>
<script>
	$(function(){
		
		//行单击事件 
		$("table > tbody >tr >td:not(:eq(2))").hover(function(){
			$(this).css("cursor","pointer");
		},function(){
			$(this).css("cursor","");
		}).click(function(){
			if($(this).find('a').length>0){
				return;
			}
			var quartzConfigId=$(this).parent("tr").find("input[name='job.quartzConfigId']").val();
			var jobName =$(this).parent("tr").find("input[name='job.jobName']").val();
			var group = $(this).parent("tr").find("input[name='job.group']").val();
			$.ajax({
				url:'getOneJob.action',
				method:'post',  		
				data:'job.quartzConfigId='+quartzConfigId+"&job.jobName="+jobName+"&job.group="+group,
				success:function(data){
					var html='<table>'+
							'<tr><td>名称</td><td>'+ data.jobName+'</td></tr>'+
							'<tr><td>所属组</td><td>'+ data.group +'</td></tr>'+
							'<tr><td>下一次触发时间</td><td>'+ new Date(data.nextFireTime).pattern('yyyy-MM-dd hh:mm:ss') +'</td></tr>'+
							'<tr><td>job 类路径</td><td>'+ data.jobClass +'</td></tr>'+
							'<tr><td>Durable</td><td>'+ data.durability +'</td></tr>'+
							'<tr><td>所属Scheduler</td><td>'+ data.scheduler.name+'@'+data.scheduler.client.config.host+':'+data.scheduler.client.config.port +'</td></tr>'+
							'<tr><td>状态</td><td>'+ data.state +'</td></tr>'+
							'<tr><td>描述</td><td>'+ data.description +'</td></tr>'+
					'</table>';
					dialog({
						fixed: false,
						title:'作业详细信息:',
					    content: html,
					    ok: function () {}
					}).show();		
				}
			});
		});
		
		//添加页面跳转
		$("#add").click(function(){
			window.location.href="show.action";
		});
	});
	
	function showTrigger(jobName,group,schedulername,quartzConfigId){
		$.ajax({
			url:'<%=path%>/trigger/list.action?job.jobName='+jobName+'&job.group='+group+'&job.schedulerName='+schedulername+'&job.quartzConfigId='+quartzConfigId,
			method:'post',
			success:function(data){
				if(data.length==0){
					dialog({
						fixed: false,
						title:'Trigger详细信息:',
					    content: '没有详细信息',
					    ok: function () {}
					}).show();	
					return;
				}
				var html='<table><tr><td>Trigger名称</td><td>Trigger组名</td><td>上一次触发时间</td><td>下一次触发时间</td><td>开始时间</td><td>描述</td></tr>';
				for ( var i = 0; i < data.length; i++) {
					html+='<tr><td>'+ data[i].name+'</td>'+
				'<td>'+ data[i].group+'</td>'+
				'<td>'+ new Date(data[i].previousFireTime).pattern('yyyy-MM-dd hh:mm:ss')+'</td>'+
				'<td>'+ new Date(data[i].nextFireTime).pattern('yyyy-MM-dd hh:mm:ss')+'</td>'+
				'<td>'+  new Date(data[i].startTime).pattern('yyyy-MM-dd hh:mm:ss')+'</td>'+
				'<td>'+ data[i].description+'</td></tr>';
				}
				html+='</table>';
				dialog({
					fixed: false,
					title:'Trigger详细信息:',
				    content: html,
				    ok: function () {}
				}).show();	
			}
		});
	}
</script>
</head>
<body>
	<%@ include file="toolbar.jsp"%>
	<div id="content" class="container_16 clearfix">
		<div class="grid_4">
			<p>
				<label>名称<small>支持模糊查询</small></label> <input type="text" />
			</p>
		</div>
		<div class="grid_5">
			<p>
				<label>所属系统</label> <input type="text" />
			</p>
		</div>
		<div class="grid_5">
			<p>
				<label>作业类型</label> <select>
					<option></option>
					<option>Simple</option>
					<option>Cron</option>
				</select>
			</p>
		</div>
		<div class="grid_2">
			<p>
				<label>&nbsp;</label> <input type="submit"
					value="&nbsp;&nbsp;查&nbsp;&nbsp;询 &nbsp;&nbsp;" />
			</p>
		</div>
		<div class="grid_2">
			<p>
				<input id="add" type="submit" value="&nbsp;&nbsp;添&nbsp;&nbsp;加 &nbsp;&nbsp;" />
			</p>
		</div>
		<div class="grid_16">
			<table>
				<thead>
					<tr>
						<th>名称</th>
						<th>所属组</th>
						<!-- 
						<th>下一次触发时间</th>
						 -->
						<th>Triggers</th>
						<!-- 
						<th>job 类路径</th>
						 -->
						<th>Durable</th>
						<th>所属Scheduler</th>
						<th>状态</th>
						<!-- 
						<th>描述</th>
						 -->
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${jobList}" var="job">
						<tr>
							<input name="job.quartzConfigId" value="${job.quartzConfigId }" type="hidden">
							<input name="job.jobName" value="${job.jobName}" type="hidden">
							<input name="job.group" value="${job.group}" type="hidden">
							<td>${job.jobName }</td>
							<td>${job.group }</td>
							<!-- 
							<td><fmt:formatDate value='${job.nextFireTime}' type='date' pattern="yyyy-MM-dd HH:mm:ss"/></td>
							 -->
							<td align='middle'><a href="javascript:showTrigger('${job.jobName}','${job.group }','${job.schedulerName }','${job.quartzConfigId}')";>${job.numTriggers }个</a></td>
							<!-- 
							<td target="jobClass">${job.jobClass }</td>
							 -->  
							<td>${job.durability }</td>
							<td>${job.schedulerName }@${job.scheduler.client.config.host }:${job.scheduler.client.config.port }</td>
							<td align="middle">
							<a href="<%=path%>/job/start.action?job.quartzConfigId=${job.quartzConfigId}&job.schedulerName=${job.schedulerName }&job.jobName=${job.jobName}&job.group=${job.group }">执行</a>
							<c:if test="${job.state == 'NORMAL' }">
			    				 <a href="<%=path%>/job/pause.action?job.quartzConfigId=${job.quartzConfigId}&job.schedulerName=${job.schedulerName }&job.jobName=${job.jobName}&job.group=${job.group }">暂停</a>
							</c:if>
							<c:if test="${job.state == 'PAUSED' }">
							      <a href="<%=path%>/job/resume.action?job.quartzConfigId=${job.quartzConfigId}&job.schedulerName=${job.schedulerName }&job.jobName=${job.jobName}&job.group=${job.group }">恢复</a>
							</c:if>
							</td>
							<!-- 
							<td target="description">${job.description }</td>
							 -->
							<td><a href="<%=path%>/job/edit.action?job.quartzConfigId=${job.quartzConfigId}&job.schedulerName=${job.schedulerName }&job.jobName=${job.jobName}&job.group=${job.group }&jobuuid=${job.uuid}" class="edit">修改</a></td>
					</tr>
				</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="10" class="pagination"><span
							class="active curved">1</span><a href="#" class="curved">2</a><a
							href="#" class="curved">3</a><a href="#" class="curved">4</a> ...
							<a href="#" class="curved">10 million</a></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>

	<div id="foot">
		<a href="#">Contact Me</a>

	</div>
</body>
</html>