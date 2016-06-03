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
					<th width="250">房间名字</th>
					<th width="250">房间说明</th>
					<th width="200">创建者</th>
					<th width="100">创建时间</th>
					<!-- <th width="100">操作</th> -->
					<th width=""></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach varStatus="i" var="o" items="${page.pageData}">
					<tr>
						<td><input type="checkbox" name="ckUserId" value="${o.id}" /></td>
						<td class="string-cell">${o.name}</td>
						<td class="string-cell">${o.desc}</td>
						<td class="string-cell">[${o.userId}]&nbsp;&nbsp;${o.nickname}</td>
						<td>${utils:format(o.createTime*1000,'yyyy-MM-dd')}</td>
						<%-- <td class="string-cell"><a href='javascript:if(confirm("是否确认删除？"))window.location.href="/console/deleteRoom?roomId=${o._id}&pageIndex=${page.pageIndex}"'>删除</a></td> --%>
						<td>&nbsp;</td>
					</tr>
				</c:forEach>
				<tr>
					<td colspan="7">
						${page.total}条记录，每页${page.pageSize}条，共${page.pageCount}页，当前第${page.pageIndex+1}页，
						<c:if test="${page.pageIndex<=0}">首页&nbsp;上一页</c:if>
						<c:if test="${page.pageIndex>0}">
							<a href="/console/roomList?pageIndex=0">首页</a>&nbsp;
							<a href="/console/roomList?pageIndex=${page.pageIndex-1}">上一页</a>
						</c:if>
						<c:if test="${page.pageIndex<page.pageCount-1}">
							<a href="/console/roomList?pageIndex=${page.pageIndex+1}">下一页</a>&nbsp;
							<a href="/console/roomList?pageIndex=${page.pageCount-1}">尾页</a>
						</c:if>
						<c:if test="${page.pageIndex>=page.pageCount-1}">下一页&nbsp;尾页</c:if>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<jsp:include page="bottom.jsp"></jsp:include>