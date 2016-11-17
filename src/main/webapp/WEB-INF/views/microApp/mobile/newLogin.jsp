<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
</head>
<body>
    <div class="full-list">
        <div class="list-header"><span>请选择归属单位</span></div>
        <div id="company-list">
        </div>
    </div>

<script type="text/html" id="tmpl-company">
    <# _.each(list, function (item) { #>
        <a href="javascript:" data-id="{{item.userId}}" class="listItem">
            {{item.companyName}}
        </a>
    <# }) #>
</script>
<script>
    seajs.use('page/micro-app/mobile/new-login', function (module) {
        module.run();
    });
    var prttype = "${prttype}"
</script>
</body>
</html>
