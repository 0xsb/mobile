define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var util = require('component/mobile/util');
    var DocPreviewModal = require('./DocPreviewModal');

    var Viewer = function (element, options) {
        this.init(element, options);
    };
    Viewer.prototype = {
        init: function (element, options) {
            var _this = this;
            this.tmpl = options.exp;
            this.$el = element;
            this.$el.find('.button').on('click', function () {
                _this.show();
            });
            this.modal = new DocPreviewModal();
        },
        bind: function (formView) {
            while (!formView.getAttribute) {
                formView = formView.parentForm;
            }
            this.formView = formView;
        },
        show: function () {
            var paramters = this.formView.getAttribute('paramters');
            var paramData = this.formView.getParamters(paramters);
            paramData.tmpl = this.tmpl;
            this.modal.show(paramData);
        }
    };

    module.exports = Viewer;
});
