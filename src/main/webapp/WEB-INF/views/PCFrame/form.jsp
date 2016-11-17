<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap/3.3.6/css/bootstrap.css"/>
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" />
<link rel="stylesheet" href="${contextPath}/assets/dep/wang-editor/css/wangEditor.min.css">
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcform.css" />
</head>
<body>
<form class="content-form-pc clearfix" style="display:none;">
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
    seajs.use('page/pcframe/form', function (module) {
        module.run();
    });
</script>
</body>
</html>
