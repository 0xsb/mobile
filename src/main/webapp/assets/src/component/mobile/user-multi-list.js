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
    var toMap = function (key, list, parser) {
        var map = {};
        _.each(list, function (item) {
            map[item[key]] = parser ? parser(item) : item;
        });
        return map;
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
    var listTemplate = '<# _.each(approvers, function (item) { #>\
            <li data-key="{{item.id}}">\
                <div class="avatar" style="background:#{{item.colorCode}}">\
                    {{item.subName}}\
                </div>\
                <span class="name">{{item.userName}}</span>\
            </li>\
        <# }) #>';
    var breadcrumbTemplate = '<# _.each(stack, function (item, index) { #>\
        <span>{{item.name}}</span>\
    <# }) #>';
    var popupTemplate = '<div class="mask"><div class="full-list multi-user-select">\
            <div class="list-header"><a href="javascript:" class="back"><i class="iconfont icon-xiangzuo1"></i></a><span>人员</span></div>\
            <div class="selected-emps clearfix"></div>\
            <div class="breadcrumb"></div>\
            <div class="emp-part">\
                <div class="list-search"><input class="search" type="text" placeholder="搜索"></div>\
                <ul class="emp-list"></ul>\
            </div>\
            <ul class="dept-list"></ul>\
            <a href="javascript:" class="ok-button">确定</a>\
        </div></div>';
    var selectItemTemplate = '<div class="approver-item" data-key={{userId}}>\
        <div class="avatar" style="background:#{{colorCode}}">\
            {{subName}}\
        </div><span class="name">{{userName}}</span></div>';

    var depRender = template(depTemplate);
    var listRender = template(listTemplate);
    var breadcrumbRender = template(breadcrumbTemplate);
    var selectItemRender = template(selectItemTemplate);

    var UserMultiList = function (options) {
        this.init(options);
    };
    UserMultiList.prototype = {
        init: function (options) {
            this.multi = options.multi;

            this.$popup = $(popupTemplate).hide().appendTo(document.body);
            this.$empPart = this.$popup.find('.emp-part'),
            this.$empList = this.$popup.find('.emp-list'),
            this.$depList = this.$popup.find('.dept-list');
            this.$seletedEmps = this.$popup.find('.selected-emps');
            this.$breadcrumb = this.$popup.find('.breadcrumb');
            this.$okButton = this.$popup.find('.ok-button');

            this.bcStack = [];
            this.empCache = {};
            this.listCache = [];

            this.initEvents();
            this.initSearch();

            this.eventMap = {};
        },
        initEvents: function () {
            var _this = this;
            this.$depList.on('click', 'li', function () {
                var depkey = $(this).data('key'),
                    depName = $(this).text();
                getDepAndEmp(depkey, function (depts, emps) {
                    _this.pushStack({ name: depName, depts: depts, emps: emps });
                });
            });
            this.$breadcrumb.on('click', 'span', function (e) {
                var index = $(e.target).index();
                if (index === _this.$breadcrumb.children().length - 1) { return; }
                _this.subStack(index + 1);
            });

            this.$popup.on('click', '.back', function () {
                //popStack();
                _this.$popup.hide();
            });
            this.$empList.on('click', 'li', function () {
                var key = $(this).data('key');
                var emp = _this.empCache[key];
                if (_this.multi) {
                    if (_this.$seletedEmps.find('.approver-item[data-key=' + key + ']').length > 0) {
                        util.alert('不能选择重复的人员！');
                        return false;
                    }
                    _this.addSelectedEmp(emp);
                }
                else {
                    _this.setSelectedEmps([emp]);
                }
            });
            this.$okButton.on('click', function () {
                var selectedEmps = [];
                _this.$seletedEmps.children().each(function () {
                    var key = $(this).data('key');
                    selectedEmps.push(_this.empCache[key]);
                });

                _this.trigger('select', [selectedEmps]);
                _this.hide();
            });
        },
        initSearch: function () {
            var inputTimer = null;
            var _this = this;
            this.$empPart.find('.search').on('input', function (e) {
                if (inputTimer) { clearTimeout(inputTimer); }
                var $el = $(this);
                inputTimer = setTimeout(function () {
                    var searchVal = $el.val();
                    var query = fuzzleSearcher(searchVal);

                    var filterList = _.filter(_this.listCache, function (item) {
                        query.lastIndex = 0;
                        return query.test(item.userName) || query.test(item.mobile);
                    });
                    _this.$empList.html(listRender({ approvers: filterList }));

                    inputTimer = null;
                }, 333);
            });
        },
        pushStack: function (stackObj) {
            this.bcStack.push(stackObj);
            this.render();
        },
        popStack: function () {
            if (this.bcStack.length <= 1) {
                this.$empPopup.hide();
                return;
            }

            this.bcStack.pop();
            this.render();
        },
        subStack: function (length) {
            while (this.bcStack.length > length) { this.bcStack.pop(); }
            this.render();
        },
        replaceStack: function (stack) {
            this.bcStack = stack;
            this.render();
        },
        render: function () {
            var stackObj = this.bcStack[this.bcStack.length - 1];

            if (!stackObj.depts) {
                this.$depList.hide();
            } else {
                this.$depList.html(depRender({ depts: stackObj.depts })).show();
            }

            if (!stackObj.emps || (stackObj.depts && stackObj.emps.length === 0)) {
                this.$empPart.hide();
            } else {
                this.listCache = stackObj.emps;
                this.empCache = _.extend(this.empCache, toMap('id', stackObj.emps, function (item) {
                    item.userId = item.id;
                    item.subName = item.userName.slice(-2);
                    item.colorCode = util.transColorCode(item.userName);
                    return item;
                }));
                this.$empPart.show().find('.search').val('');
                this.$empList.html(listRender({ approvers: stackObj.emps }));
            }

            this.$breadcrumb.html(breadcrumbRender({stack: this.bcStack}));
        },
        show: function () {
            var _this = this;
            while (this.bcStack.length > 0) { this.bcStack.pop(); }
            getDepartment('$root', function (list) {
                _this.pushStack({ name: '全部', depts: list, emps: null });
                _this.$popup.show();
            });
        },
        hide: function () {
            this.$popup.hide();
        },
        addSelectedEmp: function (emp) {
            var $addItem = $(selectItemRender(emp));
            $addItem.on('click', function () {
                $(this).off().remove();
            });

            this.$seletedEmps.append($addItem);
        },
        setSelectedEmps: function (emps) {
            var _this = this;
            this.$seletedEmps.empty();
            _.each(emps, function (emp) {
                emp.subName = emp.userName.slice(-2);
                emp.colorCode = util.transColorCode(emp.userName);
                _this.addSelectedEmp(emp);
            });
        },
        trigger: function (event, args) {
            if (this.eventMap[event]) {
                var _this = this;
                _.each(this.eventMap[event], function (func) {
                    func.apply(_this, args);
                });
            }
            return this;
        },
        on: function (event, func) {
            return this.bind(event, func);
        },
        bind: function (event, func) {
            if (!this.eventMap[event]) { this.eventMap[event] = []; }
            if (this.eventMap[event].indexOf(func) < 0) {
                this.eventMap[event].push(func);
            }
            return this;
        },
        unbind: function (event, func) {
            if (this.eventMap[event]) {
                if (!func) {
                    while (this.eventMap[event].length > 0) { this.eventMap[event].pop(); }
                }
                else {
                    var index = this.eventMap[event].indexOf(func);
                    if (index >= 0) {
                        this.eventMap[event].splice(index, 1);
                    }
                }
            }
            return this;
        }
    };

    module.exports = UserMultiList;
});
