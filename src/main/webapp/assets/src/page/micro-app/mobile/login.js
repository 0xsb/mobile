/*
 * common approval login page (to select company) for mobile client
 */
define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');

    var urls = {
        getCompany: CONTEXT_PATH + '/approvals/showCompany.do',
        selectCompany: CONTEXT_PATH + '/approvals/selectCompany.do',
        indexPage: CONTEXT_PATH + '/moblicApprove/toIndex.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do',
        welfarePage: CONTEXT_PATH + '/moblicApprove/toWelfale.do',
        taskPage: CONTEXT_PATH + '/moblicApprove/toTasks.do'
    };
    var companyRender = template($('#tmpl-company').html());

    var isLoading = false;
    
    var doLogin = function (userId) {
        var gotoUrl, wyyId = 'wyy0001';
        var prttype = util.getParam('prttype');
        switch (prttype) {
            case '1': 
                gotoUrl = urls.dailyPage;
                wyyId = 'wyy0002';
                break;
            case '2':
                gotoUrl = urls.welfarePage;
                wyyId = 'wyy0004';
                break;
            case '3':
                gotoUrl = urls.taskPage;
                wyyId = 'wyy0004';
                break;
            default: 
                gotoUrl = urls.indexPage; 
                wyyId = 'wyy0001';
                break;
        }

        if (isLoading) { return; }

        isLoading = true;
        $.ajax({
            url: urls.selectCompany,
            type: 'post',
            dataType: 'json',
            data: {
                userId: userId,
                wyyId: wyyId
            },
            success: function (res) {
                if (res.success) {
                    location.href = gotoUrl;
                }
                else {
                    util.alert(res.message || '载入公司信息出错！');
                }
            },
            error: function () {
                util.alert('载入公司信息出错！');
            },
            complete: function () {
                isLoading = false;
            }
        });
    };
    var initPage = function (list) {
        if (list.length === 1) {
            doLogin(list[0].userId);
        }
        else {
            $('#company-list').html(companyRender({ list: list }));
            $('#company-list').on('click', 'a', function (e) {
                var userId = $(e.target).data('id');
                doLogin(userId);
            });
        }
    };
    var run = function () {
        $.ajax({
            url: urls.getCompany,
            type: 'get',
            dataType: 'json',
            data: {
                mobile: util.getParam('FromUserTelNum'), //util.getParam('mobile')
            },
            beforeSend: function () {
                loader.show();
            },
            success: function (res) {
                if (res.success) {
                    initPage(res.model);
                }
                else {
                    util.alert(res.message || '读取组织架构信息错误！');
                }
            },
            error: function () {
                util.alert('读取组织架构信息错误！');
            },
            complete: function () {
                loader.hide();
            }
        });
    };

    module.exports = { run: run };
});
