<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge"> -->
<!-- <meta name="viewport" content="width=device-width, initial-scale=1"> -->
<!-- <meta name="description" content=""> -->
<!-- <meta name="author" content=""> -->
<title>酷聊管理后台</title>
<link href="http://cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
<link href="/pages/css/css.css" rel="stylesheet">
<link href="/pages/css/backgrid.css" rel="stylesheet">
<script src="http://cdn.bootcss.com/jquery/1.11.2/jquery.min.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.js"></script>
</head>
<body style="background-color: #F3F6F7;">
	<div class="mininav">
		<table style="height:100%;width:1170;align:center;" >
			<tr>
				<td valign="middle" align="left" width="50%"><a
					id="chat_logs_all" href="/console/chat_logs_all">单聊聊天记录管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="groupchat_logs_all" href="/console/groupchat_logs_all">群组聊天记录管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="set" href="/config/set">Config表配置</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="roomList" href="/console/roomList">房间管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="userList" href="/console/userList">用户管理</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- <a id="qf" href="/pages/qf.jsp">系统消息</a> -->
					<a id="xtxx" href="/console/xtxx">系统消息</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="manage" href="/console/manage">综合管理</a>
					</td>
				<td valign="middle" align="right" width="50%">欢迎你，<b>${sessionScope.LOGIN_USER}</b>&nbsp;&nbsp;[管理员]&nbsp;&nbsp;&nbsp;&nbsp;
					<c:if test="${not empty sessionScope.LOGIN_USER}">
						<a href="/console/logout">退出登录</a>
					</c:if>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			$(function() {
				var url = window.location.href;
				var id = url.substr(url.lastIndexOf('/') + 1);
				if (id.indexOf('.jsp') != -1)
					id = id.replace(/.jsp/g, "");
				console.log(id)
				$("#" + id).css({
					"color" : "#000",
					"font-weight" : "bold"
				});
			});
		</script>
	</div>