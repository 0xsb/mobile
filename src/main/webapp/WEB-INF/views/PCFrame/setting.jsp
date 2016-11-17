<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcdefault.css" />
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap/3.3.6/css/bootstrap.css" />
</head>
<body>
<div class="main">
	<!-- <div class="header">
		<div class="vol-avg4 -tr">
		<a href="javascript:window.history.go(-1);">返回</a>
		<a href="javascript:">设置</a>
		<a href="javascript:window.close();">关闭</a>
		</div>
	</div> -->
	<div class="company">
	<span class="logo -tl"><img src="/mobile/assets/src/css/img/logo-16.png"/></span>
	 <span class="com-name2">如东县农业委员会MOA</span>
	</div>
	<div class="content-block">
		<div class="content-l">
		</div>
		<div class="content-r">
		</div>
		<div class="gray-box lm">
			<div class="blue-t">
				<a id="dh" class="dh dh1 active" href="javascript:;">个人设置</a>
			</div>
			<div class="content-list">
			<ul class="dpt-list" id="dpt-list">
				<a href="javascript:;"><div class="txl-menu lv0">全部设置</div></a>
				<div class="sub">
				<a href="/mobile/moblicApprove/setting.do?t=1">
				<div id="t1" class="txl-menu lv1 active">密码修改</div></a>
				<a href="/mobile/moblicApprove/setting.do?t=2">
				<div id="t2" class="txl-menu lv1">常用邮箱</div></a>
				<a href="/mobile/moblicApprove/setting.do?t=3">
				<!-- <div id="t3" class="txl-menu lv1">公司信息</div></a> -->
				<a href="/mobile/moblicApprove/setting.do?t=4">
				<div id="t4" class="txl-menu lv1">链接管理</div></a>
				<a href="/mobile/moblicApprove/setting.do?t=5">
				<div id="t5" class="txl-menu lv1">广告设置</div></a>
				</div>
			</ul>
			<div class="main-r">
				<div id="setting-1" class="setting" style="display:none;">
					<div class="set-box set-boxs">
						<div class="f-l f-ls">原密码：</div>
						<div class="f-r">
							<input id="mymail" class="form-control f-ls-password" type="password" value="">
						</div>
						<div class="f-l f-ls">新密码：</div>
						<div class="f-r">
							<input id="mymail" class="form-control f-ls-passwordNew" type="password" value="">
						</div>
						<div class="f-l f-ls">重复新密码：</div>
						<div class="f-r">
							<input id="mymail" class="form-control f-ls-passwordNews" type="password" value="">
						</div>
						<div class="set-box">
							<span class="f-l"></span>
							<input class="btn btn-default submitmail resetPassword" type="button" value="保存设置"/>
						</div>
					</div>
				</div>
				<div id="setting-2" class="setting" style="display:none;">
					<div class="set-box">
						<div class="f-l">邮箱地址：</div>
						<div class="f-r">
							<input id="mymail" class="form-control" type="text" value="" placeholder="请输入邮箱地址">
						</div>
					</div>
					<div class="set-box">
						<div class="f-l">常用选择：</div>
						<div id="mail-list"></div>
						<!--  <span class="s-radio" for="mail-6">
						<input id="mail-6" type="radio" value="1"/>&nbsp;&nbsp;移动邮箱</span>-->
					</div>
					<div class="set-box">
						<span class="f-l"></span>
						<input class="btn btn-default submitmail" type="button" value="保存设置"/>
					</div>
				</div>
				<div id="setting-3" class="setting" style="display:none;">公司信息开发中...</div>
				<div id="setting-4" class="setting" style="display:none;">
					<ul class="tabsLeft"></ul>
                    <div class="tab_container tab_containers"> </div>
                    <!-- <div class="jtImg">
                    	<img src="/mobile/assets/src/css/img/lunbo/prev.png" />
                    	<img src="/mobile/assets/src/css/img/lunbo/next.png" />
                    </div> -->
                    <div class="tab_contentSet tab_containersNew"> </div>
                    <div class="set-box">
						<span class="f-l"></span>
						<input class="btn btn-default submitmail resetLinks" type="button" value="保存设置">
					</div>
				</div>
				<div id="setting-5" class="setting" style="display:none;">广告设置开发中...</div>
			</div>
			</div>
		</div>
		<div class="content-bot">CopyRight@江苏移动</div>
	</div>
</div>
<script type="text/template" id="tmpl-mailItem">
<label class="s-radio" for="mail-{{model.index}}"><input id="mail-{{model.index}}" name="mailradio" type="radio" value="{{model.url}}"/>&nbsp;&nbsp;{{model.name}}</label>{{model.space}}
</script>
<script type="text/template" id="tmpl-approvalItem">
<a href="{{model.url}}"><span class="icon-item -tl"></span><span class="icon-date -tl">&nbsp;&nbsp;[{{model.arriveDate}}]</span><span class="icon-name -tl">{{model.userName}}</span>{{model.approvalName}}<span class="-tr">审批类&nbsp;&nbsp;审批{{model.status}}</span></a>
</script>
<script>
    seajs.use('page/micro-app/mobile/setting', function (module) {
        module.run();
    });
</script>
</body>
</html>
