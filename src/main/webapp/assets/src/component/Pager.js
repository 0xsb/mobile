/**
 * File: Pager.js
 * Author: kimvin
 * Date: 2016-04-19
 * Description: 分页模块
 * Copyright: 版权所有 (C) 2014 中国移动 杭州研发中心.
 */
define(function(require) {
    var Backbone = require('backbone');
    var template = require('template');
    var pagerRender = template('<a href="javascript:void(0);" data-do="first">首页</a><a href="javascript:void(0);" data-do="prev"<#if(model.pageNo<=1){#> disabled<#}#>>上一页</a><span>第{{model.pageNo}}/{{model.totalPages}}页</span><a href="javascript:void(0);" data-do="next"<#if(model.pageNo>=model.totalPages){#> disabled<#}#>>下一页</a><a href="javascript:void(0);" data-do="last">尾页</a><span class="jump">转到 <input type="text" class="page-num form-control" value="{{model.pageNo}}" data-page> 页 <button type="button" class="btn-jump btn btn-default" data-do="jump">确定</button></span>');
    var Pager = Backbone.View.extend({
        className: 'pagination',
        template: pagerRender,
        events: {
            'click [data-do="first"]': 'first',
            'click [data-do="last"]': 'last',
            'click [data-do="prev"]': 'prev',
            'click [data-do="next"]': 'next',
            'click [data-do="jump"]': 'jump',
            'keyup [data-page]': 'sanitize'
        },
        initialize: function() {
            this.model = new Backbone.Model({
                pageNo: 1,
                pageSize: 10,
                totalCount: 0,
                totalPages: 0
            });
            this.listenTo(this.model, 'change', this.render);
        },
        test: function() {
            this.startSanitize();
            return false;
        },
        sanitize: function(event) {
            var $target = $(event.target);
            var val = $.trim($target.val());
            $target.val(parseInt(val, 10) || 1);
        },
        first: function() {
            this.page(1);
        },
        last: function() {
            var totalPages = this.model.get('totalPages');
            var pageNo = totalPages == 0 ? 1 : totalPages;
            this.page(pageNo);
        },
        prev: function(event) {
            if($(event.target).attr('disabled') == 'disabled') return false;

            var pageNo = this.model.get('pageNo');
            pageNo -= 1;
            if (pageNo < 1) pageNo = 1;
            this.page(pageNo);
        },
        next: function(event) {
            if($(event.target).attr('disabled') == 'disabled') return false;
            var pageNo = this.model.get('pageNo');
            var totalPages = this.model.get('totalPages');
            if (pageNo < totalPages) {
                pageNo = pageNo + 1;
                this.page(pageNo);
            }
        },
        jump: function(event) {
            var totalPages = this.model.get('totalPages');
            var pageNo = parseInt(this.$pageNo.val(), 10) || 1;
            if (pageNo < 1) pageNo = 1;
            if (pageNo < totalPages) {
                this.page(pageNo);
            }
        },
        page: function(pageNo) {
            var prevPage = this.model.get('pageNo');
            this.trigger('page', pageNo, prevPage, this);
        },
        update: function(data) {
            data = _.pick(data, ['pageNo', 'pageSize', 'totalPages', 'totalCount']);
            this.model.set(data);
        },
        render: function() {
            var data = this.model.toJSON();
            var markup = this.template({
                model: data
            });
            this.$el.html(markup);
            this.$pageNo = this.$('.page-num');
            return this;
        }
    });
    return Pager;
});