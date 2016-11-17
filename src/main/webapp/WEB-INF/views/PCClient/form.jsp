<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap/3.3.6/css/bootstrap.css"/>
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap-select/css/bootstrap-select.css" />
<link rel="stylesheet" href="${contextPath}/assets/dep/wang-editor/css/wangEditor.min.css" />
<!--<<link rel="stylesheet" href="${contextPath}/assets/dep/jquery-mobiscroll/mobiscroll.custom-2.5.0.min.css" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/clearJQMobile.css?v=100018" />-->
<link rel="stylesheet" href="${contextPath}/assets/src/css/mobile.css?v=100018" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcclient-form.css" />
<!--<link rel="stylesheet" href="${contextPath}/assets/src/css/common.css" />-->
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" />
<link rel="stylesheet" href="${contextPath}/assets/dep/zTree/css/zTreeStyle/zTreeStyle.css" />
<style>
</style>

</head>
<body>
<div id="doc" class="doc-content"></div>
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
<div>

<script type="text/template" id="tmpl-officialDocForm">
<div class="doc-toolbar">
    <button type="button" class="btn btn-default btn-act" data-do="start">提交</button>
</div>
<div class="container-fluid">
    <input type="hidden" name="formId" value="{{model.formID}}">
    <h1 class="doc-title">{{model.formName}}</h1>
    <table role="doc-form" class="form-table" style="width:100%;"></table>
    <div role="doc-preview" class="preview official-doc"></div>
</div>
</script>

<script type="text/template" id="tmpl-officialDoc">
<div class="heading">
    <h1 class="official-doc-heading">{{model.companyName}}文件</h1>
    <h2 class="official-doc-issue">{{model.issuedNumber}}</h2>
    <hr class="official-doc-heading-hr" />
</div>
<div class="body">
    <h1 class="official-doc-title">{{model.title}}</h1>
    <p class="official-doc-to">
        <# var mainPerson;
            if (typeof model.mainPerson === 'string') {
                if (model.mainPerson.length == 0) {
                    mainPerson = [];
                } else {
                    mainPerson = JSON.parse(model.mainPerson);
                }
            }
            else { mainPerson = model.mainPerson }
            var mainPersonText = _.map(mainPerson, function (item) { return item.name; }).join(',')
        #>
        {{mainPersonText}}：</p>
    <div class="official-doc-content">
        {{{model.mainBody}}}
        <# var attachments = model._attachment; if(attachments.length > 0) { #>
            <# for(var i = 0, count = 0, attachment, length = attachments.length; i < length; i++) { #>
                <#
                    attachment = attachments[i];
                    if(attachment) {
                        count ++;
                        var name = attachment.name;
                        var pos = name.lastIndexOf('.');
                        if(pos != -1) name = name.substr(0, pos);
                #>
                    <# if(count == 1) { #>
                    <p>附件：{{count}}.{{name}}</p>
                    <# } else { #>
                    <p>&nbsp;&nbsp;&nbsp;{{count}}.{{name}}</p>
                    <# } #>
                <# } #>
            <# } #>
        <# } #>
    </div>
</div>
<div class="footer">
    <hr class="official-doc-item-hr" />
    <div class="official-doc-item">
        <div class="official-doc-copy">抄送：
            <# var copyPerson;
                if (typeof model.copyPerson === 'string') {
                    if (model.copyPerson.length == 0) {
                        copyPerson = [];
                    } else {
                        copyPerson = JSON.parse(model.copyPerson);
                    }
                }
                else { copyPerson = model.copyPerson }
                var copyPersonText = _.map(copyPerson, function (item) { return item.name; }).join(',')
            #>
            {{copyPersonText}}
        </div>
    </div>
    <hr class="official-doc-item-hr" />
    <div class="official-doc-item">
        <div class="official-doc-item-left" style="width:70%;">
            <div class="official-doc-company">{{model.companyName}}</div>
        </div>
        <div class="official-doc-item-right" style="width:30%;">
            <div class="official-doc-distDate" style="text-align: right;">{{model.distributeDate}}印发</div>
        </div>
    </div>
    <hr class="official-doc-item-hr" style="border-top: 2px solid #000" />
    <div class="official-doc-item">
        <div class="official-doc-item-left" style="width:50%">
            <div class="official-doc-draftDept">主办部门：{{model.draftDept}}<br/>联系电话：{{model.contactNumber}}</div>
        </div>
        <div class="official-doc-item-right" style="width:50%">
            <div class="official-doc-contacts">联系人：{{model.contacts}}</div>
        </div>
    </div>
</div>
</script>
<script>
window.UUID = '${num}';
seajs.use('page/pcclient/form', function (module) {
    module.run();
});
</script>
</body>
</html>
