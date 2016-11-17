define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');

    var WidgetBase = require('./base');

    var htmlTmpl = '<label>{{name}}</label>\
        <input type="text" maxlength="12" name="{{id}}" value="{{value}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>';

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

    var NumberField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'NumberField'
        },
        events: {
            'input input': 'onChange'
        },
        onChange: function(e) {
            if (!this.checkValue()) {
                this.repairValue();
            }
            this._super_invoke('onChange');
        },
        checkValue: function() {
            var val = this.getValue();
            return this.model.get('integer') ? /^\d+$/.test(val) : /^\d+(\.\d{0,4}){0,1}$/.test(val);
        },
        repairValue: function() {
            var fixed = this.model.get('integer') ? 0 : 4;
            this.setValue(removeNotNumber(this.getValue(), fixed));
        },
        getValue: function() {
            return this.$('input').val();
        },
        setValue: function(val) {
            this.$('input').val(val);
            this.onChange();
        }
    });

    module.exports = NumberField;
});