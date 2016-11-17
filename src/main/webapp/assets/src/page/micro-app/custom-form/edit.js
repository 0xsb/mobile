define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('component/mobile/loader');
    var util = require('component/mobile/util');
    var WidgetSetting = require('component/custom-form/widget-setting');

    var userList = require('component/mobile/user-list');
    var SelectList = require('component/mobile/select-list');

    var alert = util.alert;
    if (!'a'.startsWith) {
        String.prototype.startsWith = function (str) {
            return (this.match('^' + str) == str);
        };
    }

    var urls = {
        save: CONTEXT_PATH + '/microApp/customForm/customform.do',
        edit: CONTEXT_PATH + '/microApp/customForm/editFrom.do',
        get: CONTEXT_PATH + '/approval/getApprovalTableInfo.do',
        list: CONTEXT_PATH + '/moblicApprove/customform/list.do'
    };

    var goBack = function () {
        location.href = urls.list;
    };

    var iconImages = ['icon_bx', 'icon_bxiao', 'icon_byj', 'icon_cc', 'icon_dh',
        'icon_fk', 'icon_htsp', 'icon_hysp', 'icon_jb', 'icon_kdjs',
        'icon_lxsq', 'icon_qj', 'icon_rb', 'icon_rbb', 'icon_sk',
        'icon_sp', 'icon_syzz', 'icon_ty', 'icon_wc', 'icon_wply',
        'icon_wprk', 'icon_wpsg', 'icon_xwbl', 'icon_yb', 'icon_ybb',
        'icon_ycsq', 'icon_ywlxd', 'icon_yz', 'icon_zb', 'icon_zbb'];
    var createWidgetId = function () {
        var idArr = [];
        var len = 16;
        while (len-- > 0) {
            idArr.push(((Math.random()*16)|0).toString(16));
        }
        return idArr.join('');
    };

    var widgetTypes = {
        'TextField': '文本型',
        'NumberField': '数字型',
        'DDDateField': '日期型',
        'DDSelectField': '下拉选择'
    };
    var defaultModel = {
        type: 'TextField',
        required: false,
        text: '',
        selects: []
    };
    var widgetItemRender = template($('#tmpl-widget').html());
    var widgetMoveRender = template($('#tmpl-widget-move').html());
    var WidgetItem = function (options) {
        options = options || {};
        this.id = createWidgetId();
        this.$el = $('<div class="widget-item"></div>');
        this.$el.attr('id', this.id);

        this.__model = {};
        this.model = options.model || _.clone(defaultModel);

        this.initEvent();
    };
    WidgetItem.prototype = {
        initEvent: function () {
            var _this = this;
            this.setting = new WidgetSetting({
                submit: function (model) {
                    _this.model = model;
                }
            });

            this.$el.on('click', '.widget-type', function () {
                _this.updateText();
                var model = _.clone(_this.model);
                if (model.text === '') {
                    alert('请先填写控件名称，再选择控件类型'); return;
                }

                _this.setting.bind(model).show();
            });

            this.$el.on('click', '.widget-move', function (e) {
                var act = $(this).data('move');
                _this.move(act);
            });
        },
        move: function (act) {
            if (act === 'up') {
                if (this.$el.index === 0) { return; }
                else {
                    this.$el.insertBefore(this.$el.prev());
                }
            }
            else {
                if (this.$el.index === this.$el.parent().children().length - 1) { return; }
                else {
                    this.$el.insertAfter(this.$el.next());
                }
            }
        },
        updateText: function () {
            this.model.text = this.$el.find('.widget-name').val();
        },
        render: function () {
            this.$el.html(widgetItemRender(this.model));
        },
        renderMove: function () {
            this.$el.html(widgetMoveRender(this.model));
        },
        get model () {
            return this.__model;
        },
        set model (model) {
            model.typename = widgetTypes[model.type];
            this.__model = model;
            this.render();
        },
        destory: function () {
            this.setting.destory();
            this.$el.off();
            this.$el.remove();
        },
        turnMove: function (isMoveMode) {
            if (isMoveMode) {
                this.updateText();
                this.renderMove();
            }
            else this.render();
        },
        toJson: function () {
            this.updateText();
            var sequence = (this.$el.index() + 1) * 100;

            var widgetModel = this.model;
            var jsonData = '';
            if (widgetModel.type === 'DDSelectField') {
                jsonData = {
                    options: widgetModel.selects
                };
            }

            var widgetData = {
                describeName: widgetModel.text,
                isRequired: (widgetModel.required === true) ? '1' : '0',
                isEffective: '0',
                isMerge: '0',
                reName: widgetModel.type + sequence.toString(),
                sequence: sequence,
                controlId: widgetModel.type,
                exp: widgetModel.type.startsWith('DD') ? '请选择' : '请输入',
                value: '', //jsonData,
                jsonData: jsonData
            };

            return widgetData;
        }
    };

    var iconRender = template($('#tmpl-icon').html());

    var getJson = function (widgetStore) {
        var jsons = [];
        _.each(widgetStore, function (item) {
            var widgetData = item.toJson();
            jsons.push(widgetData);
        });

        return JSON.stringify(jsons);
    };
    var convertWidgets = function (forminfo) {
        forminfo.sort(function (a, b) { return parseInt(a.sequence) - parseInt(b.sequence); });
        var hasErrorWidgets = false;
        var widgets = _.map(forminfo, function (item) {
            if (!widgetTypes[item.controlId]) { 
                hasErrorWidgets = true; 
                return 'error'; 
            } else {
                var selects = [];
                if (item.controlId === 'DDSelectField') {
                    selects = JSON.parse(item.jsonData).options;
                }

                return {
                    type: item.controlId,
                    required: item.isRequired === '1',
                    text: item.describeName,
                    selects: selects
                };
            }
        });

        if (hasErrorWidgets) {
            return false;
        } else {
            return widgets;
        }
    };
    var setData = function ($list, widgetStore, initData) {
        var widgets = convertWidgets(initData.forminfo);
        if (widgets === false) {
            alert('表单中有复杂控件，请使用管理平台进行编辑！', function () {
                goBack();
            });
            return;
        }

        _.each(widgets, function (item, index) {
            if (item === 'error') {
                hasErrorWidgets = true; return;
            }
            var wg = new WidgetItem({ model: item });
            widgetStore[wg.id] = wg;
            $list.append(wg.$el);
        });
        $('#form-name').val(initData.typename);
        $('.form-icon').attr('src', CONTEXT_PATH + '/assets/src/css/img/formicons/' + initData.icon + '.png');
        $('#icon-name').val(initData.icon);
    };
    var initPage = function (initData) {
        var $list = $('.widgets-list');
        var $addWidget = $('.add-widget');
        var widgetStore = {};

        $addWidget.on('click', function (e) {
            var wg = new WidgetItem();
            widgetStore[wg.id] = wg;

            $list.append(wg.$el);
        });
        $list.on('click', '.widget-remove', function () {
            var id = $(this).parent().attr('id');
            widgetStore[id].destory();
            delete widgetStore[id];
        });

        var imgList = new SelectList({ multi: false, template: iconRender });
        imgList.parser = function (data) {
            return { name: data };
        };
        imgList.setList(iconImages);
        $('.form-icon').on('click', function () {
            imgList.show();
        });
        imgList.on('select', function (e, val, obj) {
            var name = obj;
            $('.form-icon').attr('src', CONTEXT_PATH + '/assets/src/css/img/formicons/' + name + '.png');
            $('#icon-name').val(name);
            this.hide();
        });

        var moveMode = false;
        $('.turn-mode').on('click', function () {
            if (!moveMode) {
                var allHasName = true;
                $list.find('.widget-name').each(function () {
                    if ($(this).val() === '') {
                        allHasName = false;
                        return false;
                    }
                });
                if (!allHasName) {
                    alert('必须填写所有控件名称后才能进行移动!'); 
                    return;
                }
            }
            moveMode = !moveMode;
            $(this).text(moveMode ? '编辑控件' : '移动控件');

            _.each(widgetStore, function (widget) {
                widget.turnMove(moveMode);
            });
        });


        var submitUrl = urls.save,
            id = '';
        if (initData) {
            submitUrl = urls.edit;
            id = initData.id;
            setData($list, widgetStore, initData);
        }
        else {
            $addWidget.click();
        }
        $('.submit').on('click', function () {
            var formname = $('#form-name').val();
            var icon = $('#icon-name').val();
            var widgets = getJson(widgetStore);

            $.ajax({
                url: submitUrl,
                dataType: 'json',
                type: 'post',
                data: {
                    id: id,
                    mostTypeKey: 'rizhiribao',  //TODO 暂时写死
                    name: formname,
                    icon: icon,
                    des: '',
                    control: widgets
                },
                success: function (res) {
                    if (res.success) {
                        alert('提交成功!', function () {
                            goBack();
                        });
                    }
                    else {
                        alert('提交失败!');
                    }
                },
                error: function () {
                    alert('提交出错!');
                }
            });
        });
    };

    var run = function () {
        var id = util.getParam('id');
        if (id) {
            $.ajax({
                url: urls.get,
                type: 'get',
                dataType: 'json',
                data: {
                    typeId: id
                },
                success: function (res) {
                    if (res.success) {
                        res.model.id = id;
                        initPage(res.model);
                    }
                    else {
                        alert(res.message || '读取表单失败！');
                    }
                },
                error: function () {
                    alert('读取表单出错！');
                }
            });
        }
        else {
            initPage();
        }
    };

    module.exports = {
        run: run
    };
});