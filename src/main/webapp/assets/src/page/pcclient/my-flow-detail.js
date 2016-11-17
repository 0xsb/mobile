define(function(require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('component/mobile/util');
    var userList = require('component/mobile/user-list');
    var loader = require('component/mobile/loader');
    var formInit = require('component/mobile/form');
    var detailInit = require('component/mobile/flow-detail');
    var SelectList = require('component/mobile/select-list');
    var fulllist = require('component/mobile/full-list');
    var alert = require('component/Alert');
    var confirm = require('component/Confirm');

    var urls = {
        getDetail: CONTEXT_PATH + '/process/processStartedByMe.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do',
        recovery: CONTEXT_PATH + '/process/cancel.do'
    };
    var statusMap = {
        '100': 'processing', //处理中
        '101': 'draft', //草稿
        '200': 'agree', //同意
        '201': 'complete', //已完成
        '300': 'revoke', //撤销
        '400': 'reassign', //转交
        '500': 'reject', //拒绝
        '600': 'recovery' //回收
    };

    var BaseFormView = Backbone.View.extend({
        getAttribute: function (key) {
            return this.attr.get(key);
        },
        setAttribute: function (key, val) {
            this.attr.set(key, val);
        },
        setData: function (data) {
            this.data = data;
        },
        getData: function () {
            return this.data;
        }
    });


    var OfficialDocModel = Backbone.Model.extend({
        defaults: {
            companyName: '',
            _attachment: [],
            drafter: '',
            draftDept: '',
            draftDate: '',
            docType: '',
            secretLevel: '',
            urgency: '',
            issuedNumber: '',
            distributeDate: '',
            docWhereabouts: '',
            contacts: '',
            contactNumber: '',
            title: '',
            noteAppended: '',
            mainPerson: '',
            copyPerson: '',
            mainBody: ''
        },
        loadInit: function (taskId) {
            var _this = this;
            $.ajax({
                url: CONTEXT_PATH + '/document/getHisValues.do',
                dataType: 'json',
                type: 'post',
                data: { taskId: taskId }
            }).done(function (res) {
                if (res.success) {
                    _this.set(res.model);
                } else {
                    alert(res.message);
                }
            }).fail(function () {
                //do nothing
            });
        }
    });
    var RowView = Backbone.View.extend({
        tagName: 'tr',
        render: function() {
            return this;
        }
    });
    var HeadCellView = Backbone.View.extend({
        tagName: 'th',
        template: template('{{model.describeName}}'),
        render: function() {
            var markup = this.template({
                model: this.model.toJSON()
            });

            this.$el.html(markup);
            return this;
        }
    });
    var DataCellView = HeadCellView.extend({
        tagName: 'td',
        template: template('')
    });
    var PadCellView = DataCellView.extend({
        render: function() {
            return this;
        }
    });
    var textValueRender = template('{{model.value}}');
    var attachmentValueRender = template('<# _.each(model.value, function (item) { #>\
        <a class="attach-block" href="' + CONTEXT_PATH + '/file/download/{{item.id}}/{{item.name}}.do">\
            <i class="iconfont icon-xinxi"></i>\
            <span>{{item.filename || item.name}}</span>\
        </a>\
    <# }) #>');
    var richeditorValueRender = template('{{{model.value}}}');
    var departValueRender =  template('<# var text = _.map(JSON.parse(model.value), function (item) {\
        return item.name;\
    }).join(","); #>{{text}}');
    var ValueCellView = Backbone.View.extend({
        tagName: 'td',
        template: template(''),
        templates: {
            TextField: textValueRender,
            TextareaField: textValueRender,
            DDSelectField: textValueRender,
            NumberField: textValueRender,
            DDMultiSelectField: textValueRender,
            DDDateField: textValueRender,
            DDDateRangeField: textValueRender,
            DDPhotoField: textValueRender,
            DDAttachment: attachmentValueRender,
            MoneyField: textValueRender,
            RTEditor: richeditorValueRender,
            DepartmentSelect: departValueRender
        },
        render: function() {
            var type = this.model.get('controlId');
            var template = this.templates[type] || this.template;
            var markup = template({
                model: this.model.toJSON()
            });
            this.$el.html(markup);
            return this;
        }
    });
    var OfficialDocView = Backbone.View.extend({
        template: template($('#tmpl-officialDoc').html()),
        render: function () {
            var markup = this.template({
                model: this.model.toJSON()
            });
            this.$el.html(markup);
            return this;
        }
    });
    var OfficialDocForm = BaseFormView.extend({
        template: template($('#tmpl-officialDocForm').html()),
        initialize: function () {
            this.attr = new Backbone.Model;
        },
        render: function () {
            this.$el.html(this.template({
                buttons: this.buttons,
                model: this.model.toJSON()
            }));
            this.cacheEls();

            // 版本：表格
            var widgets = this.model.get('widgets');
            var rest = 12; // 当前行剩余单元格
            var currentRow = null;
            _.each(widgets, function (item) {
                // 添加表格行
                if (rest == 12) {
                    currentRow = new RowView();
                    this.$formTable.append(currentRow.render().el);
                }

                var headCell = new HeadCellView({
                    className: 'widget-head',
                    model: new Backbone.Model(item)
                });
                var valueCell = new ValueCellView({
                    model: new Backbone.Model(item)
                });
                var type = valueCell.model.get('controlId');
                var width = valueCell.model.get('field1');
                valueCell.$el.attr('colspan', width - 1);

                var width = item.field1; // cell width
                if (width <= rest) {
                    // 添加 valueCell 到当前行
                    currentRow.$el.append(headCell.render().el, valueCell.render().el);
                    rest -= width;
                    if (rest == 0) rest = 12;
                } else {
                    if (rest > 0) {
                        // 添加补足单元格到当前行
                        var padCell = new PadCellView({
                            attributes: {
                                colspan: rest
                            }
                        });
                        currentRow.$el.append(padCell.render().el);
                        rest = 12; // 重置
                    }
                    // 添加行，将当前行设置成新添加的行
                    currentRow = new RowView();
                    this.$formTable.append(currentRow.render().el);

                    // 添加 valueCell 到当前行
                    currentRow.$el.append(headCell.render().el, valueCell.render().el);
                    rest -= width;
                    if (rest == 0) rest = 12;
                }
            }, this);

            return this;
        },
        cacheEls: function () {
            this.$formTable = this.$('[role="doc-form"]');
        },
        getParamters: function () {
            return this.getAttribute('paramters');
        },
        getDataAndParamters: function () {
            return {
                data: this.getData(),
                paramterData: this.getParamters()
            };
        }
    });
    var historyRender = template($('#tmpl-history').html());

    var OffocialFormCollectionView = Backbone.View.extend({
        template: template($('#tmpl-formCollection').html()),
        previewTemplate: template($('#tmpl-officialDoc').html()),
        events: {
            'click [data-do="agree"]': 'doAgree',
            'click [data-do="reject"]': 'doReject',
            'click [data-do="relay"]': 'doRelay',
            'click [data-do="recovery"]': 'doRecovery'
        },
        initialize: function (options) {
            this.buttons = options.buttons;
            this.forms = {};
            this.docModel = new OfficialDocModel({});
            this.docView = new OfficialDocView({
                el: '#official-doc',
                model: this.docModel
            });
            this.docModel.loadInit(pageParams.taskId);

            this.render();
        },
        render: function () {
            this.$el.html(this.template({
                buttons: this.buttons
            }));
        },
        addForm: function (form) {
            var formID = form.getAttribute('id');
            this.forms[formID] = form;
            this.listenTo(form, 'change', this.onChange);
            this.listenTo(form, 'eventbutton', this.onEventButton);

            this.$el.append(form.el);
            this.onChange();
        },
        onChange: function () {
            var paramters = {};
            _.each(this.forms, function (form, key) {
                var p = form.getParamters();
                paramters = _.extend(paramters, p);
            }, this);
            this.docModel.set(paramters);
        },
        onEventButton: function (button) {
            var datas = [], paramters = {};
            _.each(this.forms, function (form, key) {
                var dnp = form.getDataAndParamters();
                datas.push({
                    id: form.getAttribute('id'),
                    title: form.getAttribute('title'),
                    data: dnp.data
                });
                paramters = _.extend(paramters, dnp.paramterData);
            }, this);
            button.eventTrigger(pageParams.taskId, datas, paramters);
        },
        doAgree: function () {
            popupShow['agree'](pageParams);
        },
        doReject: function () {
            popupShow['reject'](pageParams);
        },
        doRelay: function () {
            popupShow['relay'](pageParams);
        },
        doRecovery: function () {
            popupShow['recovery'](pageParams);
        },
    });

    var initPage = function(model, pageParams) {
        var tasksList = model.tasksList;
        var tasksFormsData = model.tasksFormsData[0];
        var forms = tasksFormsData.forms;
        var formsData = toMap(tasksFormsData.formsData, 'id');

        var formViews = new OffocialFormCollectionView({
            el: $('#official-doc-form'),
            buttons: []
        });
        _.each(forms, function (form, i) {
            var formView;
            // 显示表单信息
            var widgets = form.widgets;
            var widgetMap = toMap(widgets, 'reName');
            var datas = formsData[form.formID].data;
            var dataMap = toMap(datas, 'id');
            _.each(widgetMap, function (widget, id) {
                var data = dataMap[id];
                if (data) {
                    var value = data.value;
                    if (widget.controlId == 'DDAttachment') {
                        try {
                            value = $.parseJSON(value);
                        } catch (e) {
                            value = [];
                        }
                    }
                    widget.value = value;
                }
            });
            var formData = {
                companyName: COMPANY_NAME,
                formId: form.formId,
                widgets: _.values(widgetMap)
            };
            formView = new OfficialDocForm({
                tagName: 'div',
                model: new Backbone.Model(formData)
            });
            formView.render();
            // 显示（预览）公文
            var docData = {
                companyName: COMPANY_NAME
            };
            var paramters = form.paramters;
            _.each(paramters, function (param, key) {
                var controlId = param.controlID;
                var data = dataMap[controlId];
                if (data) {
                    docData[key] = data.value;
                }
            });

            formView.setData(datas);
            formView.setAttribute('paramters', docData);
            formViews.addForm(formView);

            formView.setAttribute('id', form.formID);
            formView.setAttribute('title', form.formName);
        });

        var historyData = {
            list: convertHistory(model.tasksList)
        };
        $('.detail-history').html(historyRender(historyData)).show();
        $('.btn[data-do="histroy"]').on('click', function () {
            $('#history-popup').show();
        });
        $('#history-popup').on('click', function () {
            $('#history-popup').hide();
        });

        // formView.$('[role="history"]').html(historyRender(historyData)).show();
    };
    var pageParams;
    var run = function() {
        var taskId = util.getParam('taskId');
        var thirdId = util.getParam('thirdId');
        var mode = util.getParam('type');
        pageParams = {
            taskId: taskId,
            thirdId: thirdId,
            mode: mode
        };

        $.ajax({
            url: urls.getDetail,
            type: 'get',
            dataType: 'json',
            data: {
                taskId: taskId
            },
            success: function(res) {
                if (res.success) {
                    initPage(res.model, pageParams);
                } else {
                    alert(res.message || '读取流程信息失败！');
                }
            },
            error: function() {
                alert('发生错误！');
            }
        });
    };

    function toMap(list, key) {
        var newMap = {};
        _.each(list, function(item) {
            newMap[item[key]] = item;
        });
        return newMap;
    };

    function convertUserData(list) {
        var newlist = _.map(list, function(item) {
            return {
                id: item.userId,
                text: item.userName
            };
        });
        return newlist;
    };

    function convertHistory(history) {
        return _.map(history, function(item, index) {
            return {
                subName: item.assignee.slice(-2),
                userName: item.assignee,
                statusText: index === 0 ? '发起' : item.status,
                statusClass: statusMap[item.statusCode],
                opinion: item.opinion === '' ? '' : '(' + item.opinion + ')',
                colorCode: '#' + util.transColorCode(item.assignee),
                time: item.endTime ? util.formatDate(new Date(item.endTime), 'yyyy-MM-dd HH:mm:ss') : ''
            };
        });
    };

    function closeWindow() {
        window.opener.close();
    }

    module.exports = {
        run: run
    };
});
