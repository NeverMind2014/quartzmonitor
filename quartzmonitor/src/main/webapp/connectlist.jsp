<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>连接管理</title>
		<%@ include file="include.jsp"%>
		<style>
			.grid_5 p input +input{
				margin-left:10px;  
			}
		</style>
		<script src="<%=path %>/js/jquery.js" type="text/javascript"></script>
		<script>
			$(function(){
				
				$("#add").click(function(){
					window.location.href="preadd.action";
				});
				
				$("#query").click(function(){
					
				});
				
			});
		</script>
	</head>
	<body>  
		
		<%@ include file="toolbar.jsp"%>
		<form action="" method="post">
			<div id="content" class="container_16 clearfix">
				<div class="grid_16">
					<c:if test="${errors['error']!=null}">
						<p class="error">${errors["error"][0]}</p>
					</c:if>
					<c:if test="${not empty actionMessages[0]}">
						<p class="success">${actionMessages[0]}</p>	
					</c:if>
				</div>
				<div class="grid_4">
					<p> 
						<label>连接名称<small>支持模糊查询</small></label>
						<input type="text" name="quartzConfig.name" />
					</p>
				</div>
				<div class="grid_5">
					<p>
						<label>主机名</label>
						<input name="quartzConfig.host" type="text" />
					</p>
				</div>
				
				<div class="grid_2">
					<p>
						<label>&nbsp;</label> 
						<input id="query" type="submit" value="&nbsp;&nbsp;查&nbsp;&nbsp;询 &nbsp;&nbsp;" />
					</p>
				</div>
				   
				<div class="grid_5">
					<p>
					<label>&nbsp;</label>   
						<input id="add" type="submit" value="&nbsp;&nbsp;添&nbsp;&nbsp;加 &nbsp;&nbsp;" />
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
						<tbody>
						<c:forEach items="${quartzMap }" var="item">
							<tr>
								<td align="middle">${item.value.name }</td>
								<td align="middle">${item.value.host }</td>
								<td align="middle">${item.value.port }</td>
								<td align="middle">${item.value.userName }</td>
								<td align="middle">${item.value.password }</td>
								<td align="middle">
									<a class="edit" href="show.action?uuid=${item.value.configId}">修改</a>&nbsp;&nbsp;&nbsp;&nbsp;<a class="delete" href="delete.action?uuid=${item.value.configId}">删除</a>
								</td>
							</tr>
						</c:forEach>
							<tfoot>
								<tr> 
									<td colspan="6" class="pagination">
										<span class="active curved">1</span><a href="#" class="curved">2</a><a href="#" class="curved">3</a><a href="#" class="curved">4</a> ... <a href="#" class="curved">10 million</a>
									</td>
								</tr>
							</tfoot>
						</tbody>
					</table>
				</div>
			</div>
		</form>
		<div id="foot">
					<a href="#">Contact Me</a>
		</div>
	</body>
</html>