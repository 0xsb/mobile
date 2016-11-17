<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
<style>
    .workflow-list > li.noraml {

    }
    .workflow-list > li.warning {
        background: #fffabf;
    }
    .workflow-list > li.expired {
        background: #ffd2d2;
    }
    .timetext {
        color: #999999;
        font-size: 0.75rem;
        line-height: 1rem;
    }
    .title {
        max-width: 100%;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        padding: 0.25rem 0;
        line-height: 1.25rem;
        font-size: 1rem;
        margin-bottom: 0.375rem;
    }
</style>
</head>
<body>

<header class="header">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span class="list-title">督办公示</span>
</header>
<div>
    <ul class="workflow-list">
    </ul>
</div>

<script type="text/html" id="tmpl-tasks">
    <# _.each(list, function (item) { #>
        <li class="{{item.status}}">
            <a href="javascript:">
                <div class="avatar" style="background:{{item.colorCode}}">{{item.subName}}</div>
                <div class="info">
                    <div class="title">{{item.name}}</div>
                    <div class="timetext">开始时间：{{item.startTime}}</div>
                    <div class="timetext">结束时间：{{item.endTime}}</div>
                </div>
            </a>
        </li>
    <# }) #>
</script>
<script type="text/html" id="tmpl-nodata">
    <li class="nodata-tip">
        <p>暂无公示的督办信息</p>
        <p>请稍后再来查看</p>
    </li>
</script>

<script>
    seajs.use('page/micro-app/tasks/publication', function (module) {
        module.run();
    });
</script>

</body>
</html>
