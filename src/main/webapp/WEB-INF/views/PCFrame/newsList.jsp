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
				<a id="dh" class="dh dh1 active" href="/mobile/moblicApprove/newsList.do">全部资讯</a>
			</div>
			<div class="content-list">
			<ul class="dpt-list" id="dpt-list">
				<a href="/mobile/moblicApprove/newsList.do"><div class="txl-menu lv0">分类列表</div></a>
				<div class="sub">
				<a href="/mobile/moblicApprove/newsList.do?t=1">
				<div id="t1" class="txl-menu lv1 active">公司公告</div></a>
				<a href="/mobile/moblicApprove/newsList.do?t=2">
				<div id="t2" class="txl-menu lv1">图片新闻</div></a>
				<a href="/mobile/moblicApprove/newsList.do?t=3">
				<div id="t3" class="txl-menu lv1">文字新闻</div></a>
				</div>
			</ul>
			<div class="main-r">
				<div class="news-all"></div>
				<div class="page-bar"></div>
			</div>
			</div>
		</div>
		<div class="content-bot">CopyRight@江苏移动</div>
	</div>
</div>
<script>
    seajs.use('page/micro-app/mobile/news-list', function (module) {
        module.run();
    });
</script>
</body>
</html>