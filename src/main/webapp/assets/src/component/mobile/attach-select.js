define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var loader = require('./loader');
    var util = require('./util');

    var getFilesUrls = function (list) {
        var arr = [];
        list.find('.attach-box').each(function (index, item) {
            var $this = $(this);
            arr.push({ id: $this.data('id'), name: $this.data('name'), filename: $this.data('filename') });
        });
        return arr.length === 0 ? '' : JSON.stringify(arr);
    };

    var globalMap = {};
    var AttachSelector = function (elem, options) {
        this.init(elem, options);
    };
    AttachSelector.prototype = {
        init: function (elem, option) {
            this.$el = elem;
            this.option = option;

            this.elements = {
                trigger: this.$el.find('.trigger'),
                input: this.$el.find('.value-input'),
                file: this.$el.find('.file-input'),
                list: this.$el.find('.list')
            };

            this.initEvent();
        },
        initEvent: function () {
            var _this = this;

            this.elements.trigger.on('click', function (e) {
                _this.elements.file.click();
            });
            this.elements.file.on('change', function (e) {
                var files = e.target.files || e.dataTransfer.files;
                _this.upload(files[0]);

                _this.elements.file.val('');
            });
            this.elements.list.on('click', '.box-remove', function (e) {
                var elem = $(this);
                elem.parent().remove();
                _this.elements.input.val(getFilesUrls(_this.elements.list));
            });
        },
        addFile: function (model) {
            var html = '<div class="attach-box" data-name="' + model.addr + '" data-filename="' + model.name + '" data-id="' + model.id + '">\
                <div class="attach-icon"><i class="iconfont icon-xinxi"></i></div>\
                <span class="attach-name">' + model.name + '</span>\
                <span class="box-remove">\
                    <i class="iconfont icon-shibai"></i>\
                </span>\
            </div>';

            this.elements.list.append($(html));
            this.elements.input.val(getFilesUrls(this.elements.list));
        },
        upload: function (file) {
            var _this = this;
            var url = CONTEXT_PATH + '/file/fileUploads/' + UUID + '/' +
                util.constant.companyId + '/' + util.constant.userId + '.do';
            var formData = new FormData();
            formData.append('file', file);

            $.ajax({
                url: url,
                data: formData,
                type: 'POST',
                dataType: 'json',
                beforeSend: function () {
                    loader.show('附件上传中，请稍候');
                },
                success: function (result) {
                    //console.log(result);
                    if (result.success) {
                        _this.addFile(result.model[0]);
                    }
                    else {
                        util.alert(result.message || '附件上传出错！');
                    }
                },
                complete: function () {
                    loader.hide();
                },
                processData: false,  // 告诉jQuery不要去处理发送的数据
                contentType: false   // 告诉jQuery不要去设置Content-Type请求头});
            });
        }
    };

    var attachSelect = function (elem, options) {
        var newSelector = new AttachSelector(elem, options);
        return newSelector;
    };

    module.exports = attachSelect;
});
