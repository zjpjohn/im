<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<form action="/console/pushToAll" method="post">
	<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<div class="row" style="margin-top: 10px;">
			<div class="col-md-12" style="text-align: center;">
				<input type="radio" value="10000" name="fromUserId" checked="checked" /> 10000&nbsp;&nbsp;<input type="radio" value="10005"
					name="fromUserId" /> 10005
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<textarea class="form-control" rows="3" id="body" name="body" required="required"></textarea>
			</div>
		</div>
		<div class="row" style="margin-top: 10px;">
			<div class="col-md-12" style="text-align: center;">
				<button type="submit" class="btn btn-success">&nbsp;批量发送&nbsp;</button>
			</div>
		</div>
	</div>
</form>
<jsp:include page="bottom.jsp"></jsp:include>