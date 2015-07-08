<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>修改作业</title>  
		<link rel="stylesheet" href="css/960.css" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="css/template.css" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="css/colour.css" type="text/css" media="screen" charset="utf-8" />
		<style>
			small{
				color:red;
			}
		</style>
	</head>
	<body>
	<h1 id="head">调度管理</h1>
	 <ul id="navigation">
			<li><a href="index.html">首页</a></li>
			<li><span class="connectlist.html">连接管理</span></li>
			<li><a href="schedulerlist.html">调度器管理</a></li>
			<li><span class="active">任务管理</span></li>
		</ul>
			<div id="content" class="container_16 clearfix">
				<div class="grid_16">
					<h2>修改作业(job) </h2>
					<p class="error">错误提示消息.</p>
				</div>

				<div class="grid_5">
					<p>  
						<label for="title">job名称:<small>*</small></label>
						<input type="text" name="title" />
					</p>
				</div> 

				<div class="grid_5">
					<p>
						<label for="title">所属组: <small>*</small></label>
						<input type="text" name="title" />
					</p>
						
				</div>
				<div class="grid_6">
					<p>
						<label for="title">所属Schedule<small>*</small></label>
						<select>
							<option>Draft</option>
							<option>Published</option>
							<option>Private</option>
						</select>
					</p>
				</div>

				<div class="grid_16">
					<p>
						<label>job类型<small>*</small></label>
						<input type="text" name="title" />
					</p>
				</div>

				<div class="grid_16">
					<p>
						<label>描述 <small>*</small></label>
						<textarea class="big"></textarea>
					</p>
					<p class="submit"> 
						<input type="reset" value="重置" />
						<input type="submit" value="提交" width=40 ;/>
					</p> 
				</div>
			</div>
		
		<div id="foot">
					<a href="#">Contact Me</a>
		</div>
	</body>
</html>