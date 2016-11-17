define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('component/mobile/loader');
    var util = require('component/mobile/util');

    var urls = {
        getList: CONTEXT_PATH + '/approvals/maiList.do',
        stop: CONTEXT_PATH + '/microApp/customForm/stopApprovel.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do'
    };

    var goBack = function () {
        //history.back();
        location.href = urls.dailyPage;
    };

    var listRender = template($('#tmpl-list').html());
    var initPage = function (model) {
        $('.list-panel').html(listRender({list: model}));

        $('.list-panel').on('click', '.item-action.delete', function (e) {
            var $el = $(this);
            var id = $el.data('id');
            util.confirm('确认要删除吗？', function (ok) {
                if (!ok) { return; }
                $.ajax({
                    url: urls.stop,
                    type: 'post',
                    dataType: 'json',
                    data: {
                        id: id
                    },
                    success: function (res) {
                        if (res.success) {
                            $el.parent().remove();
                        }
                        else {
                            util.alert(res.message || '操作失败');
                        }
                    },
                    error: function () {
                        util.alert('发生错误，操作失败');
                    }
                });
            });
        });

        $('.back').on('click', goBack);
    };
    var run = function () {
        //var wyyId = util.getParam('wyyId');
        var wyyId = 'wyy0002';  //写死，仅用于 日志日报
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
                    initPage(res.model);
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
