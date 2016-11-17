define(function (require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    require('jquery-util');
    var template = require('template');
    var util = require('component/mobile/util');
    //var formInit = require('component/mobile/form');
    //var loader = require('component/mobile/loader');
    var fulllist = require('component/mobile/full-list');
    var widgetFactory2 = require('component/pcclient/widgets-factory2');

    util.alert = require('component/Alert');

    var urls = {
        getForm: CONTEXT_PATH + '/workflow/getStartForm.do',
        // start: CONTEXT_PATH + '/workflow/startProcess.do',
        start: CONTEXT_PATH + '/document/startProcess.do',
        selectUser: CONTEXT_PATH + '/workflow/selectUser.do',

        indexPage: CONTEXT_PATH + '/moblicApprove/toIndex.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do',
        taskPage: CONTEXT_PATH + '/moblicApprove/toTasks.do',
        welfarePage: CONTEXT_PATH + '/moblicApprove/toWelfale.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toApprovalList.do',
        workflowPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do'
    };

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

    var OfficialDocModel = Backbone.Model.extend({
        defaults: {
            companyName: '',
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

    var OfficialDocView = Backbone.View.extend({
        tagName: 'form',
        template: template($('#tmpl-officialDocForm').html()),
        previewTemplate: template($('#tmpl-officialDoc').html()),
        widgets: {},
        events: {
            'click [data-do="preview"]': 'doPreview',
            'click [data-do="start"]': 'doStart',
            'click [data-do="agree"]': 'doAgree',
            'click [data-do="refuse"]': 'doRefuse',
            'click [data-do="reject"]': 'doReject',
            'click [data-do="forward"]': 'doForward',
            'click [data-do="submit"]': 'doSubmit'
        },
        initialize: function () {
            this.doc = new OfficialDocModel({
                companyName: COMPANY_NAME,
                _attachment: []
            });

            this.listenTo(this.model, 'change', this.render);
            this.listenTo(this.doc, 'change', this.renderPreview);
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
        renderPreview: function () {
            var data = this.doc.toJSON();
            this.$preview.html(this.previewTemplate({
                model: data
            }));
            return this;
        },
        onChange: function (value, model, widget) {
            var type = model.get('type');
            var name = model.get('alias');
            if (type == 'DDAttachment') {
                name = '_attachment';
            }
            else if (type == 'EventButton') {
                var buttonWidget = this.widgets[model.get('id')];
                var dataNParam = this.getDataAndParamters();
                buttonWidget.eventTrigger(pageParams.taskId, dataNParam.data, dataNParam.paramterData);
                return;
            }
            this.doc.set(name, value);
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
        getData: function () {
            return this.getDataAndParamters().data;
        },
        getParamters: function () {
            return this.getDataAndParamters().paramterData;
        },
        doStart: function () {
            var formData = this.model.pick('formID', 'formName');
            var dataAndParamters = this.getDataAndParamters();

            formData = {
                id: formData.formID,
                title: formData.formName,
                data: dataAndParamters.data
            };

            data = {
                formData: [formData],
                paramterData: dataAndParamters.paramterData
            };

            var jsonData = JSON.stringify(data);
            var flowId = this.model.get('flowId');
            var next = this.model.get('next');

            $.ajax({
                url: urls.start,
                dataType: 'json',
                type: 'post',
                context: this,
                data: {
                    key: flowId,
                    next: next,
                    model: jsonData
                },
                beforeSend: function () {
                    pending = true;
                },
                success: function (res) {
                    if (res.success) {
                        // var pageParams = this.model.pick('taskId', 'flowId', 'jsonData', 'wyyId', 'next');
                        pageParams.taskId = res.model.taskId;
                        pageParams.jsonData = jsonData;
                        if (res.model.users.length > 1) {
                            selectUserList.setMulti(res.model.userTaskType == '1');
                            selectUserList.setData(convertUserData(res.model.users));
                            selectUserList.$el.find('.ok-button').css({
                                'position': 'absolute',
                                'z-index': 12,
                                'right': 10,
                                'top': 10,
                                'color': '#3386c0'
                            });
                            $('#select-user-popup').show();
                            popupPos($('#select-user-popup'));
                        } else {
                            pageParams.nextAssignee = res.model.users[0].userId;
                            doSelectUser(pageParams);
                        }
                    } else {
                        util.alert(res.message);
                    }
                },
                error: function () {
                    util.alert('发生错误，请稍后再试！');
                },
                complete: function () {
                    pending = false;
                }
            });
        },
        doAgree: function () {
            alert(this.model.get('formID') + ' agree');
        },
        doRefuse: function () {
            alert(this.model.get('formID') + ' refuse');
        },
        doReject: function () {
            alert(this.model.get('formID') + ' reject');
        },
        doForward: function () {
            alert(this.model.get('formID') + ' forward');
        },
        doSubmit: function () {
            alert(this.model.get('formID') + ' submit');
        },
        cacheEls: function () {
            this.$formTable = this.$('[role="doc-form"]');
            this.$preview = this.$('[role="doc-preview"]');
        }
    });

    var getIndexPage = function (wyyId) {
        var url = '';
        wyyId = wyyId || 'wyy0001';
        switch (wyyId) {
            case 'wyy0001':
                url = urls.indexPage;
                break;
            case 'wyy0002':
                url = urls.dailyPage;
                break;
            case 'wyy0003':
                url = urls.taskPage;
                break;
            case 'wyy0004':
                url = urls.welfarePage;
                break;
            default:
                url = urls.indexPage;
                break;
        }
        return url;
    };
    var goBack = function () {
        /*var fromPage = util.getParam('from');
        var wyyId = util.getParam('wyyId');
        var backUrl = fromPage === 'index' ? getIndexPage(wyyId) : (urls.listPage + '?wyyId=' + wyyId);
        location.href = backUrl;*/

        window.close();
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
    var popupPos = function ($popup) {
        var w = $popup.find('.approve-modal').width(),
            h = $popup.find('.approve-modal').height();
        $popup.find('.approve-modal').css({
            'marginLeft': (w / -2) + 'px',
            'marginTop': (h / -2) + 'px'
        });
    };
    var pending = false;
    var doSelectUser = function (pageParams) {
        $.ajax({
            url: urls.selectUser,
            dataType: 'json',
            type: 'post',
            data: {
                taskId: pageParams.taskId,
                nextAssignee: pageParams.nextAssignee
            },
            beforeSend: function () {
                pending = true;
            },
            success: function (res) {
                if (res.success) {
                    util.alert('提交成功！', function () {
                        window.close();
                        //location.href = urls.workflowPage + '?type=wdfq&wyyId=' + pageParams.wyyId;
                    });
                } else {
                    util.alert(res.message);
                }
            },
            error: function () {
                util.alert('发生错误，请稍后再试！');
            },
            complete: function () {
                pending = false;
            }
        });
    };
    var doSubmit = function (pageParams, formView, model, selectUserList) {
        if (pending) {
            return;
        }
        var valid = formView.checkRequired();
        if (!valid.success) {
            util.alert(valid.message);
            return;
        }

        var formModel = model.form;
        var formdata = formView.getData();
        var data = {
            formData: {},
            paramterData: {}
        };
        data.formData = [{
            id: formView.getAttribute('id'),
            title: formView.getAttribute('title') + '(' + util.constant.userName + '-' + util.constant.deptName + ')',
            data: formdata
        }];
        data.paramterData = formView.getParamters(formModel.paramters, formdata);
        var jsonData = JSON.stringify(data);

        $.ajax({
            url: urls.start,
            dataType: 'json',
            type: 'post',
            data: {
                key: pageParams.flowId,
                next: pageParams.next,
                model: jsonData
            },
            beforeSend: function () {
                pending = true;
            },
            success: function (res) {
                if (res.success) {
                    pageParams.taskId = res.model.taskId;
                    pageParams.jsonData = jsonData;
                    if (res.model.users.length > 1) {
                        selectUserList.setMulti(res.model.userTaskType == '1');
                        selectUserList.setData(convertUserData(res.model.users));
                        $('#select-user-popup').show();
                        popupPos($('#select-user-popup'));
                        if (res.model.userTaskType == '1') {
                            selectUserList.$el.find('.ok-button').css({
                                'position': 'absolute',
                                'z-index': 1,
                                'right': 10,
                                'top': 10,
                                'color': '#3386c0'
                            });
                        }
                    } else {
                        pageParams.nextAssignee = res.model.users[0].userId;
                        doSelectUser(pageParams);
                    }
                } else {
                    util.alert(res.message);
                }
            },
            error: function () {
                util.alert('发生错误，请稍后再试！');
            },
            complete: function () {
                pending = false;
            }
        });
    };
    var initSelectUser = function (selectUserList, pageParams, formView) {
        var $popup = $('#select-user-popup');
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
            pageParams.nextAssignee = $popup.find('[role="selected-user"]').val();
            if (selectUserList.multi) {
                pageParams.nextAssignee = JSON.stringify(pageParams.nextAssignee.split(','));
            }
            if (pageParams.nextAssignee.length === 0 || pageParams.nextAssignee === '[]') {
                alert('请选择办理人！');
                return;
            }

            doSelectUser(pageParams);
        });
    };

    var selectUserList;
    var pageParams;
    var initPage = function (model, flowId) {
        var wyyId = util.getParam('wyyId');
        pageParams = {
            taskId: flowId,
            flowId: flowId,
            wyyId: wyyId,
            next: model.next
        };
        selectUserList = fulllist(model.userTaskType);

        try {
            var docModel = new Backbone.Model(pageParams);
            _.extend(docModel, {
                url: CONTEXT_PATH + '/workflow/getStartForm.do',
                parse: formDataParse
            });
            var formView = new OfficialDocView({
                el: $('#doc'),
                model: docModel
            });
            // 加载表单数据，自动渲染表单
            docModel.set(model.form);
        } catch (ex) {
            util.alert('表单配置有错，请联系管理员！', function () {
                //goBack();
                window.close();
            });
        }

        initSelectUser(selectUserList, pageParams, formView);
    };
    var run = function () {
        // var key = 'ACT_2990d631-3be2-485f-9342-dbdd07f759a8';
        var flowId = util.getParam('flowId');
        $.ajax({
            url: urls.getForm,
            data: {
                key: flowId,
                rnd: (new Date).getTime()
            },
            dataType: 'json',
            type: 'get',
            success: function (res) {
                if (res.success) {
                    initPage(res.model, flowId);
                } else {
                    util.alert(res.message || '获取表单配置出错！', function () {
                        goBack();
                    });
                }
            },
            error: function () {
                util.alert('获取表单配置出错！', function () {
                    //goBack();
                    window.close();
                });
            }
        });
    };

    module.exports = {
        run: run
    };
});
