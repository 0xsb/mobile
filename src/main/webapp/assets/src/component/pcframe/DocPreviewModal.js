define(function (require) {
    var $ = require('jquery');
    var Backbone = require('backbone');
    var template = require('template');
    require('bootstrap');

    var modalTpl = require('../templates/modal.tpl');

    var DocPreviewModal = Backbone.View.extend({
        template: template(modalTpl),
        className: 'modal',
        events: {
            'click [data-do="save"]': 'doSave'
        },
        initialize: function () {
            this.$el.on('hidden.bs.modal', _.bind(this.hide, this));
            this.$el.appendTo(document.body);

            this.$el.html(this.template({ }));
            this.$body = this.$el.find('.modal-body');
        },
        render: function (data) {
            var $body = this.$body;
            require.async('../templates/' + data.tmpl + '.tpl', function (tmpl) {
                var markup = template(tmpl)(data);
                $body.html(markup);
            });
            return this;
        },
        show: function (data) {
            this.render(data).$el.modal('show');
        },
        hide: function () {
            this.$el.modal('hide');
        }
    });

    return DocPreviewModal;
});
