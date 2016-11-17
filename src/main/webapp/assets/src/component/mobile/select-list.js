define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('./util');

    var isDecimal = function (val) { return /^\d+(\.\d*){0,1}$/.test(val); };
    var popupTemplate = '<div class="mask"><div class="select-list">\
        <ul></ul>\
        </div></div>';
    var itemTemplate = '<# _.each(list, function (item, index) { #>\
            <li data-key="{{item.value}}" data-index="{{index}}">{{item.text}}</li>\
        <# }) #>';
    var listRender = template(itemTemplate);

    var selectList = function (option) {
        this.init(option);
    };
    selectList.prototype = {
        init: function (option) {
            this.$popup = $(popupTemplate);
            this.$popup.hide().appendTo(document.body);
            this.multi = option.multi;
            this.eventMap = {};
            this.template = option.template || listRender;

            if (option.couldAdd) {
                this.initAddPanel(option);
            }
            if (this.multi) {
                this.initSubmitPanel();
            }

            var fieldSet = {
                textField: option.textField || 'text',
                valueField: option.valueField || 'value'
            };
            this.parser = option.parser || function (data) {
                return { text: data[fieldSet.textField], value: data[fieldSet.valueField] };
            };

            var $list = this.$popup.find('ul'),
                startScroll = { top: 0 };
            util.disableMove(this.$popup);
            util.touchMove($list, {
                start: function (startPoints, isMulti) {
                    if (!isMulti) startScroll = { top: $list[0].scrollTop };
                },
                move: function (info, isMulti) {
                    if (!isMulti) {
                        //$viewer[0].scrollLeft = startScroll.left + info.startPoints.x1 - info.currentPoints.x1;
                        $list[0].scrollTop = startScroll.top + info.startPoints.y1 - info.currentPoints.y1;
                    }
                }
            });

            var _this = this;
            this.$popup.on('click', function (e) {
                var elem = $(e.target);
                if (elem.is('li')) {
                    if (_this.multi) {
                        elem.is('.current') ? elem.removeClass('current') : elem.addClass('current');
                        var objs = [];
                        var vals = _this.$popup.find('li.current').map(function (index, item) {
                                objs.push(_this.dataList[$(item).data('index')]);
                                return { value: $(item).data('key'), text: $(item).text() };
                            }).toArray();
                        _this.trigger('select', [e, vals, objs, elem, this]);
                    }
                    else {
                        _this.$popup.find('li.current').removeClass('current');
                        elem.addClass('current');
                        var obj = _this.dataList[$(elem).data('index')];
                        var val = { value: $(elem).data('key'), text: $(elem).text() };
                        _this.trigger('select', [e, val, obj, elem, this]);
                    }
                }
                else {
                    _this.hide();
                }
            });
        },
        initSubmitPanel: function () {
            var _this = this;
            var submitBtn = $('<a class="list-submit-btn" href="javascript:">确定</a>');

            this.$popup.find('.select-list').append(submitBtn);
            submitBtn.on('click', function (e) {
                _this.hide();
                e.stopPropagation();
            });
        },
        initAddPanel: function (option) {
            var _this = this;
            var newBtn = $('<a class="list-add-btn" href="javascript:">新建项</a>');
            var newPanel = $('<form class="list-add">\
                    <h6>新建项</h6>\
                    <input type="text">\
                    <div class="buttons">\
                        <a href="javascript:" data-do="add">确定</a>\
                        <a href="javascript:" data-do="cancle">取消</a>\
                    </div>\
                </form>').hide();
            this.$popup.find('.select-list').prepend(newBtn);
            this.$popup.append(newPanel);

            newBtn.on('click', function (e) {
                newPanel[0].reset();
                _this.$popup.find('.select-list').hide();
                newPanel.show();

                var w = newPanel.width(),
                    h = newPanel.height();
                newPanel.css({
                    'marginLeft': (w / -2) + 'px',
                    'marginTop': (h / -2) + 'px'
                });

                e.stopPropagation();
            });
            newPanel.on('click', function (e) {
                var elem = $(e.target);
                if (elem.is('[data-do=add]')) {
                    val = newPanel.find('input').val();
                    _this.$popup.find('.select-list').show().find('li.current').removeClass('current');
                    newPanel.hide();
                    _this.trigger('select', [e, { text: val, value: val }, this]).hide();
                }
                else if (elem.is('[data-do=cancle]')) {
                    _this.$popup.find('.select-list').show();
                    newPanel.hide();
                }
                e.stopPropagation();
            });

            if (option.Number === true) {
                newPanel.find('input[type=text]').on('input', function (e) {
                    var val = $(this).val();
                    if (!isDecimal(val)) {
                        e.target.value = val.slice(0,-1);
                        e.preventDefault();
                        return false;
                    }
                });
            }
        },
        triggerFor: function (triggerEl) {
            var _this = this;
            this.$trigger = triggerEl;
            this.$trigger.on('click', function () {
                _this.show();
            });

            return this;
        },
        setList: function (list) {
            var _this = this;
            this.dataList = list;
            list = _.map(list, function (item) { return _this.parser(item); });
            this.$popup.find('ul').html(this.template({ list: list }));

            return this;
        },
        show: function () {
            this.$popup.show();
            var w = this.$popup.find('.select-list').width(),
                h = this.$popup.find('.select-list').height();
            this.$popup.find('.select-list').css({
                'marginLeft': (w / -2) + 'px',
                'marginTop': (h / -2) + 'px'
            });

            return this.trigger('show');
        },
        hide: function () {
            this.$popup.hide();
            return this.trigger('hide');
        },
        trigger: function (event, args) {
            if (this.eventMap[event]) {
                var _this = this;
                _.each(this.eventMap[event], function (func) {
                    func.apply(_this, args);
                });
            }
            return this;
        },
        on: function (event, func) {
            return this.bind(event, func);
        },
        bind: function (event, func) {
            if (!this.eventMap[event]) { this.eventMap[event] = []; }
            if (this.eventMap[event].indexOf(func) < 0) {
                this.eventMap[event].push(func);
            }
            return this;
        },
        unbind: function (event, func) {
            if (this.eventMap[event]) {
                if (!func) {
                    while (this.eventMap[event].length > 0) { this.eventMap[event].pop(); }
                }
                else {
                    var index = this.eventMap[event].indexOf(func);
                    if (index >= 0) {
                        this.eventMap[event].splice(index, 1);
                    }
                }
            }
            return this;
        }
    };

    module.exports = selectList;
});
