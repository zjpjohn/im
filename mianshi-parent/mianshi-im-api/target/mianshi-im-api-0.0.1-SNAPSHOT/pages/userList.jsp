<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls"
	prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<div class="container"
	style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
	<div class="backgrid-container">
		<table class="backgrid" style="background-color: #fff;">
			<thead>
				<tr>
					<th width="30"></th>
					<th width="100">用户Id</th>
					<th width="200">昵称</v>
					<th width="100">注册时间</th>
					<th width="100">操作</th>
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td><input type="checkbox" name="ckUserId"
							value="${o.userId}" /></td>
						<td>${o.userId}</td>
						<td>${o.nickname}</td>
						<td>${utils:format(o.createTime*1000,'yyyy-MM-dd')}</td>
						<td><a
							href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/deleteUser?userId=${o.userId}&pageIndex=${page.pageIndex}"'>删除</a></td>
						<td></td>
					</tr>
				</c:forEach>
				<tr>
					<td colspan="6"><jsp:include page="pageBar.jsp"></jsp:include></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<jsp:include page="bottom.jsp"></jsp:include>