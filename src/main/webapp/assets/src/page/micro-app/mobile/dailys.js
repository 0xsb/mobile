/*
 * common approval index page for mobile client
 */
define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('component/mobile/loader');
    var util = require('component/mobile/util');

    var urls = {
        getInfoCount: CONTEXT_PATH + '/approval/getInfoCount.do',
        getReadCount: CONTEXT_PATH + '/approvals/readCount.do',
        loginPage: CONTEXT_PATH + '/moblicApprove/toOldLogin.do',
        getAntuarity: CONTEXT_PATH + '/approval/getAntuarity.do'
    };
    var getWyyId = function () {
        var href = location.href;
        var hrefString = href.slice(href.indexOf('to'));
        if (hrefString == 'toTasks.do') {
            return 'wyy0003';
        } else if (hrefString == 'toDailys.do') {
            return 'wyy0002';
        } else if (hrefString == 'toWelfale.do') {
            return 'wyy0004';
        } else {
            return 'wyy0001';
        }
    };
    var getprttype = function (wyyId) {
        prttype = 0;
        switch (wyyId) {
            case 'wyy0001': prttype = 0; break;
            case 'wyy0002': prttype = 1; break;
            case 'wyy0004': prttype = 2; break;
            case 'wyy0003': prttype = 3; break;
            default: prttype = 0; break;
        }
        return prttype;
    };
    var wyyId;

    var gridRender = template($('#tmpl-approvals').html());
    var getAntuarities = function () {
        $.ajax({
            url: urls.getAntuarity,
            type: 'post',
            dataType: 'json',
            data: {
                rnd: (new Date).getTime(),   //send a random param to clean cache of broswer
                wyyId: wyyId
            },
            beforeSend: function () {
                loader.show();
            },
            success: function (res) {
                if (res.success) {
                    //maybe model's fields are JSON strings
                    //so that's need to parse them to objects
                    if (typeof res.model.top === 'string') res.model.top = JSON.parse(res.model.top);
                    if (typeof res.model.default === 'string') res.model.default = JSON.parse(res.model.default);
                    if (res.model.key == '0') {
                        $('.myReview').hide();
                        $('.myApproval').css('width', '50%');
                    }

                    var list = res.model.top.concat(res.model.default);
                    $('.grid-panel').html(gridRender({ list: list, wyyId: wyyId }));
                }
                else {
                    util.alert(res.message || '获取审批列表出错！');
                }
            },
            error: function () {
                util.alert('获取审批列表出错！');
            },
            complete: function () {
                loader.hide();
            }
        });
    };
    var refreshInfoCount = function () {
        $.ajax({
            url: urls.getInfoCount,
            type: 'get',
            dataType: 'json',
            data: {
                rnd: (new Date).getTime(),
                wyyId: wyyId
            },
            success: function (res) {
                if (res.success) {
                    if (res.model > 0) {
                        //this redcircle just could hold
                        $('.approval-count').show().text(res.model > 99 ? 99 : res.model);
                    }
                    else {
                        $('.approval-count').hide();
                    }
                }
                else {
                    //do nothing
                }
            }
        });
    };
    var refreshReadCount = function () {
        $.ajax({
            url: urls.getReadCount,
            type: 'get',
            dataType: 'json',
            data: {
                rnd: (new Date).getTime(),
                wyyId: wyyId
            },
            success: function (res) {
                if (res.success) {
                    if (res.model > 0) {
                        //this redcircle just could hold
                        $('.read-count').show().text(res.model > 99 ? 99 : res.model);
                    }
                    else {
                        $('.read-count').hide();
                    }
                }
                else {
                    //do nothing
                }
            }
        });
    };

    var run = function () {
        wyyId = getWyyId();

        getAntuarities();
        refreshInfoCount();
        refreshReadCount();

        $('.page-title').text(COMPANY_NAME);
        $('a.switch').on('click', function () {
            location.href = urls.loginPage + '?FromUserTelNum=' + MOBILEPHONE + '&prttype=' + getprttype(wyyId);
        });
    };

    module.exports = { run: run };
});
