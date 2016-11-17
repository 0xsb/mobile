<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcframe.css" />
</head>
<body>

<div class="frame-container">
    <div class="frame-header">
        <h3>移动审批</h3>
    </div>
    <div class="frame-body">
        <div class="sidebar">
            <div class="menu">
            	<a href="/mobile/moblicApprove/PCFrame.do"  data-frame="approval">新</a>
                <a href="javascript:" class="active" data-frame="approval">审批</a>
                <a href="javascript:" data-frame="dailys">日志日报</a>
                <a href="javascript:" data-frame="building">报表</a>
                <a href="javascript:" data-frame="building">设置</a>
            </div>
            <div class="status">
                <a href="${contextPath}/user/quit.do">退出</a>
            </div>
        </div>
        <div class="content">
        </div>
    </div>
</div>

<script id="tmpl-approval" type="text/html">
    <div class="iframe-box">
        <iframe id="approval-frame"
            sandbox="allow-forms allow-modals allow-orientation-lock allow-pointer-lock allow-same-origin allow-scripts allow-popups"
            src="toIndex.do"></iframe>
        <img class="ad-img" src="${contextPath}/assets/src/css/img/ad2.jpg" alt="">
    </div>
</script>
<script id="tmpl-dailys" type="text/html">
    <div class="iframe-box">
        <iframe id="approval-frame"
            sandbox="allow-forms allow-modals allow-orientation-lock allow-pointer-lock allow-same-origin allow-scripts allow-popups"
            src="toDailys.do"></iframe>
        <a href="/mobile/moblicApprove/PCFrame.do"><img class="ad-img" src="${contextPath}/assets/src/css/img/ad2.jpg" alt=""></a>
    </div>
</script>
<script id="tmpl-building" type="text/html">
    <div class="iframe-box">
        <h2 id="building-text">建设中</h2>
    </div>
</script>

<script>
    seajs.use('page/pcframe/frame', function (module) {
        module.run();
    });
</script>
</body>
</html>
