<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>
<style>
    .create-button {
        float: right;
        font-size: 0.75rem;
    }
    .item-action {
        float: right;
        font-size: 0.75rem;
        text-align: center;
        position: relative;
        margin: 0.25rem;
        color: #777;
    }
    .item-action:before {
        content: ' ';
        display: block;
        width: 2rem;
        height: 2rem;
        font-size: 1.25rem;
        line-height: 2rem;
        font-family: 'iconfont';
    }
    .item-action.edit:before {
        content: '\e604';
    }
    .item-action.setting:before {
        content: '\e644';
    }
    .item-action.delete:before {
        content: '\e654';
    }
</style>

<header class="header">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>移动审批</span>
    <a href="${contextPath}/moblicApprove/customform/edit.do" class="create-button">创建新表单</a>
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
                
                <a href="javascript:" class="item-action delete" data-id="{{approvalItem.id}}">删除</a>
                <a href="${contextPath}/moblicApprove/customform/setting.do?id={{approvalItem.id}}" class="item-action setting">设置</a>
                <a href="${contextPath}/moblicApprove/customform/edit.do?id={{approvalItem.id}}" class="item-action edit">编辑</a>
            </li>
        <# }) #>
        </ul>
    </div>
<# }) #>
</script>

<script>
    seajs.use('page/micro-app/custom-form/list', function (module) {
        module.run();
    });
</script>

</body>
</html>
