define(function (require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    var ztree = require('ztree');
    var util = require('../../mobile/util');

    var WidgetBase = require('./base');
    var depUrl = CONTEXT_PATH + '/users/getAllDept.do';

    var htmlTmpl = '<input type="text" placeholder="请选择部门" readonly \
            {{isRequired ? "required" : ""}} role="trigger">\
        <input role="value" name="{{id}}" type="hidden" {{isRequired ? "required" : ""}}>';

    var popupTmpl = '<div class="department-popup">\
        <div class="left-part">\
            <ul class="dept-tree ztree"></ul>\
        </div>\
        <div class="button-part">\
            <a class="add-button">添加</a>\
            <a class="delete-button">移除</a>\
        </div>\
        <div class="right-part">\
            <ul class="dept-list"></ul>\
        </div>\
        <div class="other-part">\
            <span>其他:</span>\
            <input class="other-depart" type="text">\
            <a class="add-other">添加</a>\
        </div>\
        <div class="footer-part">\
            <a class="btn btn-primary" role="ok">确定</a>\
            <a class="btn btn-default" role="cancel">取消</a>\
        </div>\
    </div>';

    var DepartmentPopup = Backbone.View.extend({
        tagName: 'div',
        className: 'mask',
        template: template(popupTmpl),
        events: {
            'click .add-button': 'doAdd',
            'click .delete-button': 'doDelete',
            'click .selected-depart': 'selectDept',
            'click .add-other': 'doAddOther',
            'click [role="ok"]': 'doOK',
            'click [role="cancel"]': 'doCancel'
        },
        initialize: function (options) {
            this.departments = null;
            this.render();
        },
        render: function () {
            var markup = this.template({});
            this.$el.hide().html(markup).appendTo(document.body);
            this.cacheEls();

            return this;
        },
        cacheEls: function () {
            this.$tree = this.$('.dept-tree');
            this.$list = this.$('.dept-list');
        },
        load: function () {
            var _this = this;
            $.ajax({
                url: depUrl,
                type: 'get',
                dataType: 'json',
                data: {
                    companyId: util.constant.companyId
                }
            }).done(function (res) {
                if (res.success) {
                    _this.departments = _this.dataParser(res.model);
                    _this.initTree();
                } else {
                    //something error
                }
            }).fail(function () {
                alert('读取部门时发生错误');
            });
        },
        show: function () {
            if (!this.departments) {
                this.load();
            }
            else {
                this.$el.show();
            }
        },
        dataParser: function (model) {
            var tempMap = {}, data = [];
            model.sort(function (a, b) {
                return a.showindex - b.showindex;
            });
            _.each(model, function (item) {
                var pId = item.previousId;
                var id = 'dept' + item.id;
                if (pId != '' && pId != null) {
                    pId = 'dept' + pId;
                }
                var temp = {
                    userType: 'dept',
                    isParent: false,
                    id: id,
                    pId: pId,
                    name: item.orgName,
                    orgId: item.id,
                    previousId: pId,
                    showindex: item.showindex
                };

                data.push(temp);
                tempMap[id] = temp;
            });

            var isDataError = false;
            _.each(data, function (item) {
                if (item.pId !== '' && item.pId != null) {
                    if (!tempMap[item.pId]) {
                        alert('组织结构数据有错误！\r\n请联系管理员！');
                        isDataError = true;
                        return false;
                    }
                    tempMap[item.pId].isParent = true;
                }
            });

            return isDataError ? [] : data;
        },
        initTree: function (data) {
            var addDiyDom = function () {

            };
            var onClick = function () {

            };
            var setting = {
                view: {
                    showLine: false,
                    showIcon: false,
                    selectedMulti: false,
                    autoCancelSelected: false,
                    showSelectStyle: true
                    //addDiyDom: addDiyDom
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                edit: {
                    enable: false
                },
                callback: {
                    onClick: onClick
                }
            };

            this.tree = $.fn.zTree.init(this.$tree, setting, this.departments);
            this.show();
        },
        doAdd: function () {
            var node = this.tree.getSelectedNodes();
            if (node.length > 0) { node = node[0]; }

            var hasNode = this.$list.find('[data-id="' + node.orgId + '"]');
            if (hasNode.length > 0) { return; }

            this.addDept(node.orgId, node.name);
        },
        doAddOther: function () {
            var name = $.trim(this.$('.other-depart').val());
            if (name.length > 0) {
                this.addDept('-1', name);
            }
        },
        doDelete: function () {
            this.$list.find('.active').remove();
        },
        selectDept: function (e) {
            var $el = $(e.currentTarget);
            this.$list.find('li').removeClass('active');
            $el.addClass('active');
        },
        addDept: function (id, name) {
            var newNode = $('<li class="selected-depart" data-id="' + id + '">' + name + '</li>');
            this.$list.append(newNode);
        },
        doOK: function () {
            var data = this.$list.find('li').map(function () {
                return {
                    id: $(this).data('id'),
                    name: $(this).text()
                };
            }).toArray();
            this.trigger('ok', data);

            this.$el.hide();
        },
        doCancel: function () {
            this.$el.hide();
        },
        setData: function (data) {
            this.$list.empty();
            _.each(data, function (item) {
                this.addDept(item.id, item.name);
            }, this);
        }
    });

    var DepartmentSelect = WidgetBase.extend({
        tagName: 'td',
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'DepartmentSelect'
        },
        events: {
            'input input': 'onChange',
            'click [role="trigger"]': 'showPopup'
        },
        initialize: function () {
            this._super_invoke('initialize', arguments);
            this.popup = new DepartmentPopup();

            this.listenTo(this.popup, 'ok', this.onOk);

            this.$trigger = this.$('[role="trigger"]');
            this.$value = this.$('[role="value"]');
        },
        showPopup: function () {
            var json = this.$value.val();
            var data = json.length > 0 ? JSON.parse(json) : [];

            this.popup.setData(data);
            this.popup.show();
        },
        getValue: function () {
            return this.$value.val();
        },
        setValue: function (val) {
            var data = val.length > 0 ? JSON.parse(val) : [];
            var text = _.map(data, function (item) {
                    return item.name;
                }).join(',');

            this.$trigger.val(text);
            this.$value.val(val);
            this.onChange();
        },
        onOk: function (data) {
            var text = _.map(data, function (item) {
                    return item.name;
                }).join(',');

            this.$trigger.val(text);
            this.$value.val(JSON.stringify(data));
            this.onChange();
        }
    });

    module.exports = DepartmentSelect;
});
