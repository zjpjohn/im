<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>

<div class="backgrid-container">
	<table class="backgrid" style="background-color: #fff;">
		<thead>
			<tr>
				<th width="150">发送者</th>
				<th width="150">接收者</th>
				<th width="100">时间</th>
				<th width="">内容</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach varStatus="i" var="o" items="${page.pageData}">
				<tr>
					<c:if test="${o.direction == 0}">
						<td>${o.sender_nickname}</td>
						<td>${o.receiver_nickname}</td>
					</c:if>
					<c:if test="${o.direction == 1}">
						<td>${o.receiver_nickname}</td>
						<td>${o.sender_nickname}</td>
					</c:if>
					<td>${utils:format(o.ts,'yyyy-MM-dd')}</td>
					<td>${o.body}</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="4">
					${page.total}条记录，每页${page.pageSize}条，共${page.pageCount}页，当前第${page.pageIndex+1}页，
					<c:if test="${page.pageIndex<=0}">首页&nbsp;上一页</c:if>
					<c:if test="${page.pageIndex>0}">
						<a href="/console/chat_logs_all?pageIndex=0">首页</a>&nbsp;
						<a href="/console/chat_logs_all?pageIndex=${page.pageIndex-1}">上一页</a>
					</c:if>
					<c:if test="${page.pageIndex<page.pageCount-1}">
						<a href="/console/chat_logs_all?pageIndex=${page.pageIndex+1}">下一页</a>&nbsp;
						<a href="/console/chat_logs_all?pageIndex=${page.pageCount-1}">尾页</a>
					</c:if>
					<c:if test="${page.pageIndex>=page.pageCount-1}">下一页&nbsp;尾页</c:if>
				</td>
			</tr>
		</tbody>
	</table>
</div>
<jsp:include page="bottom.jsp"></jsp:include>