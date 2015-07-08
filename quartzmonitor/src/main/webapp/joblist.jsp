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
		<div class="grid_5">
			<p>
				<input type="submit" value="&nbsp;&nbsp;添&nbsp;&nbsp;加 &nbsp;&nbsp;" />

				<input type="submit" value="&nbsp;&nbsp;删&nbsp;&nbsp;除 &nbsp;&nbsp;" />

				<input type="submit" value="&nbsp;&nbsp;修&nbsp;&nbsp;改 &nbsp;&nbsp;" />
			</p>
		</div>
		<div class="grid_16">
			<table>
				<thead>
					<tr>
						<th>名称</th>
						<th>所属组</th>
						<th>下一次触发时间</th>
						<th>Triggers</th>
						<th>Durable</th>
						<th>所属Scheduler</th>
						<th>描述</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td target="jobName">${job.jobName }</td>
						<td target="group">${job.group }</td>
						<td><fmt:formatDate value='${job.nextFireTime}' type='date' pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td align='middle'><a href="<%=path%>/trigger/list?job.jobName=${job.jobName}&job.group=${job.group }&job.schedulerName=${job.schedulerName }&job.quartzConfigId=${job.quartzConfigId}" target="dialog" title="Trigger列表" mask="true" rel="triggerList" width="800">${job.numTriggers }</a></td>
						<td target="jobClass">${job.jobClass }</td>
						<td>${job.durability }</td>
						<td>${job.schedulerName }@${job.scheduler.config.host }:${job.scheduler.config.port }</td>
						<td align="middle">
						<a href="<%=path%>/job/start.action?job.quartzConfigId=${job.quartzConfigId}&job.schedulerName=${job.schedulerName }&job.jobName=${job.jobName}&job.group=${job.group }" target="ajaxTodo">执行</a>
						<c:if test="${job.state == 'NORMAL' }">
		    				 <a href="<%=path%>/job/pause.action?job.quartzConfigId=${job.quartzConfigId}&job.schedulerName=${job.schedulerName }&job.jobName=${job.jobName}&job.group=${job.group }" target="ajaxTodo">暂停</a>
						</c:if>
<%-- 						<c:if test="${job.state == 'COMPLETE' }"> --%>
<%-- 						</c:if> --%>
						<c:if test="${job.state == 'PAUSED' }">
						      <a href="<%=path%>/job/resume.action?job.quartzConfigId=${job.quartzConfigId}&job.schedulerName=${job.schedulerName }&job.jobName=${job.jobName}&job.group=${job.group }" target="ajaxTodo">恢复</a>
						</c:if>
						</td>
						<td target="description">${job.description }</td>
						<td><a href="#" class="edit">Edit</a><a href="#" class="delete">Delete</a></td>
				</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="8" class="pagination"><span
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