define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');

    require('jquery-util');
    var util = require('component/mobile/util');
    var formInit = require('component/mobile/form');
    var loader = require('component/mobile/loader');

    var urls = {
        getFormConfig: CONTEXT_PATH + '/approval/getApprovalTableInfo.do',
        getDefaults: CONTEXT_PATH + '/approvals/defaultapproval.do',
        getLastApp: CONTEXT_PATH + '/approval/getLastApprovalIds.do',

        indexPage: CONTEXT_PATH + '/moblicApprove/toIndex.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do',
        taskPage: CONTEXT_PATH + '/moblicApprove/toTasks.do',
        welfarePage: CONTEXT_PATH + '/moblicApprove/toWelfale.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toApprovalList.do',
        workflowPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do'
    };
    var getIndexPage = function (wyyId) {
        var url = '';
        wyyId = wyyId || 'wyy0001';
        switch (wyyId) {
            case 'wyy0001': url = urls.indexPage; break;
            case 'wyy0002': url = urls.dailyPage; break;
            case 'wyy0003': url = urls.taskPage; break;
            case 'wyy0004': url = urls.welfarePage; break;
            default: url = urls.indexPage; break;
        }

        return url;
    };
    var goBack = function () {
        var fromPage = util.getParam('from');
        var wyyId = util.getParam('wyyId');
        var backUrl = fromPage === 'index' ? getIndexPage(wyyId) : (urls.listPage + '?wyyId=' + wyyId);
        location.href = backUrl;
    };

    var getColor = function (str) {
        var code = 0, temp = 0, codeStr = '', i = 0;
        for (i = 0; i < str.length; i++) {
            code += str.charCodeAt(i);
        }
        for (i = 0; i < 3; i++) {
            temp = code % 192;
            codeStr += ('00' + temp.toString(16)).slice(-2);
            code = Math.round(code * temp / 127);
        }
        return codeStr;
    };
    var subname = function (name, length) {
        if (!length || length <= 0) {
            length = 2;
        }
        return name.slice(-1 * length);
    };
    var getDefaultApprovars = function (typeId, cb) {
        $.ajax({
            url: urls.getDefaults,
            type: 'get',
            dataType: 'json',
            data: {
                id: typeId,
                rnd: (new Date).getTime()
            },
            success: function (res) {
                if (res.success) {
                    if (!(res.model instanceof Array) || res.model.length === 0) {
                        res.model = false;
                    }
                    else {
                        _.each(res.model, function (item) {
                            if (item.id === '$$$') { return; }
                            item.userName = item.approvalName || '未知';
                            item.subName = subname(item.userName);
                            item.colorCode = getColor(item.userName);
                        });
                    }
                    cb(res.model);
                }
                else {
                    util.alert(res.message || '获取审批人信息出错！', function () {
                        goBack();
                    });
                }
            },
            error: function () {
                util.alert('获取审批人信息出错！', function () {
                    goBack();
                });
            }
        });
    };
    var getLastApprovars = function (typeId, cb) {
        $.ajax({
            url: urls.getLastApp,
            type: 'get',
            dataType: 'json',
            data: {
                typeId: typeId,
                rnd: (new Date).getTime()
            },
            success: function (res) {
                if (res.success) {
                    if (!(res.model instanceof Array) || res.model.length === 0) {
                        res.model = false;
                    }
                    else {
                        _.each(res.model, function (item) {
                            if (item.id === '$$$') { return; }
                            item.userId = item.id;
                            item.userName = item.name;
                            item.subName = subname(item.userName);
                            item.colorCode = getColor(item.userName);
                        });
                    }
                    cb(res.model);
                }
                else {
                    util.alert(res.message || '获取审批人信息出错！', function () {
                        goBack();
                    });
                }
            },
            error: function () {
                util.alert('获取审批人信息出错！', function () {
                    goBack();
                });
            }
        });
    };

    var getInputData = function ($input) {
        return {
            controlName: $input.attr('name').match(/[A-z]+/ig)[0],
            id: $input.attr('name'),
            value: $input.val()
        };
    };
    var serializeTablefield = function ($tablefield) {
        var listCollection = [], sumList = [];
        var boxes = $tablefield.find('.table-box');
        boxes.each(function (index, box) {
            var boxList = [];
            $(box).children().each(function (i, item) {
                var $item = $(item);
                if ($item.is('.textnote') || $item.is('.picturenote')) {
                    return;
                }
                if ($item.is('.form-item')) {
                    if ($item.is('.dddaterangefield')) {
                        var startInput = $item.find('.range-start'),
                            endInput = $item.find('.range-end');

                        boxList.push({
                            controlName: startInput.attr('name').match(/[A-z]+/ig)[0],
                            id: startInput.attr('name'),
                            value: [startInput.val(), endInput.val()]
                        });
                    }
                    else if ($item.is('.linkageselectfield')) {
                        var parentInput =  $item.find('input.parent'),
                            childInput =  $item.find('input.child');

                        boxList.push({
                            controlName: parentInput.attr('name').match(/[A-z]+/ig)[0],
                            id: parentInput.attr('name'),
                            value: [parentInput.val(), childInput.val()]
                        });
                    }
                    else {
                        var $input = $item.find('input[name] ,textarea[name]');
                        boxList.push(getInputData($input));
                    }
                }
            });
            listCollection.push({ list: boxList });
        });
        var sumFields = $tablefield.find('.compute-container .numbercompute');
        sumFields.each(function (index, item) {
            var $input = $(item).find('input[name]');
            sumList.push(getInputData($input));
        });

        return {
            controlName: 'TableField',
            id: $tablefield.find('.table-container').data('name'),
            value: listCollection,
            sumList: sumList
        };
    };
    var serializeCustomForm = function ($form) {
        var dataList = [];
        $form.children().each(function (index, item) {
            var $item = $(item);
            if ($item.is('.textnote') || $item.is('.picturenote') || $item.is('.approveselect')) {
                return;
            }
            else if ($item.is('.tablefield')) {
                dataList.push(serializeTablefield($item));
            }
            else if ($item.is('.dddaterangefield')) {
                var startInput = $item.find('.range-start'),
                    endInput = $item.find('.range-end');

                dataList.push({
                    controlName: startInput.attr('name').match(/[A-z]+/ig)[0],
                    id: startInput.attr('name'),
                    value: [startInput.val(), endInput.val()]
                });
            }
            else if ($item.is('.linkageselectfield')) {
                var parentInput =  $item.find('input.parent'),
                    childInput =  $item.find('input.child');

                dataList.push({
                    controlName: parentInput.attr('name').match(/[A-z]+/ig)[0],
                    id: parentInput.attr('name'),
                    value: [parentInput.val(), childInput.val()]
                });
            }
            else {
                var $input = $item.find('input[name] ,textarea[name]');
                dataList.push(getInputData($input));
            }
        });

        return { data: dataList };
    };

    var initPage = function (model, defApps, lastApps) {
        $('.form-title').text('我的' + model.typename);
        $('.back').on('click', goBack);

        model.forminfo.push({
            'describeName': '审批人',
            'isRequired': true,
            'sequence': 99900, //model.forminfo.length * 100,
            'reName': 'approvalIds',
            'controlId': 'ApproveSelect',
            'positioning': true,
            'multi': true,
            'lastApprover': lastApps,
            'defaultApprover': defApps
        });
        var $form = $('.content-form');
        var formObj;
        try {
            formObj = formInit($form, model.forminfo);
        }
        catch (ex) {
            console && console.error(ex);
            util.alert('表单配置有错，请联系管理员！', function () {
                goBack();
            });
        }

        var pending = false;
        $('.submit').on('click', function () {
            //var str = $('[name=TextField100]').val();
            //alert(str.replace(/\ud83c[\udf00-\udfff]|\ud83d[\udc00-\ude4f]|\ud83d[\ude80-\udeff]/g, ''));
            //return;
            var wyyId = util.getParam('wyyId');

            if (pending) { return; }
            var valid = formObj.checkRequired();
            if (!valid.success) {
                util.alert(valid.message); return;
            }

            var formdata = {};
            var model = serializeCustomForm($form);
            formdata['approvalIds'] = $form.find('[name=approvalIds]').val();
            formdata['model'] = JSON.stringify(model);
            formdata['typeId'] = util.getParam('typeId');
            //formdata['companyId'] = util.constant.companyId;
            formdata['thirdId'] = util.getParam('thirdId');
            formdata['status'] = util.getParam('status');
            formdata['taskId'] = util.getParam('taskId');
            formdata['isBatch'] = util.getParam('isBatch');
            formdata['wyyId'] = wyyId;

            $.ajax({
                url: CONTEXT_PATH + '/approval/startProcess.do',
                type: 'post',
                dataType: 'json',
                data: formdata,
                beforeSend: function () {
                    pending = true;
                },
                success: function (res) {
                    if (res.success) {
                        util.alert('提交成功！', function () {
                            location.href = urls.workflowPage + '?type=wdfq&wyyId=' + wyyId;
                        });
                    }
                    else {
                        util.alert(res.message);
                    }
                },
                error: function () {
                    util.alert('发生错误，请稍后再试！');
                },
                complete: function () {
                    pending = false;
                }
            });
        });

        $('.header, .content-form, .footer').show();
    };
    var run = function () {
        loader.show();
        var typeId = util.getParam('typeId');
        var wyyId = util.getParam('wyyId');
        $('.header').addClass(wyyId);
        $.ajax({
            url: urls.getFormConfig,
            type: 'get',
            dataType: 'json',
            data: {
                typeId: typeId,
                rnd: (new Date).getTime()
            },
            success: function (res) {
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    if (typeof res.model.forminfo === 'string') res.model.forminfo = JSON.parse(res.model.forminfo);
                    //TODO
                    getDefaultApprovars(typeId, function (defApps) {
                        if (!defApps) {
                            getLastApprovars(typeId, function (lastApps) {
                                initPage(res.model, false, lastApps);
                                loader.hide();
                            });
                        }
                        else {
                            initPage(res.model, defApps);
                            loader.hide();
                        }
                    });
                }
                else {
                    util.alert(res.message || '获取表单配置出错！', function () {
                        goBack();
                    });
                }
            },
            error: function () {
                util.alert('获取表单配置出错！', function () {
                    goBack();
                });
            }
        });
    };

    module.exports = { run: run };
});
