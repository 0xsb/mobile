define(function(require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    require('jquery-util');
    var util = require('component/mobile/util');
    var formInit = require('component/mobile/form');
    var loader = require('component/mobile/loader');
    var fulllist = require('component/mobile/full-list');

    var urls = {
        getForm: CONTEXT_PATH + '/workflow/getStartForm.do',
        // start: CONTEXT_PATH + '/workflow/startProcess.do',
        start: CONTEXT_PATH + '/document/startProcess.do',
        selectUser: CONTEXT_PATH + '/workflow/selectUser.do',

        indexPage: CONTEXT_PATH + '/moblicApprove/toIndex.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do',
        taskPage: CONTEXT_PATH + '/moblicApprove/toTasks.do',
        welfarePage: CONTEXT_PATH + '/moblicApprove/toWelfale.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toApprovalList.do',
        workflowPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do'
    };
    var getIndexPage = function(wyyId) {
        var url = '';
        wyyId = wyyId || 'wyy0001';
        switch (wyyId) {
            case 'wyy0001':
                url = urls.indexPage;
                break;
            case 'wyy0002':
                url = urls.dailyPage;
                break;
            case 'wyy0003':
                url = urls.taskPage;
                break;
            case 'wyy0004':
                url = urls.welfarePage;
                break;
            default:
                url = urls.indexPage;
                break;
        }

        return url;
    };
    var goBack = function() {
        var fromPage = util.getParam('from');
        var wyyId = util.getParam('wyyId');
        var backUrl = fromPage === 'index' ? getIndexPage(wyyId) : (urls.listPage + '?wyyId=' + wyyId);
        location.href = backUrl;
    };

    var convertUserData = function(list) {
        var newlist = _.map(list, function(item) {
            return {
                id: item.userId,
                text: item.userName
            };
        });
        return newlist;
    };
    var popupPos = function($popup) {
        var w = $popup.find('.approve-modal').width(),
            h = $popup.find('.approve-modal').height();
        $popup.find('.approve-modal').css({
            'marginLeft': (w / -2) + 'px',
            'marginTop': (h / -2) + 'px'
        });
    };

    var pending = false;
    var doSelectUser = function(pageParams) {
        $.ajax({
            url: urls.selectUser,
            dataType: 'json',
            type: 'post',
            data: {
                taskId: pageParams.taskId,
                nextAssignee: pageParams.nextAssignee
            },
            beforeSend: function() {
                pending = true;
            },
            success: function(res) {
                if (res.success) {
                    util.alert('提交成功！', function() {
                        location.href = urls.workflowPage + '?type=wdfq&wyyId=' + pageParams.wyyId;
                    });
                } else {
                    util.alert(res.message);
                }
            },
            error: function() {
                util.alert('发生错误，请稍后再试！');
            },
            complete: function() {
                pending = false;
            }
        });
    };
    var doSubmit = function(pageParams, formView, model, selectUserList) {
        if (pending) {
            return;
        }
        var valid = formView.checkRequired();
        if (!valid.success) {
            util.alert(valid.message);
            return;
        }

        var formModel = model.form;
        var formdata = formView.getData();
        var data = {
            formData: {},
            paramterData: {}
        };
        data.formData = [{
            id: formView.getAttribute('id'),
            title: formView.getAttribute('title') + '(' + util.constant.userName + '-' + util.constant.deptName + ')',
            data: formdata
        }];
        data.paramterData = formView.getParamters(formModel.paramters, formdata);
        var jsonData = JSON.stringify(data);

        $.ajax({
            url: urls.start,
            dataType: 'json',
            type: 'post',
            data: {
                key: pageParams.flowId,
                next: pageParams.next,
                model: jsonData
            },
            beforeSend: function() {
                pending = true;
            },
            success: function(res) {
                if (res.success) {
                    pageParams.taskId = res.model.taskId;
                    pageParams.jsonData = jsonData;
                    if (res.model.users.length > 1) {
                        selectUserList.setMulti(res.model.userTaskType == '1');
                        selectUserList.setData(convertUserData(res.model.users));
                        $('#select-user-popup').show();
                        popupPos($('#select-user-popup'));
                        if (res.model.userTaskType == '1') {
                            selectUserList.$el.find('.ok-button').css({
                                'position': 'absolute',
                                'z-index': 12,
                                'right': 10,
                                'top': 10,
                                'color': '#3386c0'
                            });
                        }
                    } else {
                        pageParams.nextAssignee = res.model.users[0].userId;
                        doSelectUser(pageParams);
                    }
                } else {
                    util.alert(res.message);
                }
            },
            error: function() {
                util.alert('发生错误，请稍后再试！');
            },
            complete: function() {
                pending = false;
            }
        });
    };

    var initSelectUser = function(selectUserList, pageParams, formView) {
        var $popup = $('#select-user-popup');
        $popup.find('textarea').on('click', function() {
            selectUserList.show();
        });
        selectUserList.on('select', function(e, user) {
            if (user instanceof Array) {
                var texts = [],
                    ids = [];
                _.each(user, function(item) {
                    texts.push(item.text);
                    ids.push(item.id);
                    $popup.find('textarea').val(texts.join(','));
                    $popup.find('[role="selected-user"]').val(ids.join(','));
                });
            } else {
                $popup.find('textarea').val(user.text);
                $popup.find('[role="selected-user"]').val(user.id);
            }
            selectUserList.hide();
        });
        $popup.find('.btn-cancle').on('click', function() {
            $popup.trigger('hide');
        });
        $popup.on('hide', function() {
            $popup.find('textarea').val('');
            $popup.find('[role="selected-user"]').val('');
            $popup.hide();
        });
        $popup.find('.btn-apply').on('click', function() {
            pageParams.nextAssignee = $popup.find('[role="selected-user"]').val();
            if (selectUserList.multi) {
                pageParams.nextAssignee = JSON.stringify(pageParams.nextAssignee.split(','));
            }
            if (pageParams.nextAssignee.length === 0 || pageParams.nextAssignee === '[]') {
                alert('请选择办理人！');
                return;
            }

            doSelectUser(pageParams);
        });
    };

    var initPage = function(model, flowId) {
        $('.form-title').text('我的' + model.form.formName);
        $('.back').on('click', goBack);

        var wyyId = util.getParam('wyyId');
        var pageParams = {
            flowId: flowId,
            wyyId: wyyId,
            next: model.next
        };
        var selectUserList = fulllist(model.userTaskType);

        var $form = $('.content-form');
        var formView;
        try {
            formView = formInit($form, model.form.widgets);
            formView.setAttribute('id', model.form.formID);
            formView.setAttribute('title', model.form.formName);
            formView.setAttribute('taskId', flowId);
            formView.setAttribute('paramterMap', model.form.paramters);
        } catch (ex) {
            console.error(ex);
            util.alert('表单配置有错，请联系管理员！', function() {
                goBack();
            });
        }

        $('[role="form-submit"]').on('click', function() {
            doSubmit(pageParams, formView, model, selectUserList);
        });
        $('.header, .content-form, .footer').show();

        initSelectUser(selectUserList, pageParams, formView);
    };

    var run = function() {
        var flowId = util.getParam('flowId');
        $.ajax({
            url: urls.getForm,
            data: {
                key: flowId,
                rnd: (new Date).getTime()
            },
            dataType: 'json',
            type: 'get',
            success: function(res) {
                if (res.success) {
                    initPage(res.model, flowId);
                } else {
                    util.alert(res.message || '获取表单配置出错！', function() {
                        goBack();
                    });
                }
            },
            error: function() {
                util.alert('获取表单配置出错！', function() {
                    goBack();
                });
            }
        });
    };

    module.exports = {
        run: run
    };
});
