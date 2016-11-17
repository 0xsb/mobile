define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('./loader');
    var util = require('./util');

    var documentRenders = {};
    var loadRender = function (tplName, callback) {
        if (documentRenders[tplName]) {
            callback(documentRenders[tplName]);
        } else {
            require.async('component/templates/' + tplName + '.tpl', function (tpltext) {
                var render = template(tpltext);
                documentRenders[tplName] = render;
                callback(render);
            });
        }
    };

    var DocumentView = function () {
        this.init.apply(this, arguments);
    };
    DocumentView.prototype = {
        init: function (element, options) {
            this.$el = element;
            this.options = options;
        },
        render: function (paramters) {
            var $el = this.$el;
            loadRender(options.tplName, function (render) {
                $el.html(render(paramters));
            });
        }
    };

    module.exports = Preview;
});
