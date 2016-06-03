<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls"
	prefix="utils"%>
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
				<td colspan="4"><jsp:include page="pageBar.jsp"></jsp:include></td>
			</tr>
		</tbody>
	</table>
</div>
<jsp:include page="bottom.jsp"></jsp:include>