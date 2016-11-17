define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');

    var WidgetBase = require('./base');

    var htmlTmpl = '<a href="javascript:" class="trigger" role="trigger">选择附件</a>\
        <input role="uploader" type="file" accept="image/*" style="display: none;">\
        <input role="value-heap" name="{{id}}" type="hidden" {{isRequired ? "required" : ""}}>\
        <div role="item-list" class="list clearfix"></div>';

    var itemTmpl = '<span class="attach-name"><i class="attach-icon iconfont icon-xinxi"></i> {{name}}</span><a href="javascript:void(0);" class="box-remove" role="remove"><i class="iconfont icon-shibai"></i></a>';

    var Attachment = Backbone.Model.extend({
        destroy: function() {
            this.stopListening();
            this.trigger('destroy', this, this.collection);
        }
    });
    var AttachmentView = Backbone.View.extend({
        tagName: 'div',
        className: 'attach-box',
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

    var DDAttachment = WidgetBase.extend({
        tagName: 'td',
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'DDAttachment'
        },
        events: {
            'click [role="trigger"]': 'selectFile',
            'change [role="uploader"]': 'uploadFile'
        },
        initialize: function () {
            this._super_invoke('initialize', arguments);
            this.collection = new Backbone.Collection(null, {
                model: Attachment
            });

            this.listenTo(this.collection, 'add', this.addItem);
            this.listenTo(this.collection, 'remove', this.removeItem);
        },
        render: function () {
            this._super_invoke('render');
            this.cacheEl();
            return this;
        },
        cacheEl: function () {
            this.$uploader = this.$('[role="uploader"]');
            this.$valueHeap = this.$('[role="value-heap"]');
            this.$itemList = this.$('[role="item-list"]');
        },
        selectFile: function () {
            this.$uploader.click();
        },
        uploadFile: function (e) {
            var files = e.target.files || e.dataTransfer.files;
            var file = files[0];
            var url = CONTEXT_PATH + '/file/fileUploads/' + UUID + '/' +
                util.constant.companyId + '/' + util.constant.userId + '.do';
            var formData = new FormData();
            formData.append('file', file);

            $.ajax({
                url: url,
                data: formData,
                type: 'POST',
                dataType: 'json',
                context: this,
                beforeSend: function () {
                    loader.show('附件上传中，请稍候');
                },
                success: function (result) {
                    if (result.success) {
                        if (_.isArray(result.model) && result.model.length > 0) {
                            this.collection.add(new Attachment(result.model[0]));
                        }
                    } else {
                        util.alert(result.message || '附件上传出错！');
                    }
                },
                complete: function () {
                    loader.hide();
                },
                processData: false, // 告诉jQuery不要去处理发送的数据
                contentType: false // 告诉jQuery不要去设置Content-Type请求头
            });
        },
        removeItem: function () {
            this.onChange();
        },
        addItem: function (model, collection, options) {
            var view = new AttachmentView({
                model: model
            });
            this.$itemList.append(view.render().el);
            this.onChange();
        },
        getValue: function () {
            var data = this.collection.toJSON();
            return data;
        },
        setValue: function (val) {
            this.collection.reset(null);
            if (typeof val === 'string') {
                val = JSON.parse(val);
            }

            _.each(val, function (item) {
                var model = {
                    name: item.filename,
                    addr: item.name,
                    id: item.id
                };
                this.collection.add(model);
            }, this);
            this.onChange();
        },
        readonly: function (readonly) {
            if (readonly) {
                $('a[role="trigger"]').hide();
                $('a[role="remove"]').hide();
            }
        }
    });

    module.exports = DDAttachment;
});
