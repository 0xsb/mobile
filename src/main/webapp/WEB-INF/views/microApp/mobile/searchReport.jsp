<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>
<header class="header">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>查询报表</span>
</header>
<form class="content-form">

</form>
<footer class="footer" style="">
    <a href="javascript:" class="submit">提交</a>
</footer>

<script>
    seajs.use('page/micro-app/mobile/searchReport', function (module) {
        module.run();
    });
</script>

</body>
</html>
