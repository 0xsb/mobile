define(function(require) {
    require('jquery-util');
    var Backbone = require('backbone');
    var template = require('template');

    var SearchForm = Backbone.View.extend({
        tagName: 'form',
        id: function() {
            return this.cid;
        },
        template: template.empty,
        events: {
            'click [data-do="search"]': 'onSubmit',
            'submit': 'onSubmit',
            'click [data-do="reset"]': 'onReset'
        },
        onSubmit: function(event) {
            event.preventDefault();

            var data = this.serialize();
            this.trigger('search', data, this);
        },
        onReset: function() {
            this.el.reset();

            var data = this.serialize();
            this.trigger('search', data, this);
        },
        render: function() {
            var data = this.model.serialize();
            var markup = this.template(data);
            this.$el.html(markup);
            return this;
        },
        serialize: function() {
            var data = this.$el.serializeObject();
            return data;
        }
    });

    return SearchForm;
});