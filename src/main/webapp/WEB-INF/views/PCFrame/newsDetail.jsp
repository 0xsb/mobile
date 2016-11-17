<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcdefault.css" />
</head>
<body>
<div class="main">
	<div class="header">
		<div class="vol-avg4 -tr">
		<!--<a href="javascript:window.history.go(-1);">返回</a>
		<a href="javascript:">设置</a>-->
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
				<a id="dh" class="dh dh1 active" href="/mobile/moblicApprove/newsList.do">公司<span class="type"></span></a>
			</div>
			<div class="content-list">
			<div class="dpt-list" id="dpt-list">
				<div class="news-t2">相关文章</div>
				<ul id="news-list"></ul>
			</div>
			<div class="txl-list">
				<div id="news-t" class="news-t -bold">昨日还能买上城，今朝只能买下城</div>
				<div class="news-info">发布:<span id="news-author">迷猫</span>&nbsp;&nbsp;
				时间:<span id="time">2016-09-28 15:30</span>&nbsp;&nbsp;</div>
				<div id="content" class="news-content">
					<div id="real-content"></div>
					<div id="fj-list"><br></div>
				</div>
			</div>
			</div>
		</div>
		<div class="content-bot">CopyRight@江苏移动</div>
	</div>
</div>
<script>
    seajs.use('page/micro-app/mobile/newsdetail', function (module) {
        module.run();
    });
</script>
</body>
</html>
