define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');

    var WidgetBase = require('./base');

    var htmlTmpl = '<input type="text" name="{{id}}" maxlength="12" value="{{value}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>';


    var isDecimal = function(val) {
        return /^\d+(\.\d{0,4}){0,1}$/.test(val);
    };
    var removeNotNumber = function(str, fixed) {
        if (str.length === 0) return str;

        var segment = str.split('.');
        var integerPart = segment.shift().replace(/\D/g, '');
        if (integerPart.length === 0) integerPart = '0';
        if (segment.length > 0 && fixed > 0) {
            integerPart += '.';
            var decimalPart = segment.join('').replace(/\D/g, '');
            if (typeof fixed === 'number') {
                decimalPart = decimalPart.slice(0, fixed);
            }
            if (decimalPart.length > 0) {
                integerPart += decimalPart;
            }
        }

        return integerPart;
    };
    var MoneyField = WidgetBase.extend({
        tagName: 'td',
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'MoneyField'
        },
        events: {
            'input input': 'onChange'
        },
        onChange: function(e) {
            var val = this.$('input').val();
            if (!isDecimal(val)) {
                e.target.value = removeNotNumber(val, 4);
            }
            this.trigger('change', this.getValue());
        },
        getValue: function() {
            return this.$('input').val();
        },
        setValue: function(val) {
            this.$('input').val(val);
            this.onChange();
        }
    });

    module.exports = MoneyField;
});