define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('./util');

    var itemSelect = require('./item-select'),
        datetimeSelect = require('./date-select'),
        tableField = require('./table-field'),
        userList = require('./user-list'),
        approveSelect = require('./approve-select'),
        syncSelect = require('./sync-select'),
        linkageSelect = require('./linkage-select'),
        UserSelecter = require('./user-select');
    var attachSelect = require('./attach-select'),
        imageSelect = require('./image-select');
    var EventButton = require('./event-button');
    var imgViewer = require('./img-viewer');
    var expressionParser = require('component/util/computeExpression');

    var isDecimal = function (val) { return /^\d+(\.\d{0,4}){0,1}$/.test(val); };
    var isInteger = function (val) { return /^\d+$/.test(val); };
    var sumArray = function (arr) {
        var sum = 0;
        _.each(arr, function (num) {
            sum += num;
        });
        return sum;
    };
    var bak_removeNotNumber = function (str) {
        var chars = str.split('');
        var firstDot = false;

        var code, index = 0, offset = 0, length = chars.length;
        for (var i = 0; i < length; i++) {
            index = i - offset;
            code = chars[index].charCodeAt(0);
            if (code === 46 && !firstDot) {
                firstDot = true;
            }
            else if (code < 48 || code > 57) {
                chars.splice(index, 1);
                offset++;
            }
        };

        return chars.join('');
    };
    var removeNotNumber = function (str, fixed) {
        if (str.length === 0) return str;

        var segment = str.split('.');
        var integerPart = segment.shift().replace(/\D/g, '');
        if (integerPart.length === 0) integerPart = '0';
        if (segment.length > 0 && fixed > 0) {
            integerPart += '.';
            var decimalPart = segment.join('').replace(/\D/g, '');
            if (typeof fixed === 'number') {
                decimalPart = decimalPart.slice(0, fixed);
            }
            if (decimalPart.length > 0) {
                integerPart += decimalPart;
            }
        }

        return integerPart;
    };

    var renderMap = {};
    var componentRender = function (type, content) {
        if (!renderMap[type]) {
            var templateStr = '<div class="form-item ' + type.toLowerCase() + '" data-widget="' + type + '">'
                + content + '</div>';
            renderMap[type] = template(templateStr);
        }
        return renderMap[type];
    };

    //V网通提供的系统判断方法
    function isAndroid () {
		var ua = navigator.userAgent.toLowerCase();
		if (/iphone|ipad|ipod/.test(ua)) {
			return false;
		} else if (/android/.test(ua)) {
			return true;
		}
	}

    var widgets = {
        TextField: function (option) {
            var render = componentRender('TextField',
                '<label>{{describeName}}</label>' +
                '<input name="{{reName}}" type="text" value="{{value}}" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" ' +
                '{{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>');
            return { $el: $(render(option)) };
        },
        TextareaField: function (option) {
            var render = componentRender('TextareaField',
                '<label>{{describeName}}</label>' +
                '<textarea name="{{reName}}" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" ' +
                '{{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>{{value}}</textarea>');
            return { $el: $(render(option)) };
        },
        NumberField: function (option) {
            var render = componentRender('NumberField',
                '<label>{{describeName}}</label>' +
                '<input name="{{reName}}" maxlength="12" type="text" value="{{value}}" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" ' +
                '{{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>');
            var element = $(render(option));

            var checker = isDecimal, fixed = 4;
            if (option.jsonData) {
                if (option.jsonData.integer === true) { checker = isInteger; fixed = 0; }
            }

            element.find('input[type=text]').on('input', function (e) {
                var val = $(this).val();
                if (!checker(val)) {
                    e.target.value = removeNotNumber(val, fixed);
                }
            });
            return { $el: element };
        },
        DDSelectField: function (option) {
            var render = componentRender('DDSelectField',
                '<label>{{describeName}}</label>' +
                '<input name="{{reName}}" type="text" class="trigger" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly {{isRequired ? "required" : ""}}>');
            var element = $(render(option));
            itemSelect(element.find('input.trigger'), option.jsonData.options, false);
            return { $el: element };
        },
        DDMultiSelectField: function (option) {
            var render = componentRender('DDMultiSelectField',
                '<label>{{describeName}}</label>' +
                '<input name="{{reName}}" type="text" class="trigger" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly {{isRequired ? "required" : ""}}>');
            var element = $(render(option));
            itemSelect(element.find('input.trigger'), option.jsonData.options, true);
            return { $el: element };
        },
        DDDateField: function (option) {
            var render = componentRender('DDDateField',
                '<label>{{describeName}}</label>' +
                '<input name="{{reName}}" type="text" class="trigger" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly {{isRequired ? "required" : ""}}>');
            var element = $(render(option));
            datetimeSelect(element.find('input.trigger'), option.timeformat);
            return { $el: element };
        },
        DDDateRangeField: function (option) {
            option.label = option.describeName.split(',');
            var render = componentRender('DDDateRangeField',
                '<div class="range-item">' +
                    '<label>{{label[0]}}</label>' +
                    '<input name="{{reName}}" type="text" class="trigger range-start" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly {{isRequired ? "required" : ""}}>' +
                '</div><div class="range-item">' +
                    '<label>{{label[1]}}</label>' +
                    '<input name="{{reName}}" type="text" class="trigger range-end" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly {{isRequired ? "required" : ""}}>' +
                '</div>');
            var element = $(render(option));

            datetimeSelect(element.find('input.trigger'), option.timeformat);

            element.on('change', 'input.trigger', function (e) {
                var elem = $(e.target);
                var rangeStart = element.find('.range-start').val(),
                    rangeEnd = element.find('.range-end').val();
                if (rangeStart.length > 0 && rangeEnd.length > 0 && rangeStart > rangeEnd) {
                    util.alert(option.label[0] + '不能大于' + option.label[1]);
                    elem.val('');
                }
            });

            return { $el: element };
        },
        DDPhotoField: function (option) {
            var render = componentRender('DDPhotoField',
                '<span>{{describeName}}</span>' +
                '<a href="javascript:" class="camara-trigger trigger"><i class="iconfont icon-zhaoxiang"></i></a>' +
                '<input class="file-input" type="file" accept="image/*" capture="camera" style="visibility: hidden;width: 1px;">' +
                '<input class="value-input" name="{{reName}}" type="hidden" {{isRequired ? "required" : ""}}>' +
                '<div class="list"></div>');
            var element = $(render(option));
            var component = imageSelect(element);
            return component;
        },
        TableField: function (option, children, afterRender) {
            var render = componentRender('TableField',
                '<div class="table-container" data-name="{{reName}}"></div>' +
                '<div class="compute-container"></div>' +
                '<div class="add-button">+ {{exp}}</div>');
            var element = $(render(option));
            var tabelView = tableField(element, option, children, widgets);

            afterRender.push(function ($form, formObj) {
                tabelView.render($form, formObj);
            });

            return tabelView;
        },
        TextNote: function (option) {
            var render = componentRender('TextNote', '<# var _exp = exp.split(\'<br>\'); _.each(_exp, function (t, i) { #>\
                <p>{{t}}</p>\
            <# }) #>');
            //option.exp = option.exp.split('<br>');
            return { $el: $(render(option)) };
        },
        MoneyField: function (option) {
            var render = componentRender('MoneyField',
                '<label>{{describeName}}</label>' +
                '<input name="{{reName}}" maxlength="12" type="text" value="{{value}}" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" ' +
                '{{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>');
            var element = $(render(option));

            element.find('input[type=text]').on('input', function (e) {
                var val = $(this).val();
                if (!isDecimal(val)) {
                    e.target.value = removeNotNumber(val, 4);
                }
            });
            return { $el: element };
        },
        DDAttachment: function (option) {
            var render = componentRender('DDAttachment',
                '<span>{{describeName}}</span>' +
                '<a href="javascript:" class="camara-trigger trigger"><i class="iconfont icon-fujian"></i></a>' +
                '<input class="file-input" type="file" style="visibility: hidden;width: 1px;">' +
                '<input class="value-input" name="{{reName}}" type="hidden" {{isRequired ? "required" : ""}}>' +
                '<div class="list"></div>');
            var element = $(render(option));
            var component = attachSelect(element);
            return component;
        },
        PictureNote: function (option) {
            var render = componentRender('PictureNote', '<# var _exp = exp.split(\'<br>\'); _.each(_exp, function (t, i) { #>\
                <p>{{t}}</p>\
            <# }) #>\
            <div class="picture-box">\
                <# if (jsonData.imginfo && jsonData.imginfo.id) { #>\
                <img src="' + CONTEXT_PATH + '/file/download/{{jsonData.imginfo.id}}/thum_{{jsonData.imginfo.addr}}.do" alt="">\
                <# } #>\
            </div>');
            var element = $(render(option));
            var fullimgPath = CONTEXT_PATH + '/file/download/' +
                option.jsonData.imginfo.id + '/' + option.jsonData.imginfo.addr + '.do';

            element.find('img').on('click', function (e) {
                imgViewer.show(fullimgPath);
            });
            return { $el: element };
        },
        LinkageSelectField: function (option) {
            option.label = option.describeName.split(',');
            var render = componentRender('LinkageSelectField',
                '<div class="range-item">' +
                    '<label>{{label[0]}}</label>' +
                    '<input name="{{reName}}" type="text" class="trigger parent" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly {{isRequired ? "required" : ""}}>' +
                '</div><div class="range-item">' +
                    '<label>{{label[1]}}</label>' +
                    '<input name="{{reName}}" type="text" class="trigger child" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly {{isRequired ? "required" : ""}}>' +
                '</div>');
            var element = $(render(option));
            linkageSelect(element, option);
            return { $el: element };
        },
        ComputeField: function (option, children, afterRender) {
            var render = componentRender('ComputeField',
                '<label>{{describeName}}</label>' +
                '<input name="{{reName}}" type="text" readonly>');
            var element = $(render(option));

            var astRoot = expressionParser.parseExpression(option.jsonData.expression);
            var paramData = option.jsonData.expressionData;
            var selectStr = [];
            for (var k in paramData) {
                if (paramData[k].indexOf('LinkageSelectField') >= 0) {
                    selectStr.push('[name=' + paramData[k] + '].child');
                }
                else {
                    selectStr.push('[name=' + paramData[k] + ']');
                }
            }
            selectStr = selectStr.join(',');
            var inputer = element.find('input[type=text]');

            afterRender.push(function ($form) {
                $form.on('change input', selectStr, function () {
                    var valMap = {}, valEl;
                    for (var k in paramData) {
                        valEl = paramData[k].indexOf('LinkageSelectField') >= 0 ?
                            $form.find('[name=' + paramData[k] + '].child') :
                            $form.find('[name=' + paramData[k] + ']');
                        if (valEl.length > 1) {
                            var numArr = valEl.map(function (i, el) {
                                var temp_val = $(el).val();
                                return isNaN(temp_val) ? 0 : parseFloat(temp_val);
                            }).toArray();
                            valMap[k] = sumArray(numArr);
                        }
                        else {
                            valMap[k] = parseFloat(valEl.val());
                        }
                        if (isNaN(valMap[k])) valMap[k] = 0;
                    }

                    var result = astRoot.getValue(valMap);
                    if (result === Infinity || result === -Infinity || isNaN(result)) {
                        result = '';
                    }
                    else {
                        result = result.toString();
                        if (result.indexOf('.') >= 0) {
                            result = result.substr(0,result.indexOf('.') + 3);
                        }
                    }
                    inputer.val(result);
                    inputer.trigger('change');
                });
            });

            return { $el: element };
        },
        ValidField: function (option, children, afterRender) {
            //var render = componentRender('ValidField',
            //    '<input name="{{reName}}" type="hidden">');
            //var element = $(render(option));
            var astRoot = expressionParser.parseExpression(option.jsonData.expression);
            var paramData = option.jsonData.expressionData;

            return {
                message: option.exp,
                valid: function ($form) {
                    var valMap = {};
                    var valEl;
                    for (var k in paramData) {
                        if (paramData[k].indexOf('LinkageSelectField') >= 0) {
                            valEl = $form.find('[name=' + paramData[k] + '].child');
                        }
                        else {
                            valEl = $form.find('[name=' + paramData[k] + ']');
                        }
                        if (valEl.length > 1) {
                            var numArr = valEl.map(function (i, el) {
                                var temp_val = $(el).val();
                                return isNaN(temp_val) ? 0 : parseFloat(temp_val);
                            }).toArray();
                            valMap[k] = sumArray(numArr);
                        }
                        else {
                            valMap[k] = parseFloat(valEl.val());
                        }
                        if (isNaN(valMap[k])) valMap[k] = 0;
                    }
                    var result = astRoot.getValue(valMap);
                    return result;
                }
            };
        },
        EventButton: function (option, children, afterRender) {
            var render = componentRender('EventButton',
                '<a href="javascript:" class="button" role="event-button">{{describeName}}</a>');
            var element = $(render(option));
            var button = new EventButton(element, option);

            afterRender.push(function ($form, formObj) {
                button.bind(formObj);
            });

            return button;
        },
        UserSelect: function (option) {
            var render = componentRender('UserSelect',
                '<span>{{describeName}}</span>' +
                '<a href="javascript:" class="camara-trigger trigger"><i class="iconfont icon-tianjialeimu"></i></a>' +
                '<input name="{{reName}}" type="hidden">' +
                '<div class="list"></div>');
            var element = $(render(option));

            var widget = new UserSelecter(element, { multi: option.multi });
            return widget;
        },
        Location: function (option, children, afterRender) {
            var render = componentRender('Location',
                '<label><a href="javascript:" class="button {{isReadonly ? "disabled" : ""}}">{{describeName}}</a></label>' +
                '<input name="{{reName}}" type="text" value="{{value}}" role="vwt-location" ' +
                    'data-format="{{exp}}" readonly>');
            var element = $(render(option));
            afterRender.push(function ($form, formObj) {
                element.find('.button').on('click', function () {
                    if ($(this).is('.disabled')) { return; }
                    if (isAndroid()) {
            			window.vwtNativeJs.getLocation();
            		} else {
            			iosGetLocation();
            		}
                });
            });

            return { $el: element };
        }
    };

    //供V网通调用的位置获取回调方法
    /* V网通定义的locationInfo格式如下
    {
        latitude:”39.000001”,
		longitude:”37.00134”,
		address:”南京市软件大道”,
        status:” success”,   是否成功 success成功，error获取失败
		desc	:””          失败原因描述
	}
    */
    window.setLocation = function (locationInfo) {
        if (locationInfo.status != 'success') {
            util.alert(locationInfo.desc);
            return;
        }

        $('[role="vwt-location"]').each(function () {
            var format = $(this).data('format');
            if (format == '0') {
                $(this).val(locationInfo.address);
            } else {
                $(this).val(locationInfo.address + ' ' + util.formatDate(new Date, 'yyyy-MM-dd HH:mm'));
            }
        });
    };

    //额外附加控件
    //审批人选择
    widgets['ApproveSelect'] = function (option) {
        var render = componentRender('ApproveSelect',
            '<span>{{describeName}}</span>' +
            '<input name="{{reName}}" type="hidden">' +
            '<div class="list">\
            </div>');
        var element = $(render(option));
        approveSelect(element, option);
        return { $el: element };
    };
    //数值计算
    widgets['NumberCompute'] = function (option, children, afterRender) {
        var render = componentRender('NumberCompute',
            '<label>{{describeName}}</label>' +
            '<input name="{{reName}}" type="text" readonly>');
        var element = $(render(option));

        var inputer = element.find('input[type=text]');
        var selectStr = _.map(option.factors, function (item) {
                return '[name=' + item + ']';
            }).join(',');
        var operation,
            parseType = option.type === 'float' ? parseFloat : parseInt;
        if (option.operation === 'multiply') {
            operation = function (items) {
                var product, temp;
                while (items.length > 0) {
                    temp = items.pop();
                    if (temp === '') { product = 0; break; }
                    if (typeof product === 'undefined') { product = parseType(temp); }
                    else { product *= parseType(temp); }
                }

                return option.type === 'float' ? product.toFixed(option.fixed || 2) : product.toString();
            };
        }
        else {
            operation = function (items) {
                var sum = 0;
                for (var i = 0; i < items.length; i++) {
                    if (items[i] === '') { continue; }
                    sum += parseType(items[i]) || 0;
                }
                return option.type === 'float' ? sum.toFixed(option.fixed || 2) : sum.toString();
            };
        }

        afterRender.push(function ($form) {
            //var factorElems = $form.find(selectStr);
            $form.on('change input', selectStr, function () {
                var values = $form.find(selectStr)
                        .map(function (index, elem) { return $.trim(elem.value); })
                        .toArray();
                inputer.val(operation(values));
                inputer.trigger('change');
            });
        });
        return { $el: element };
    };
    //人员选择
    widgets['EmployeeField'] = function (option) {
        var render = componentRender('DDSelectField',
            '<label>{{describeName}}</label>' +
            '<input name="{{reName}}" type="hidden" {{isRequired ? "required" : ""}}>' +
            '<input name="{{reName}}-text" type="text" class="trigger" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly>');
        var element = $(render(option));
        var empSelect = userList.employeeSelect();
        element.find('input.trigger').on('click', function () { empSelect.show(); });
        empSelect.on('select', function (e, emp) {
            element.find('input.trigger').val(emp.userName);
            element.find('input[type=hidden]').val(emp.userId);
        });
        return { $el: element };
    };
    //部门选择
    widgets['DepartmentField'] = function (option) {
        var render = componentRender('DDSelectField',
            '<label>{{describeName}}</label>' +
            '<input name="{{reName}}" type="hidden" {{isRequired ? "required" : ""}}>' +
            '<input name="{{reName}}-text" type="text" class="trigger" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly>');
        var element = $(render(option));
        var depSelect = userList.departmentSelect();
        element.find('input.trigger').on('click', function () { depSelect.show(); });
        depSelect.on('select', function (e, dep) {
            element.find('input.trigger').val(dep.orgName);
            element.find('input[type=hidden]').val(dep.orgId);
        });
        return { $el: element };
    };
    //同步联动的选择控件
    widgets['SyncSelectField'] = function (option, children, afterRender) {
        var render = componentRender('DDSelectField',
            '<label>{{describeName}}</label>' +
            '<input name="{{reName}}" type="hidden" class="value-field" {{isRequired ? "required" : ""}}>' +
            '<input name="{{reName}}-text" type="text" class="trigger" placeholder="{{exp}}{{isRequired ? \'(必填)\' : \'\'}}" readonly>');
        var element = $(render(option));
        var lister = syncSelect(element, option);

        lister.setUrl(option.url);
        var selectStr = _.map(option.dependents, function (item) {
                return '[name=' + item + ']';
            }).join(',');
        afterRender.push(function ($form) {
            lister.setDependents($form.find(selectStr));
        });
        return { $el: element };
    };

    module.exports = widgets;
});
