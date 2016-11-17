/*
 * common approval index page for mobile client
 */
define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('component/mobile/loader');
    var util = require('component/mobile/util');
    require('dep/jquery-tablesorter/jquery.tablesorter');
    var urls = {
        getReport: CONTEXT_PATH + '/report/getReportList.do',
    };

    var listRender = template($('#tmpl-list').html());
    var initPage = function (model) {
        $('.list-panel').html(listRender(model));
        $('#report').tablesorter();
        // $('#report th i').click(function(e) {
        //     var that = $(e.target).children();
        //     that.css('visibility','visible');
        //     if (that.hasClass('down')){
        //         that.removeClass('down').addClass('up');
        //     }else if(that.hasClass('up')){
        //         that.removeClass('up').addClass('down');
        //     }
        // })
    };

    var run = function () {   
        var query = {};
        var params = _.each(['type','startDate','endDate','userName','drapId','drapName'], function (item) {
            query[item] = util.getParam(item);
            query[item] === '' && (query[item] = null);
        });

        $.ajax({
            url: urls.getReport,
            type: 'post',
            dataType: 'json',
            data: _.defaults(query, {
                rnd: (new Date).getTime(),
            }),
            beforeSend: function () {
                loader.show();
            },
            success: function (res) {
                if (res.success) {
                    initPage(res.model);
                }
                else {
                    util.alert(res.message || '读取报表出错！');
                }
            },
            error: function () {
                util.alert('读取报表出错！');
            },
            complete: function () {
                loader.hide();
            }
        });
        
    };

    module.exports = { run: run };
});
