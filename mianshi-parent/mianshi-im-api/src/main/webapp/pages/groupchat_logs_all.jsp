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
				<td width="180">房间Id</td>
				<td width="150">发送者</td>
				<td width="100">时间</td>
				<td>内容</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach varStatus="i" var="o" items="${page.pageData}">
				<tr>
					<td>${o.room_jid_id}</td>
					<td>${o.sender_jid}</td>
					<td>${utils:format(o.ts,'yyyy-MM-dd')}</td>
					<td>${o.body}</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="4">
					${page.total}条记录，每页${page.pageSize}条，共${page.pageCount}页，当前第${page.pageIndex+1}页，
					<c:if test="${page.pageIndex<=0}">首页&nbsp;上一页</c:if>
					<c:if test="${page.pageIndex>0}">
						<a href="/console/groupchat_logs_all?pageIndex=0">首页</a>&nbsp;
						<a href="/console/groupchat_logs_all?pageIndex=${page.pageIndex-1}">上一页</a>
					</c:if>
					<c:if test="${page.pageIndex<page.pageCount-1}">
						<a href="/console/groupchat_logs_all?pageIndex=${page.pageIndex+1}">下一页</a>&nbsp;
						<a href="/console/groupchat_logs_all?pageIndex=${page.pageCount-1}">尾页</a>
					</c:if>
					<c:if test="${page.pageIndex>=page.pageCount-1}">下一页&nbsp;尾页</c:if>
				</td>
			</tr>
		</tbody>
	</table>
</div>

<jsp:include page="bottom.jsp"></jsp:include>