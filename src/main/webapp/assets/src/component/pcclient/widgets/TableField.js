define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    var WidgetBase = require('./base');

    var htmlTmpl = '<div class="table-region"></div>\
        <div class="sum-region"></div>\
        <div class="add-region"><a role="add" href="javascript:">{{placeholder}}</a></div>';
    var itemTmpl = '<div class="item-header">\
        <span>{{name}}</span><a href="javascript:" role="remove" class="remove">删除</a>\
        </div><div class="item-container"></div>';

    var TableItemModel = Backbone.Model.extend({
        destroy: function() {
            this.stopListening();
            this.trigger('destroy', this, this.collection);
        }
    });
    var TableItemView = Backbone.View.extend({
        tagName: 'div',
        className: 'table-item',
        template: template(itemTmpl),
        events: {
            'click [role="remove"]': 'doRemove'
        },
        initialize: function(options) {
            this.widgets = {};
            this.listenTo(this.model, 'remove', this.remove);
            this.listenTo(this.model, 'updateIndex', this.updateIndex);

            _.each(options.widgets, function(setting) {
                this.appendWidget(setting);
            }, this);
        },
        render: function() {
            this.$el.html(this.template(this.model.toJSON()));
            return this;
        },
        updateIndex: function() {
            var index = this.$el.index();
            this.$('.item-header span').text(this.name + '(' + (index + 1) + ')');
            if (index > 0) {
                this.$('.remove').show();
            } else {
                this.$('.remove').hide();
            }
        },
        appendWidget: function(setting) {
            var widget = widgetFactory(setting);
            this.widgets[widget.model.get('id')] = widget;
            this.$el.append(widget);
        },
        doRemove: function() {
            this.model.restory();
        }
    });

    var TableField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'TableField'
        },
        events: {
            'click [role="add"]': 'doAdd'
        },
        initialize: function(options) {
            this.items = [];
            this.collection = new Backbone.Collection({
                model: TableItemModel
            });

            this.listenTo(this.collection, 'add', this.addOne);
            this.listenTo(this.collection, 'remove', this.updateIndex);
        },
        render: function() {
            this._super_invoke('render');
            this.cacheEls();
            return this;
        },
        cacheEls: function() {
            this.$tabelRegion = this.$('.table-region');
            this.$sumRegion = this.$('.table-region');
        },
        doAdd: function() {
            this.collection.add(new TableItemModel({
                name: this.model.get('name')
            }));
        },
        addOne: function(model, collection, options) {
            var view = new TableItemView({
                model: model
            });
            this.$tabelRegion.append(view.render().el);
        },
        updateIndex: function() {
            this.collection.trigger('updateIndex');
        },
        getValue: function() {

        },
        setValue: function(val) {

        }
    });

    module.exports = TableField;
});