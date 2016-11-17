<%@ page language="java" pageEncoding="utf-8"%>
<script id="tmpl-info" type="text/html">
    <div class="avatar" style="background:{{colorCode}}">{{subName}}</div>
    <div class="info">
        <div class="name">{{userName}}</div>
        <div class="step">{{text}}</div>
    </div>
</script>
<script id="tmpl-history" type="text/html">
    <# _.each(list, function (item, index) { #>
        <div class="history-item {{item.statusClass}}" style="margin-bottom: 10px;padding: 5px 0; border-bottom: 1px dashed #ccc;">
            <span class="user" style="display: inline-block;width: 200px;">{{item.userName}}</span><span style="display: inline-block;">{{item.statusText}}</span>
            <# if(item.time != '') { #><div style="color: #666">{{item.time}}</div><# } #>
        </div>
    <# }) #>
</script>
<script id="tmpl-buttons" type="text/html">
    <# _.each(buttons, function (item) { #>
        <div class="button" data-action="{{item.action}}">{{item.text}}</div>
    <# }) #>
</script>
<script id="tmpl-formCollection" type="text/html">
    <div class="doc-toolbar clearfix">
        <# for(var i = 0, item, length = buttons.length; i < length; i++) { item = buttons[i]; #>
        <button type="button" class="btn btn-default btn-act" data-do="{{item.action}}">{{item.text}}</button>
        <# } #>
        <button type="button" class="btn btn-default pull-right" data-do="histroy">流程跟踪</button>
    </div>
</script>
<script id="tmpl-officialDocForm" type="text/html">
<div class="container-fluid">
    <input type="hidden" name="formId" value="{{model.formID}}">
    <h1 class="doc-title">{{model.companyName}}</h1>
    <table role="doc-form" class="form-table" style="width:100%;"></table>
</div>
</script>

<script id="tmpl-officialDoc" type="text/html">
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
                <# attachment = attachments[i];
                    if(attachment) {
                        count ++;
                        var name = attachment.name;
                        var pos = name.lastIndexOf('.');
                        if(pos != -1) name = name.substr(0, pos); #>
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
            {{copyPersonText}}</div>
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
