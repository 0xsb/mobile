/*
 * common approval index page for mobile client
 */
define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var formInit = require('component/mobile/form');
    var loader = require('component/mobile/loader');
    var util = require('component/mobile/util');

    var urls = {
        getReport: CONTEXT_PATH + '/report/getReport.do',
        getAntuarity: CONTEXT_PATH + '/approval/getAntuarity.do',
        toReport: CONTEXT_PATH + '/moblicApprove/toReport.do',
        toIndex: CONTEXT_PATH + '/moblicApprove/toDailys.do'
    };

    var defaultFormSettings = {
        widgets: [
            {
        		'describeName': '报表类型',
        		'isRequired': false,
        		'reName': 'type',
        		'sequence': 100,
        		'controlId': 'SyncSelectField',
                'url': urls.getAntuarity,
                'textField': 'name',
                'valueField': 'name',
                'preprocess': function (model) {
                    return model.AntulList;
                },
                'exp': '请选择',
        		'value': '',
        		'jsonData': ''
        	},
            {
				'describeName': '起始时间',
				'isEffective': '0',
				'isMerge': '0',
				'isRequired': null,
				'isStorage': null,
				'reName': 'startDate',
				'sequence': 200,
				'controlId': 'DDDateField',
				'exp': '请选择(必填)',
				'value': null,
				'previousId': '',
				'jsonData': ''
			},
            {
				'describeName': '结束时间',
				'isEffective': '0',
				'isMerge': '0',
				'isRequired': null,
				'isStorage': null,
				'reName': 'endDate',
				'sequence': 300,
				'controlId': 'DDDateField',
				'exp': '请选择(必填)',
				'value': null,
				'previousId': '',
				'jsonData': ''
			},
            {
				'describeName': '填报人',
				'isEffective': '0',
				'isMerge': '0',
				'isRequired': null,
				'isStorage': null,
				'reName': 'userName',
				'sequence': 400,
				'controlId': 'TextField',
				'exp': '',
				'value': null,
				'previousId': '',
				'jsonData': ''
			},
            {
                'describeName': '关键字',
                'isRequired': false,
                'reName': 'drapId',
                'sequence': 500,
                'controlId': 'SyncSelectField',
                'url': urls.getReport,
                'dependents': ['type'],
                'textField': 'type',
                'valueField': 're_name',
                'preprocess': function (model) {
                    model.type.unshift({ re_name: '', type: '无' });
                    return model.type;
                },
                'exp': '请选择',
                'value': '',
                'jsonData': ''
            },
            {
				'describeName': '关键字内容',
				'isEffective': '0',
				'isMerge': '0',
				'isRequired': null,
				'isStorage': null,
				'reName': 'drapName',
				'sequence': 600,
				'controlId': 'TextField',
				'exp': '',
				'value': null,
				'previousId': '',
				'jsonData': ''
			}
        ]
    };

    var run = function () {
        var $form = $('.content-form');
        var formView = formInit($form, defaultFormSettings.widgets);

        $('.submit').on('click', function () {
            var queryStr = $form.serialize();
            location.href = urls.toReport + '?' + queryStr;
        });

        $('.back').on('click', function () {
            location.href = urls.toIndex;
        });
    };

    module.exports = {
        run: run
    };
});
