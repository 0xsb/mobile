<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/common/head-mobile.jsp"%>
</head>
<body>
<header class="header" style="display:none;">
    <a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>
    <span class="form-title"></span>
</header>
<form class="content-form" style="display:none;">
</form>
<footer class="footer" style="display:none;">
    <a href="javascript:" class="submit" role="form-submit">提交</a>
</footer>

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

<script>
    window.UUID = '${num}';
    seajs.use('page/edition-third/form', function (module) {
        module.run();
    });
</script>
</body>
</html>
