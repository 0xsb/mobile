define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');

    var WidgetBase = require('./base');

    var htmlTmpl = '<label>{{name}}</label>\
        <a href="javascript:" class="trigger" role="trigger">上传图片</a>\
        <input role="uploader" type="file" accept="image/*" style="display: none;">\
        <input role="value-heap" name="{{id}}" type="hidden" {{isRequired ? "required" : ""}}>\
        <div role="img-list" class="list"></div>';
    var itemTmpl = '<a href="javascript:void(0);" class="box-remove" role="remove"><i class="iconfont icon-shibai"></i></a>\
        <img id="images" src="' + CONTEXT_PATH + '/file/download/{{id}}/thum_{{addr}}.do">';

    var ImageItem = Backbone.Model.extend({
        destroy: function() {
            this.stopListening();
            this.trigger('destroy', this, this.collection);
        }
    });
    var ImageItemView = Backbone.View.extend({
        tagName: 'div',
        className: 'img-box',
        template: template(itemTmpl),
        events: {
            'click [role="remove"]': 'doRemove'
        },
        initialize: function(option) {
            this.listenTo(this.model, 'remove', this.remove);
        },
        render: function() {
            var data = this.model.toJSON();
            var html = this.template(data);
            this.$el.html(html);

            return this;
        },
        doRemove: function() {
            this.model.destroy();
        }
    });

    var DDPhotoField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'DDPhotoField'
        },
        events: {
            'click [role="trigger"]': 'selectFile',
            'change [role="uploader"]': 'uploadFile'
        },
        initialize: function() {
            this._super_invoke('initialize', arguments);
            this.collection = new Backbone.Collection(null, {
                model: ImageItem
            });

            this.listenTo(this.collection, 'add', this.addImage);
        },
        render: function() {
            this._super_invoke('render');
            this.cacheEl();
            return this;
        },
        cacheEl: function() {
            this.$uploader = this.$('[role="uploader"]');
            this.$valueHeap = this.$('[role="value-heap"]');
            this.$imgList = this.$('[role="img-list"]');
        },
        selectFile: function() {
            this.$uploader.click();
        },
        uploadFile: function(e) {
            var files = e.target.files || e.dataTransfer.files;
            var file = files[0];
            var url = CONTEXT_PATH + '/file/imageUpload.do?num=' + UUID + '&companyId=' +
                util.constant.companyId + '&userId=' + util.constant.userId;
            var formData = new FormData();
            formData.append('file', file);

            $.ajax({
                url: url,
                data: formData,
                type: 'POST',
                dataType: 'json',
                context: this,
                beforeSend: function() {
                    loader.show('图片上传中，请稍候');
                },
                success: function(result) {
                    if (result.success) {
                        this.collection.add(new ImageItem(result.model));
                    } else {
                        util.alert(result.message || '图片上传出错！');
                    }
                },
                complete: function() {
                    loader.hide();
                },
                processData: false, // 告诉jQuery不要去处理发送的数据
                contentType: false // 告诉jQuery不要去设置Content-Type请求头
            });
        },
        addImage: function(model, collection, options) {
            var view = new ImageItemView({
                model: model
            });
            this.$imgList.append(view.render().el);
        },
        getValue: function() {
            var data = this.collection.toJSON();
            return data;
        },
        setValue: function(val) {
            this.collection.reset(null);
            _.each(val, function(item) {
                this.collection.add(item);
            }, this);
            this.onChange();
        }
    });

    module.exports = DDPhotoField;
});