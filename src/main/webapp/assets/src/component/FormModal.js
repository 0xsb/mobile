define(function(require) {
    var $ = require('jquery');
    var Backbone = require('backbone');
    var template = require('template');
    require('bootstrap');
    
    
    var FormModal = Backbone.View.extend({
        template: template(''),
        className: 'modal fade',
        id: function() {
            return this.cid;
        },
        events: {
            'click [data-do="submit"]': 'submit',
            'click [data-do="TokenBotton"]': 'TokenBotton',
             'click [data-do="EncodingAESkey"]': 'EncodingAESkey'
        },
        initialize: function() {
            this.$el.on('hidden.bs.modal', _.bind(this.remove, this));
        },
        render: function() {
            var markup = this.template({
                model: this.model.serialize()
            });

            this.$el.html(markup);
            this.cacheEls();
            return this;
        },
        TokenBotton:function(){
        },
        EncodingAESkey:function(){
        },
        show: function() {
            this.$el.modal('show');
        },
        hide: function() {
            this.$el.modal('hide');
        },
        submit: function() {
            // override
        },
        cacheEls: function() {
            this.$form = this.$('form');
        }
    });

    return FormModal;
});