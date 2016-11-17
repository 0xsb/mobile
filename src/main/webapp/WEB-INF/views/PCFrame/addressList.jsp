<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcdefault.css" />
</head>
<body>
<div class="main">
	<div class="company">
	<span class="logo -tl"><img src="/mobile/assets/src/css/img/logo-16.png"/></span>
	 <span class="com-name2">如东县农业委员会MOA通讯录</span>
	</div>
	<div class="content-block">
		<div class="content-l">
		</div>
		<div class="content-r">
		</div>
		<div class="gray-box lm">
			<div class="blue-t">
				<a id="dh" class="dh dh1 active" href="javascript:;">通讯录</a>
			</div>
			<div class="content-list">
			<ul class="dpt-list" id="dpt-list"></ul>
			<div class="txl-list">
				<table class="table" width="98%" cellspacing="0" cellpadding="0">
					<thead><th>姓名</th><th>部门</th><th>职务</th><th>电话</th><th>工号</th></thead>
					<tbody class="contacts"></tbody>
				</table>
			</div>
			</div>
		</div>
		<div class="content-bot">CopyRight@江苏移动</div>
	</div>
</div>
<script>
    seajs.use('page/micro-app/mobile/addressList', function (module) {
        module.run();
    });
</script>
</body>
</html>