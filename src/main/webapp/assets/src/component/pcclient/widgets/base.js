define(function (require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');

    //控件配置格式及字段含义
    var widgetSetting = {
        'describeName': '单行输入框',   //控件名称
		'isRequired': '0',            //是否必填 0否 1是
		'isEffective': '0',           //是否可导入 0否 1是
		'isMerge': '0',               //是否只读 0否 1是
        'isStorage': '',              //日期时间控件的格式
		'reName': 'TextField100',     //控件id
		'sequence': 100,              //控件排序号
		'controlId': 'TextField',     //控件类型
		'exp': '请输入',               //控件placeholder 在部分控件上有不同含义
		'value': '',                  //控件默认值
		'jsonData': '',               //控件数据 在不同控件中有不同含义 为json格式的string,使用时需要解析
		'alias': '',                  //控件别名
		'useAlias': false,            //控件是否使用了别名
		'field1': 10,                 //PC端 控件宽度
		'field2': null,               //暂未使用
		'field3': null                //暂未使用
    };
    //jsondata格式及字段含义
    var jsonData = {
        'options': [],                  //string数组 单选,多选控件中表示选项列表
        'imginfo': { 'id': '', 'addr': '' },    //object 图片说明控件中表示图片信息
        'integer': false,               //boolean 数字控件中表示是否只能输入整数
        'toSum': false,                 //boolean 数字,金额控件在明细控件中时，表示是否需要计算合计值
        'linkageOption': {              //object 联动选择控件配置项
            'mustnumber': false,        //boolena 联动选择子项是否必须为数字,true时表示该控件可参与计算控件计算
            'selects': [{               //object数组 父项列表数组,value表示父项值,children表示子项列表
                'value': '',
                'children': [{ 'value': '' }]   //object数组 联动选择子项列表,value表示子项值
            }]
        },
        'expressOption': {              //object 数值计算和条件判断控件的配置项
            'expression': '',           //string 计算或条件表达式
            'expressionData': {}        //object 参与计算或条件判断的控件的map,key为变量名,value为对应控件id
        }
    };
    //控件数据格式及字段含义
    var widgetData = {
        'controlName': 'TextField', //控件类型
        'id': 'TextField100',       //控件id
        'value': '88'               //控件值
    };

    var WidgetModel = Backbone.Model.extend({
        parse: function (resp, options) {
            var widgetAttrs = {
                id: resp.reName,
                type: resp.controlId,
                name: resp.describeName,
                sequence: resp.sequence,
                placeholder: resp.exp,
                isRequired: resp.isRequired === '1',
                isReadonly: resp.isMerge === '1',
                timeFormat: resp.isStorage,
                value: resp.value,
                useAlias: resp.useAlias,
                alias: resp.alias,
                width: resp.field1
            };
            if (typeof resp.jsonData === 'string' && resp.jsonData.length > 0 && resp.controlId !== 'EventButton') {
                var jsonData = JSON.parse(resp.jsonData);
                widgetAttrs = _.defaults(widgetAttrs, jsonData);
            } else if (typeof resp.jsonData === 'object') {
                widgetAttrs = _.defaults(widgetAttrs, resp.jsonData);
            } else if (resp.controlId == 'EventButton') {
                widgetAttrs.eventParam = resp.jsonData;
                widgetAttrs.eventAttributes = JSON.parse(resp.field2);
            }

            return widgetAttrs;
        }
    });

    var WidgetBase = Backbone.View.extend({
        tagName: 'div',
        className: 'widget-region',
        _super_invoke: function (superFn, params) {
            if (!this._super_level) { this._super_level = {}; }
            if (!this._super_level[superFn]) { this._super_level[superFn] = 0; }
            var proto = this.constructor.__super__,
                currentLevel = 0;
            while (proto !== WidgetBase && typeof proto !== 'undefined') {
                currentLevel++;
                if (proto[superFn] && this._super_level[superFn] < currentLevel) {
                    this._super_level[superFn] = currentLevel;
                    proto[superFn].apply(this, params);
                    break;
                }
                proto = proto.constructor.__super__;
            }

            if (currentLevel >= this._super_level[superFn]) {
                delete this._super_level[superFn];
            }
            return proto;
        },
        initialize: function (options) {
            options || (options = {});
            this.model = new WidgetModel(options.modelAttrs, { parse: true });
            this.$el.addClass(this.attributes['data-widget']);
            this.render();
        },
        render: function () {
            var attrJson = this.model.toJSON();
            var html = this.template(attrJson);
            this.$el.html(html); //.css('width', attrJson.width * 100 / 12 + '%');
            return this;
        },
        onChange: function () {
            this.trigger('change', this.getValue(), this.model, this);
        },
        getData: function () {
            return {
                'controlName': this.model.get('type'),
                'id': this.model.get('id'),
                'value': this.getValue()
            };
        },
        readonly: function (readonly) {
            this.$('input').prop('readonly', readonly);
        }
    });

    module.exports = WidgetBase;
});
