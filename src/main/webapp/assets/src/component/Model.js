define(function(require) {
    var _ = require('underscore');
    var Backbone = require('backbone');

    var Model = Backbone.Model.extend({
        syncOptions: {
            wait: true,
            processData: true,
            parse: true
        },
        save: function(attrs, options) {
            Model.__super__.save.call(this, attrs, _.extend({}, this.syncOptions, options));
        },
        serialize: function() {
            var data = this.toJSON();
            data._isNew = this.isNew();
            return data;
        }
    });

    return Model;
});