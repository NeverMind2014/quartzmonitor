<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>调度器管理</title>
			<%@ include file="include.jsp"%>
	</head>
	<body>  
			<%@ include file="toolbar.jsp"%>
		
			<div id="content" class="container_16 clearfix">
				<div class="grid_4">
					<p> 
						<label>调度器名称<small>支持模糊查询</small></label>
						<input type="text" />
					</p>
				</div>
				<div class="grid_5">
					<p>
						<label>远程地址</label>
						<input type="text" />
					</p>
				</div>
				<div class="grid_5">
					<p>
						<label>状态</label>
						<select> 
							<option>全部</option>
							<option>开启</option>
							<option>关闭</option>
						</select>
					</p>
				</div>
				<div class="grid_2">
					<p>
						<label>&nbsp;</label> 
						<input type="submit" value="&nbsp;&nbsp;查&nbsp;&nbsp;询 &nbsp;&nbsp;" />
					</p>
				</div>
				<div class="grid_16">
					<table>
						<thead>
							<tr>
								<th>远程地址</th>
								<th>调度器名称</th>
								<th>JMX MBean名称</th>
								<th>是否关闭</th>
								<th>待机模式</th>
								<th>存储器类名</th>
								<th>线程池类名</th>
								<th>线程池大小</th>
								<th>操作</th>
							</tr>
						</thead>
						<tfoot>
							<tr> 
								<td colspan="9" class="pagination">
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