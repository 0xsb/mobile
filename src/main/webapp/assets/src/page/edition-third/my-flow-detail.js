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
        getDetail: CONTEXT_PATH + '/process/processStartedByMe.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do',
        recovery: CONTEXT_PATH + '/process/cancel.do'
    };

    var goBack = function () {
        var wyyId = util.getParam('wyyId');
        location.href = urls.listPage + '?type=wdfq&wyyId=' + wyyId;
        //histroy.back();
    };
    var toMap = function (list, key) {
        var newMap = {};
        _.each(list, function (item) {
            newMap[item[key]] = item;
        });
        return newMap;
    };

    var createFormsData = function (model) {
        var forms = [];
        forms = forms.concat(model.forms);
        _.each(forms, function (item) {
            item.writeble = false;
        });
        return forms;
    };
    var createFormViews = function (formsSet, formsData, pageParams) {
        var formsList = [];
        _.each(formsSet, function (item, key) {
            var $item, formView, formData;
            formData = formsData[item.formID];
            if (!item.writeble) {
                $item = $('<div></div>').addClass('detail-data');
                formView = detailInit($item, formData.data, item.widgets);
                formView.setAttribute('paramters', formData.paramters);
            } else {
                $item = $('<form></form>').addClass('detail-form');
                formView = formInit($item, item.widgets);
                formView.setAttribute('taskId', pageParams.taskId);
                if (formData) {
                    formView.setData(formData.data);
                }
            }

            formView.setAttribute('id', item.formID);
            formView.setAttribute('title', formData.title); //item.formName);
            formsList.push(formView);
        });

        return formsList;
    };

    var statusMap = {
        '100': 'processing',    //处理中
        '101': 'draft',         //草稿
        '200': 'agree',         //同意
        '201': 'complete',      //已完成
        '300': 'revoke',        //撤销
        '400': 'reassign',      //转交
        '500': 'reject',        //拒绝
        '600': 'recovery'       //回收
    };
    var getFormsData = function (formViews) {
        var formsData = [], paramters = {};
        _.each(formViews, function (formView) {
            var itemData = {}, itemParam;
            itemData.id = formView.getAttribute('id');
            itemData.data = formView.getData();
            formsData.push(itemData);

            if (formView.getParamters) {
                itemParam = formView.getParamters(itemData.data);   //可编辑表单
            }
            else {
                itemParam = formView.getAttribute('paramters'); //详情
            }
            _.extend(paramters, itemParam);
        });
        return {
            formData: formsData,
            paramterData: paramters
        };
    };
    var convertUserData = function (list) {
        var newlist = _.map(list, function (item) {
            return { id: item.userId, text: item.userName };
        });
        return newlist;
    };
    var convertHistory = function (history) {
        return _.map(history, function (item, index) {
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

    var doRecovery = function (taskId) {
        $.ajax({
            url: urls.recovery,
            type: 'post',
            dataType: 'json',
            data: {
                taskId: taskId,
                opinion: ''
            },
            success: function (res) {
                if (res.success) {
                    alert('撤销成功!', function () {
                        goBack();
                    });
                }
                else {
                    alert(res.message || '撤销失败!');
                }
            },
            error: function () {
                alert('发生错误!');
            }
        });
    };

    var historyRender = template($('#tmpl-history').html());
    var initPage = function (model, pageParams) {
        var $forms = $('.detail-forms');
        var taskForms = model.tasksFormsData[0];
        var formsSet = createFormsData(taskForms);
        var formsData = toMap(taskForms.formsData, 'id');

        var formViews = createFormViews(formsSet, formsData, pageParams);
        _.each(formViews, function (form, index) {
            var $box = $('<div><h5>' + form.getAttribute('title') + '</h5></div>').addClass('form-box');
            $box.append(form.$el);
            $forms.append($box);
        });

        var historyData = { list: convertHistory(model.tasksList) };
        $('.detail-history').html(historyRender(historyData)).show();

        //if (model.tasksList.length > 1) {
        //    $('.detail-footer').hide();
        //} else {
            //20150906 修改 根据客户方需求，前端页面撤销按钮常驻，判断由后端处理
            $('.detail-footer').on('click', '[data-action="recovery"]', function () {
                util.confirm('确定要撤销吗?', function (ok) {
                    if (ok) {
                        doRecovery(pageParams.taskId);
                    }
                });
            });
        //}
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
            success: function (res) {
                if (res.success) {
                    initPage(res.model, pageParams);
                }
                else {
                    alert(res.message || '读取流程信息失败！', function () {
                        goBack();
                    });
                }
            },
            error: function () {
                alert('发生错误！', function () {
                    goBack();
                });
            }
        });

        $('.back').on('click', function () {
            goBack();
        });
    };
    module.exports = {
        run: run
    };
});
