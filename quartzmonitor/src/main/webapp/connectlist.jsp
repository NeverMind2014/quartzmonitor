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
		<title>连接管理</title>
		<link rel="stylesheet" href="css/960.css" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="css/template.css" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="css/colour.css" type="text/css" media="screen" charset="utf-8" />
		<style>
			.grid_5 p input +input{
				margin-left:10px;  
			}
		</style>
	</head>
	<body>  
		
					<h1 id="head">连接管理</h1>
		
		<ul id="navigation">
			<li><a href="index.html">首页</a></li>
			<li><span class="active">连接管理</span></li>
			<li><a href="schedulerlist.html">调度器管理</a></li>
			<li><a href="joblist.html">任务管理</a></li>
		</ul>
		
			<div id="content" class="container_16 clearfix">
				<div class="grid_4">
					<p> 
						<label>连接名称<small>支持模糊查询</small></label>
						<input type="text" />
					</p>
				</div>
				<div class="grid_5">
					<p>
						<label>主机名</label>
						<input type="text" />
					</p>
				</div>
				
				<div class="grid_2">
					<p>
						<label>&nbsp;</label> 
						<input type="submit" value="&nbsp;&nbsp;查&nbsp;&nbsp;询 &nbsp;&nbsp;" />
					</p>
				</div>
				   
				<div class="grid_5">
					<p>
					<label>&nbsp;</label>   
						<input type="submit" value="&nbsp;&nbsp;添&nbsp;&nbsp;加 &nbsp;&nbsp;" />
						
						<input type="submit" value="&nbsp;&nbsp;删&nbsp;&nbsp;除 &nbsp;&nbsp;" />
						
						<input type="submit" value="&nbsp;&nbsp;修&nbsp;&nbsp;改 &nbsp;&nbsp;" />
					</p>
				</div>
				<div class="grid_16">
					<table>
						<thead>
							<tr>
								<th>连接名称</th>
								<th>主机名</th>
								<th>端口</th>
								<th>用户名</th>
								<th>密码</th>
								<th>操作</th>
							</tr>
						</thead>
						<tfoot>
							<tr> 
								<td colspan="6" class="pagination">
									<span class="active curved">1</span><a href="#" class="curved">2</a><a href="#" class="curved">3</a><a href="#" class="curved">4</a> ... <a href="#" class="curved">10 million</a>
								</td>
							</tr>
						</tfoot>
						<tbody>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td><a href="#" class="edit">Edit</a><a href="#" class="delete">Delete</a></td>
							</tr>
							<tr class="alt">
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td><a href="#" class="edit">Edit</a><a href="#" class="delete">Delete</a></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		
		<div id="foot">
					<a href="#">Contact Me</a>
				
		</div>
	</body>
</html>