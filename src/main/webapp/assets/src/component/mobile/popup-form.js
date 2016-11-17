define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var util = require('./util');
    var formInit = require('./form');

    var PopupForm = function (options) {
        var _this = this;
        this.eventName = options.eventName;
        this.eventParam = options.eventParam;
        this.taskId = options.taskId;

        this.init(options.model);

        /*
        this.getForm()
        .done(function (res) {
            if (res.success) {
                _this.init(res.model);
            }
            else {
                util.alert(res.message);
				//_this.init(testJson.model);	//TODO this is test code
            }
        })
        .fail(function () {
            util.alert('发生错误！');
            //_this.init(testJson.model);	//TODO this is test code
        });
        */
    };
    PopupForm.prototype = {
        getForm: function () {
            return $.ajax({
                url: CONTEXT_PATH + '/process/toCustomForm.do?' + this.eventParam,
                type: 'post',
                dataType: 'json',
                data: {
                    taskId: this.taskId,
                    formType: this.eventName
                }
            });
        },
        init: function (model) {
			var _this = this;
			this.model = model;
			this.$el = $('<div><div class="header">\
					<a class="back" href="javascript:"><i class="iconfont icon-xiangzuo1"></i></a>\
					<span class="form-title">' + model.formName +'</span>\
				</div><div class="forms"></div></div>')
				.css({
					'width': '100%',
					'height': '100%',
					'position': 'fixed',
					'top': '0',
					'left': '0',
					'background': '#eee'
				}).appendTo(document.body);

            this.$button = $('<a href="javascript:" class="submit">' + model.buttons[0].name + '</a>');
            this.$el.append(this.$button);
            //this.$form = $('<form></form>');
			//this.$el.append(this.$form);

            this.$forms = this.$el.find('.forms');
            this.formViews = [];
            _.each(model.forms, function (item) {
                var $form = $('<form></form>');
                var formView = formInit($form, item.widgets);
                formView.setAttribute('id', item.formID);
                formView.setAttribute('title', item.formName);
                formView.setAttribute('paramters', item.paramters);

                formView.setDataByParamters(model.paramterDataTo, item.paramters);

                this.formViews.push(formView);
                this.$forms.append($form);
            }, this);

            //var form = model.form[0];
			//this.formObj = formInit(this.$form, model.form.widgets);
			//this.formObj.setDataByParamters(model.paramterDataTo, model.form.paramters);

			this.$el.find('.back').on('click', function () {
				_this.close();
			});
            this.$button.on('click', function () {
				_this.submit();
            });

			util.disableMove(this.$el);
        },
		submit: function () {
			var _this = this;
			var formViews = this.formViews;
			var data = {
				formData: [],
				paramterData: { }
			};
            _.each(formViews, function (formView) {
                var itemData = {
                    id: formView.getAttribute('id'),
                    title: formView.getAttribute('title') + '(' + util.constant.userName + '-' + util.constant.deptName + ')',
                    data: formView.getData()
                };

                var paramterData = formView.getParamters(formView.getAttribute('paramters'), itemData.data);

                data.formData.push(itemData);
                _.extend(data.paramterData, paramterData);
            });

			$.ajax({
				url: CONTEXT_PATH + '/' + this.model.buttons[0].url,
				type: 'post',
				dataType: 'json',
				data: {
					taskId: _this.taskId,
					model: JSON.stringify(data)
				},
				success: function (res) {
					if (res.success) {
						util.alert('提交成功!', function () {
							_this.close();
						});
					}
					else {
						util.alert(res.message);
					}
				}
			});
		},
		close: function () {
			this.$button.off();
			this.$el.remove();
		}
    };

    module.exports = PopupForm;
});



var testJson = {
	"success" : true,
	"message" : null,
	"encoding" : "UTF-8",
	"model" : {
        forms: [{
            "formName" : "延期申请",
            "widgets" : [{
                    "exp" : "请输入",
                    "describeName" : "督办名称",
                    "isRequired" : "0",
                    "alias" : "督办名称",
                    "isMerge" : "0",
                    "sequence" : 100,
                    "value" : "",
                    "isEffective" : "0",
                    "controlId" : "TextField",
                    "reName" : "TextField100",
                    "useAlias" : "on",
                    "jsonData" : ""
                }, {
                    "exp" : "请选择",
                    "describeName" : "开始时间",
                    "isRequired" : "0",
                    "alias" : "开始时间",
                    "isMerge" : "0",
                    "sequence" : 200,
                    "value" : "",
                    "isEffective" : "0",
                    "controlId" : "DDDateField",
                    "reName" : "DDDateField200",
                    "useAlias" : "on",
                    "jsonData" : ""
                }, {
                    "exp" : "请选择",
                    "describeName" : "结束时间",
                    "isRequired" : "0",
                    "alias" : "结束时间",
                    "isMerge" : "0",
                    "sequence" : 300,
                    "value" : "",
                    "isEffective" : "0",
                    "controlId" : "DDDateField",
                    "reName" : "DDDateField300",
                    "useAlias" : "on",
                    "jsonData" : ""
                }, {
                    "exp" : "请选择",
                    "describeName" : "申请延期到",
                    "isRequired" : "0",
                    "alias" : "申请延期到",
                    "isMerge" : "0",
                    "sequence" : 400,
                    "value" : "",
                    "isEffective" : "0",
                    "controlId" : "DDDateField",
                    "reName" : "DDDateField400",
                    "useAlias" : "on",
                    "jsonData" : ""
                }, {
                    "exp" : "请输入",
                    "describeName" : "延期理由",
                    "isRequired" : "0",
                    "alias" : "",
                    "isMerge" : "0",
                    "sequence" : 500,
                    "value" : "",
                    "isEffective" : "0",
                    "controlId" : "TextField",
                    "reName" : "TextField500",
                    "useAlias" : false,
                    "jsonData" : ""
                }
            ],
            "paramters" : {
                "督办名称" : {
                    "name" : "督办名称",
                    "controlID" : "TextField100",
                    "type" : "string"
                },
                "申请延期到" : {
                    "name" : "申请延期到",
                    "controlID" : "DDDateField400",
                    "type" : "string"
                },
                "结束时间" : {
                    "name" : "结束时间",
                    "controlID" : "DDDateField300",
                    "type" : "string"
                },
                "开始时间" : {
                    "name" : "开始时间",
                    "controlID" : "DDDateField200",
                    "type" : "string"
                }
            },
            "formID" : "flowform1471525870871"
        }],
		"buttons" : [{
				"name" : "提交",
				"url" : "process/requestExtension.do"
			}
		],
		"paramterDataTo" : {
			"督办名称" : {
				"value" : "0825督办任务测试",
				"readonly" : true
			},
			"结束时间" : {
				"value" : "2016-08-29 23:00:00",
				"readonly" : true
			},
			"开始时间" : {
				"value" : "2016-08-26 15:38:20",
				"readonly" : true
			}
		}
	}
};
