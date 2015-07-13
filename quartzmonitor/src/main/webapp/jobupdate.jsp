<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>修改作业</title>  
			<%@ include file="include.jsp"%>
		<style>
			small{
				color:red;
			}
		</style>
		<script src="<%=path %>/js/jquery.js"></script>
	</head>
	<body>
	<%@ include file="toolbar.jsp"%>
	<form action="update.action" method="post">
			<div id="content" class="container_16 clearfix">
				<div class="grid_16">
					<h2>修改作业(job) </h2>
					<c:if test="${not empty actionErrors[0]}">
						<p class="error">${actionErrors[0]}</p>
					</c:if>
					<c:if test="${not empty actionMessages[0]}">
						<p class="success">${actionMessages[0]}</p>	
					</c:if>
				</div>

				<div class="grid_5">
					<p>  
						<label for="title">job名称:<small>*</small></label>
						<input type="text" name="job.jobName" value="${job.jobName }" />
					</p>
				</div> 

				<div class="grid_5">
					<p>
						<label for="title">所属组: <small>*</small></label>
						<input type="text" name="job.group" value="${job.group }" />
					</p>
						
				</div>
				<div class="grid_6">
					<p>
						<label for="title">所属Schedule<small>*</small></label>
						<input name="job.schedulerName" value="${job.schedulerName}" readOnly="readOnly"/>
						<input type="hidden" name="job.quartzConfigId" value="${job.scheduler.client.config.configId}"/>
					</p>
				</div>

				<div class="grid_16">
					<p>
						<label>job类<small>*</small></label>
						<input type="text" name="job.jobClass" value="${job.jobClass}" />
					</p>
				</div>

				<div class="grid_16">
					<p>
						<label>描述 <small>*</small></label>
						<textarea class="big" name="job.description">${job.description }</textarea>
					</p>
					<p class="submit"> 
						<input type="reset" value="重置" />
						<input type="submit" value="提交" width=40/>
					</p> 
				</div>
			</div>
</form>		
		<div id="foot">
					<a href="#">Contact Me</a>
		</div>
	</body>
</html>