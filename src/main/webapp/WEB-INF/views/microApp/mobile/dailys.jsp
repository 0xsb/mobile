<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>

<header class="header clearfix" style="background-color:#a8f">
    <a class="switch" href="javascript:">
        <i class="iconfont icon-duihuan"></i>
    </a>
    <span class="page-title"></span>
    <a href="${contextPath}/moblicApprove/customform/list.do?wyyId=wyy0002" style="float:right">管理</a>
</header>
<div class="todo-panel clearfix" style="background-color:#a8f">
    <a class="myApproval" href="${contextPath}/moblicApprove/toWorkflowList.do?type=wdfq&wyyId=wyy0002" style="width:33.3%"><span>我发起的</span></a>
    <a class="myApproval" href="${contextPath}/moblicApprove/toWorkflowList.do?type=dwsp&wyyId=wyy0002" style="width:33.3%"><span>我收到的</span><sup class="info-count approval-count">0</sup></a>
    <a class="myReview" href="${contextPath}/moblicApprove/toSearchReport.do" style="width:33.3%"><span>报表</span><sup class="info-count read-count">0</sup></a>
</div>
<div class="grid-panel clearfix">
</div>

<script type="text/html" id="tmpl-approvals">
    <# _.each(list, function (item) { #>
        <# if (item.scene == 3) { #>
            <a href="${contextPath}/moblicApprove/toForm-e3.do?flowId={{item.id}}&from=index&wyyId={{wyyId}}">
        <# } else { #>
            <a href="${contextPath}/moblicApprove/toForm.do?typeId={{item.id}}&from=index&wyyId={{wyyId}}">
        <# } #>
            <img src="${contextPath}/assets/src/css/img/formicons/{{item.icon}}.png" alt="{{item.name}}">
            <span>{{item.name}}</span>
        </a>
    <# }) #>
</script>

<script>
    seajs.use('page/micro-app/mobile/dailys', function (module) {
        module.run();
    });
</script>
</body>
</html>
