<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/taglibs/samples-uitls" prefix="utils"%>
<jsp:include page="top.jsp"></jsp:include>
<!-- <form action="/console/pushToAll" method="post">
	<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
		<div class="row" style="margin-top: 10px;">
			<div class="col-md-12" style="text-align: center;">
				<input type="radio" value="10000" name="fromUserId" checked="checked" style="display: none;" />
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<textarea class="form-control" rows="3" id="body" name="body" required="required"></textarea>
			</div>
		</div>
		<div class="row" style="margin-top: 10px;">
			<div class="col-md-12" style="text-align: center;">
				<button type="submit" class="btn btn-success">&nbsp;发送&nbsp;</button>
			</div>
		</div>
	</div>
</form> -->
<!-- <div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
<ul id="myTab" class="nav nav-tabs">
   <li class="active"><a href="#updateCommunitycodes" data-toggle="tab">修改用户入住的小区集</a>
   </li>
   <li><a href="#updateRoomUserSize" data-toggle="tab">修改群聊房间最大成员数</a></li>
</ul>
<div id="myTabContent" class="tab-content">
   <div class="tab-pane fade in active" id="updateCommunitycodes">
      <a href='javascript:if(confirm("是否确认修改？"))window.location.href="/console/updateUserCommunitycodeStrs"'>修改</a>
   </div>
   <div class="tab-pane fade" id="updateRoomUserSize">
      <a href='javascript:if(confirm("是否确认修改？"))window.location.href="/console/updateRoomUserSize"'>修改</a>
   </div>
</div>
</div> -->
<div class="container" style="margin-top: 10px; margin-bottom: 10px; padding-left: 0px; padding-right: 0px;">
<ul id="myTab" class="nav nav-tabs">
   <li class="active"><a href="#updateCommunitycodes" data-toggle="tab">修改用户入住的小区集</a>
   </li>
   <li><a href="#updateRoomUserSize" data-toggle="tab">修改群聊房间最大成员数</a></li>
</ul>
<div id="myTabContent" class="tab-content">
   <div class="tab-pane fade in active" id="updateCommunitycodes">
      <form id="uccForm" action="/console/updateUserCommunitycodeStrs" method="post">
			<div class="row">
				<div class="col-md-12" style="margin-top: 10px; margin-bottom: 10px; margin-left: 30px;">
					<select id="uccType" name="uccType">
					  <option value ="all">所有用户</option>
					  <option value ="one">指定用户</option>
					</select><br><br>
					<div id="uccTypeDiv">
						用户手机号：<input type="text" id="tel" name="tel">
					</div>
				</div>
			</div>
			<div class="row" style="margin-top: 10px;">
				<div class="col-md-12" style="text-align: center;">
					<button type="button" onclick="updateCommunitycodes()" class="btn btn-success">&nbsp;修改&nbsp;</button>
				</div>
			</div>
	</form>
   </div>
   <div class="tab-pane fade" id="updateRoomUserSize">
      <form id="rusForm" action="/console/updateRoomUserSize" method="post">
			<div class="row">
				<div class="col-md-12" style="margin-top: 10px; margin-bottom: 10px; margin-left: 30px;">
					<select id="rusType" name="rusType">
					  <option value ="all">所有房间</option>
					  <option value ="one">指定房间</option>
					</select><br><br>
					<div id="rusTypeDiv">
						房间名称：<input type="text" id="roomName" name="roomName"><br><br>
						创建者手机号：<input type="text" id="utel" name="utel"><br><br>
					</div>
					最大成员数设置：<input type="text" id="maxUserSize" name="maxUserSize"><br>
				</div>
			</div>
			<div class="row" style="margin-top: 10px;">
				<div class="col-md-12" style="text-align: center;">
					<button type="button" onclick="updateRoomUserSize()" class="btn btn-success">&nbsp;修改&nbsp;</button>
				</div>
			</div>
	</form>
   </div>
</div>
</div>
<script type="text/javascript">
$(function(){  
	var uccType=$("#uccType").val();
	if(uccType=="all"){
		$("#uccTypeDiv").hide();
	}else{
		$("#uccTypeDiv").show();
	}
	var rusType=$("#rusType").val();
	if(rusType=="all"){
		$("#rusTypeDiv").hide();
	}else{
		$("#rusTypeDiv").show();
	}
}); 
$("#uccType").change(function(){
	var uccType=$("#uccType").val();
	if(uccType=="all"){
		$("#uccTypeDiv").hide();
	}else{
		$("#uccTypeDiv").show();
	}
});
$("#rusType").change(function(){
	var rusType=$("#rusType").val();
	if(rusType=="all"){
		$("#rusTypeDiv").hide();
	}else{
		$("#rusTypeDiv").show();
	}
});
function updateCommunitycodes(){
	if(confirm("是否确认修改？")){
		$('#uccForm').submit();
	}   
}
function updateRoomUserSize(){
	var maxUserSize=$('#maxUserSize').val();
	maxUserSize = maxUserSize.replace(/\s+/g,"");
	if(maxUserSize==null||maxUserSize==""){
		alert("请填写最大成员数！");
	}else{
		if(confirm("是否确认修改？")){
			$('#rusForm').submit();
		}  
	}
}
</script>
<jsp:include page="bottom.jsp"></jsp:include>