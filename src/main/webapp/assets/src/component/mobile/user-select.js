define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('./util');
    var UserMultiList = require('./user-multi-list');

    var itemTemplate = '<div class="approver-item" data-key={{userId}}>\
        <div class="avatar" style="background:#{{colorCode}}">\
            {{subName}}\
        </div><span class="name">{{userName}}</span></div>';
    var itemRender = template(itemTemplate);
    var defAppTemplate = '<# _.each(list, function (item) { #>\
        <# if (item.id !== \'$$$\') { #>\
        <div class="approver-item" data-key={{item.id}}>\
            <div class="avatar" style="background:#{{item.colorCode}}">\
                {{item.subName}}\
            </div><span class="name">{{item.userName}}</span>\
        </div>\
        <# } else { #>\
            <div class="trigger"><i class="iconfont icon-tianjialeimu"></i></div>\
        <# } #>\
    <# }) #>';
    var defAppRender = template(defAppTemplate);

    var getSelectedEmps = function ($list) {
        var emps = $list.find('.approver-item').map(function (index, item) {
                return { userId: $(item).data('key'), userName: $(item).find('.name').text() };
            }).toArray();
        return emps; //JSON.stringify(emps);
    };

    var UserSelecter = function (elem, options) {
        var _this = this;
        this.multi = options.multi;
        this.$el = elem;
        this.$list = elem.find('.list');
        this.$input = elem.find('input[type=hidden]');

        var dataCache, selectedItems = {};
        this.list = new UserMultiList({ multi: this.multi });

        elem.find('.trigger').on('click', function () {
            var emps = getSelectedEmps(_this.$list);
            _this.list.setSelectedEmps(emps);
            _this.list.show();
        });

        this.list.on('select', function (emps) {
            _this.setEmps(emps);
        });
    };
    UserSelecter.prototype.addEmp = function (emp) {
        var _this = this;
        if (!emp.subName) { emp.subName = emp.userName.slice(-2); }
        if (!emp.colorCode) { emp.colorCode = util.transColorCode(emp.userName); }

        var $item = $(itemRender(emp));
        $item.find('.avatar').on('click', function () {
            $item.remove();
            _this.$input.val(JSON.stringify(getSelectedEmps(_this.$list)));
        });

        this.$list.append($item);
    };
    UserSelecter.prototype.setEmps = function (emps) {
        var _this = this;
        _this.$list.empty();
        _.each(emps, function (emp) {
            _this.addEmp(emp);
        });
        _this.$input.val(JSON.stringify(getSelectedEmps(_this.$list)));
    };
    UserSelecter.prototype.getEmps = function () {
        return getSelectedEmps(this.$list);
    };


    module.exports = UserSelecter;
});
