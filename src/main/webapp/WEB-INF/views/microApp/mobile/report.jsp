<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<style>
    .header .search{
        float: right;
    }
    #report .up{
        border: 10px solid transparent;
        border-bottom-color: #fff;
        width: 0px;
        height: 0px;
        position: relative;
        bottom: 30px;
    }
    #report .down{
        border: 10px solid transparent;
        border-top-color: #fff;
        width: 0px;
        height: 0px;
        position: relative;
        top: 30px;
    }
</style>
<body>

<header class="header">
    <a class="back" href="${contextPath}/moblicApprove/toSearchReport.do"><i class="iconfont icon-xiangzuo1"></i></a>
    <span>报表</span>
    <a class="search" href="${contextPath}/moblicApprove/toSearchReport.do">筛选</a>
</header>
<div class="list-panel">
</div>

<script type="text/html" id="tmpl-list">
    <table class="table table-maintain" id="report">
        <thead style="background-color:#efefef">
            <tr>
                <th>姓名 <i style="visibility:hidden"></i></th>
                <th>时间 <i style="visibility:hidden"></i></th>
                <# _.each(type, function (item) { #>
                    <th>{{item.describeName}} <i style="visibility:hidden"></i></th>
                <# }) #>
            </tr>
        </thead>
        <tbody>
            <# _.each(lists, function (item) { #>
                <tr>
                    <td>{{item.userName}}</td>
                    <td>{{item.draft_date}}</td>
                    <# _.each(type, function (typeitem) { #>
                        <td>{{item[typeitem.re_name]}}</td>                
                    <# }); #>
                </tr>
            <# }) #>
        </tbody>
    </table>
</script>

<script>
    seajs.use('page/micro-app/mobile/report', function (module) {
        module.run();
    });
</script>

</body>
</html>
