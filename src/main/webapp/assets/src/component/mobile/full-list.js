define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('./loader');
    var util = require('./util');

    var listTemplate = '<div class="mask"><div class="full-list">\
            <div class="list-header"><a href="javascript:" class="back"><i class="iconfont icon-xiangzuo1"></i></a><span role="list-title">列表</span></div>\
            <div class="list-part">\
                <div class="list-search"><input class="search" type="text" placeholder="搜索"></div>\
                <ul class="emp-list"></ul>\
            </div>\
        </div></div>';
    var itemsTemplate = '<# _.each(list, function (item, index) { #>\
            <li data-key="{{item.id}}">\
                <# if (item.avatar) { #>\
                    <div class="avatar" style="background:#{{item.avatar.colorCode}}">\
                        {{item.avatar.subName}}\
                    </div>\
                <# } #>\
                <span class="name">{{item.text}}</span>\
                <# if (item.remark) { #>\
                    <span class="remark">({{item.remark}})</span>\
                <# } #>\
            </li>\
        <# }) #>';
    var itemsRender = template(itemsTemplate);

    var fuzzleSearcher = function (query) {
        var queryList = query.replace(/^\s+|\s+$/g, '').split(/\s+/);
        var regex = '.*';
        for (var i = 0; i < queryList.length; i++) {
            regex += '(' + queryList[i] + ')+.*';
        }
        return new RegExp(regex, 'ig');
    };
    var toMap = function (key, list, parser) {
        var map = {};
        _.each(list, function (item) {
            map[item[key]] = parser ? parser(item) : item;
        });
        return map;
    };

    var FullList = function (options) {
        this.init(options);
    };
    FullList.prototype = {
        init: function (options) {
            var _this = this;
            this.multi = options.multi;
            this.eventMap = {};
            this.$el = $(listTemplate).appendTo(document.body).hide();
            this.$list = this.$el.find('.emp-list');
            this.template = options.template || itemsRender;

            this.$list.on('click', 'li', function (e) {
                var $item = $(this);
                if (_this.multi) {
                    $item.is('.active') ? $item.removeClass('active') : $item.addClass('active');
                }
                else {
                    var key = $item.data('key');
                    _this.trigger('select', [e, _this.map[key]]);
                }
            });
            this.okButton = $('<div class="ok-button">确定</div>').appendTo(this.$el);
            this.okButton.on('click', function (e) {
                var selectedItems = _this.$list.find('li.active').map(function (index, item) {
                    var key = $(this).data('key');
                    return _this.map[key];
                }).toArray();

                _this.trigger('select', [e, selectedItems]);
            });
            this.setMulti(options.multi);


            var inputTimer;
            this.$el.find('.search').on('input', function (e) {
                if (inputTimer) { clearTimeout(inputTimer); }
                var searchVal = $(this).val();
                inputTimer = setTimeout(function () {
                    _this.filter(searchVal);
                    inputTimer = null;
                }, 333);
            });
            this.$el.find('.back').on('click', function (e) {
                _this.hide();
                if (_this.multi) {
                    _this.$list.find('li.active').removeClass('active');
                }
            });
        },
        setMulti: function (multi) {
            this.multi = multi;
            if (this.multi) {
                this.okButton.show();
            }
            else {
                this.okButton.hide();
            }
        },
        setData: function (list) {
            this.list = list;
            this.map = toMap('id', this.list);
            this.render(this.list);
        },
        render: function (list) {
            this.$list.html(this.template({ list: list }));
        },
        filter: function (query) {
            var regex = fuzzleSearcher(query);

            var filterList = _.filter(this.list, function (item) {
                regex.lastIndex = 0;
                return regex.test(item.text);
            });

            this.render(filterList);
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
        },
        show: function () {
            this.$el.show().css({
                'z-index': 11
            });
            var w = this.$el.find('.select-list').width(),
                h = this.$el.find('.select-list').height();
            this.$el.find('.select-list').css({
                'marginLeft': (w / -2) + 'px',
                'marginTop': (h / -2) + 'px'
            });

            this.$el.find('.full-list').css({
                'z-index': 11
            });

            return this.trigger('show');
        },
        hide: function () {
            this.$el.hide();
            return this.trigger('hide');
        },
    };

    module.exports = function (multi, template) {
        return new FullList({ multi: multi, template: template });
    };
});
