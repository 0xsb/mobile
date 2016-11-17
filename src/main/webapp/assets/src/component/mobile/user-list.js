/*
    user and department select list
*/
define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('./loader');
    var util = require('./util');

    var depUrl = CONTEXT_PATH + '/users/getAllDept.do';
    var empUrl = CONTEXT_PATH + '/users/getUserByDeptId.do';

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
    var fuzzleSearcher = function (query) {
        var queryList = query.replace(/^\s+|\s+$/g, '').split(/\s+/);
        var regex = '.*';
        for (var i = 0; i < queryList.length; i++) {
            regex += '(' + queryList[i] + ')+.*';
        }
        return new RegExp(regex, 'ig');
    };

    var depTemplate = '<# _.each(depts, function (dep) { #>\
            <li data-key="{{dep.orgId}}">\
                <span class="name">{{dep.orgName}}</span>\
            </li>\
        <# }) #>';
    var depRender = template(depTemplate);
    var listTemplate = '<# _.each(approvers, function (item) { #>\
            <li data-key="{{item.id}}">\
                <div class="avatar" style="background:#{{item.colorCode}}">\
                    {{item.subName}}\
                </div>\
                <span class="name">{{item.userName}}</span>\
            </li>\
        <# }) #>';
    var listRender = template(listTemplate);
    var breadcrumbTemplate = '<# _.each(stack, function (item, index) { #>\
        <span>{{item.name}}</span>\
    <# }) #>';
    var breadcrumbRender = template(breadcrumbTemplate);

    var DepartmentBuffer = null,
        DepartmentMapBuffer = {},
        EmployeeBuffer = {
            'emp$root': []
        };
    var buildDepart= function (data) {
        var db = {};
        db['dep$root'] = [];
        DepartmentMapBuffer['dep$root'] = { orgName: '全部', orgId: '$root' };
        _.each(data, function (item) {
            var depItem = { orgName: item.orgName, orgId: item.id, previousId: item.previousId, showindex: item.showindex };
            DepartmentMapBuffer['dep' + depItem.orgId] = depItem;
            if (!item.previousId || item.previousId === '') {
                db['dep$root'].push(depItem);
            }
            else {
                if (!db['dep' + item.previousId]) {
                    db['dep' + item.previousId] = [];
                }
                db['dep' + item.previousId].push(depItem);
            }
        });
        _.each(db, function (list, key) {
            list.sort(function (a,b) { return a.showindex - b.showindex; });
        });
        return db;
    };
    var getDepartment = function (parentId, cb) {
        if (DepartmentBuffer) { cb && cb(DepartmentBuffer['dep' + parentId]); }
        else {
            $.ajax({
                url: depUrl,
                type: 'get',
                dataType: 'json',
                data: {
                    parentId: parentId,
                    companyId: util.constant.companyId
                },
                beforeSend: function () {
                    loader.show();
                },
                complete: function () {
                    loader.hide();
                },
                success: function (res) {
                    if (res.success) {
                        DepartmentBuffer = buildDepart(res.model);
                        cb && cb(DepartmentBuffer['dep' + parentId]);
                    }
                }
            });
        }
    };
    var getDepartmentInfo = function (deptId) {
        return DepartmentMapBuffer['dep' + deptId];
    };
    var getEmployees = function (orgId, cb) {
        if (EmployeeBuffer['emp' + orgId]) {
            cb && cb(EmployeeBuffer['emp' + orgId]);
        }
        else {
            $.ajax({
                url: empUrl,
                type: 'get',
                dataType: 'json',
                data: {
                    deptId: orgId,
                    companyId: util.constant.companyId
                },
                beforeSend: function () {
                    loader.show();
                },
                complete: function () {
                    loader.hide();
                },
                success: function (res) {
                    if (res.success) {
                        if (typeof res.model === 'string') res.model = [];
                        if (res.model.length > 0) res.model.sort(function (a,b) { return a.showindex - b.showindex; });
                        EmployeeBuffer['emp' + orgId] = res.model;
                        cb && cb(EmployeeBuffer['emp' + orgId]);
                    }
                }
            });
        }
    };
    var getDepAndEmp = function (depId, cb) {
        var depCache, empCache,
            deploaded = false, emploaded = false,
            isCallBack = false;
        getDepartment(depId, function (res) {
            deploaded = true;
            depCache = res;
            if (emploaded && !isCallBack) {
                isCallBack = true;
                cb && cb(depCache, empCache);
            }
        });
        getEmployees(depId, function (res) {
            emploaded = true;
            empCache = res;
            if (deploaded && !isCallBack) {
                isCallBack = true;
                cb && cb(depCache, empCache);
            }
        });
    };
    var toMap = function (key, list, parser) {
        var map = {};
        _.each(list, function (item) {
            map[item[key]] = parser ? parser(item) : item;
        });
        return map;
    };

    var createDepSelect = function () {
        var $depPopup = $('<div class="mask"><div class="full-list">\
            <div class="list-header"><a href="javascript:" class="back"><i class="iconfont icon-xiangzuo1"></i></a><span>部门</span></div>\
            <div class="breadcrumb"></div>\
            <ul></ul></div></div>').hide().appendTo(document.body);
        return $depPopup;
    };
    var createEmpSelest = function () {
        var $empPopup = $('<div class="mask"><div class="full-list">\
            <div class="list-header"><a href="javascript:" class="back"><i class="iconfont icon-xiangzuo1"></i></a><span>人员</span></div>\
            <div class="breadcrumb"></div>\
            <div class="emp-part">\
                <div class="list-search"><input class="search" type="text" placeholder="搜索"></div>\
                <ul class="emp-list"></ul>\
            </div>\
            <ul class="dept-list"></ul>\
            </div></div>').hide().appendTo(document.body);
        return $empPopup;
    };

    var getLevelDeparts = function (idList, cb, stack) {
        if (!stack) { stack = []; }
        if (idList.length === 0) {
            cb(stack); return;
        }

        var thisId = idList.shift();
        getDepartment(thisId, function (depts) {
            stack.push({ name: getDepartmentInfo(thisId).orgName, depts: depts });
            getLevelDeparts(idList, cb, stack);
        });
    };
    var getLevelDepAndEmp = function (idList, cb, stack) {
        if (!stack) { stack = []; }
        if (idList.length === 0) {
            cb(stack); return;
        }

        var thisId = idList.shift();
        getDepAndEmp(thisId, function (depts, emps) {
            stack.push({ name: getDepartmentInfo(thisId).orgName, depts: depts, emps: emps });
            getLevelDepAndEmp(idList, cb, stack);
        });
    };

    var getDepartmentSelect = function () {
        var listStack = [], mapCache = {};

        var $depPopup = createDepSelect();
        var $breadcrumb = $depPopup.find('.breadcrumb');

        var render = function () {
            mapCache = toMap('orgId' ,listStack[listStack.length - 1].depts);
            $depPopup.find('ul').html(depRender({ depts: listStack[listStack.length - 1].depts }));

            $breadcrumb.html(breadcrumbRender({stack: listStack}));
        };
        var pushStack = function (stackObj) {
            listStack.push(stackObj);
            render();
        };
        var popStack = function () {
            if (listStack.length <= 1) {
                $depPopup.hide();
                return;
            }
            listStack.pop();
            render();
        };
        $breadcrumb.on('click', 'span', function (e) {
            var index = $(e.target).index();
            if (index === $breadcrumb.children().length - 1) { return; }
            lenStack(index + 2);
        });

        var orgShow = $depPopup.show;
        $depPopup.show = function (defaultDeptId) {
            while (listStack.length > 0) { listStack.pop(); }
            getDepartment('$root', function (list) {
                pushStack({ name: '全部', depts: list });
                orgShow.apply($depPopup);
            });
        };

        $depPopup.on('click', '.back', function () {
            popStack();
        }).on('click', 'li', function () {
            var depkey = $(this).data('key'),
                depName = $(this).text();
            getDepartment(depkey, function (list) {
                if (!list) {
                    var hide = $depPopup.triggerHandler('select', mapCache[depkey]);
                    if (hide !== false) { $depPopup.hide(); }
                }
                else {
                    pushStack({ name: depName,  depts: list});
                }
            });
        });
        $breadcrumb.on('click', 'span', function (e) {
            var index = $(e.target).index();
            if (index === $breadcrumb.children().length - 1) { return; }
            lenStack(index + 2);
        });

        return $depPopup;
    };
    var getEmployeeSelect = function () {
        var empCache = {}, listCache = [];
        var listStack = [];

        var $empPopup = createEmpSelest();
        var $empPart = $empPopup.find('.emp-part'),
            $empList = $empPopup.find('.emp-list'),
            $depList = $empPopup.find('.dept-list');
        var $breadcrumb = $empPopup.find('.breadcrumb');

        var render = function () {
            var stackObj = listStack[listStack.length - 1];

            if (!stackObj.depts) {
                $depList.hide();
            } else {
                $depList.html(depRender({ depts: stackObj.depts })).show();
            }

            if (!stackObj.emps || (stackObj.depts && stackObj.emps.length === 0)) {
                $empPart.hide();
            } else {
                listCache = stackObj.emps;
                empCache = toMap('id', stackObj.emps, function (item) {
                    item.userId = item.id;
                    item.subName = subname(item.userName);
                    item.colorCode = getColor(item.userName);
                    return item;
                });
                $empPart.show().find('.search').val('');
                $empList.html(listRender({ approvers: stackObj.emps }));
            }

            $breadcrumb.html(breadcrumbRender({stack: listStack}));
        };
        var pushStack = function (stackObj) {
            listStack.push(stackObj);
            render();
        };
        var popStack = function () {
            if (listStack.length <= 1) {
                $empPopup.hide();
                return;
            }

            listStack.pop();
            render();
        };
        var lenStack = function (len) {
            while (listStack.length > len) { listStack.pop(); }
            render();
        };
        var replaceStack = function (stack) {
            listStack = stack;
            render();
        };

        var orgShow = $empPopup.show;
        $empPopup.show = function (defaultDeptId) {
            while (listStack.length > 0) { listStack.pop(); }

            if (!defaultDeptId) {
                getDepartment('$root', function (list) {
                    pushStack({ name: '全部', depts: list, emps: null });
                    orgShow.apply($empPopup);
                });
            }
            else {
                var posList = [];
                getDepartment('$root', function (list) {
                    var partId = defaultDeptId;
                    while (partId) {
                        //if parent department not alive, clear posList to show root list
                        if (!DepartmentMapBuffer['dep' + partId]) {
                            posList = [];
                            break;
                        }
                        posList.unshift(partId);
                        partId = DepartmentMapBuffer['dep' + partId].previousId;
                    }

                    posList.unshift('$root');
                    getLevelDepAndEmp(posList, function (stack) {
                        replaceStack(stack);
                        orgShow.apply($empPopup);
                    });
                });
            }
        };

        $depList.on('click', 'li', function () {
            var depkey = $(this).data('key'),
                depName = $(this).text();
            getDepAndEmp(depkey, function (depts, emps) {
                pushStack({ name: depName, depts: depts, emps: emps });
            });
        });
        $empList.on('click', 'li', function () {
            var key = $(this).data('key');
            var emp = empCache[key];

            var hide = $empPopup.triggerHandler('select', emp);
            if (hide !== false) { $empPopup.hide(); }
        });
        $breadcrumb.on('click', 'span', function (e) {
            var index = $(e.target).index();
            if (index === $breadcrumb.children().length - 1) { return; }
            lenStack(index + 1);
        });

        $empPopup.on('click', '.back', function () {
            //popStack();
            $empPopup.hide();
        });

        var inputTimer = null;
        $empPart.find('.search').on('input', function (e) {
            if (inputTimer) { clearTimeout(inputTimer); }
            var _this = this;
            inputTimer = setTimeout(function () {
                var searchVal = $(_this).val();
                var query = fuzzleSearcher(searchVal);

                var filterList = _.filter(listCache, function (item) {
                    query.lastIndex = 0;
                    return query.test(item.userName) || query.test(item.mobile);
                });
                $empList.html(listRender({ approvers: filterList }));

                inputTimer = null;
            }, 333);
        });

        return $empPopup;
    };

    module.exports = {
        departmentSelect: getDepartmentSelect,
        employeeSelect: getEmployeeSelect
    };
});
