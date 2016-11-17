define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('component/mobile/util');
    var userList = require('component/mobile/user-list');
    var SelectList = require('component/mobile/select-list');
    var loader = require('component/mobile/loader');
    var formInit = require('component/mobile/form');
    var imgViewer = require('component/mobile/img-viewer');

    var urls = {
        userById: CONTEXT_PATH + '/users/getUserById.do',
        getApprovalInfo: CONTEXT_PATH + '/approval/getApprovalInfo.do',
        listPage: CONTEXT_PATH + '/moblicApprove/toWorkflowList.do',

        doApproval: CONTEXT_PATH + '/approval/ManageProcess.do',
        doAlRead: CONTEXT_PATH + '/approval/alRead.do'
    };

    var getEmpInfo = function (id, cb) {
        $.ajax({
            url: urls.userById,
            type: 'post',
            dataType: 'json',
            data: {
                secretKey: G.secretKey,
                selfCompanyId: G.companyId,
                companyId: G.companyId,
                id: id
            },
            success: function (res) {
                if (res.success) {
                    cb({
                        userName: res.model.userName,
                        showIndex: res.model.showindex,
                        mobile: res.model.mobile
                    });
                }
                else {
                    cb(null, res.message);
                }
            },
            error: function () {
                cb(null, '未知错误');
            }
        });
    };
    var getAllEmpInfo = function (steps, cb) {
        var userIdList = [];
        _.each(steps, function (step) {
            if (userIdList.indexOf(step.userId) < 0) {
                userIdList.push(step.userId);
            }
        });

        var empMap = {};
        var len = userIdList.length;
        _.each(userIdList, function (userid) {
            getEmpInfo(userid, function (info, error) {
                empMap[userid] = info;
                if (len >= 0 && error) {
                    len = -1;
                    cb(null, error);
                }
                else {
                    len--;
                    if (len === 0) {
                        cb(empMap);
                    }
                }
            });
        });
    };
    var getConfigMap = function (table) {
        var map = {};
        _.each(table, function (item) {
            map[item.reName] = item;
        });
        return map;
    };

    var dataRender = template($('#tmpl-datas').html()),
        infoRender = template($('#tmpl-info').html()),
        historyRender = template($('#tmpl-history').html());

    var getColor = function (str) {
        var code = 0, temp = 0, codeStr = '', i = 0;
        for (i = 0; i < str.length; i++) {
            code += str.charCodeAt(i);
        }
        for (i = 0; i < 3; i++) {
            temp = code % 192;
            codeStr += ('00' + temp.toString(16)).slice(-2);
            code = Math.round(code * temp / 127);
        }
        return '#' + codeStr;
    };
    var subname = function (name, length) {
        if (!length || length <= 0) {
            length = 2;
        }
        return name.slice(-1 * length);
    };

    var parseData = function (json, configMap) {
        var list = [];
        _.each(json, function (item, index) {
            //old data has some wrong fields
            if (item.controlName === 'approvalIds') {
                return;
            }

            if (item.controlName === 'TableField') {
                _.each(item.value, function (tableBox, i) {
                    list.push({ type: 'title', name: configMap[item.id].describeName, value: i + 1});
                    var t_list = parseData(tableBox.list, configMap);
                    list = list.concat(t_list);

                    //_.each(tableBox.list, function (tableItem) {
                    //    list.push({ type: 'item', name: configMap[tableItem.id].describeName, value: tableItem.value });
                    //});
                });
                _.each(item.sumList, function (sum, i) {
                    var ctrlId = sum.id.slice(5);   //sum data's id is begin with "total"
                    list.push({ type: 'sum', name: configMap[ctrlId].describeName, value: sum.value});
                });
            }
            else if (item.controlName === 'DDDateRangeField' || item.controlName === 'LinkageSelectField') {
                var labels = configMap[item.id].describeName.split(',');
                list.push({ type: 'item', name: labels[0], value: item.value[0] });
                list.push({ type: 'item', name: labels[1], value: item.value[1] });
            }
            else if (item.controlName === 'DDPhotoField') {
                var imgList = [];
                if (item.value.length > 0) {
                    try { imgList = JSON.parse(item.value); }
                    catch (ex) {
                        imgList = [];
                        //console.log('image wrong');
                    }
                }
                list.push({
                    type: 'img',
                    name: configMap[item.id].describeName,
                    value: imgList
                });
            }
            else if (item.controlName === 'DDAttachment') {
                list.push({
                    type: 'attach',
                    name: configMap[item.id].describeName,
                    value: item.value.length > 0 ? JSON.parse(item.value) : []
                });
            }
            else {
                list.push({ type: 'item', name: configMap[item.id].describeName, value: item.value });
            }
        });

        return list;
    };
    var statusMap = {
        '0': { text: '待审批', class: '' },
        '1': { text: '待审批', class: '' },
        '2': { text: '已通过', class: 'complete' },
        '3': { text: '已拒绝', class: 'reject' },
        '4': { text: '已撤销', class: '' },
        '5': { text: '待审阅', class: '' },
        '6': { text: '已审阅', class: 'complete' }
    };
    var parseStep = function (step, empMap) {
        var name = empMap[step.userId].userName; //step.name.slice(0, -2);
        return {
            userName: name,
            subName: subname(name),
            colorCode: getColor(name),
            time: step.examineDate || '',
            runStatus: step.runStatus,
            statusText: statusMap[step.runStatus].text,
            statusClass: statusMap[step.runStatus].class,
            opinion: step.opinion || ''
        };
    };

    var goBack = function () {
        var type = G.getParam('type');
        var wyyId = G.getParam('wyyId');
        location.href = urls.listPage + '?type=' + type + '&wyyId=' + wyyId;
        //history.back();
    };

    var getInputData = function ($input) {
        return {
            controlName: $input.attr('name').match(/[A-z]+/ig)[0],
            id: $input.attr('name'),
            value: $input.val()
        };
    };
    var serializeTablefield = function ($tablefield) {
        var listCollection = [], sumList = [];
        var boxes = $tablefield.find('.table-box');
        boxes.each(function (index, box) {
            var boxList = [];
            $(box).children().each(function (i, item) {
                var $item = $(item);
                if ($item.is('.textnote') || $item.is('.picturenote')) {
                    return;
                }
                if ($item.is('.form-item')) {
                    if ($item.is('.dddaterangefield')) {
                        var startInput = $item.find('.range-start'),
                            endInput = $item.find('.range-end');

                        boxList.push({
                            controlName: startInput.attr('name').match(/[A-z]+/ig)[0],
                            id: startInput.attr('name'),
                            value: [startInput.val(), endInput.val()]
                        });
                    }
                    else if ($item.is('.linkageselectfield')) {
                        var parentInput =  $item.find('input.parent'),
                            childInput =  $item.find('input.child');

                        boxList.push({
                            controlName: parentInput.attr('name').match(/[A-z]+/ig)[0],
                            id: parentInput.attr('name'),
                            value: [parentInput.val(), childInput.val()]
                        });
                    }
                    else {
                        var $input = $item.find('input[name] ,textarea[name]');
                        boxList.push(getInputData($input));
                    }
                }
            });
            listCollection.push({ list: boxList });
        });
        var sumFields = $tablefield.find('.compute-container .numbercompute');
        sumFields.each(function (index, item) {
            var $input = $(item).find('input[name]');
            sumList.push(getInputData($input));
        });

        return {
            controlName: 'TableField',
            id: $tablefield.find('.table-container').data('name'),
            value: listCollection,
            sumList: sumList
        };
    };
    var serializeCustomForm = function ($form) {
        var dataList = [];
        $form.children().each(function (index, item) {
            var $item = $(item);
            if ($item.is('.textnote') || $item.is('.picturenote') || $item.is('.approveselect')) {
                return;
            }
            else if ($item.is('.tablefield')) {
                dataList.push(serializeTablefield($item));
            }
            else if ($item.is('.dddaterangefield')) {
                var startInput = $item.find('.range-start'),
                    endInput = $item.find('.range-end');

                dataList.push({
                    controlName: startInput.attr('name').match(/[A-z]+/ig)[0],
                    id: startInput.attr('name'),
                    value: [startInput.val(), endInput.val()]
                });
            }
            else if ($item.is('.linkageselectfield')) {
                var parentInput =  $item.find('input.parent'),
                    childInput =  $item.find('input.child');

                dataList.push({
                    controlName: parentInput.attr('name').match(/[A-z]+/ig)[0],
                    id: parentInput.attr('name'),
                    value: [parentInput.val(), childInput.val()]
                });
            }
            else {
                var $input = $item.find('input[name] ,textarea[name]');
                dataList.push(getInputData($input));
            }
        });

        return { data: dataList };
    };

    var formObj;
    var typeModeMap = {
        'dwsp': 'approver',
        'wdfq': 'lunch',
        'dwyl': 'read',
        'wysp': ''
    };
    var showPopup = function (showUserSelect) {
        var $popup = $('#approve-popup');
        showUserSelect ? $popup.find('.user-select').show() : $popup.find('.user-select').hide();
        $popup.show();

        var w = $popup.find('.approve-modal').width(),
            h = $popup.find('.approve-modal').height();
        $popup.find('.approve-modal').css({
            'marginLeft': (w / -2) + 'px',
            'marginTop': (h / -2) + 'px'
        });
    };
    var doApproval = function (flowInfo, type, message, userid) {
        var url = type === 'yy' ? urls.doAlRead : urls.doApproval;
        var data = type === 'yy' ? {
                flowId: flowInfo.flowid,
                thirdId: flowInfo.thirdId,
                status: flowInfo.status,
                message: message,
                wyyId: G.getParam('wyyId'),
                taskId: flowInfo.taskId
            } : {
                flowid: flowInfo.flowid,
                thirdId: flowInfo.thirdId,
                status: flowInfo.status,
                type: type,
                message: message,
                userid: userid,
                wyyId: G.getParam('wyyId'),
                taskId: flowInfo.taskId
            };

        if (type === 'ty' && $('.detail-form').is(':visible')) {
            var jsondata = serializeCustomForm($('.detail-form'));
            data.jsondata = JSON.stringify(jsondata);
        }

        $.ajax({
            url: url,
            type: 'post',
            dataType: 'json',
            data: data,
            beforeSend: function () {
                loader.show();
            },
            success: function (res) {
                if (res.success) {
                    util.alert('操作成功！', function () {
                    	if(window.opener){
                    		//pc端，刷新首页并关闭当前
                    		window.opener.partRefresh('');
                    		window.close();
                    	}
                    	else goBack();	
                    });
                }
                else {
                    util.alert(res.message || '发生错误，操作失败！');
                }
            },
            complete: function () {
                loader.hide();
            }
        });
    };
    var initApproval = function (flowInfo, canMove) {
        var type = G.getParam('type');
        var mode = typeModeMap[type];
        if (mode !== '') {
            $('.detail-footer.' + mode).show();
        }
        if (!canMove) {
            $('.detail-footer').find('.move').hide();
        }

        var appType = '';
        $('.detail-footer').on('click', '.button', function (e) {
            var el = $(e.target);
            if (el.is('.revoke')) {
                util.confirm('确定撤销？', function (ok) {
                    if (ok) {
                        appType = 'cx';
                        doApproval(flowInfo, appType, '', '');
                    }
                });
            }
            else {
                var showUserSelect = el.is('.move');
                if (el.is('.agree')) {
                    appType = 'ty';
                    if (formObj) {
                        var valid = formObj.checkRequired();
                        if (!valid.success) {
                            util.alert(valid.message); return;
                        }
                    }
                }
                else if  (el.is('.reject')) appType = 'jj';
                else if  (el.is('.move')) appType = 'zf';
                else if  (el.is('.read')) appType = 'yy';
                showPopup(showUserSelect);
            }
        });

        var defaultDeptId = null;
        $.ajax({
            url: urls.userById,
            dataType: 'json',
            type: 'get',
            data: {
                companyId: G.companyId,
                selfCompanyId: G.companyId,
                secretKey: G.secretKey,
                id: G.userId,
                rnd: (new Date).getTime()
            },
            success: function (res) {
                if (res.success && res.model) {
                    defaultDeptId = res.model.orgId;
                }
            }
        });

        var empTrigger = $('#approve-popup input[name=username]'),
            empInput = $('#approve-popup input[name=userid]');
        var empSelect = userList.employeeSelect();
        empTrigger.on('click', function () {
            empSelect.show(defaultDeptId);
        });
        empSelect.on('select', function (e, emp) {
            empTrigger.val(emp.userName);
            empInput.val(emp.userId);
        });

        var commonAppTrigger = $('[data-do="common-app"]');
        var commonAppList = new SelectList({ multi: false });
        commonAppList.setList([
                { text: '同意', value: '同意' },
                { text: '已阅', value: '已阅' },
                { text: '拒绝', value: '拒绝' }
            ])
            .triggerFor(commonAppTrigger)
            .on('select', function (e, val) {
                $('#approve-popup').find('textarea').val(val.text);
                this.hide();
            });

        $('#approve-popup').find('.buttons').on('click', function (e) {
            var el = $(e.target);
            if (el.is('.btn-apply')) {
                var userid = '';
                if (appType === 'zf') {
                    userid = empInput.val();
                    if (userid.length == 0) {
                        util.alert('请选择转发人！'); return;
                    }
                }
                var message = $('#approve-popup').find('textarea').val();
                if (message.length > 100) {
                    util.alert('审批意见不能超过100个字符！'); return;
                }
                doApproval(flowInfo, appType, message, userid);
            }
            else {
                empTrigger.val('');
                empInput.val('');
                $('#approve-popup').find('textarea').val('');
                $('#approve-popup').hide();
            }
        });
    };

    var getFilesUrls = function (type, list) {
        var arr = [];
        list.find('.' + type + '-box').each(function (index, item) {
            var $this = $(this);
            arr.push({ id: $this.data('id'), name: $this.data('name') });
        });
        return arr.length === 0 ? '' : JSON.stringify(arr);
    };
    var addFile = function (model, list) {
        var html = '<div class="attach-box" data-name="' + model.name + '" data-id="' + model.id + '">\
            <div class="attach-icon"><i class="iconfont icon-xinxi"></i></div>\
            <span class="attach-name">' + model.name + '</span>\
            <span class="box-remove">\
                <i class="iconfont icon-shibai"></i>\
            </span>\
        </div>';

        list.append($(html));
    };
    var addImage = function (model, list) {
        var html = '<div class="img-box" data-name="' + model.name + '" data-id="' + model.id + '">\
            <span class="box-remove">\
                <i class="iconfont icon-shibai"></i>\
            </span>\
            <img id="images" src="' + CONTEXT_PATH + '/file/download/' + model.id + '/thum_' + model.name + '.do' + '">\
        </div>';

        list.append($(html));
    };

    var setField = function (parent, valItem) {
        if (valItem.controlName === 'DDDateRangeField') {
            parent.find('.range-start[name=' + valItem.id + ']').val(valItem.value[0]);
            parent.find('.range-end[name=' + valItem.id + ']').val(valItem.value[1]);
        }
        else if (valItem.controlName === 'DDPhotoField' || valItem.controlName === 'DDAttachment') {
            var valInput = parent.find('[name=' + valItem.id + ']');
            valInput.val(valItem.value);
            var list = valInput.parent().find('.list');

            var models = valItem.value.length > 0 ? JSON.parse(valItem.value) : [];
            _.each(models, function (model) {
                valItem.controlName === 'DDPhotoField' ? addImage(model, list) : addFile(model, list);
            });
        }
        else {
            parent.find('[name=' + valItem.id + ']').val(valItem.value);
        }
    };
    var setTableField = function (container, values) {
        var addBtn = container.parent().find('.add-button');
        _.each(values, function (item, index) {
            if (index > 0) { addBtn.click(); }
            var box = container.children().eq(index);
            _.each(item.list, function (valItem) {
                setField(box, valItem);
            });
        });
    };
    var initForm = function (control, jsonData) {
        var $form = $('.detail-form').show();
        $('.detail-data').hide();
        formObj = formInit($form, control);

        _.each(jsonData.data, function (item) {
            if (item.controlName === 'TableField') {
                var container = $('[data-name=' + item.id + ']');
                setTableField(container, item.value);

                var sumContainer = container.next();
                _.each(item.sumList, function (sumItem) {
                    setField(sumContainer, sumItem);
                });
            }
            else {
                setField($form, item);
            }
        });
    };

    var initDetail = function (list) {
        $('.detail-data').html(dataRender({ list: list }));

        $('.img-block').on('click', function (e) {
            var $this = $(this);
            var fullImagePath = $this.data('fullpath');
            imgViewer.show(fullImagePath, true);
        });
    };

    var initPage = function (model, empMap) {
        var type = G.getParam('type');
        var configMap = getConfigMap(model.table);

        var currentStep = model.processrecord[0];
        var infoData = parseStep(currentStep, empMap);
        infoData.text = infoData.userName + '的' + model.typename + '申请';

        var history = _.map(model.processrecord, function (item, index) {
            return parseStep(item, empMap);
        });

        $('.detail-info').html(infoRender(infoData));
        $('.detail-history').html(historyRender({ list: history }));

        var isNeedToWrite = model.editstatus === '2' || model.editstatus === '3';
        if (isNeedToWrite && type === 'dwsp') {
            var jsData = model.editstatus === '3' ? model.json : { data: [] };
            initForm(model.table, jsData);
        }
        else {
            var list = parseData(model.json.data, configMap);
            initDetail(list);
        }

        var couldOperation = true;
        var steps = model.processrecord;
        if (type == 'wdfq' && steps[1].runStatus != '1' && steps[1].runStatus != '0') {
            couldOperation = false;
        }
        else if (type !== 'wdfq' && type !== 'dwsp' && type !== 'dwyl') {
            couldOperation = false;
        }
        return couldOperation;
    };

    var run = function () {
        loader.show();
        var flowid = G.getParam('flowid'),
            thirdId = G.getParam('thirdId'),
            status = G.getParam('status'),
        	taskId = G.getParam('taskId');
        var flowInfo = {
            flowid: flowid,
            thirdId: thirdId,
            status: status,
            taskId: taskId
        };

        var wyyId = util.getParam('wyyId');
        $('.header').addClass(wyyId);

        $.ajax({
            url: urls.getApprovalInfo,
            type: 'get',
            dataType: 'json',
            data: {
                rnd: (new Date).getTime(),
                flowid: flowid
            },
            success: function (res) {
                if (res.success) {
                    getAllEmpInfo(res.model.processrecord, function (empMap, error) {
                        if (error) {
                            util.alert(error);
                        }
                        else {
                            var couldOperation = initPage(res.model, empMap);
                            if (couldOperation) initApproval(flowInfo, true); //res.model.status === '0');
                        }

                        loader.hide();
                    });
                }
                else {
                    util.alert(res.message || '读取审批数据出错！', function () {
                        goBack();
                    });
                }
            },
            error: function () {
                util.alert('读取审批数据出错！', function () {
                    goBack();
                });
            }
        });
        $('.back').on('click', goBack);
    };

    module.exports = {
        run: run
    };
});
