define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    require('jquery-util');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');

    var urls = {
        getList: CONTEXT_PATH + '/approval/getProcessInfoList.do',
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
            item.colorCode = getColor(item.userName);
            item.subName = subname(item.userName);
        });
        return list;
    };

    var listRender = template($('#tmpl-approvals').html());
    var nodataRender = template($('#tmpl-nodata').html());
    var typeMap = {
        'dwsp': { name: '我的待办', type: 'dwsp' },
        'wdfq': { name: '我的发起', type: 'wdfq' },
        'dwyl': { name: '我的待阅', type: 'dwyl' },
        'wysp': { name: '我的已办', type: 'wysp' }
    };
    var defaultSize = 10;

    var getList = function (query, type, callback) {
        $.ajax({
            url: urls.getList,
            type: 'post',
            dataType: 'json',
            data: {
                type: type,
                page: query.pageNo,
                pageSize: query.pageSize,
                term: query.search,
                rnd: (new Date).getTime(),
                wyyId: query.wyyId
            },
            beforeSend: function () {
                loader.show();
            },
            success: function (res) {
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    callback(res.model, query, type);
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
    var renderList = function (model, query, type) {
        if (model.total === 0) {
            $('.workflow-list').html(nodataRender({ type: type, wyyId: query.wyyId }));
            return;
        }

        var list = itemProcess(model.list);
        var typeObj = typeMap[type];

        //list.sort(function (a, b) { return a.startTime > b.startTime ? -1 : 1; });
        var addItems = $(listRender({ list: list, type: typeObj.type, wyyId: query.wyyId }));
        $('.workflow-list').append(addItems);

        query.pageNo = model.pageNum;
        query.pageCount = model.pages;

        query.pageNo < query.pageCount ? $('.more-bar').show() : $('.more-bar').hide();
        //$('.page-no').text(query.pageNo + '/' + query.pageCount);
    };

    var initPage = function (type) {
        var typeObj = typeMap[type];
        $('.list-title').text(typeObj.name);

        var query = {
            pageNo: 1,
            pageCount: 1,
            pageSize: defaultSize,
            search: '',
            wyyId: util.getParam('wyyId')
        };
        /*$('.page-button').on('click', function (e) {
            var btn = $(this);
            if (btn.is('.prev')) {
                if (query.pageNo <= 1) return;
                query.pageNo--;
                getList(query, type, renderList);
            }
            else {
                if (query.pageNo >= page.pageCount) return;
                query.pageNo++;
                getList(query, type, renderList);
            }
        });*/
        $('.more-bar').on('click', 'a', function (e) {
            query.pageNo++;
            getList(query, type, renderList);
        });
        $('.list-search').on('submit', function (e) {
            e.preventDefault();

            $('.workflow-list').empty();
            query.search = $('.search').val();
            query.pageNo = 1;
            getList(query, type, renderList);
        });

        getList(query, type, renderList);

        $('.back').on('click', goBack);
    };
    var run = function () {
        var type = util.getParam('type');
        var wyyId = util.getParam('wyyId');
        $('.header').addClass(wyyId);
        initPage(type);
    };

    module.exports = { run: run };
});
