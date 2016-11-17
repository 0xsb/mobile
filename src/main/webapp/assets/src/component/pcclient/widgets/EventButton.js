define(function (require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');

    var WidgetBase = require('./base');

    var htmlTmpl = '<a href="javascript:" class="btn btn-primary" role="event-button">{{name}}</a>';

    var EventButton = WidgetBase.extend({
        tagName: 'td',
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'EventButton'
        },
        events: {
            'click [role="event-button"]': 'onChange'
        },
        eventTrigger: function (taskId, datas, paramters) {
            var eventName = this.model.get('placeholder'),
                eventParam = this.model.get('eventParam'),
                eventAttr = this.model.get('eventAttributes');

            var withParam = eventAttr.withParam,
                submitType = eventAttr.submitType;

            var data = {
                taskId: taskId,
                formType: eventName
            };

            if (submitType == '1') {
                data.paramterData = JSON.stringify(paramters);
            }
            else if (submitType == '2') {
                data.formData = JSON.stringify(datas);
                data.paramterData = JSON.stringify(paramters);
            }

            eventParam = eventParam.length > 0 ? eventParam + '&' + withParam : withParam;
            $.ajax({
                url: CONTEXT_PATH + '/process/toCustomForm.do?' + eventParam,
                type: 'post',
                dataType: 'json',
                data: data
            })
            .done(function (res) {
                if (res.success) {
                    if (res.model.methodType == '1') {
                        //DoNothing
                    } else if (res.model.methodType == '2') {
                        //DoNothing
                    }
                    alert(res.message);
                }
                else {
                    alert(res.message);
                }
            })
            .fail(function () {
                alert('发生错误！');
            });
        },
        getValue: function () {
            return null;
        },
        setValue: function (val) {
            //DoNothing
        }
    });

    module.exports = EventButton;
});
