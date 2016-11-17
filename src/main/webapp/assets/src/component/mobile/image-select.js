define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var loader = require('./loader');
    var util = require('./util');

    var getImgsUrls = function (list) {
        var arr = [];
        list.find('.img-box').each(function (index, item) {
            var $this = $(this);
            arr.push({ id: $this.data('id'), name: $this.data('name') });
        });
        return arr.length === 0 ? '' : JSON.stringify(arr);
    };

    var ImageSelecter = function (elem, option) {
        this.init(elem, option);
    };
    ImageSelecter.prototype = {
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
                _this.elements.input.val(getImgsUrls(_this.elements.list));
            });
        },
        addImage: function (model) {
            var html = '<div class="img-box" data-name="' + model.addr + '" data-id="' + model.id + '">\
                <span class="box-remove">\
                    <i class="iconfont icon-shibai"></i>\
                </span>\
                <img id="images" src="' + CONTEXT_PATH + '/file/download/' + model.id + '/thum_' + model.addr + '.do' + '">\
            </div>';

            this.elements.list.append($(html));
            this.elements.input.val(getImgsUrls(this.elements.list));
        },
        upload: function (file) {
            var _this = this;
            var url = CONTEXT_PATH + '/file/imageUpload.do?num=' + UUID + '&companyId=' +
                util.constant.companyId + '&userId=' + util.constant.userId;
            var formData = new FormData();
            formData.append('file', file);

            $.ajax({
                url: url,
                data: formData,
                type: 'POST',
                dataType: 'json',
                beforeSend: function () {
                    loader.show('图片上传中，请稍候');
                },
                success: function (result) {
                    //console.log(result);
                    if (result.success) {
                        _this.addImage(result.model);
                    }
                    else {
                        util.alert(result.message || '图片上传出错！');
                    }
                },
                complete: function () {
                    loader.hide();
                },
                processData: false,  // 告诉jQuery不要去处理发送的数据
                contentType: false   // 告诉jQuery不要去设置Content-Type请求头});
            });
        },
        showPreview: function (file) {
            var fileReader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = function (e) {
                var html = '<div class="img-block fullscreen">\
                        <img src="' + e.target.result + '">\
                    </div>';
            };
        }
    };

    var imageSelect = function (elem, option) {
        return new ImageSelecter(elem, option);
    };

    module.exports = imageSelect;
});
