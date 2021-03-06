<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>添加连接</title>  
		<%@ include file="include.jsp"%>
		<style>
			small{
				color:red;
			}
		</style>
	</head>
	<body> 
	<%@ include file="toolbar.jsp"%>
		<form action="add.action" method="post">
			<div id="content" class="container_16 clearfix">
				<div class="grid_16">
					<h2>添加连接(connect) </h2>
					<c:if test="${errors['error']!=null}">
						<p class="error">${errors["error"][0]}</p>
					</c:if>
					<c:if test="${not empty actionMessages[0]}">
						<p class="success">${actionMessages[0]}</p>	
					</c:if>
				</div>
				<div class="container2 clearfix">
					<div class="grid_5">
						<p>  
							<label for="title">主机ip:<small>*</small></label>
							<input type="text" name="host" value="${host }"/>
						</p>
					</div> 
					<div class="grid_5">
						<p>
							<label for="title">主机端口: <small>*</small></label>
							<input type="text" name="port" value="${port }"/>
						</p>
					</div>
				</div>
				<div class="container2 clearfix">
					<div class="grid_5">
						<p>
							<label for="title">用户名<small>*</small></label>
							<input type="text" name="username" value="${username }"/>
						</p>
					</div>
 
					<div class="grid_5">
						<p>
							<label>密码<small>*</small></label>
							<input type="text" name="password" value="${password }"/>
						</p>
					</div>
				</div>
				<div class="grid_16">
					<p class="submit"> 
						<input type="reset" value="重置" />
						<input type="submit" value="提交" width=40 ;/>
					</p> 
				</div>
			</div>
		</form>
		<div id="foot">
					<a href="#">Contact Me</a>
		</div>
	</body>
</html>