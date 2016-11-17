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
        getList: CONTEXT_PATH + '/approvals/maiList.do',
        collection: CONTEXT_PATH + '/approvals/collection.do',

        indexPage: CONTEXT_PATH + '/moblicApprove/toIndex.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do',
        taskPage: CONTEXT_PATH + '/moblicApprove/toTasks.do',
        welfarePage: CONTEXT_PATH + '/moblicApprove/toWelfale.do',
    };

    var getIndexPage = function () {
        var url = '';
        var wyyId = util.getParam('wyyId');
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
        var backUrl = getIndexPage();
        location.href = backUrl;
    };

    var listRender = template($('#tmpl-list').html());
    var initPage = function (model, wyyId) {
        $('.list-panel').html(listRender({list: model, wyyId: wyyId}));

        $('.list-panel').on('click', '.link-btn.collection', function (e) {
            var elem = $(e.target);
            var id = elem.data('id');

            $.ajax({
                url: urls.collection,
                type: 'post',
                dataType: 'json',
                data: {
                    approvalTypeId: id
                },
                success: function (res) {
                    if (res.success) {
                        res.model === '1' ?
                            elem.addClass('active').text('取消') :
                            elem.removeClass('active').text('收藏');
                    }
                    else {
                        util.alert(res.message || '发生错误，操作失败');
                    }
                },
                error: function () {
                    util.alert('发生错误，操作失败');
                }
            });
        });

        $('.back').on('click', goBack);
    };
    var run = function () {
        var wyyId = util.getParam('wyyId');
        $('.header').addClass(wyyId);
        $.ajax({
            url: urls.getList,
            type: 'get',
            dataType: 'json',
            data: {
                rnd: (new Date).getTime(),
                wyyid: wyyId
            },
            beforeSend: function () {
                loader.show();
            },
            success: function (res) {
                if (res.success) {
                    initPage(res.model, wyyId);
                }
                else {
                    util.alert(res.message || '读取分类列表出错！');
                }
            },
            error: function () {
                util.alert('读取分类列表出错！');
            },
            complete: function () {
                loader.hide();
            }
        });
    };

    module.exports = { run: run };
});
