<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>
<style>
.panel-header {
    padding: 0.5rem;
    
}
</style>

<header class="header">
    <a class="back" href="${contextPath}/moblicApprove/customform/list.do"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>设置表单</span>
</header>
<form class="content-form">
    <div class="form-item userselect" role="approval-default">
        <span>默认审批人</span>
        <a href="javascript:" class="camara-trigger trigger">
            <i class="iconfont icon-tianjialeimu"></i></a>
        <input name="approvel-id" type="hidden">
        <div class="list"></div>
    </div>
    <div class="form-item userselect" role="approval-last">
        <span>归口办理人</span>
        <a href="javascript:" class="camara-trigger trigger">
            <i class="iconfont icon-tianjialeimu"></i></a>
        <input name="last-approvel-id" type="hidden">
        <div class="list"></div>
    </div>

    <div class="form-item textnote">
        <p>不设置可使用人员和角色时，默认所有人可用</p>
    </div>
    <div class="form-item approveselect" role="auth-users">
        <span>可使用的人员</span>
        <input name="auth-users" type="hidden">
        <div class="list">
            <div class="trigger">
                <i class="iconfont icon-tianjialeimu"></i>
            </div>
        </div>
    </div>
    <div class="form-item approveselect" role="auth-roles">
        <span>可使用的角色</span>
        <input name="auth-roles" type="hidden">
        <div class="list">
            <div class="trigger">
                <i class="iconfont icon-tianjialeimu"></i>
            </div>
        </div>
    </div>
    <div class="form-item approveselect" role="auth-readers">
        <span>可查看的人员</span>
        <input name="auth-readers" type="hidden">
        <div class="list">
            <div class="trigger">
                <i class="iconfont icon-tianjialeimu"></i>
            </div>
        </div>
    </div>
</form>
<footer class="footer" style="">
    <a href="javascript:" class="submit">提交</a>
</footer>


<script type="text/html" id="tmpl-role">
    <div class="approver-item" data-key="{{id}}">
        <div class="avatar" style="background:{{'#' + avatar.colorCode}}">{{avatar.subName}}</div>
        <span class="name">{{text}}</span>
    </div>
</script>
<script>
    seajs.use('page/micro-app/custom-form/setting', function (module) {
        module.run();
    });
</script>

</body>
</html>
