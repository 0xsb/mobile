<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>
<style>
.panel-header {
    padding: 0.5rem;
    
}
.turn-mode {
    float: right;
    color: #fff;
    vertical-align: middle;
    background: #37f;
    font-size: 0.75rem;
    padding: 0.15rem 0.5rem;
    border-radius: 0.75rem;
}

.widgets-list {
    background: #fff;
    border-top: 1px solid #ccc;
}
.widget-item {
    height: 3rem;
    line-height: 2rem;
    padding: 0.5rem;
    border-bottom: 1px solid #ccc;
}
.widget-item .widget-remove {
    color: red;
}
.widget-item .widget-name {
    line-height: 2rem;
    width: calc(100% - 7rem);
    padding: 0 0.5rem;
}
.widget-item .widget-type {
    float: right;
}
.widget-item .widget-move {
    float: right;
    display: inline-block;
    width: 1.5rem;
    height: 1.5rem;
    text-align: center;
    border-radius: 50%;
    vertical-align: middle;
    line-height: 1.5rem;
    margin: 0.25rem;
    border: 1px solid #37f;
    color: #37f;
}

.add-widget {
    color: #4271e2;
    display: block;
    background: #fff;
    border-bottom: 1px solid #ccc;
    padding: 0.5rem;
}

.ws-info {
    padding: 0.5rem;
    border-bottom: 1px solid #ccc;
}
.ws-attribute {
    padding: 0.5rem;
    background: #fff;
    border-bottom: 1px solid #ccc;
}
.ws-attribute .checkicon {
    display: inline-block;
    width: 1.5rem;
    color: #37f;
}
.ws-attribute .checkicon {
    display: inline-block;
    width: 1.5rem;
    color: #37f;
}
.ws-attribute .ws-type {
    display: block;
}
.ws-attribute .selects-list {
    padding-top: 0.5rem;
}
.ws-attribute .selects-list .select-item {
    display: block;
    padding: 0.5rem;
    color: #37f;
    border-top: 1px solid #ccc;
}
.ws-attribute .selects-list .select-item .icon-delete {
    color: red;
}
.ws-attribute > span {
    vertical-align: middle;
}
.switcher {
    display: inline-block;
    width: 42px;
    height: 21px;
    border-radius: 21px;
    background: #ccc;
    position: relative;
}
.switcher:before {
    content: ' ';
    position: absolute;
    width: 19px;
    height: 19px;
    left: 1px;
    top: 1px;
    background: #fff;
    border-radius: 50%;
}
.switcher.on {
    background: #37f;
}
.switcher.on:before {
    left: 22px;
}

.list-footer {
    padding: 0.5rem;
    text-align: center;
}
.ws-save {
    display: inline-block;
    padding: 0.5rem 2rem;
    background: #37f;
    color: #fff;
    border-radius: 1rem;
}
.form-item > .form-icon{
    float: right;
    margin-right: 1rem;
    height: 21px;
    width: 21px;
}
</style>

<header class="header">
    <a class="back" href="${contextPath}/moblicApprove/customform/list.do"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>自定义表单</span>
</header>
<form class="content-form">
    <div class="form-item textfield">
        <label>模板名称</label>
        <input id="form-name" name="form-name" type="text" value="" placeholder="请输入">
    </div>

    <div class="widgets-panel">
        <div class="panel-header">
            <span>控件列表</span>
            <a class="turn-mode">移动控件</a>
        </div>
        <div class="widgets-list">
        </div>
        <a href="javascript:" class="add-widget">
            <i class="iconfont icon-zengjia"></i>
            <span>添加控件</span>
        </a>
    </div>

    <div class="form-item">
        <label>选择图标</label>
        <img class="form-icon" src="${contextPath}/assets/src/css/img/formicons/icon_rb.png" alt="">
        <input id="icon-name" type="hidden" value="icon_rb">
    </div>
</form>
<footer class="footer" style="">
    <a href="javascript:" class="submit">提交</a>
</footer>

<script type="text/html" id="tmpl-widget">
    <a class="widget-remove"><i class="iconfont icon-delete"></i></a>
    <input class="widget-name" type="text" placeholder="请输入控件名称" value="{{text}}">
    <a class="widget-type">{{typename}}</a>
</script>
<script type="text/html" id="tmpl-widget-move">
    <a class="widget-remove"><i class="iconfont icon-delete"></i></a>
    <span class="widget-name">{{text}}</span>
    <a class="widget-move" data-move="up">↑</a>
    <a class="widget-move" data-move="down">↓</a>
</script>

<script type="text/html" id="tmpl-icon">
<# _.each(list, function (item, index) { #>
<li data-key="{{item.name}}" data-index="{{index}}">
    <img style="width:32px;" src="${contextPath}/assets/src/css/img/formicons/{{item.name}}.png" alt="">
</li>
<# }) #>
</script>

<script>
    seajs.use('page/micro-app/custom-form/edit', function (module) {
        module.run();
    });
</script>

</body>
</html>
