<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>

<header class="header">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>移动审批</span>
</header>
<div class="list-panel">
</div>

<script type="text/html" id="tmpl-list">
<# _.each(list, function (item) { #>
    <div class="approval-type">
        <span class="most-type">{{item.mostType.name}}</span>
        <ul class="approval-list">
        <# _.each(item.approvalTypeList, function (approvalItem) { #>
            <li>
                <img src="${contextPath}/assets/src/css/img/formicons/{{approvalItem.icon}}.png" alt="{{approvalItem.name}}">
                <span>{{approvalItem.name}}</span>
                <a href="javascript:" data-id="{{approvalItem.id}}" class="link-btn collection {{approvalItem.isCollection == 1 ? 'active' : ''}}">
                    {{approvalItem.isCollection == 1 ? '取消' : '收藏'}}
                </a>
                <# if (approvalItem.scene == 3) { #>
                    <a href="${contextPath}/moblicApprove/toForm-e3.do?flowId={{approvalItem.id}}&from=list&wyyId={{wyyId}}" class="link-btn">创建</a>
                <# } else { #>
                    <a href="${contextPath}/moblicApprove/toForm.do?typeId={{approvalItem.id}}&from=list&wyyId={{wyyId}}" class="link-btn">创建</a>
                <# } #>
            </li>
        <# }) #>
        </ul>
    </div>
<# }) #>
</script>

<script>
    seajs.use('page/micro-app/mobile/approval-list', function (module) {
        module.run();
    });
</script>

</body>
</html>
