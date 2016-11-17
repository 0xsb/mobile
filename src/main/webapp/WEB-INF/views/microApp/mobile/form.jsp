<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>
    <header class="header" style="display:none;">
        <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
        <span class="form-title"></span>
    </header>
    <form class="content-form" style="display:none;">
    </form>
    <footer class="footer" style="display:none;">
        <a href="javascript:" class="submit">提交</a>
    </footer>
    <script>
        window.UUID = '${num}';
        seajs.use('page/micro-app/mobile/form', function (module) {
            module.run();
        });
    </script>
</body>
</html>
