define(function (require, exports, module) {
    var $ = require('jquery');
    require('jqueryui');
    require('jquery-util');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var confirm = require('component/Confirm');
    var alert = require('component/Alert');
    var FormModal = require('component/FormModal');
    var SearchForm = require('component/SearchForm');
    var Pager = require('component/Pager');
    var Model = require('component/Model');
    var template = require('template');
    var moment = require('moment');
    var fileData = {
        data: []
    };
    require('umeditor-config');
    require('umeditor');
    require('umeditor-lang');
    require('jquery-form');
    require('metisMenu');
    require('jquery-validate');
    require('jquery-validate-additional');
    require('ztree');
    require('ztree-excheck');
    Backbone.emulateHTTP = true;
    /*
     * 模型
     */
    var Notice = Model.extend({
        defaults: {
            id: 0,
            title: '',
            cid: 0,
            detail: '',
            content: '',
            createTime: '',
            cname: '',
            //			dname : ''
        },
        serialize: function () {
            var data = this.toJSON();


            var status = this.get('status');
            var statusText = '未知';
            if (status == 1)
                statusText = '发送成功';
            if (status == 2)
                statusText = '待审核';
            if (status == 3)
                statusText = '发送失败';

            var cname = this.get('cname');
            var dname = this.get('dname');
            var sender = dname != '' ? dname : cname;
            data.statusText = statusText;
            data.sender = sender;
            return data;
        },
        syncOptions: {
            wait: true,
            processData: true
        },
        destroy: function (options) {
            var data = this.pick('id');
            Notice.__super__.destroy.call(this, _.extend({
                dataType: 'text',
                url: CONTEXT_PATH + '/web/deleteMessage.do',
                data: data,
            }, this.syncOptions, options));
        },
        save: function (attrs, options) {
            Notice.__super__.save.call(this, attrs, _.extend({},
                this.syncOptions, options));
        }
    });
    require('json2');
    /*
     *获取url中参数
     */
    var getRequest = function (key) {
        var url = location.search; //获取url中"?"符后的字串
        var theRequest = new Object();
        if (url.indexOf("?") != -1) {
            var str = url.substr(1);
            strs = str.split("&");
            for (var i = 0; i < strs.length; i++) {
                theRequest[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]);
            }
        }
        return theRequest[key];
    }
    var deleFile = function (id, addr) {
        alert(id);
        alert(addr);
    };
    window.deleFile = deleFile;
    var noticeCreateModalRender = template($('#tmpl-noticeCreateModal').html());
    var NoticeCreateModal = FormModal
        .extend({
            template: noticeCreateModalRender,
            initForm: function () {
                this.$form.validate({
                    rules: {
                        title: {
                            required: true,
                            rangelength: [5, 30]
                        },
                        picfile: {
                            required: current == '1' ? false : true,
                            accept: 'image/*'
                        }
                    },
                    messages: {
                        title: {
                            required: '必须填写',
                            rangelength: '标题字符数5-30'
                        },
                        picfile: {
                            required: '请上传封面图片',
                            accept: '必须是图片文件'
                        }
                    }
                });
            },
            initialize: function () {
                NoticeCreateModal.__super__.initialize.call(this);
                this.model.set('ntype', current);
            },
            render: function () {
                NoticeCreateModal.__super__.render.call(this);
                var self = this;
                if (current == 2) $('.type').text('新闻');
                $("body").on("click", "#filebegin", function () {
                    $("#inputDocfile").click();
                });
                $("body").on("change", "#inputDocfile", function (e) {
                    //alert('Changed!');
                    var files = e.target.files || e.dataTransfer.files;
                    //files[0];
                    var oData = new FormData();
                    oData.append('file', files[0]);
                    //$('#uploadinfo').text(JSON.stringify(oData));
                    var oReq = new XMLHttpRequest();
                    oReq.open("POST", CONTEXT_PATH + "/file/filesUpload.do", true);
                    oReq.onload = function (oEvent) {
                        if (oReq.status == 200) {

                            //alert(oReq.responseText);
                            var obj = eval('(' + oReq.responseText + ')');
                            var _path = '/mobile/file/download/' + obj.model[0].id + '/' + obj.model[0].addr + '.do';
                            fileData.data.push({
                                'path': _path,
                                'name': obj.model[0].name
                            });
                            //alert(JSON.stringify(fileData));
                            $('#filePath').val(JSON.stringify(fileData));
                            //alert($('#filePath').val());
                            $('#uploadinfo').append('<div id="' + obj.model[0].id + '">附件：' + obj.model[0].name +
                                '&nbsp;&nbsp;<a href="javascript:window.deleFile(\'' +
                                obj.model[0].id + '\',\'' + obj.model[0].addr + '\');"></a><span class="deleteFile">删除</span></div>');
                            $(".deleteFile").on("click",function(){
                            	$(this).parent().remove();
                            	$('#filePath').val('');
                            })
                        } else {
                            $('#uploadinfo').text("Error " + oReq.status + " occurred uploading your file.<br \/>");
                        }
                    };
                    oReq.send(oData);
                })

                return this;
            },
            initEditor: function () {
                this.editor = UM
                    .getEditor(
                        'editor', {
                            uploadComplete: function (json) {
                                json = _.extend({
                                    success: false,
                                    model: {}
                                }, json);

                                var url = '';
                                var state = json.success ? 'SUCCESS' : 'ERROR';
                                if (json.success) {
                                    var model = json.model;
                                    url = CONTEXT_PATH + '/file/download/' + model.id + '/' + model.addr + '.do';
                                }

                                return {
                                    url: url,
                                    state: state
                                }
                            },
                            initialFrameWidth: '100%',
                            initialFrameHeight: '300',
                            autoHeightEnabled: false,
                            imagePath: CONTEXT_PATH,
                            imageUrl: CONTEXT_PATH + '/file/imageUpload.do',
                            imageFieldName: 'mr',
                            // imageUrlPrefix : SourceUrl,
                            toolbar: [
                                'undo redo | bold italic underline strikethrough fontfamily fontsize forecolor backcolor | superscript subscript',
                                'paragraph justifyleft justifycenter justifyright justifyjustify | insertorderedlist insertunorderedlist | removeformat | selectall cleardoc | image | link unlink | horizontal source'
                            ]
                        });
            },
            submit: function (event) {
                var $target = $(event.target);
                var params = this.$form.serializeObject();
                var items = [];
                _.each(null, function (item) {
                    var status = item.getCheckStatus();
                    if (!status.half) {

                        item = {
                            cid: COMPANY_ID,
                            cname: COMPANY_NAME,
                        }
                    }
                    items.push(item);

                });
                if (this.$form.valid()) {
                    this.$form.ajaxSubmit({
                        url: CONTEXT_PATH + '/web/notice/add.do',
                        context: this,
                        beforeSend: function () {
                            $target.prop('disabled', true);
                        },
                        data: {},
                        success: function (resp) {
                            resp = _.extend({
                                success: false,
                                model: {}
                            }, resp);
                            if (resp.success) {
                                this.collection.refresh();
                                alert('操作成功').delay(1);
                                this.hide();
                            } else {
                                alert(resp.message);
                            }
                        },
                        error: function () {
                            alert('操作失败');
                        },
                        complete: function () {
                            $target.prop('disabled', false);
                        }
                    });
                }
            },
            remove: function () {
                if (this.editor)
                    this.editor.destroy();
                NoticeCreateModal.__super__.remove.call(this);
            }
        });
    var noticeEditModalRender = template($('#tmpl-noticeEditModal').html());
    var NoticeEditModal = FormModal.extend({
        template: noticeEditModalRender,
        initForm: function () {
            this.$form.validate({
                rules: {
                    title: {
                        required: true,
                        rangelength: [5, 30]
                    },
                    picfile: {
                        accept: 'image/*'
                    }
                },
                messages: {
                    title: {
                        required: '必须填写',
                        rangelength: '标题字符数5-30'
                    },
                    picfile: {
                        accept: '必须是图片文件'
                    }
                }
            });
        },
        initialize: function () {
            NoticeCreateModal.__super__.initialize.call(this);
            this.model.set('ntype', current);
        },
        render: function () {
            NoticeCreateModal.__super__.render.call(this);
            if (current == 2) $('.type').text('新闻');
            $("body").on("click", "#filebegin", function () {
                $("#inputDocfile").click();
            });
            $("body").on("change", "#inputDocfile", function (e) {
                //alert('Changed!');
                var files = e.target.files || e.dataTransfer.files;
                //files[0];
                var oData = new FormData();
                oData.append('file', files[0]);
                //$('#uploadinfo').text(JSON.stringify(oData));
                var oReq = new XMLHttpRequest();
                oReq.open("POST", CONTEXT_PATH + "/file/filesUpload.do", true);
                oReq.onload = function (oEvent) {
                    if (oReq.status == 200) {

                        //alert(oReq.responseText);
                        var obj = eval('(' + oReq.responseText + ')');
                        var _path = '/mobile/file/download/' + obj.model[0].id + '/' + obj.model[0].addr + '.do';
                        fileData.data.push({
                            'path': _path,
                            'name': obj.model[0].name
                        });
                        //alert(JSON.stringify(fileData));
                        $('#filePath').val(JSON.stringify(fileData));
                        //alert($('#filePath').val());
                        $('#uploadinfo').append('<div id="' + obj.model[0].id + '">附件：' + obj.model[0].name +
                            '&nbsp;&nbsp;<a href="javascript:window.deleFile(\'' +
                            obj.model[0].id + '\',\'' + obj.model[0].addr + '\');"></a></div>');
                    } else {
                        $('#uploadinfo').text("Error " + oReq.status + " occurred uploading your file.<br \/>");
                    }
                };
                oReq.send(oData);
            })
            return this;
        },
        initEditor: function () {
            this.editor = UM
                .getEditor(
                    'editor', {
                        uploadComplete: function (json) {
                            json = _.extend({
                                success: false,
                                model: {}
                            }, json);

                            var url = '';
                            var state = json.success ? 'SUCCESS' : 'ERROR';
                            if (json.success) {
                                var model = json.model;
                                url = CONTEXT_PATH + '/file/download/' + model.id + '/' + model.addr + '.do';
                            }

                            return {
                                url: url,
                                state: state
                            }
                        },
                        initialFrameWidth: '100%',
                        initialFrameHeight: '300',
                        autoHeightEnabled: false,
                        imagePath: CONTEXT_PATH,
                        imageUrl: CONTEXT_PATH + '/file/imageUpload.do',
                        imageFieldName: 'mr',
                        toolbar: [
                            'undo redo | bold italic underline strikethrough fontfamily fontsize forecolor backcolor | superscript subscript',
                            'paragraph justifyleft justifycenter justifyright justifyjustify | insertorderedlist insertunorderedlist | removeformat | selectall cleardoc | image | link unlink | horizontal source'
                        ]
                    });
        },
        checkTree: function () {
            var id = this.model.get('id');
            var that = this;
            $.ajax({
                type: "GET",
                url: CONTEXT_PATH + '/moblicApprove/getMessage.do',
                dataType: "json",
                data: "id=" + id,
                success: function (data) {

                }
            });
        },
        submit: function (event) {
            var $target = $(event.target);
            var params = this.$form.serializeObject();
            var items = [];

            item = {
                cid: COMPANY_ID,
                cname: COMPANY_NAME,

            }
            items.push(item);

            if (this.$form.valid()) {
                this.$form.ajaxSubmit({
                    url: CONTEXT_PATH + '/web/updateMessage.do',
                    context: this,
                    beforeSend: function () {
                        $target.prop('disabled', true);
                    },
                    success: function (resp) {
                        resp = _.extend({
                            success: false,
                            message: '操作失败'
                        }, resp);

                        if (resp.success) {
                            attrs = _.pick(params, 'title');
                            this.model.set(attrs);
                            alert('操作成功').delay(1);
                            this.hide();
                        } else {
                            alert(resp.message);
                        }
                    },
                    error: function () {
                        alert('操作失败');
                    },
                    complete: function () {
                        $target.prop('disabled', false);
                    }
                });
            }
        },
        remove: function () {
            if (this.editor)
                this.editor.destroy();
            NoticeCreateModal.__super__.remove.call(this);
        }
    });

    /*
     * 数据行
     * 监听对象的 remove 和 change 事件更新视图
     */

    var itemRender = template($('#tmpl-item').html());
    var ItemView = Backbone.View.extend({
        tagName: 'tr',
        template: itemRender,
        events: {
            'click [data-do="edit"]': 'doEdit',
            'click [data-do="delete"]': 'doDelete',
            'click [data-do="stick"]': 'doStick',
            'click [data-do="disable"]': 'doDisable',
            'click [data-do="enable"]': 'doEnable'
        },
        initialize: function () {
            this.listenTo(this.model, 'remove', this.remove);
            this.listenTo(this.model, 'change', this.render);
        },
        doEdit: function () {
            Backbone.trigger('edit:notice', this.model, this);

        },
        doDelete: function () {
            var model = this.model;
            confirm('确认删除？', function () {
                model.destroy();
            });
        },
        doStick: function () {
            var id = this.model.get('id');
            var sort = this.model.get('sort');

            if (sort == 0) {
                sort = 1;
            } else {
                sort = 0;
            }

            this.model.save(null, {
                url: CONTEXT_PATH + '/web/setMessageTop.do',
                type: 'post',
                context: this,
                data: {
                    id: id,
                    sort: sort
                },
                success: function (model, resp, options) {
                    resp = _.extend({
                        success: false,
                        message: '操作失败'
                    }, resp);

                    if (resp.success == true) {
                        alert('操作成功').delay(1);
                        this.model.set({
                            sort: sort
                        });
                    } else {
                        alert(resp.message);
                    }
                },
                error: function () {
                    alert('操作失败');
                }
            });
        },
        doDisable: function () {
            this.model.toggleStatus();
        },
        doEnable: function () {
            this.model.toggleStatus();
        },
        render: function () {
            var markup = this.template({
                model: this.model.serialize()
            });
            this.$el.html(markup);
            return this;
        }
    });
    /*
     * 表格
     *
     * 监听集合的 reset 事件更新视图
     */
    var DataTable = Backbone.View.extend({
        noDataRender: template('<tr><td colspan="{{count}}">暂无数据</td></tr>'),
        loadingRender: template('<tr><td colspan="{{count}}">数据加载中...</td></tr>'),
        initialize: function () {
            this.cacheEls();

            this.listenTo(this.collection, 'reset', this.reset);
            this.listenTo(this.collection, 'request', this.request);
            this.listenTo(this.collection, 'sync', this.sync);
            this.listenTo(this.collection, 'error', this.error);
            this.listenTo(this.collection, 'destroy', this.refresh);

            this.listenTo(this.collection, 'change:sort', this.refresh);
        },
        addOne: function (model, collection, options) {
            model.set('cid', COMPANY_ID);
            var itemView = new ItemView({
                model: model
            });
            this.$items.append(itemView.render().el);
        },
        reset: function (collection, options) {
            var previousModels = options.previousModels;
            _.each(previousModels, function (model) {
                model.trigger('remove');
            });

            this.$items.empty();

            if (collection.length == 0) {
                this.$items.html(this.noDataRender({
                    count: this.colHeadersCount
                }));
            } else {
                collection.each(function (model, index) {
                    model.set({
                        index: index + 1,
                        $index: index
                    });
                    this.addOne(model, collection);
                }, this);
            }
        },
        cacheEls: function () {
            this.$headers = this.$('[role="col-headers"]');
            this.$items = this.$('[role="items"]');
            this.colHeadersCount = this.$headers.find('th').size();
        },
        request: function (collection) {
            if (collection instanceof Backbone.Collection) {
                var markup = this.loadingRender({
                    count: this.colHeadersCount
                });

                this.$items.empty().html(markup);
            }
        },
        refresh: function () {
            var data = this.model.getData();
            this.collection.refresh({});
        }
    });
    var current = getRequest('t');

    function run () {
        $('.primary-nav').metisMenu();
        if (current == 2) $('.type').text('新闻');
        var query = new Backbone.Model({
            cid: COMPANY_ID,
            type: current
        });
        $('#ntype').html(template($('#tmpl-ntypes').html())({ ntype: current }));
        $('#ntype').val(current);
        _.extend(query, {
            autoParam: function () {
                return {
                    cid: COMPANY_ID
                }
            },
            getData: function () {
                var attrs = this.toJSON();
                return _.extend(attrs, this.autoParam());
            }
        });
        var list = new Backbone.Collection(null, {
            model: Notice
        });
        _.extend(list, {
            url: CONTEXT_PATH + '/web/notice/findAll.do',
            parse: function (resp) {
                var parsed = _.extend({
                    success: false,
                    model: []
                }, resp);
                var items = _.isArray(parsed.model) ? parsed.model : [];
                return items;
            },
            refresh: function (options) {
                var data = query.getData();
                options = $.extend(true, {
                    type: 'post',
                    parse: true,
                    reset: true,
                    data: data
                }, options);

                this.fetch(options);
            }
        });

        var table = new DataTable({
            el: '#datatable',
            model: query,
            collection: list
        });

        // 搜索表单
        var search = new SearchForm({
            el: '#search',
            collection: list
        });
        search.on('search', function (data) {
            query.clear();
            _.extend(data, {

            });
            query.set(data);
            searchHandler(query.getData());
        });

        var pager = new Pager({
            className: 'pagination pull-right'
        });

        pager.listenTo(list, 'sync', function (collection, resp, options) {
            var pageVo = resp.pageVo;
            if (pageVo == null) {
                this.$el.empty();
            } else {
                var attrs = _.pick(pageVo, 'pageNo', 'pageSize', 'totalCount');
                attrs.totalPages = pageVo.pageTotal;
                this.update(attrs);
            }
        });

        pager.on('page', function (pageNo) {
            query.set('pageNo', pageNo);
            searchHandler(query.getData());
        });

        table.$el.after(pager.render().$el);

        function searchHandler(data) {
            var clean = data;
            // 过滤空的搜索条件
            if (_.isObject(data) && !_.isArray(data)) {
                clean = {};
                _.each(data, function (value, key) {
                    if (_.isObject(value)) {
                        if (!_isEmpty(value))
                            clean[key] = value;
                    } else {
                        if (value.toString() != '')
                            clean[key] = value;
                    }
                });
            }
            list.fetch({
                type: 'post',
                parse: true,
                reset: true,
                data: clean
            });
        }

        searchHandler(query.getData());

        var modal;
        $('[data-do="create:notice"]').on('click', function () {
            var attrs = {
                cid: COMPANY_ID
            };
            modal = new NoticeCreateModal({
                model: new Notice(attrs),
                collection: list
            });
            modal.render().$el.appendTo(document.body);
            modal.initEditor();
            modal.initForm();
            modal.show();
        });

        Backbone.on('edit:notice', function (model) {
            modal = new NoticeEditModal({
                model: model
            });
            modal.render().$el.appendTo(document.body);
            modal.initEditor();
            modal.initForm();
            modal.show();
        });
    }

    exports.run = run;
});
