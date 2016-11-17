<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-mobile.jsp"%>
<style>
.form-box {
    margin: 0.5rem 0;
}
.form-box > h5 {
    padding: 0.5rem;
    background: #cfcfcf;
    display: inline-block;
    margin-left: 0.5rem;
    border-radius: 0.25rem;
}
</style>
</head>
<body>

<header class="header">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>审批详情</span>
</header>
<div class="detail-box">
    <div class="detail-info" style="display:none;"></div>
    <div class="detail-forms"></div>
    <div class="detail-history" style="display:none;"></div>
    <div class="detail-footer">
        <div class="button" data-action="recovery">撤销</div>
    </div>
</div>
<div id="approve-popup" class="mask" style="display:none;">
    <div class="approve-modal">
        <textarea placeholder="审批意见"></textarea>
        <div class="common-app-select" data-do="common-app">
            <label>常用审批意见</label>
        </div>
        <div class="user-select">
            <label for="username">转发人</label>
            <input type="hidden" name="userid">
            <input class="trigger" type="text" name="username" placeholder="请选择" readonly>
        </div>
        <div class="buttons">
            <a href="javascript:" class="btn-apply">确定</a>
            <a href="javascript:" class="btn-cancle">取消</a>
        </div>
    </div>
</div>
<div id="select-user-popup" class="mask" style="display:none;">
    <div class="approve-modal">
        <div>
            <label>请选择办理人</label>
            <textarea placeholder="办理人" readonly></textarea>
            <input type="hidden" role="selected-user">
        </div>
        <div class="buttons">
            <a href="javascript:" class="btn-apply">确定</a>
            <a href="javascript:" class="btn-cancle">取消</a>
        </div>
    </div>
<div>

<script id="tmpl-info" type="text/html">
    <div class="avatar" style="background:{{colorCode}}">{{subName}}</div>
    <div class="info">
        <div class="name">{{userName}}</div>
        <div class="step">{{text}}</div>
    </div>
</script>
<script id="tmpl-history" type="text/html">
    <# _.each(list, function (item, index) { #>
        <div class="history-item {{item.statusClass}}">
            <div class="avatar" style="background:{{item.colorCode}}">{{item.subName}}</div>
            <div class="info">
                <div class="name">{{item.userName}}</div>
                <div class="step">
                    {{item.statusText}}
                    <span class="approve-des">{{item.opinion}}</span>
                </div>
                <div class="time">{{item.time}}</div>
            </div>
        </div>
    <# }) #>
</script>
<script id="tmpl-buttons" type="text/html">
    <# _.each(buttons, function (item) { #>
        <div class="button" data-action="{{item.action}}">{{item.text}}</div>
    <# }) #>
</script>

<script>
    window.UUID = '${num}';
    seajs.use('page/edition-third/my-flow-detail', function (module) {
        module.run();
    });
</script>

</body>
</html>
