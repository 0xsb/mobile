<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap/3.3.6/css/bootstrap.css"/>
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap-select/css/bootstrap-select.css" />
<link rel="stylesheet" href="${contextPath}/assets/dep/wang-editor/css/wangEditor.min.css" />

<link rel="stylesheet" href="${contextPath}/assets/dep/jquery-mobiscroll/mobiscroll.custom-2.5.0.min.css" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/clearJQMobile.css?v=100018" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/mobile.css?v=100018" />

<link rel="stylesheet" href="${contextPath}/assets/src/css/pcclient-form.css" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/common.css" />
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" />

</head>
<body>
<div id="official-doc-form" class="doc-content"></div>
<div id="official-doc" class="doc-content official-doc"></div>

<div id="select-user-popup" class="mask" style="display:none;z-index: 10;">
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
</div>

<div id="history-popup" class="mask" style="display:none;">
    <div class="detail-history" style="background: #fff;border: 1px solid #ddd;width: 400px;
        margin-left: -200px;position: absolute;left: 50%;top: 30px;
        border-radius: 5px;padding: 10px;"></div>
</div>

<%@ include file="./tmpl-officialDoc.jsp"%>
<script>
window.UUID = '${num}';
seajs.use('page/pcclient/my-flow-detail', function (module) {
    module.run();
});
</script>
</body>
</html>
