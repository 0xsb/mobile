<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>

<header class="header">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span class="list-title">移动审批</span>
</header>
<div>
    <form class="list-search">
        <input class="search" type="text" name="term" placeholder="">
        <input class="search-btn" type="submit" value="搜索">
    </form>
    <ul class="workflow-list">
    </ul>
    <div class="more-bar" style="display: none;">
        <a href="javascript:">更多</a>
    </div>
    <!--<div class="pagination">
        <a class="page-button prev"><i class="iconfont icon-left"></i></a>
        <a class="page-no">1/1</a>
        <a class="page-button next"><i class="iconfont icon-right"></i></a>
    </div>-->
</div>

<script type="text/html" id="tmpl-approvals">
    <# _.each(list, function (item) { #>
        <li>
            <a href="{{item.link}}&type={{type}}&wyyId={{wyyId}}">
                <div class="avatar" style="background:{{item.colorCode}}">{{item.subName}}</div>
                <div class="info">
                    <span class="text">{{item.approvalName}}</span>
                    <span class="time">{{item.arriveDate}}</span>
                    <span class="step">{{item.status == '1' ? '审核中' : '完成'}}</span>
                </div>
            </a>
        </li>
    <# }) #>
</script>
<script type="text/html" id="tmpl-nodata">
    <li class="nodata-tip">
        <# if (type == 'dwsp') #>
            <p>暂无您待办的事务</p>
            <p>请稍后再来查看</p>
        <# else if (type == 'wdfq') #>
            <p>暂无您发起的事务</p>
            <p>现在就去<a href="${contextPath}/moblicApprove/toApprovalList.do?wyyId={{wyyId}}">创建</a></p>
        <# else if (type == 'dwyl') #>
            <p>暂无您待阅的事务</p>
            <p>请稍后再来查看</p>
        <# else if (type == 'wysp') #>
            <p>暂无您已办的事务</p>
            <p>查看<a href="${contextPath}/moblicApprove/toWorkflowList.do?type=dwsp&wyyId={{wyyId}}">我的待办</a></p>
    </li>
</script>

<script>
    seajs.use('page/micro-app/mobile/workflow-list', function (module) {
        module.run();
    });
</script>

</body>
</html>
