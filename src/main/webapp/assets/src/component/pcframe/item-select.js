define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var SelectList = require('./select-list');

    var itemSelect = function (trigger, list, multi) {
        var lister = new SelectList({ multi: multi });
        lister.triggerFor(trigger);
        lister.setList(_.map(list, function (item) { return { text: item, value: item }; }));

        var valCache = [];
        lister.on('select', function (e, val) {
            if (multi) {
                valCache = val;
            }
            else {
                this.hide();
                trigger.val(val.text);
                trigger.triggerHandler('change');
            }
        });
        lister.on('hide', function (e) {
            if (multi) {
                trigger.val(_.map(valCache, function (item) { return item.text; }).join(','));
                trigger.triggerHandler('change');
            }
        });
    };

    module.exports = itemSelect;
});
