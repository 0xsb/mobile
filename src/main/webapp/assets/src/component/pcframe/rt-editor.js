define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var util = require('component/mobile/util');
    require('wangEditor')($);
    var WangEditor = wangEditor;

    var RichTextView = function (element, options) {
        this.init(element, options);
    };
    RichTextView.prototype = {
        init: function (element, options) {
            this.$el = element;
            this.$box = this.$el.find('.editor-box')[0];
            var editor = new WangEditor(this.$box);

            editor.config.menus = [
                'source',
                '|',    //分割线
                'bold',
                'underline',
                'italic',
                'strikethrough',
                'eraser',
                'forecolor',
                'bgcolor',
                '|',
                'quote',
                'fontfamily',
                'fontsize',
                'head',
                'unorderlist',
                'orderlist',
                'alignleft',
                'aligncenter',
                'alignright',
                '|',
                'link',
                'unlink',
                'table',
                'img',
                '|',
                'undo',
                'redo'
            ];
            editor.config.menuFixed = false;
            editor.config.uploadImgUrl = CONTEXT_PATH + '/file/imageUpload.do';
            editor.config.uploadParams = {
                num: UUID,
                companyId: util.constant.companyId,
                userId: util.constant.userId
            };

            var insertImg = function (src) {
                var img = document.createElement('img');
                img.onload = function () {
                    var html = '<img src="' + src + '" style="max-width:100%;"/>';
                    editor.command(null, 'insertHtml', html);
                    img = null;
                };
                img.onerror = function () {
                    img = null;
                };
                img.src = src;
            };
            editor.config.uploadImgFns.onload = function (resultText, xhr) {
                var resp = JSON.parse(resultText);
                if (resp.success) {
                    var src = CONTEXT_PATH + '/file/download/' + resp.model.id + '/' + resp.model.addr + '.do';
                    insertImg(src);
                } else {
                    util.alert(resp.message || '上传时出错！');
                }
            };

            editor.config.hideLinkImg = true;
            this.editor = editor;
        },
        create: function () {
            this.editor.create();
        },
        getHtml: function () {
            return this.editor.$txt.html();
        },
        getText: function () {
            return this.editor.$txt.text();
        },
        getFormatText: function () {
            return this.editor.$txt.formatText();
        },
        setHtml: function (val) {
            this.editor.$txt.html(val);
        }
    };

    module.exports = RichTextView;
});
