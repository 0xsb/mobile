define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var imgViewer = require('./img-viewer');

    var toMap = function (list, key) {
        var newMap = {};
        _.each(list, function (item) {
            newMap[item[key]] = item;
        });
        return newMap;
    };

    var detailTemplate = '<# _.each(list, function (item, index) { #>\
            <# if (item.type === \'item\') { #>\
            <div class="data-item">\
                <label>{{item.name}}</label>\
                <p>{{item.value}}</p>\
            </div>\
            <# } else if (item.type === \'title\') { #>\
            <div class="data-title">\
                <span>{{item.value}}</span>{{item.name}}\
            </div>\
            <#} else if (item.type === \'sum\') { #>\
            <div class="data-item data-sumitem \
                {{ (!list[index + 1] || list[index + 1].type !== \'sum\') ? \'lastsum\' : \'\' }}">\
                <label>合计{{item.name}}</label>\
                <p>{{item.value}}</p>\
            </div>\
            <#} else if (item.type === \'note\') { #>\
            <div class="data-note">\
                <# var note_split = item.value.split(\'<br>\');  _.each(note_split, function (nt, i) { #>\
                    <p>{{nt}}</p>\
                <# }) #>\
            </div>\
            <# } else if (item.type === \'img\') { #>\
            <div class="data-item">\
                <label>{{item.name}}</label>\
                <div class="data-list clearfix">\
                <# _.each(item.value, function (item) { #>\
                    <div class="img-block" data-fullpath="' + CONTEXT_PATH + '/file/download/{{item.id}}/{{item.name}}.do">\
                        <img src="' + CONTEXT_PATH + '/file/download/{{item.id}}/thum_{{item.name}}.do" alt="">\
                    </div>\
                <# }) #>\
                </div>\
            </div>\
            <# } else if (item.type === \'attach\') { #>\
            <div class="data-item">\
                <label>{{item.name}}</label>\
                <div class="data-list clearfix">\
                <# _.each(item.value, function (item) { #>\
                    <a class="attach-block" href="' + CONTEXT_PATH + '/file/download/{{item.id}}/{{item.name}}.do">\
                        <i class="iconfont icon-xinxi"></i>\
                        <span>{{item.filename || item.name}}</span>\
                    </a>\
                <# }) #>\
                </div>\
            </div>\
            <# } else if (item.type === \'richtext\') { #>\
            <div class="data-item">\
                <label>{{item.name}}</label>\
                <div id="{{item.id}}" class="richtext" role="richtext"></div>\
            </div>\
            <# } #>\
        <# }) #>';
    var tabsTemplate = '<# _.each(tabs, function (item) { #>\
        <a href="javascript:" class="tab-switch" data-id="{{item.id}}">{{item.name}}</a>\
        <# }) #>';

    var processes = {
        'Default': function (item) {
            return {
                type: 'item',
                name: this.controlsMap[item.id].describeName,
                value: item.value
            };
        },
        'DDDateRangeField': function (item) {
            var labels = this.controlsMap[item.id].describeName.split(',');
            return [{
                type: 'item',
                name: labels[0],
                value: item.value[0]
            }, {
                type: 'item',
                name: labels[1],
                value: item.value[1]
            }];
        },
        'LinkageSelectField': function (item) {
            var labels = this.controlsMap[item.id].describeName.split(',');
            return [{
                type: 'item',
                name: labels[0],
                value: item.value[0]
            }, {
                type: 'item',
                name: labels[1],
                value: item.value[1]
            }];
        },
        'DDPhotoField': function (item) {
            var imgList = [];
            if (item.value.length > 0) {
                try { imgList = JSON.parse(item.value); }
                catch (ex) {
                    imgList = [];
                    //console.log('image wrong');
                }
            }
            return {
                type: 'img',
                name: this.controlsMap[item.id].describeName,
                value: imgList
            };
        },
        'DDAttachment': function (item) {
            return {
                type: 'attach',
                name: this.controlsMap[item.id].describeName,
                value: item.value.length > 0 ? JSON.parse(item.value) : []
            };
        },
        'UserSelect': function (item) {
            var users = item.value.length > 0 ? JSON.parse(item.value) : [];
            var userNames = _.map(users, function (item) { return item.userName; }).join(',');
            return {
                type: 'item',
                name: this.controlsMap[item.id].describeName,
                value: userNames
            };
        },
        'TableField': function (item) {
            var _this = this;
            var list = [];
            _.each(item.value, function (tableBox, i) {
                list.push({ type: 'title', name: _this.controlsMap[item.id].describeName, value: i + 1});
                var childList = [];
                _.each(tableBox.list, function (childItem, j) {
                    var detailData;
                    var widgetType = childItem.controlName;
                    if (!processes.hasOwnProperty(widgetType)) {
                        widgetType = 'Default';
                    }
                    detailData = processes[widgetType].apply(_this, [childItem]);
                    childList = childList.concat(detailData);
                });
                list = list.concat(childList);
            });
            _.each(item.sumList, function (sum, i) {
                var ctrlId = sum.id.slice(5);   //sum data's id is begin with "total"
                list.push({ type: 'sum', name: _this.controlsMap[ctrlId].describeName, value: sum.value});
            });
            return list;
        },
        'RTEditor': function (item) {
            return {
                type: 'richtext',
                id: item.id,
                name: this.controlsMap[item.id].describeName,
                value: item.value
            };
        }
    };

    var FormDetail = function (element, data, controls) {
        this.$el = element;
        this.template = template(detailTemplate);

        this.attributes = {};
        this.data = data;
        this.dataMap = toMap(data, 'id');
        this.controls = controls;
        this.controlsMap = toMap(controls, 'reName');

        _.each(this.controls, function (item) {
            if (item.controlId === 'TabPage') {
                this.hasTabPage = true;
            }
        }, this);

        if (this.hasTabPage) {
            this.renderTabPage();
        } else {
            this.render();
        }
    };
    FormDetail.prototype.render = function () {
        var dataMap = this.dataMap;
        var html = this.template({
            list: this.preProcess(this.controls, this.dataMap)
        });
        this.$el.html(html);

        if (this.$el.find('[role="richtext"]').length > 0) {
            this.$el.find('[role="richtext"]').each(function () {
                var id = $(this).attr('id');
                $(this).html(dataMap[id].value);
            });
        }

        this.afterRender();
    };
    FormDetail.prototype.renderTabPage = function () {
        var $el = this.$el;
        var pageControls = {};
        pageControls['default'] = [];
        _.each(this.controls, function (item) {
            if (item.controlId === 'TabPage') {
                pageControls[item.reName] = [];
            }
        });
        _.each(this.controls, function (item) {
            if (item.controlId === 'TabPage') return;

            if (item.sequence % 100 !== 0) {
                var parentSequence = Math.floor(item.sequence / 100) * 100;
                if (pageControls['TabPage' + parentSequence]) {
                    pageControls['TabPage' + parentSequence].push(item);
                }
                else {
                    pageControls['default'].push(item);
                }
            } else {
                pageControls['default'].push(item);
            }
        });
        if (pageControls['default'].length === 0) {
            delete pageControls['default'];
        }

        var pages = [], tabs = [];
        _.each(pageControls, function (ctrls, key) {
            var $page = $('<div class="data-page" id="page-' + key + '"></div>');
            var dataMap = key == 'default' ? this.dataMap : toMap(this.dataMap[key].value, 'id');

            var dataList = this.preProcess(ctrls, dataMap);
            $page.html(this.template({ list: dataList }));
            if ($page.find('[role="richtext"]').length > 0) {
                $page.find('[role="richtext"]').each(function () {
                    var id = $(this).attr('id');
                    $(this).html(dataMap[id].value);
                });
            }

            tabs.push({ id: key, name: key == 'default' ? '默认' : this.controlsMap[key].describeName });
            pages.push($page);
        }, this);

        var $tabs = $('<div class="data-tabs clearfix"></div>');
        $tabs.html(template(tabsTemplate)({ tabs: tabs }));

        $el.append($tabs);
        $el.append(pages);

        $tabs.on('click', 'a', function () {
            $tabs.find('a').removeClass('active');
            $(this).addClass('active');

            $el.find('.data-page').hide();
            $el.find('#page-' + $(this).data('id')).show();
        }).find('a').eq(0).click();

        this.afterRender();
    };
    FormDetail.prototype.afterRender = function () {
        this.$el.find('.img-block').on('click', function (e) {
            var $this = $(this);
            var fullImagePath = $this.data('fullpath');
            imgViewer.show(fullImagePath, true);
        });
    };
    FormDetail.prototype.preProcess = function (controls, dataMap) {
        var list = [];
        /* _.each(this.data, function (item, index) {
            var detailData;
            var widgetType = item.controlName;
            if (!processes.hasOwnProperty(widgetType)) {
                widgetType = 'Default';
            }
            detailData = processes[widgetType].apply(this, [item]);

            list = list.concat(detailData);
        }, this); */

        _.each(controls, function (item, index) {
            var detailData;
            if (item.controlId === 'TextNote') {
                detailData = {
                    type: 'note',
                    name: '',
                    value: item.exp
                };
            }
            else {
                var dataItem = dataMap[item.reName];
                if (!dataItem) { return; }
                var widgetType = dataItem.controlName;
                if (!processes.hasOwnProperty(widgetType)) {
                    widgetType = 'Default';
                }
                detailData = processes[widgetType].apply(this, [dataItem]);
            }

            list = list.concat(detailData);
        }, this);

        return list;
    };
    FormDetail.prototype.getData = function () {
        return this.data;
    };
    FormDetail.prototype.setAttribute = function (key, val) {
        this.attributes[key] = val;
    };
    FormDetail.prototype.getAttribute = function (key) {
        return this.attributes[key];
    };

    module.exports = function (element, data, controls) { return new FormDetail(element, data, controls); };
});
