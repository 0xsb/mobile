define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var userList = require('./user-list');
    var util = require('./util');

    var itemTemplate = '<div class="approver-item" data-key={{userId}}>\
        <div class="avatar" style="background:#{{colorCode}}">\
            {{subName}}\
        </div><span class="name">{{userName}}</span></div>';
    var itemRender = template(itemTemplate);
    var defAppTemplate = '<# _.each(list, function (item) { #>\
        <# if (item.id !== \'$$$\') { #>\
        <div class="approver-item" data-key={{item.id}}>\
            <div class="avatar" style="background:#{{item.colorCode}}">\
                {{item.subName}}\
            </div><span class="name">{{item.userName}}</span>\
        </div>\
        <# } else { #>\
            <div class="trigger"><i class="iconfont icon-tianjialeimu"></i></div>\
        <# } #>\
    <# }) #>';
    var defAppRender = template(defAppTemplate);

    var getSelectedIds = function ($list) {
        var ids = $list.find('.approver-item').map(function (index, item) {
                return $(item).data('key');
            }).toArray();

        return ids.join(',');
    };

    var approveSelect = function (elem, options) {
        var multi = options.multi && !options.defaultApprover;
        var $list = elem.find('.list'),
            inputer = elem.find('input[type=hidden]');
        var dataCache, selectedItems = {};
        var empSelect = userList.employeeSelect();

        var defaultDeptId = null;
        if (options.positioning === true) {
            $.ajax({
                url: CONTEXT_PATH + '/users/getUserById.do',
                dataType: 'json',
                type: 'get',
                data: {
                    companyId: util.constant.companyId,
                    selfCompanyId: util.constant.companyId,
                    secretKey: util.constant.secretKey,
                    id: util.constant.userId,
                    rnd: (new Date).getTime()
                },
                success: function (res) {
                    if (res.success && res.model) {
                        defaultDeptId = res.model.orgId;
                        //posList = [];
                        //var dept = res.model.org;
                        //while (dept) {
                        //    posList.unshift(dept.id);
                        //    dept = dept.previous;
                        //}
                    }
                }
            });
        }

        var temp_trigger;
        var addEmp = function (emp) {
            if ($list.find('.approver-item[data-key=' + emp.userId + ']').length > 0) {
                util.alert('不能选择重复的人员！');
                return false;
            }

            var $item = $(itemRender(emp));
            $item.find('.avatar').on('click', function () {
                //selectedItems[emp.userId] = false;
                if (!multi) {
                    $item.next('.trigger').show();
                }
                $item.remove();
                inputer.val(getSelectedIds($list));
            });

            //selectedItems[emp.userId] = true;
            $item.insertBefore(temp_trigger);
            inputer.val(getSelectedIds($list));
            if (!multi) {
                $item.next('.trigger').hide();
            }
        };
        $list.on('click', '.trigger', function (e) {
            temp_trigger = $(this);
            empSelect.show(defaultDeptId);
        });
        empSelect.on('select', function (e, emp) {
            addEmp(emp);
        });

        elem.valid = function () {
            var ids = inputer.val().split(',');
            var success = true;
            if (options.defaultApprover && options.defaultApprover.length > ids.length) {
                success = false;
            }
            else {
                success = ids.length > 0 && ids[0] != '';
            }
            return success;
        };

        if (!options.defaultApprover) {
            $list.html('<div class="trigger"><i class="iconfont icon-tianjialeimu"></i></div>');
            temp_trigger = $list.find('.trigger');
            if (options.lastApprover) {
                _.each(options.lastApprover, function (emp) {
                    addEmp(emp);
                });
            }
        }
        else {
            $list.html(defAppRender({ list: options.defaultApprover }));
            inputer.val(getSelectedIds($list));
        }
    };

    module.exports = approveSelect;
});
