<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>调度器管理</title>
		<%@ include file="include.jsp"%>
		<script src="<%=path %>/js/jquery.js"></script>
		<link rel="stylesheet" href="<%=path %>/css/ui-dialog.css">
		<script src="<%=path %>/js/dialog-min.js"></script>
		<script>  
		$(function(){
			$("table > tbody > tr").click(function(){
				var uuid = $(this).find("input:hidden").val();
				$.ajax({
					url:'queryScheduler.action?uuid='+uuid,
					success:function(data){
						var html='<table>'+
								'<tr><td>远程地址</td><td>'+ data.config.host+":"+data.config.port +'</td></tr>'+
								'<tr><td>调度器名称</td><td>'+ data.name +'</td></tr>'+
								'<tr><td>JMX MBean名称</td><td>'+ data.remoteInstanceId +'</td></tr>'+
								'<tr><td>是否关闭</td><td>'+ data.started +'</td></tr>'+
								'<tr><td>待机模式</td><td>'+ data.standByMode +'</td></tr>'+
								'<tr><td>存储器类名</td><td>'+ data.jobStoreClassName +'</td></tr>'+
								'<tr><td>线程池类名</td><td>'+ data.threadPoolClassName +'</td></tr>'+
								'<tr><td>线程池大小</td><td>'+ data.threadPoolSize +'</td></tr>'+
						'</table>';
						dialog({
							fixed: false,
							title:'调度器详细信息:',
						    content: html,
						    ok: function () {},
						}).show();		
					}
				});
			});
		})
		</script>
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
								<th width="490">调度器名称</th><!-- 
								<th>JMX MBean名称</th> -->
								<th width="55">是否关闭</th>
								<th width="55">待机模式</th>
								<!--     
								<th>存储器类名</th>
								<th>线程池类名</th>
								<th>线程池大小</th>
								 -->      
								<th style="align:center">操作</th>
							</tr>
						</thead>
						<tfoot>
							<tr>   
								<td colspan="5" class="pagination">
									<span class="active curved">1</span><a href="#" class="curved">2</a><a href="#" class="curved">3</a><a href="#" class="curved">4</a> ... <a href="#" class="curved">10 million</a>
								</td>
							</tr>
						</tfoot>
						<tbody>
						<c:forEach items="${schedulerList }" var="scheduler" varStatus="status">
							<tr <c:if test="${status.index%2==0 }">class="alt"</c:if>>
								<td>${scheduler.client.config.host }:${scheduler.client.config.port}</td>
								<!-- 
								<td>${scheduler.name}</td>
								 -->
								 <input type="hidden" value="${scheduler.client.config.configId}"/>
								<td>${scheduler.objectName }</td>
								<td>
									<c:if test="${scheduler.shutdown}">
										关闭
									</c:if>
									<c:if test="${scheduler.started}">
										started
									</c:if>
								</td>
								<td>${scheduler.standByMode}</td>
								<!-- 
								<td>${scheduler.jobStoreClassName}</td>
								<td>${scheduler.threadPoolClassName}</td>
								<td>${scheduler.threadPoolSize}</td>
								 -->
								<td><a href="#" class="edit">暂停</a><a href="#" class="delete">恢复</a></td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		
		<div id="foot">
				<a href="#">Contact Me</a>
		</div>
	</body>
</html>