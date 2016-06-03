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
				<td colspan="4"><jsp:include page="pageBar.jsp"></jsp:include></td>
			</tr>
		</tbody>
	</table>
</div>

<jsp:include page="bottom.jsp"></jsp:include>