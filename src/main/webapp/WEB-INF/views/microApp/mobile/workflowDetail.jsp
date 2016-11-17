<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>

<header class="header">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>审批详情</span>
</header>
<div class="detail-box">
    <div class="detail-info"></div>
    <div class="detail-data"></div>
    <form class="detail-form"></form>
    <div class="detail-history"></div>

    <div class="detail-footer approver" style="display: none;">
        <div class="button agree">同意</div>
        <div class="button reject">拒绝</div>
        <div class="button move">转发</div>
    </div>
    <div class="detail-footer lunch" style="display: none;">
        <div class="button revoke">撤销</div>
    </div>
    <div class="detail-footer read" style="display: none;">
        <div class="button read">已阅</div>
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

<script id="tmpl-info" type="text/html">
    <div class="avatar" style="background:{{colorCode}}">{{subName}}</div>
    <div class="info">
        <div class="name">{{userName}}</div>
        <div class="step">{{text}}</div>
    </div>
</script>
<script id="tmpl-history" type="text/html">
    <# _.each(list, function (item, index) {
        var complete = item.runStatus != '1';
        var opText = index === 0 ? '发起' : item.statusText;
        var opdes = (index === 0 || item.opinion === '') ? '' : '(' + item.opinion + ')';
    #>
        <div class="history-item {{item.statusClass}}">
            <div class="avatar" style="background:{{item.colorCode}}">{{item.subName}}</div>
            <div class="info">
                <div class="name">{{item.userName}}</div>
                <div class="step {{item.statusClass}}">
                    {{opText}}
                    <span class="approve-des">{{opdes}}</span>
                </div>
                <div class="time">{{item.time}}</div>
            </div>
        </div>
    <# }) #>
</script>
<script type="text/html" id="tmpl-datas">
<# _.each(list, function (item, index) { #>
    <# if (item.type === 'item') { #>
    <div class="data-item">
        <label>{{item.name}}</label>
        <p>{{item.value}}</p>
    </div>
    <# } else if (item.type === 'title') { #>
    <div class="data-title">
        <span>{{item.value}}</span>{{item.name}}
    </div>
    <#} else if (item.type === 'sum') { #>
    <div class="data-item data-sumitem {{ (!list[index + 1] || list[index + 1].type !== 'sum') ? 'lastsum' : '' }}">
        <label>合计{{item.name}}</label>
        <p>{{item.value}}</p>
    </div>
    <# } else if (item.type === 'img') { #>
    <div class="data-item">
        <label>{{item.name}}</label>
        <div class="data-list clearfix">
        <# _.each(item.value, function (item) { #>
            <div class="img-block" data-fullpath="${contextPath}/file/download/{{item.id}}/{{item.name}}.do">
                <img src="${contextPath}/file/download/{{item.id}}/thum_{{item.name}}.do" alt="">
            </div>
        <# }) #>
        </div>
    </div>
    <# } else if (item.type === 'attach') { #>
    <div class="data-item">
        <label>{{item.name}}</label>
        <div class="data-list clearfix">
        <# _.each(item.value, function (item) { #>
            <a class="attach-block" href="${contextPath}/file/download/{{item.id}}/{{item.name}}.do">
                <i class="iconfont icon-xinxi"></i>
                <span>{{item.filename || item.name}}</span>
            </a>
        <# }) #>
        </div>
    </div>
    <# } #>
<# }) #>
</script>

<script>
    window.UUID = '${num}';
    seajs.use('page/micro-app/mobile/workflow-detail', function (module) {
        module.run();
    });
</script>

</body>
</html>
