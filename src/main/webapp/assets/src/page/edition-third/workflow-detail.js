define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('component/mobile/util');
    var userList = require('component/mobile/user-list');
    var loader = require('component/mobile/loader');
    var formInit = require('component/mobile/form');
    var detailInit = require('component/mobile/flow-detail');
    var SelectList = require('component/mobile/select-list');
    var fulllist = require('component/mobile/full-list');

    var alert = util.alert;

    var urls = {
        getDetail: CONTEXT_PATH + '/workflow/taskDetail.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do',

        // agree: CONTEXT_PATH + '/workflow/agree.do',
        agree: CONTEXT_PATH + '/document/agree.do',
        next: CONTEXT_PATH + '/document/selectNextTask.do',
        selectUser: CONTEXT_PATH + '/workflow/selectUser.do',
        reject: CONTEXT_PATH + '/process/reject.do',
        relay: CONTEXT_PATH + '/process/reassign.do',
        recovery: CONTEXT_PATH + '/process/recovery.do'
    };
    var MODE_MYLAUNCH = 0, //我的发起
        MODE_WAITAPPROVEL = 1, //待我审批
        MODE_APPROVELED = 2, //我已审批
        MODE_WAITREAD = 3; //待我阅览
    var modeStatus = {
        'wdfq': MODE_MYLAUNCH,
        'dwsp': MODE_WAITAPPROVEL,
        'wysp': MODE_APPROVELED,
        'dwyl': MODE_WAITREAD
    };
    //实际共4种动作，[agree][reject][recovery][relay]
    //这个配置与admin项目中edition-third下flow\flowchart.js中的配置项匹配
    var buttonsSet = {
        '1': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '拒绝',
            action: 'reject'
        }],
        '2': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '拒绝',
            action: 'reject'
        }, {
            text: '转发',
            action: 'relay'
        }],
        '3': [{
            text: '提交',
            action: 'agree'
        }],
        '4': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '驳回',
            action: 'recovery'
        }],
        '5': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '驳回',
            action: 'recovery'
        }, {
            text: '转发',
            action: 'relay'
        }],
        '6': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '拒绝',
            action: 'reject'
        }, {
            text: '驳回',
            action: 'recovery'
        }],
        '7': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '转发',
            action: 'relay'
        }]
    };

    var goBack = function (mode) {
        var wyyId = util.getParam('wyyId');
        location.href = urls.listPage + '?type=' + mode + '&wyyId=' + wyyId;
        //history.back();
    };
    var toMap = function (list, key) {
        var newMap = {};
        _.each(list, function (item, index) {
            var newKey;
            if (typeof key === 'function') {
                newKey = key(item, index);
            } else {
                newKey = item[key];
            }

            newMap[newKey] = item;
        });
        return newMap;
    };

    var buttonsRender = template($('#tmpl-buttons').html());
    var historyRender = template($('#tmpl-history').html());
    var nextNodesRender = template($('#tmpl-nextNode').html());

    var createFormsSet = function (model, mode) {
        var forms = [];
        if (modeStatus[mode] === MODE_WAITAPPROVEL) {
            if (model.taskConfig.__success) { //继承表单
                forms = forms.concat(model.forms);
                _.each(forms, function (item, index) {
                    item.writeble = model.taskConfig.__writable;
                    item.indexId = item.formID + '-' + index.toString();
                });
            }
            if (model.currTaskForm && model.currTaskForm.formID) { //有当前表单存在
                model.currTaskForm.writeble = true;
                model.currTaskForm.isCurrent = true;
                model.currTaskForm.indexId = model.currTaskForm.formID + '-' + forms.length.toString();
                forms.push(model.currTaskForm);
            }
            if (model.currentNoSuccessForm) {
                model.currentNoSuccessForm.writeble = true;
                model.currentNoSuccessForm.isNoSuccess = true;
                forms.push(model.currentNoSuccessForm);
            }
        } else {
            forms = forms.concat(model.forms);
            _.each(forms, function (item, index) {
                item.writeble = false;
                item.indexId = item.formID + '-' + index.toString();
            });
        }
        return forms;
    };
    var createFormViews = function (formsSet, formsData) {
        var formsList = [];
        _.each(formsSet, function (item, key) {
            var $item, formView, formData;
            formData = formsData[item.indexId];
            if (!item.writeble) {
                $item = $('<div></div>').addClass('detail-data');
                formView = detailInit($item, formData.data, item.widgets);
                formView.setAttribute('paramters', formData.paramters);
            } else {
                $item = $('<form></form>').addClass('detail-form');
                formView = formInit($item, item.widgets);
                formView.setAttribute('paramterMap', item.paramters);
                if (formData) {
                    formView.setData(formData.data);
                }
            }

            formView.setAttribute('id', item.formID);
            if (item.isCurrent) {
                formView.setAttribute('isCurrent', true);
                formView.setAttribute('title', item.formName + '(' + util.constant.userName + '-' + util.constant.deptName + ')');
            } else if (item.isNoSuccess) {
                formView.setAttribute('isNoSuccess', true);
                formView.setAttribute('title', item.formName);
            } else {
                formView.setAttribute('title', formData.title); //item.formName);
            }
            formsList.push(formView);
        });

        return formsList;
    };

    var statusMap = {
        '100': 'processing', //处理中
        '101': 'draft', //草稿
        '200': 'agree', //同意
        '201': 'complete', //已完成
        '300': 'revoke', //撤销
        '400': 'reassign', //转交
        '500': 'reject', //拒绝
        '600': 'recovery' //回收
    };
    var getFormsData = function (formViews) {
        var formsData = [],
            paramters = {},
            CurrentNoSuccessFormData;
        _.each(formViews, function (formView) {
            var itemData = {},
                itemParam;
            itemData.id = formView.getAttribute('id');
            itemData.data = formView.getData();
            itemData.title = formView.getAttribute('title');

            if (formView.getAttribute('isNoSuccess')) { //不可继承表单
                CurrentNoSuccessFormData = itemData;
            } else {
                formsData.push(itemData);
            }

            if (formView.getParamters) {
                var paramterMap = formView.getAttribute('paramterMap');
                itemParam = formView.getParamters(paramterMap, itemData.data); //可编辑表单
            } else {
                itemParam = formView.getAttribute('paramters'); //详情
            }
            _.extend(paramters, itemParam);
        });

        var result = {
            formData: formsData,
            paramterData: paramters
        };
        if (CurrentNoSuccessFormData) {
            result['CurrentNoSuccessFormData'] = CurrentNoSuccessFormData;
        }
        return result;
    };
    var convertUserData = function (list) {
        var newlist = _.map(list, function(item) {
            return {
                id: item.userId,
                text: item.userName
            };
        });
        return newlist;
    };
    var convertHistory = function (history) {
        return _.map(history, function(item, index) {
            return {
                subName: item.assignee.slice(-2),
                userName: item.assignee,
                statusText: index === 0 ? '发起' : item.status,
                statusClass: statusMap[item.statusCode],
                opinion: item.opinion === '' ? '' : '(' + item.opinion + ')',
                colorCode: '#' + util.transColorCode(item.assignee),
                time: item.endTime ? util.formatDate(new Date(item.endTime), 'yyyy-MM-dd HH:mm:ss') : ''
            };
        });
    };

    var popupPos = function ($popup) {
        var w = $popup.find('.approve-modal').width(),
            h = $popup.find('.approve-modal').height();
        $popup.find('.approve-modal').css({
            'marginLeft': (w / -2) + 'px',
            'marginTop': (h / -2) + 'px'
        });
    };
    var popupShow = {
        'agree': function (pageParams) {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').hide();
            $('#approve-popup').find('.btn-apply').data('action', 'agree');
            popupPos($('#approve-popup'));
        },
        'next': function (pageParams) {
            var markup = nextNodesRender({
                taskId: pageParams.taskId,
                items: pageParams.nextNodes
            });
            $('#next-node-popup').find('.next-nodes').html(markup);
            $('#next-node-popup').show();
            $('#next-node-popup').find('.btn-apply').data('action', 'next');
            popupPos($('#next-node-popup'));
        },
        'selectuser': function (pageParams) {
            $('#select-user-popup').show();
            popupPos($('#select-user-popup'));
        },
        'reject': function (pageParams) {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').hide();
            $('#approve-popup').find('.btn-apply').data('action', 'reject');
            popupPos($('#approve-popup'));
        },
        'relay': function (pageParams) {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').show();
            $('#approve-popup').find('.btn-apply').data('action', 'relay');
            popupPos($('#approve-popup'));
        },
        'recovery': function () {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').hide();
            $('#approve-popup').find('.btn-apply').data('action', 'recovery');
            popupPos($('#approve-popup'));
        }
    };
    var actions = {
        //同意
        'agree': function($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.agree,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    model: dataJson
                },
                beforeSend: function() {
                    loader.show();
                },
                success: function(res) {
                    if (res.success) {
                        $popup.trigger('hide');
                        var nextNodes = res.model.nextNodes;
                        if (_.isArray(nextNodes)) {
                            pageParams.nextNodes = nextNodes;
                            popupShow['next'](pageParams);
                        } else {
                            var users = convertUserData(res.model.users);
                            pageParams.isEnd = res.model.isEnd;
                            pageParams.opinion = opinion;
                            pageParams.dataJson = dataJson;
                            if (users.length === 1 || pageParams.isEnd) {
                                pageParams.nextUser = pageParams.isEnd ? '' : users[0].id;
                                actions['selectuser']($('#select-user-popup'), pageParams.opinion, pageParams, formViews);
                            } else {
                                pageParams.selectUserList.setMulti(res.model.userTaskType == '1');
                                pageParams.selectUserList.setData(users);
                                popupShow['selectuser'](pageParams);
                                pageParams.selectUserList.$el.find('.ok-button').css({
                                    'position': 'absolute',
                                    'z-index': 12,
                                    'right': 10,
                                    'top': 10,
                                    'color': '#3386c0'
                                });
                            }
                        }
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function() {
                    alert('提交出错');
                },
                complete: function() {
                    loader.hide();
                }
            });
        },
        'next': function($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);
            var data = {};
            if (_.isArray(opinion)) {
                _.each(opinion, function(field) {
                    data[field.name] = field.value;
                });
            }
            $.ajax({
                url: urls.next,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: data.taskId,
                    nextTaskDefId: data.nextTaskDefId
                },
                beforeSend: function() {
                    loader.show();
                },
                success: function(res) {
                    if (res.success) {
                        $popup.trigger('hide');
                        var model = _.extend({
                            isEnd: false
                        }, res.model);
                        var users = convertUserData(model.users);
                        pageParams.isEnd = model.isEnd;
                        pageParams.opinion = opinion;
                        pageParams.dataJson = dataJson;
                        if (users.length === 1 || pageParams.isEnd) {
                            pageParams.nextUser = pageParams.isEnd ? '' : users[0].id;
                            actions['selectuser']($('#select-user-popup'), pageParams.opinion, pageParams, formViews);
                        } else {
                            pageParams.selectUserList.setMulti(model.userTaskType == '1');
                            pageParams.selectUserList.setData(users);
                            pageParams.selectUserList.$el.find('.ok-button').css({
                                'position': 'absolute',
                                'z-index': 12,
                                'right': 10,
                                'top': 10,
                                'color': '#3386c0'
                            });
                            popupShow['selectuser'](pageParams);
                        }
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function() {
                    alert('提交出错');
                },
                complete: function() {
                    loader.hide();
                }
            });
        },
        //选择办理人(同意的第二步)
        'selectuser': function($popup, opinion, pageParams, formViews) {
            if (pageParams.nextUser === '' && !pageParams.isEnd) {
                alert('请选择办理人！');
                return;
            }

            $.ajax({
                url: urls.selectUser,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    nextAssignee: pageParams.nextUser,
                    thirdDealId: pageParams.thirdId
                        //model: dataJson
                },
                beforeSend: function() {
                    loader.show();
                },
                success: function(res) {
                    if (res.success) {
                        alert('提交成功！', function() {
                            goBack(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function() {
                    alert('提交出错');
                },
                complete: function() {
                    loader.hide();
                }
            });
        },
        //拒绝
        'reject': function($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.reject,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    model: dataJson,
                    thirdDealId: pageParams.thirdId
                },
                beforeSend: function() {
                    loader.show();
                },
                success: function(res) {
                    if (res.success) {
                        alert('提交成功！', function() {
                            goBack(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function() {
                    alert('提交出错');
                },
                complete: function() {
                    loader.hide();
                }
            });
        },
        //转交
        'relay': function($popup, opinion, pageParams, formViews) {
            var userId = $popup.find('input[name=userid]').val();
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.relay,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    userId: userId,
                    opinion: opinion,
                    model: dataJson,
                    thirdDealId: pageParams.thirdId
                },
                beforeSend: function() {
                    loader.show();
                },
                success: function(res) {
                    if (res.success) {
                        alert('提交成功！', function() {
                            goBack(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function() {
                    alert('提交出错');
                },
                complete: function() {
                    loader.hide();
                }
            });
        },
        //驳回(到上一步)
        'recovery': function($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.recovery,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    thirdDealId: pageParams.thirdId,
                    model: dataJson
                },
                beforeSend: function() {
                    loader.show();
                },
                success: function(res) {
                    if (res.success) {
                        alert('提交成功！', function() {
                            goBack(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function() {
                    alert('提交出错');
                },
                complete: function() {
                    loader.hide();
                }
            });
        },
    };

    var initSelectUser = function (pageParams, formViews) {
        var $popup = $('#select-user-popup');
        var selectUserList = pageParams.selectUserList;
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
            pageParams.nextUser = $popup.find('[role="selected-user"]').val();
            if (selectUserList.multi) {
                pageParams.nextUser = JSON.stringify(pageParams.nextUser.split(','));
            }
            if (pageParams.nextUser.length === 0 || pageParams.nextUser === '[]') {
                alert('请选择办理人！');
                return;
            }

            actions['selectuser']($popup, pageParams.opinion, pageParams, formViews);
        });
    };
    var initPopup = function (pageParams, formViews) {
        var $popup = $('#approve-popup');
        $popup.find('.btn-apply').on('click', function() {
            var action = $(this).data('action');
            var opinion = $popup.find('textarea').val();
            actions[action]($popup, opinion, pageParams, formViews);
        });

        var empTrigger = $popup.find('input[name=username]'),
            empInput = $popup.find('input[name=userid]');
        var empSelect = userList.employeeSelect();
        empTrigger.on('click', function() {
            empSelect.show();
        });
        empSelect.on('select', function(e, emp) {
            empTrigger.val(emp.userName);
            empInput.val(emp.userId);
        });

        var commonAppTrigger = $popup.find('[data-do="common-app"]');
        var commonAppList = new SelectList({
            multi: false
        });
        commonAppList.setList([{
                text: '同意',
                value: '同意'
            }, {
                text: '已阅',
                value: '已阅'
            }, {
                text: '拒绝',
                value: '拒绝'
            }])
            .triggerFor(commonAppTrigger)
            .on('select', function(e, val) {
                $popup.find('textarea').val(val.text);
                this.hide();
            });

        $popup.find('.btn-cancle').on('click', function() {
            $popup.trigger('hide');
        });
        $popup.on('hide', function() {
            empTrigger.val('');
            empInput.val('');
            $popup.find('textarea').val('');
            $popup.hide();
        });

        var $nextPopup = $('#next-node-popup');
        $nextPopup.find('.btn-cancle').on('click', function() {
            $nextPopup.trigger('hide');
        });
        $nextPopup.find('.btn-apply').on('click', function() {
            var action = $(this).data('action');
            var option = $nextPopup.find(':input').serializeArray();
            actions['next']($nextPopup, option, pageParams, formViews);
        });
        $nextPopup.on('hide', function() {
            $nextPopup.find('.next-nodes').empty();
            $nextPopup.hide();
        });
    };
    var initPage = function (model, pageParams) {
        var $forms = $('.detail-forms');
        var formsSet = createFormsSet(model, pageParams.mode);
        var formsData = toMap(model.fromsData, function (item, index) {
            return item.id + '-' + index.toString();
        });

        var formViews = createFormViews(formsSet, formsData);
        var getAllFormsData = function () {
            return getFormsData(formViews);
        };
        var setAllFormsData = function (paramterDataTo) {
            _.each(formViews, function (view) {
                view.setDataByParamters(paramterDataTo, view.getAttribute('paramterMap'));
            });
        };

        _.each(formViews, function (form, index) {
            var $box = $('<div><h5>' + form.getAttribute('title') + '</h5></div>').addClass('form-box');
            $box.append(form.$el);
            $forms.append($box);
            //给每个form添加taskId,可能会被 事件按钮 控件使用
            form.setAttribute('taskId', pageParams.taskId);
            if (model.paramterDataTo && form.setDataByParamters) {
                form.setDataByParamters(model.paramterDataTo, form.getAttribute('paramterMap'));
            }
            form.setAttribute('getAll', getAllFormsData);
            form.setAttribute('setAll', setAllFormsData);
        });
        //我的已办 只用于显示，不需要渲染按钮
        if (modeStatus[pageParams.mode] !== MODE_APPROVELED) {
            $('.detail-footer').html(buttonsRender({
                buttons: buttonsSet[model.taskConfig.__button]
            }));
            $('.detail-footer').on('click', '.button', function () {
                var action = $(this).data('action');
                popupShow[action](pageParams);
            });
        }

        var historyData = {
            list: convertHistory(model.tasksList)
        };
        $('.detail-history').html(historyRender(historyData)).show();

        var selectUserList = fulllist(false);
        pageParams.selectUserList = selectUserList;
        initSelectUser(pageParams, formViews);
        initPopup(pageParams, formViews);
    };

    var run = function () {
        var taskId = util.getParam('taskId'),
            thirdId = util.getParam('thirdId'),
            mode = util.getParam('type');
        var pageParams = {
            taskId: taskId,
            thirdId: thirdId,
            mode: mode
        };

        $.ajax({
            url: urls.getDetail,
            type: 'get',
            dataType: 'json',
            data: {
                taskId: taskId
            },
            success: function(res) {
                if (res.success) {
                    initPage(res.model, pageParams);
                } else {
                    alert(res.message || '读取流程信息失败！', function() {
                        goBack(pageParams.mode);
                    });
                }
            },
            error: function() {
                alert('发生错误！', function() {
                    goBack(mode);
                });
            }
        });

        $('.back').on('click', function () {
            goBack(pageParams.mode);
        });
    };
    module.exports = {
        run: run
    };
});
