define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('./loader');
    var SelectList = require('./select-list');
    var util = require('./util');

    var syncSelect = function (element, option) {
        var trigger = element.find('.trigger'),
            valueField = element.find('.value-field');
        if (option.hiddenFields && !option.multi) {
            _.each(option.hiddenFields, function (item) {
                element.append($('<input type="hidden" name="' + item + '">'));
            });
        }

        var lister = new SelectList(option);
        lister.lastParams = null;

        lister.triggerFor(trigger);
        lister
            .on('select', function (e, vals, obj) {
                this.hide();
                if (vals instanceof Array) {
                    valueField.val(_.map(vals, function (item) { return item.value; }).join(','));
                    trigger.val(_.map(vals, function (item) { return item.text; }).join(','));
                }
                else {
                    trigger.val(vals.text);
                    valueField.val(vals.value);
                    if (option.hiddenFields && !option.multi) {
                        _.each(option.hiddenFields, function (item) {
                            element.find('[name=' + item + ']').val(obj[item]).trigger('change');
                        });
                    }
                }
                valueField.trigger('change');
            })
            .on('show', function () {
                var params = {}, depSuccess = true, message = '';
                this.dependents.each(function (index, item) {
                    var elem = $(item);
                    if (elem.val() === '') {
                        depSuccess = false;
                        message = elem.parent().find('label').text();
                        return false;
                    }
                    params[elem.attr('name')] = elem.val();
                });
                if (!depSuccess) {
                    this.hide();
                    util.alert('需要先填写' + message + '！'); return;
                }

                //params.companyId = util.constant.companyId;
                if (_.isEqual(params, this.lastParams)) {
                    return;
                }
                else {
                    var _this = this;
                    $.ajax({
                        url: this.url,
                        type: 'get',
                        dataType: 'json',
                        data: params,
                        beforeSend: function () {
                            loader.show();
                        },
                        complete: function () {
                            loader.hide();
                        },
                        success: function (res) {
                            _this.lastParams = params;
                            var listdata = option.preprocess ? option.preprocess(res.model) : res.model;
                            _this.setList(listdata);
                        }
                    });
                }
            });

        lister.setUrl = function (url) {
            this.url = url;
        };
        lister.setDependents = function (dependents) {
            var _this = this;
            this.dependents = dependents;
            this.dependents.on('change', function () {
                trigger.val('');
                valueField.val('');
                valueField.trigger('change');
                _this.$popup.find('li.current').removeClass('current');
            });
        };

        return lister;
    };

    module.exports = syncSelect;
});
