<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>

<header class="header">
    <a class="switch" href="javascript:">
        <i class="iconfont icon-duihuan"></i>
    </a>
    <span class="page-title"></span>
</header>
<div class="todo-panel clearfix">
    <a class="myApproval" href="${contextPath}/moblicApprove/toWorkflowList.do?type=dwsp&wyyId=wyy0001"><span>我的待办</span><sup class="info-count approval-count">0</sup></a>
    <a class="iHaveApproval" href="${contextPath}/moblicApprove/toWorkflowList.do?type=wysp&wyyId=wyy0001"><span>我的已办</span></a>
    <a class="iHaveLaunch" href="${contextPath}/moblicApprove/toWorkflowList.do?type=wdfq&wyyId=wyy0001"><span>我的发起</span></a>
    <a class="myReview" href="${contextPath}/moblicApprove/toWorkflowList.do?type=dwyl&wyyId=wyy0001"><span>我的待阅</span><sup class="info-count read-count">0</sup></a>
</div>
<div class="grid-panel clearfix">
</div>

<script type="text/html" id="tmpl-approvals">
    <# _.each(list, function (item) { #>
        <# if (item.scene == 3) { #>
            <a href="${contextPath}/moblicApprove/toForm-e3.do?flowId={{item.id}}&from=index">
        <# } else { #>
            <a href="${contextPath}/moblicApprove/toForm.do?typeId={{item.id}}&from=index">
        <# } #>
            <img src="${contextPath}/assets/src/css/img/formicons/{{item.icon}}.png" alt="{{item.name}}">
            <span>{{item.name}}</span>
        </a>
    <# }) #>
    <a class="add" href="${contextPath}/moblicApprove/toApprovalList.do?wyyId={{wyyId}}">
        <img src="${contextPath}/assets/src/css/img/mobile/h_add.png" alt="更多">
        <span>更多</span>
    </a>
</script>

<script>
    seajs.use('page/micro-app/mobile/index', function (module) {
        module.run();
    });
</script>
</body>
</html>
