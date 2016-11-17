define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('component/mobile/util');
    var userList = require('component/mobile/user-list');
    var loader = require('component/mobile/loader');
    //var formInit = require('component/mobile/form');
    //var detailInit = require('component/mobile/flow-detail');
    var SelectList = require('component/mobile/select-list');
    var fulllist = require('component/mobile/full-list');
    var widgetFactory2 = require('component/pcclient/widgets-factory2');

    var alert = require('component/Alert');
    var confirm = require('component/Confirm');

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

    var urls = {
        getDetail: CONTEXT_PATH + '/workflow/taskDetail.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do',

        // agree: CONTEXT_PATH + '/workflow/agree.do',
        agree: CONTEXT_PATH + '/document/agree.do',
        next: CONTEXT_PATH + '/document/selectNextTask.do',
        selectUser: CONTEXT_PATH + '/workflow/selectUser.do',
        reject: CONTEXT_PATH + '/process/reject.do',
        relay: CONTEXT_PATH + '/process/reassign.do',
        recovery: CONTEXT_PATH + '/process/recovery.do'
    };
    var MODE_MYLAUNCH = 0, //我的发起
        MODE_WAITAPPROVEL = 1, //待我审批
        MODE_APPROVELED = 2, //我已审批
        MODE_WAITREAD = 3; //待我阅览
    var modeStatus = {
        'wdfq': MODE_MYLAUNCH,
        'dwsp': MODE_WAITAPPROVEL,
        'wysp': MODE_APPROVELED,
        'dwyl': MODE_WAITREAD
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
    //实际共4种动作，[agree][reject][recovery][relay]
    //这个配置与admin项目中edition-third下flow\flowchart.js中的配置项匹配
    var buttonsSet = {
        '1': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '拒绝',
            action: 'reject'
        }],
        '2': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '拒绝',
            action: 'reject'
        }, {
            text: '转发',
            action: 'relay'
        }],
        '3': [{
            text: '提交',
            action: 'agree'
        }],
        '4': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '驳回',
            action: 'recovery'
        }],
        '5': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '驳回',
            action: 'recovery'
        }, {
            text: '转发',
            action: 'relay'
        }],
        '6': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '拒绝',
            action: 'reject'
        }, {
            text: '驳回',
            action: 'recovery'
        }],
        '7': [{
            text: '同意',
            action: 'agree'
        }, {
            text: '转发',
            action: 'relay'
        }]
    };
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
        render: function () {
            return this;
        }
    });
    var HeadCellView = Backbone.View.extend({
        tagName: 'th',
        template: template('{{model.describeName}}'),
        render: function () {
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
        render: function () {
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
        render: function () {
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
        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
        },
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

    function formDataParse (resp) {
        resp = _.extend({
            success: false,
            model: {}
        }, resp);

        var form = {};
        if (resp.success) {
            var model = resp.model;
            form = model.form;
            form.button = model.button;
            form.next = model.next;
        }

        return form;
    }
    var WritebleOfficialDocView = BaseFormView.extend({
        tagName: 'form',
        template: template($('#tmpl-officialDocForm').html()),

        initialize: function (options) {
            this.widgets = {};
            this.attr = new Backbone.Model;
            this.listenTo(this.model, 'change', this.render);
        },
        render: function () {
            this.$el.html(this.template({
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

                // 添加 widget
                var widget = widgetFactory2(item);
                var headCell = new HeadCellView({
                    className: 'widget-head',
                    model: new Backbone.Model(item)
                });

                if (widget instanceof Backbone.View) {
                    this.listenTo(widget, 'change', this.onChange);

                    var type = widget.model.get('type');
                    var width = widget.model.get('width');
                    widget.$el.attr('colspan', width - 1);

                    this.widgets[item.reName] = widget;

                    var width = item.field1;
                    if (width <= rest) {
                        // 添加 widget 到当前行
                        currentRow.$el.append(headCell.render().el, widget.el);
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

                        // 添加 widget 到当前行
                        currentRow.$el.append(headCell.render().el, widget.render().el);
                        rest -= width;
                        if (rest == 0) rest = 12;
                    }
                }
            }, this);
        },
        onChange: function (value, model, widget) {
            var type = model.get('type');
            var name = model.get('alias');
            if (type == 'DDAttachment') {
                name = '_attachment';
            }
            else if (type == 'EventButton') {
                var buttonWidget = this.widgets[model.get('id')];
                //var dataNParam = this.getDataAndParamters();
                //buttonWidget.eventTrigger(pageParams.taskId, dataNParam.data, dataNParam.paramterData);
                this.trigger('eventbutton', buttonWidget);
                return;
            }
            //this.doc.set(name, value);
            this.trigger('change', this.model);
        },
        getDataAndParamters: function () {
            var data = [];
            var paramterData = {};
            _.each(this.widgets, function (widget) {
                var model = widget.model;
                var type = model.get('type');
                switch (type) {
                    case 'TextField':
                    case 'TextareaField':
                    case 'DDMultiSelectField':
                    case 'DDSelectField':
                    case 'DDDateField':
                    case 'DDDateRangeField':
                    case 'DDPhotoField':
                    case 'RTEditor':
                    case 'DepartmentSelect':
                        var alias = model.get('alias');
                        var value = widget.getValue();
                        paramterData[alias] = value;
                        widgetData = {
                            controlName: type,
                            id: model.id,
                            value: widget.getValue()
                        };
                        data.push(widgetData);
                        break;
                    case 'DDAttachment':
                        var attachments = widget.getValue();
                        var value = [];
                        _.each(attachments, function (attachment) {
                            value.push({
                                id: attachment.id,
                                filename: attachment.name,
                                name: attachment.addr
                            });
                        });
                        paramterData['_attachment'] = attachments;
                        widgetData = {
                            controlName: type,
                            id: model.id,
                            value: JSON.stringify(value)
                        };
                        data.push(widgetData);
                        break;
                    default:
                        break;
                }
            });

            return {data: data, paramterData: paramterData};
        },
        setData: function (data) {
            _.each(data, function (item) {
                var widget = this.widgets[item.id];
                if (widget) {
                    widget.setValue(item.value);
                }
            }, this);
        },
        getData: function () {
            return this.getDataAndParamters().data;
        },
        getParamters: function () {
            return this.getDataAndParamters().paramterData;
        },
        cacheEls: function () {
            this.$formTable = this.$('[role="doc-form"]');
            //this.$preview = $('#official-doc'); //this.$('[role="doc-preview"]');
        }
    });

    var historyRender = template($('#tmpl-history').html());
    var nextNodesRender = template($('#tmpl-nextNode').html());

    /*var createFormsSet = function(model, mode) {
        var forms = [];
        if (modeStatus[mode] === MODE_WAITAPPROVEL) {
            if (model.taskConfig.__success) { //继承表单
                forms = forms.concat(model.forms);
                _.each(forms, function(item, index) {
                    item.writeble = model.taskConfig.__writable;
                    item.indexId = item.formID + '-' + index.toString();
                });
            }
            if (model.currTaskForm && model.currTaskForm.formID) { //有当前表单存在
                model.currTaskForm.writeble = true;
                model.currTaskForm.isCurrent = true;
                model.currTaskForm.indexId = model.currTaskForm.formID + '-' + forms.length.toString();
                forms.push(model.currTaskForm);
            }
            if (model.currentNoSuccessForm) {
                model.currentNoSuccessForm.writeble = true;
                model.currentNoSuccessForm.isNoSuccess = true;
                forms.push(model.currentNoSuccessForm);
            }
        } else {
            forms = forms.concat(model.forms);
            _.each(forms, function(item, index) {
                item.writeble = false;
                item.indexId = item.formID + '-' + index.toString();
            });
        }
        return forms;
    };*/

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

    var getFormsData = function (formViews) {
        var formsData = [],
            paramters = {},
            CurrentNoSuccessFormData;
        _.each(formViews.forms, function (formView) {
            var itemData = {},
                itemParam;
            itemData.id = formView.getAttribute('id');
            itemData.data = formView.getData();
            itemData.title = formView.getAttribute('title');

            if (formView.getAttribute('isNoSuccess')) { //不可继承表单
                CurrentNoSuccessFormData = itemData;
            } else {
                formsData.push(itemData);
            }

            if (formView.getParamters) {
                var paramterMap = formView.getAttribute('paramterMap');
                itemParam = formView.getParamters(paramterMap, itemData.data); //可编辑表单
            } else {
                itemParam = formView.getAttribute('paramters'); //详情
            }
            _.extend(paramters, itemParam);
        });

        var result = {
            formData: formsData,
            paramterData: paramters
        };
        if (CurrentNoSuccessFormData) {
            result['CurrentNoSuccessFormData'] = CurrentNoSuccessFormData;
        }
        return result;
    };
    var convertUserData = function (list) {
        var newlist = _.map(list, function (item) {
            return {
                id: item.userId,
                text: item.userName
            };
        });
        return newlist;
    };
    var convertHistory = function (history) {
        return _.map(history, function (item, index) {
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
    var popupPos = function ($popup) {
        var w = $popup.find('.approve-modal').width(),
            h = $popup.find('.approve-modal').height();
        $popup.find('.approve-modal').css({
            'marginLeft': (w / -2) + 'px',
            'marginTop': (h / -2) + 'px'
        });
    };
    var popupShow = {
        'agree': function (pageParams) {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').hide();
            $('#approve-popup').find('.btn-apply').data('action', 'agree');
            popupPos($('#approve-popup'));
        },
        'next': function (pageParams) {
            var markup = nextNodesRender({
                taskId: pageParams.taskId,
                items: pageParams.nextNodes
            });
            $('#next-node-popup').find('.next-nodes').html(markup);
            $('#next-node-popup').show();
            $('#next-node-popup').find('.btn-apply').data('action', 'next');
            popupPos($('#next-node-popup'));
        },
        'selectuser': function (pageParams) {
            $('#select-user-popup').show();
            popupPos($('#select-user-popup'));
        },
        'reject': function (pageParams) {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').hide();
            $('#approve-popup').find('.btn-apply').data('action', 'reject');
            popupPos($('#approve-popup'));
        },
        'relay': function (pageParams) {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').show();
            $('#approve-popup').find('.btn-apply').data('action', 'relay');
            popupPos($('#approve-popup'));
        },
        'recovery': function () {
            $('#approve-popup').show();
            $('#approve-popup').find('.user-select').hide();
            $('#approve-popup').find('.btn-apply').data('action', 'recovery');
            popupPos($('#approve-popup'));
        }
    };
    var actions = {
        //同意
        'agree': function ($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.agree,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    model: dataJson
                },
                beforeSend: function () {
                    loader.show();
                },
                success: function (res) {
                    if (res.success) {
                        $popup.trigger('hide');
                        var nextNodes = res.model.nextNodes;
                        if (_.isArray(nextNodes)) {
                            pageParams.nextNodes = nextNodes;
                            popupShow['next'](pageParams);
                        } else {
                            var users = convertUserData(res.model.users);
                            pageParams.isEnd = res.model.isEnd;
                            pageParams.opinion = opinion;
                            pageParams.dataJson = dataJson;
                            if (users.length === 1 || pageParams.isEnd) {
                                pageParams.nextUser = pageParams.isEnd ? '' : users[0].id;
                                actions['selectuser']($('#select-user-popup'), pageParams.opinion, pageParams, formViews);
                            } else {
                                pageParams.selectUserList.setMulti(res.model.userTaskType == '1');
                                pageParams.selectUserList.setData(users);
                                popupShow['selectuser'](pageParams);
                                pageParams.selectUserList.$el.find('.ok-button').css({
                                    'position': 'absolute',
                                    'z-index': 12,
                                    'right': 10,
                                    'top': 10,
                                    'color': '#3386c0'
                                });
                            }
                        }
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function () {
                    alert('提交出错');
                },
                complete: function () {
                    loader.hide();
                }
            });
        },
        'next': function ($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);
            var data = {};
            if (_.isArray(opinion)) {
                _.each(opinion, function (field) {
                    data[field.name] = field.value;
                });
            }
            $.ajax({
                url: urls.next,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: data.taskId,
                    nextTaskDefId: data.nextTaskDefId
                },
                beforeSend: function () {
                    loader.show();
                },
                success: function (res) {
                    if (res.success) {
                        $popup.trigger('hide');
                        var model = _.extend({
                            isEnd: false
                        }, res.model);
                        var users = convertUserData(model.users);
                        pageParams.isEnd = model.isEnd;
                        pageParams.opinion = opinion;
                        pageParams.dataJson = dataJson;
                        if (users.length === 1 || pageParams.isEnd) {
                            pageParams.nextUser = pageParams.isEnd ? '' : users[0].id;
                            actions['selectuser']($('#select-user-popup'), pageParams.opinion, pageParams, formViews);
                        } else {
                            pageParams.selectUserList.setMulti(model.userTaskType == '1');
                            pageParams.selectUserList.setData(users);
                            pageParams.selectUserList.$el.find('.ok-button').css({
                                'position': 'absolute',
                                'z-index': 12,
                                'right': 10,
                                'top': 10,
                                'color': '#3386c0'
                            });
                            popupShow['selectuser'](pageParams);
                        }
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function () {
                    alert('提交出错');
                },
                complete: function () {
                    loader.hide();
                }
            });
        },
        //选择办理人(同意的第二步)
        'selectuser': function ($popup, opinion, pageParams, formViews) {
            if (pageParams.nextUser === '' && !pageParams.isEnd) {
                alert('请选择办理人！');
                return;
            }

            $.ajax({
                url: urls.selectUser,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    nextAssignee: pageParams.nextUser,
                    thirdDealId: pageParams.thirdId
                        //model: dataJson
                },
                beforeSend: function () {
                    loader.show();
                },
                success: function (res) {
                    if (res.success) {
                        alert('提交成功！', function () {
                            closeWindow(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function () {
                    alert('提交出错');
                },
                complete: function () {
                    loader.hide();
                }
            });
        },
        //拒绝
        'reject': function ($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.reject,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    model: dataJson,
                    thirdDealId: pageParams.thirdId
                },
                beforeSend: function () {
                    loader.show();
                },
                success: function (res) {
                    if (res.success) {
                        alert('提交成功！', function() {
                            closeWindow(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function () {
                    alert('提交出错');
                },
                complete: function () {
                    loader.hide();
                }
            });
        },
        //转交
        'relay': function ($popup, opinion, pageParams, formViews) {
            var userId = $popup.find('input[name=userid]').val();
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.relay,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    userId: userId,
                    opinion: opinion,
                    model: dataJson,
                    thirdDealId: pageParams.thirdId
                },
                beforeSend: function () {
                    loader.show();
                },
                success: function (res) {
                    if (res.success) {
                        alert('提交成功！', function() {
                            closeWindow(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function () {
                    alert('提交出错');
                },
                complete: function () {
                    loader.hide();
                }
            });
        },
        //驳回(到上一步)
        'recovery': function ($popup, opinion, pageParams, formViews) {
            var formsData = getFormsData(formViews);
            var dataJson = JSON.stringify(formsData);

            $.ajax({
                url: urls.recovery,
                dataType: 'json',
                type: 'post',
                data: {
                    taskId: pageParams.taskId,
                    opinion: opinion,
                    thirdDealId: pageParams.thirdId,
                    model: dataJson
                },
                beforeSend: function () {
                    loader.show();
                },
                success: function (res) {
                    if (res.success) {
                        alert('提交成功！', function() {
                            closeWindow(pageParams.mode);
                        });
                    } else {
                        alert(res.message || '提交失败');
                    }
                },
                error: function () {
                    alert('提交出错');
                },
                complete: function () {
                    loader.hide();
                }
            });
        },
    };
    var initSelectUser = function (pageParams, formViews) {
        var $popup = $('#select-user-popup');
        var selectUserList = pageParams.selectUserList;
        $popup.find('textarea').on('click', function () {
            selectUserList.show();
        });
        selectUserList.on('select', function (e, user) {
            if (user instanceof Array) {
                var texts = [],
                    ids = [];
                _.each(user, function (item) {
                    texts.push(item.text);
                    ids.push(item.id);
                    $popup.find('textarea').val(texts.join(','));
                    $popup.find('[role="selected-user"]').val(ids.join(','));
                });
            } else {
                $popup.find('textarea').val(user.text);
                $popup.find('[role="selected-user"]').val(user.id);
            }
            selectUserList.hide();
        });
        $popup.find('.btn-cancle').on('click', function () {
            $popup.trigger('hide');
        });
        $popup.on('hide', function () {
            $popup.find('textarea').val('');
            $popup.find('[role="selected-user"]').val('');
            $popup.hide();
        });
        $popup.find('.btn-apply').on('click', function () {
            pageParams.nextUser = $popup.find('[role="selected-user"]').val();
            if (selectUserList.multi) {
                pageParams.nextUser = JSON.stringify(pageParams.nextUser.split(','));
            }
            if (pageParams.nextUser.length === 0 || pageParams.nextUser === '[]') {
                alert('请选择办理人！');
                return;
            }

            actions['selectuser']($popup, pageParams.opinion, pageParams, formViews);
        });
    };
    var initPopup = function (pageParams, formViews) {
        var $popup = $('#approve-popup');
        $popup.find('.btn-apply').on('click', function () {
            var action = $(this).data('action');
            var opinion = $popup.find('textarea').val();
            actions[action]($popup, opinion, pageParams, formViews);
        });

        var empTrigger = $popup.find('input[name=username]'),
            empInput = $popup.find('input[name=userid]');
        var empSelect = userList.employeeSelect();
        empTrigger.on('click', function () {
            empSelect.show();
        });
        empSelect.on('select', function (e, emp) {
            empTrigger.val(emp.userName);
            empInput.val(emp.userId);
        });

        var commonAppTrigger = $popup.find('[data-do="common-app"]');
        var commonAppList = new SelectList({
            multi: false
        });
        commonAppList.setList([{
                text: '同意',
                value: '同意'
            }, {
                text: '已阅',
                value: '已阅'
            }, {
                text: '拒绝',
                value: '拒绝'
            }])
            .triggerFor(commonAppTrigger)
            .on('select', function (e, val) {
                $popup.find('textarea').val(val.text);
                this.hide();
            });

        $popup.find('.btn-cancle').on('click', function () {
            $popup.trigger('hide');
        });
        $popup.on('hide', function () {
            empTrigger.val('');
            empInput.val('');
            $popup.find('textarea').val('');
            $popup.hide();
        });

        var $nextPopup = $('#next-node-popup');
        $nextPopup.find('.btn-cancle').on('click', function () {
            $nextPopup.trigger('hide');
        });
        $nextPopup.find('.btn-apply').on('click', function () {
            var action = $(this).data('action');
            var option = $nextPopup.find(':input').serializeArray();
            actions['next']($nextPopup, option, pageParams, formViews);
        });
        $nextPopup.on('hide', function () {
            $nextPopup.find('.next-nodes').empty();
            $nextPopup.hide();
        });
    };
    var initPage = function (model, pageParams) {
        var tasksList = model.tasksList;
        //var tasksFormsData = model.tasksFormsData[0];
        var fromsData = toMap(model.fromsData, 'id');
        var writeble = pageParams.mode == 'wysp' ? false : model.taskConfig.__writable;
        var buttons = pageParams.mode == 'wysp' ? [] : buttonsSet[model.taskConfig.__button];

        var formViews = new OffocialFormCollectionView({
            el: $('#official-doc-form'),
            buttons: buttons
        });
        if (model.currTaskForm && pageParams.mode != 'wysp') {
            var currentModel = new Backbone.Model(pageParams);
            _.extend(currentModel, {
                url: CONTEXT_PATH + '/workflow/getStartForm.do',
                parse: formDataParse
            });
            var currentFormView = new WritebleOfficialDocView({
                tagName: 'div',
                model: currentModel
            });
            currentModel.set(model.currTaskForm);
            currentFormView.setAttribute('id', model.currTaskForm.formID);
            currentFormView.setAttribute('title', model.currTaskForm.formName);

            formViews.addForm(currentFormView);
        }
        _.each(model.forms, function (form, i) {
            var formView;
            if (writeble) {
                try {
                    var formModel = new Backbone.Model(pageParams);
                    _.extend(formModel, {
                        url: CONTEXT_PATH + '/workflow/getStartForm.do',
                        parse: formDataParse
                    });
                    formView = new WritebleOfficialDocView({
                        tagName: 'div',
                        model: formModel
                    });
                    // 加载表单数据，自动渲染表单
                    formModel.set(form);

                    var formData = fromsData[form.formID];
                    if (formData) {
                        formView.setData(formData.data);
                    }

                    formViews.addForm(formView);
                } catch (ex) {
                    util.alert('表单配置有错，请联系管理员！', function () {
                        closeWindow();
                    });
                }
            } else {
                // 显示表单信息
                var widgets = form.widgets;
                var widgetMap = toMap(widgets, 'reName');
                var datas = fromsData[form.formID].data;
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
            }

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

        var selectUserList = fulllist(false);
        pageParams.selectUserList = selectUserList;
        initSelectUser(pageParams, formViews);
        initPopup(pageParams, formViews);
    };
    var pageParams;
    var run = function () {
        var taskId = util.getParam('taskId'),
            thirdId = util.getParam('thirdId'),
            mode = util.getParam('type');

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
            success: function (res) {
                if (res.success) {
                    initPage(res.model, pageParams);
                } else {
                    alert(res.message || '读取流程信息失败！', function () {
                        closeWindow(pageParams.mode);
                    });
                }
            },
            error: function () {
                alert('发生错误！', function () {
                    closeWindow(mode);
                });
            }
        });

        $('.back').on('click', function () {
            closeWindow(pageParams.mode);
        });
    };

    function toMap (list, key) {
        var newMap = {};
        _.each(list, function (item, index) {
            var newKey;
            if (typeof key === 'function') {
                newKey = key(item, index);
            } else {
                newKey = item[key];
            }

            newMap[newKey] = item;
        });
        return newMap;
    };

    function closeWindow () {
        //window.opener.close();
        window.close();
    }

    module.exports = {
        run: run
    };
});
