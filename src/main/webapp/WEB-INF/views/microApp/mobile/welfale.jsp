<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
<style>
    .todo-panel{
        background:url('${contextPath}/assets/src/css/img/welfale.jpg') no-repeat center;
        width: 100%;
        height: 600px;
    }
    .todo-panel>a{
        width: 27.5%;
        height: 20%;
        border: 1px solid #ccc;
        border-radius: 1em;
        margin-left: 15%;
        align: center;
    }
    .todo-panel>h1{
        margin-top: 8%;
        margin-bottom: 8%;
    }
    .myApproval{
        background-color: rgba(197,188,147,0.5);
    }
    .iHaveApproval{
        background-color: rgba(245,66,238,0.5);
    }
</style>
</head>
<body>

<header class="header" style="background-color:#4ca">
    <a class="switch" href="javascript:">
        <i class="iconfont icon-duihuan"></i>
    </a>
    <span class="page-title"></span>
</header>
<div class="todo-panel clearfix">
    <h1 align="center">福利通</h1>
    <a class="myApproval" href="${contextPath}/moblicApprove/toWorkflowList.do?type=dwsp&wyyId=wyy0004"><span>我的待办</span><sup class="info-count approval-count">0</sup></a>
    <a class="iHaveApproval" href="${contextPath}/moblicApprove/toWorkflowList.do?type=wysp&wyyId=wyy0004"><span>已办</span></a>
</div>
<div class="grid-panel clearfix">
</div>


<script>
    seajs.use('page/micro-app/mobile/index', function (module) {
        var wyyId = 'wyy0004';
        module.run(wyyId);
    });
</script>
</body>
</html>
