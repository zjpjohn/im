<%@ page language="java" pageEncoding="UTF-8"%>

<jsp:include page="top.jsp"></jsp:include>
<form id="form3" role="form" class="form-horizontal container" method="post" action="/config/set"
	style="margin-top: 40px; padding-left: 0px; padding-right: 0px;">
	<div class="form-group">
		<label class="col-sm-2 control-label mp_fwn">XMPP主机IP或域名</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" value="${config.XMPPDomain}" name="XMPPDomain" />
			<p class="help-block">根据安装文档第4步配置获得（Tigase所在机器的IP或域名）</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label mp_fwn">酷聊接口URL</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" value="${config.apiUrl}" name="apiUrl" />
			<p class="help-block">根据安装文档第6步配置（http://酷聊所在机器的IP或域名:8092/）</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label mp_fwn">头像访问URL</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" value="${config.downloadAvatarUrl}" name="downloadAvatarUrl" />
			<p class="help-block">根据安装篇第8步配置（http://nginx所在机器的IP或域名:nginx监听的端口/）</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label mp_fwn">资源访问URL</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" value="${config.downloadUrl}" name="downloadUrl" />
			<p class="help-block">根据安装篇第8步配置（http://nginx所在机器的IP或域名:nginx监听的端口/）</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label mp_fwn">资源上传URL</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" value="${config.uploadUrl}" name="uploadUrl" />
			<p class="help-block">根据安装篇第7步配置（http://上传服务所在机器的IP或域名:上传服务绑定的端口/）</p>
		</div>
	</div>
	<div class="form-group">
		<div class="col-sm-12" style="text-align: center;">
			<button type="submit" class="btn btn-success">&nbsp;更新配置&nbsp;</button>
		</div>
	</div>
</form>
<jsp:include page="bottom.jsp"></jsp:include>