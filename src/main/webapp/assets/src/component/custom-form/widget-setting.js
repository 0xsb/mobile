define(function (require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var template = require('template');

    var baseTemplate = '<div class="full-list">\
        <div class="list-header">\
            <a href="javascript:" class="back" role="back"><i class="iconfont icon-xiangzuo1"></i></a>\
            <span>编辑控件</span>\
        </div>\
        <div class="list-part">\
        </div>\
        <div class="list-footer">\
            <a href="javascript:" class="ws-save" role="submit">保存控件配置</a>\
        </div>\
    </div>';
    var attrTemplate = '<div class="ws-info">\
            <span>当前编辑控件：</span><span>{{model.text}}</span>\
        </div>\
        <div class="ws-attribute">\
            <span>是否必填：</span>\
            <span role="required" class="switcher {{model.required ? \'on\' : \'off\'}}"></span>\
        </div>\
        <div class="ws-info">\
            <span>字段类型：</span>\
        </div>\
        <div class="ws-attribute">\
            <span role="type" class="ws-type" data-type="TextField">\
                <i class="checkicon iconfont {{model.type === \'TextField\' ? \'icon-check\' : \'\'}}"></i>\
                文本型\
            </span>\
        </div>\
        <div class="ws-attribute">\
            <span role="type" class="ws-type" data-type="NumberField">\
                <i class="checkicon iconfont {{model.type === \'NumberField\' ? \'icon-check\' : \'\'}}"></i>\
                数字型\
            </span>\
        </div>\
        <div class="ws-attribute">\
            <span role="type" class="ws-type" data-type="DDSelectField">\
                <i class="checkicon iconfont {{model.type === \'DDSelectField\' ? \'icon-check\' : \'\'}}"></i>\
                下拉选择\
            </span>\
            <# if (model.type === \'DDSelectField\') { #>\
                <div class="selects-list">\
                    <# _.each(model.selects, function (item, index) { #>\
                    <a class="select-item"><i role="removeselect" data-index="{{index}}" class="iconfont icon-delete"></i>\
                        <input type="text" placeholder="请输入备选项" value="{{item}}">\
                    </a>\
                    <# }) #>\
                    <a class="select-item" role="addselect"><i class="iconfont icon-tianjialeimu"></i>\
                        <span>添加备选项</span>\
                    </a>\
                </div>\
            <# } #>\
        </div>\
        <div class="ws-attribute">\
            <span role="type" class="ws-type" data-type="DDDateField">\
                <i class="checkicon iconfont {{model.type === \'DDDateField\' ? \'icon-check\' : \'\'}}"></i>\
                日期型\
            </span>\
        </div>';

    var attrRender = template(attrTemplate);

    var roleActions = {
        'back': function (e, item) {
            this.hide();
        },
        'required': function (e, item) {
            this.updateSelects();
            this.model.required = !this.model.required;
            this.render();
        },
        'type': function (e, item) {
            this.updateSelects();
            var type = $(item).data('type');
            this.model.type = type;
            this.render();
        },
        'addselect': function (e, item) {
            this.updateSelects();
            this.model.selects.push('');
            this.render();
        },
        'removeselect': function (e, item) {
            this.updateSelects();
            var index = parseInt($(item).data('index'));
            this.model.selects.splice(index, 1);
            this.render();
        },
        'submit': function (e, item) {
            this.updateSelects();
            this.submit(this.model);
            this.hide();
        }
    };
    var WidgetSetting = function (options) {
        this.$el = $(baseTemplate);
        this.$content = this.$el.find('.list-part');

        var _this = this;
        _.each(roleActions, function (act, key) {
            _this.$el.on('click', '[role="' + key + '"]', function (e) {
                act.apply(_this, [e, this]);
            });
        });

        this.$el.appendTo(document.body).hide();
        this.submit = options.submit;
    };
    WidgetSetting.prototype = {
        updateSelects: function () {
            var inputs = this.$el.find('.selects-list input').map(function () {
                    return $(this).val();
                }).toArray();
            this.model.selects = inputs;
        },
        render: function () {
            this.$content.html(attrRender({ model: this.model }));
            return this;
        },
        bind: function (model) {
            this.model = model;
            return this;
        },
        show: function () {
            this.render();
            this.$el.show();
            return this;
        },
        hide: function () {
            this.$el.hide();
            return this;
        },
        destory: function () {
            this.$el.off();
            this.$el.remove();
            delete this.submit;
        }
    };

    module.exports = WidgetSetting;
});