<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcdefault.css" />
</head>
<body>
<div class="main">
	<div class="header">
		<div class="vol-avg4 -tr">
		<a href="javascript:window.history.go(-1);">返回</a>
		<a href="javascript:">设置</a>
		<a href="javascript:window.close();">关闭</a>
		</div>
	</div>
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
				<a id="dh" class="dh dh1 active" href="javascript:;">移动办公</a>
			</div>
			<div class="content-list">
			<ul class="dpt-list" id="dpt-list">
				<a href="javascript:;"><div class="txl-menu lv0">分类列表</div></a>
				<div class="sub">
				<a href="/mobile/moblicApprove/flowList.do?t=1">
				<div id="t1" class="txl-menu lv1 active">我的待办</div></a>
				<a href="/mobile/moblicApprove/flowList.do?t=2">
				<div id="t2" class="txl-menu lv1">我的已办</div></a>
				<a href="/mobile/moblicApprove/flowList.do?t=3">
				<div id="t3" class="txl-menu lv1">我的发起</div></a>
				<a href="/mobile/moblicApprove/flowList.do?t=4">
				<div id="t4" class="txl-menu lv1">我的待阅</div></a>
				</div>
			</ul>
			<div class="main-r">
				<div class="sp-list"></div>
				<div class="page-bar"></div>
			</div>
			</div>
		</div>
		<div class="content-bot">CopyRight@江苏移动</div>
	</div>
</div>
<script type="text/template" id="tmpl-approvalItem">
<a href="{{model.url}}"><span class="icon-item -tl"></span><span class="icon-date -tl">&nbsp;&nbsp;[{{model.arriveDate}}]</span><span class="icon-name -tl">{{model.userName}}</span>{{model.approvalName}}<span class="-tr">审批类&nbsp;&nbsp;审批{{model.status}}</span></a>
</script>
<script>
    seajs.use('page/micro-app/mobile/flow-list', function (module) {
        module.run();
    });
</script>
</body>
</html>