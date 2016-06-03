<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<link href="/pages/ztree/css/zTreeStyle.css" rel="stylesheet">
<script src="/pages/ztree/jquery.ztree.core-3.5.min.js"></script>
<script src="/pages/ztree/jquery.ztree.excheck-3.5.min.js"></script>
<script src="/pages/ztree/jquery.ztree.exedit-3.5.min.js"></script>
<script src="/pages/ztree/initztree.js"></script>
<form id="pushForm" action="/console/pushToArea" method="post">
	<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<div class="row" style="margin-top: 10px;">
			<div class="col-md-12" style="text-align: center;">
				<ul id="areaTree" class="ztree"></ul>
				<input type="hidden" id="communitycodes" name="communitycodes">
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<textarea class="form-control" rows="3" id="body" name="body" required="required"></textarea>
			</div>
		</div>
		<div class="row" style="margin-top: 10px;">
			<div class="col-md-12" style="text-align: center;">
				<button type="button" onclick="pushToArea()" class="btn btn-success">&nbsp;发送&nbsp;</button>
			</div>
		</div>
	</div>
</form>
<jsp:include page="bottom.jsp"></jsp:include>