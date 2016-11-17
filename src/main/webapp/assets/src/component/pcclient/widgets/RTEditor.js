define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    var wangEditor = require('wangEditor');
    var util = require('component/mobile/util');

    var WidgetBase = require('./base');

    var htmlTmpl = '<textarea style="width:100%; height:200px;" type="text" name="{{id}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>{{value}}</textarea>';

    var RTEditor = WidgetBase.extend({
        tagName: 'td',
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'RTEditor'
        },
        events: {
            'input textarea': 'onChange'
        },
        render: function() {
            this._super_invoke('render');
            this.$editor = this.$('textarea');
            var _this = this;
            var editor = new wangEditor(this.$editor);
            this.editor = editor;
            $.extend(true, this.editor.config, {
                menuFixed: false,
                printLog: false,
                uploadImgFileName: 'file',
                uploadImgUrl: CONTEXT_PATH + '/file/imageUpload.do?num=' + UUID + '&companyId=' + util.constant.companyId + '&userId=' + util.constant.userId,
                uploadImgFns: {
                    onload: function(resultText, xhr) {
                        try {
                            var result = $.parseJSON(resultText);
                            if (result.success) {
                                var model = result.model;
                                var src = CONTEXT_PATH + '/file/download/' + model.id + '/thum_' + model.addr + '.do';
                                editor.command(null, 'insertHtml', '<img src="' + src + '" alt="' + model.name + '" style="max-width:100%;"/>');
                            }
                        } catch (e) {}
                    }
                }
            });
            this.editor.config.menus = [
                'source',
                '|',
                'bold',
                'underline',
                'italic',
                'strikethrough',
                'eraser',
                'forecolor',
                '|',
                'quote',
                'unorderlist',
                'orderlist',
                'alignleft',
                'aligncenter',
                'alignright',
                '|',
                'undo',
                'redo'
            ];
            this.editor.onchange = _.bind(this.onChange, this);
            this.editor.create();
            return this;
        },
        getValue: function() {
            var html = this.editor.$txt.html();
            return html;
        },
        getText: function() {
            var text = this.editor.$txt.text();
            return text;
        },
        setValue: function(val) {
            if (this.editor) this.editor.$txt.append(val);
            this.onChange();
        }
    });

    module.exports = RTEditor;
});