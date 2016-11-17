define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    require('jquery-util');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');

    var urls = {
        getList: CONTEXT_PATH + '/process/getSupversionList.do',
        indexPage: CONTEXT_PATH + '/moblicApprove/toIndex.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do',
        taskPage: CONTEXT_PATH + '/moblicApprove/toTasks.do',
        welfarePage: CONTEXT_PATH + '/moblicApprove/toWelfale.do'
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

    var getColor = function (str) {
        if (!str || str.length === 0) { return '#dddddd'; }
        var code = 0, temp = 0, codeStr = '', i = 0;
        for (i = 0; i < str.length; i++) {
            code += str.charCodeAt(i);
        }
        for (i = 0; i < 3; i++) {
            temp = code % 192;
            codeStr += ('00' + temp.toString(16)).slice(-2);
            code = Math.round(code * temp / 127);
        }
        return '#' + codeStr;
    };
    var subname = function (name, length) {
        name = name || '';
        if (!length || length <= 0) {
            length = 2;
        }
        return name.slice(-1 * length);
    };
    var itemProcess = function (list) {
        _.each(list, function (item) {
            var majorUser = JSON.parse(item.majorUser)[0];
            item.colorCode = getColor(majorUser.userName);
            item.subName = subname(majorUser.userName);
        });
        return list;
    };

    var listRender = template($('#tmpl-tasks').html());
    var nodataRender = template($('#tmpl-nodata').html());

    var getList = function (query, callback) {
        $.ajax({
            url: urls.getList,
            type: 'post',
            dataType: 'json',
            data: {
                rnd: (new Date).getTime(),
                wyyId: query.wyyId
            },
            beforeSend: function () {
                loader.show();
            },
            success: function (res) {
                if (res.success) {
                    callback(res.model, query);
                }
                else {
                    util.alert(res.message || '获取公示列表失败！');
                }
            },
            error: function () {
                util.alert('获取公示列表出错！');
            },
            complete: function () {
                loader.hide();
            }
        });
    };
    var renderList = function (model, query) {
        var list = itemProcess(model);
        list.sort(function (a, b) { return a.endTime > b.endTime ? 1 : -1; });

        var addItems = listRender({ list: list, wyyId: query.wyyId });
        $('.workflow-list').html(addItems);
    };

    var initPage = function (type) {
        var query = {
            wyyId: util.getParam('wyyId')
        };
        getList(query, renderList);

        $('.back').on('click', goBack);
    };
    var run = function () {
        var wyyId = util.getParam('wyyId');
        $('.header').addClass(wyyId);
        initPage();
    };

    module.exports = { run: run };
});
