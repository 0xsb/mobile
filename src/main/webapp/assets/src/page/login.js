define(function(require, exports, module) {
	var $ = require('jquery');
	var Backbone = require('backbone');
	var template = require('template');
	require('jquery-validate');
	var BaseForm = Backbone.View.extend({
		events: {
			'submit': 'onSubmit'
		},
		interval: 2000,
		timer: null,
		locked: false,
		initialize: function() {
			this.cacheEls();
			this._hideAlert = _.bind(this.hideAlert, this);
		},
		show: function() {
			this.$el.show();
		},
		hide: function() {
			this.$el.hide();
		},
		alert: function(message) {
			this.$alert.html(message).removeClass('hide');
			this.timer = setTimeout(this._hideAlert, this.interval);
		},
		lock: function(model, xhr, options) {
			this.locked = true;
		},
		done: function(model, response, options) {
			this.locked = false;
		},
		error: function(model, response, options) {
			this.alert('请求失败');
			// this.alert(response.statusText);
			this.locked = false;
		},
		hideAlert: function() {
			this.$alert.addClass('hide').empty();
		},
		onSubmit: function() {
			if (this.timer) {
				this.hideAlert();
				clearTimeout(this.timer);
			}
			if (!this.locked && this.$el.valid()) {
				var data = this.$el.serializeArray();
				this.model.fetch({
					type: 'post',
					dataType: 'json',
					data: data
				});
			}
			return false;
		},
		cacheEls: function() {

		}
	});
	var LoginForm = BaseForm.extend({
		initialize: function() {
			LoginForm.__super__.initialize.apply(this, arguments);
			this.$el.validate({
				errorClass: 'error-valid',
				rules: {
					'username': {
						required: true
					},
					'password': {
						required: true
					}
				},
				messages: {
					'username': {
						required: '请输入用户名'
					},
					'password': {
						required: '请输入密码'
					}
				}
			});

			this.model.url = this.$el.attr('action');
			this.listenTo(this.model, 'request', this.lock);
			this.listenTo(this.model, 'sync', this.done);
			this.listenTo(this.model, 'error', this.error);
		},
		cacheEls: function() {
			this.$alert = this.$('.alert-login');
		}
	});

	var CompanyForm = BaseForm.extend({
		initialize: function() {
			CompanyForm.__super__.initialize.apply(this, arguments);
			if (!this.model) {
				this.model = new Backbone.Model;
			}
			this.model.url = this.$el.attr('action');
			this.listenTo(this.model, 'request', this.lock);
			this.listenTo(this.model, 'sync', this.done);
			this.listenTo(this.model, 'error', this.error);
		},
		onSubmit: function() {
			if (this.timer) {
				this.hideAlert();
				clearTimeout(this.timer);
			}
			if (!this.locked) {
				var companyId = this.$company.val();
				var model = this.collection.get(companyId);
				var data = model.toJSON();
				this.model.fetch({
					type: 'post',
					data: data
				});
			}
			return false;
		},
		done: function(model, response, options) {
			_.defaults(response, {
				success: false,
				model: '',
				message: '设置失败'
			});
			if (response.success) {
                if (this.isTrial) {
                    window.location.href = CONTEXT_PATH + '/trialEdition/customForm/manager.do';
                }
                else {
                    window.location.href = CONTEXT_PATH + '/microApp/list.do';
                }
			} else {
				this.alert(response.message);
			}
		},
		cacheEls: function() {
			this.$alert = this.$('.alert-company');
			this.$company = this.$('#company');
		}
	});

	var Company = Backbone.Model.extend({
		idAttribute: 'companyId',
		defaults: {
			companyName: '',
			companyId: '',
			userId: '',
			orgId: '',
			companyName: '未命名',
			databaseName: ''
		}
	})

	function run() {
		var model = new Backbone.Model;
		var loginForm = new LoginForm({
			el: '#form-login',
			model: model
		});
		var companyCollection = new Backbone.Collection(null, {
			model: Company
		});
		var groupForm = new CompanyForm({
			el: '#form-company',
			collection: companyCollection
		});

		model.on('sync', function(model, response, options) {
			_.defaults(response, {
				success: false,
				message: '登录失败',
				model: []
			});
			if (!response.success) {
				loginForm.alert(response.message);
			} else {
				loginForm.hide();

				var data = response.model;
				var item;
				var markup = [];
				companyCollection.reset(data);
				companyCollection.each(function(model) {
					var item = model.toJSON();
					markup.push('<option value="' + item.companyId + '">' + item.companyName + '</option>');
				});
				$('#company').html(markup.join(''));

                if (options.data[0].value === '13800000000') {
                    groupForm.isTrial = true;
                    groupForm.onSubmit();
                    return;
                }
				groupForm.show();
			}
		});

        loginForm.show();
	}

	exports.run = run;
});
