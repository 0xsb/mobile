define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('component/mobile/loader');
    var util = require('component/mobile/util');
    var userList = require('component/mobile/user-list');
    var approveSelect = require('component/mobile/approve-select');
    var UserSelect = require('component/mobile/user-select');
    var fulllist = require('component/mobile/full-list');
    var alert = util.alert;

    var urls = {
        getDefApprove: CONTEXT_PATH + '/microApp/customForm/getDefApprovalUsers.do',
        saveDefApprove: CONTEXT_PATH + '/microApp/customForm/setDefApprovalUsers.do',
        getAuth: CONTEXT_PATH + '/authority/getPerson.do',
        saveAuth: CONTEXT_PATH + '/authority/getApproval.do',
        getRole: CONTEXT_PATH + '/role/getRole.do',
        list: CONTEXT_PATH + '/moblicApprove/customform/list.do'
    };
    var goBack = function () {
        location.href = urls.list;
        //history.back();
    };

    var roleItemRender = template($('#tmpl-role').html());
    var getSelectedIds = function ($list) {
        var ids = $list.find('.approver-item').map(function (index, item) {
                return $(item).data('key');
            }).toArray();

        return ids.join(',');
    };
    var initRoleSelect = function ($el, defRoles) {
        var $list = $el.find('.list');
            $input = $el.find('input[type=hidden]');
        var dataCache, selectedItems = {};
        var selectRoleList = fulllist(false);
        var temp_trigger = $list.find('.trigger');;

        var addRole = function (role) {
            if ($list.find('.approver-item[data-key=' + role.id + ']').length > 0) {
                util.alert('不能选择重复的角色！');
                return false;
            }

            var $item = $(roleItemRender(role));
            $item.find('.avatar').on('click', function () {
                $item.remove();
                $input.val(getSelectedIds($list));
            });

            $item.insertBefore(temp_trigger);
            $input.val(getSelectedIds($list));
        };

        selectRoleList.on('select', function (e, item) {
            addRole(item);
            selectRoleList.hide();
        });

        $.ajax({
            url: urls.getRole,
            type: 'get',
            dataType: 'json',
            data: { rnd: (new Date).getTime() },
            success: function (res) {
                var roleData = _.map(res.model, function (item) { 
                    return { 
                        id: item.id, 
                        text: item.roleName,
                        avatar: {
                            colorCode: util.transColorCode(item.roleName),
                            subName: item.roleName.slice(-2)
                        }
                    }; 
                });
                selectRoleList.setData(roleData);

                $list.on('click', '.trigger', function (e) {
                    temp_trigger = $(this);
                    selectRoleList.show();
                });
            },
            error: function () {
                //TODO
            }
        });
        _.each(defRoles, function (item) {
            addRole({ 
                id: item.id, 
                text: item.roleName,
                avatar: {
                    colorCode: util.transColorCode(item.roleName),
                    subName: item.roleName.slice(-2)
                }
            });
        });
    };

    var doSubmit = function (id, defaultApprovalUserIds, lastUserId, authUsers, authReaders, authRoles) {
        var reqCount = 2;
        var submitDone = function () {
            reqCount--;
            if (reqCount <= 0) {
                alert('提交成功!', goBack);
            }
        };
        $.ajax({
            url: urls.saveDefApprove,
            dataType: 'json',
            type: 'post',
            data: {
                id: id,
                defaultApprovalUserIds: defaultApprovalUserIds,
                lastUserId: lastUserId,
                lastDealWay: '0'    //固定为【送代办】
            },
            success: function (res) {
                if (res.success) {
                    submitDone();
                }
                else {
                    alert(res.message || '提交失败！');
                }
            },
            error: function () {
                alert('发生错误！');
            }
        });
        $.ajax({
            url: urls.saveAuth,
            dataType: 'json',
            type: 'post',
            data: {
                id: id,
                userids: authUsers,
                reportUserIds: authReaders,
                roleId: authRoles,
                wyyId: 'wyy0002'    //固定为日志
            },
            success: function (res) {
                if (res.success) {
                    submitDone();
                }
                else {
                    alert(res.message || '提交失败！');
                }
            },
            error: function () {
                alert('发生错误！');
            }
        });
    };

    var initPage = function (id, approveData, authData) {
        var $defaultApproval = $('.form-item[role="approval-default"]');
        var approvalSelect = new UserSelect($defaultApproval, { multi: false });
        if (approveData.defUserList.length > 0) {
            var defUsers = _.map(approveData.defUserList, function (item) {
                item.userId = item.id;
                return item;
            });
            approvalSelect.setEmps(defUsers);
        }
        var $lastApproval = $('.form-item[role="approval-last"]');
        var lastApprovalSelect = new UserSelect($lastApproval, { multi: false });
        if (approveData.lastUser) {
            approveData.lastUser.userId = approveData.lastUser.id;
            lastApprovalSelect.setEmps([approveData.lastUser]);
        }

        var $authUsers = $('.form-item[role="auth-users"]');
        var authUserData = authData ? _.map(authData.users, function (item) {
            item.userId = item.id;
            item.subName = item.userName.slice(-2);
            item.colorCode = util.transColorCode(item.userName);
            return item;
        }) : null;
        approveSelect($authUsers, { multi: true, lastApprover: authUserData });

        var $authReaders = $('.form-item[role="auth-readers"]');
        var authReaderData = authData ? _.map(authData.reports, function (item) {
            item.userId = item.id;
            item.subName = item.userName.slice(-2);
            item.colorCode = util.transColorCode(item.userName);
            return item;
        }) : null;
        approveSelect($authReaders, { multi: true, lastApprover: authReaderData });


        var $authRoles = $('.form-item[role="auth-roles"]');
        initRoleSelect($authRoles, authData ? authData.roles : null);

        $('.submit').on('click', function () {
            var defaultUserId = approvalSelect.getEmps();
            defaultUserId = defaultUserId.length > 0 ? defaultUserId[0].userId : '';
            var lastUserId = lastApprovalSelect.getEmps();
            lastUserId = lastUserId.length > 0 ? lastUserId[0].userId : '';
            var authUsers = $('input[name="auth-users"]').val();
            var authRoles = $('input[name="auth-roles"]').val();
            var authReaders = $('input[name="auth-readers"]').val();

            doSubmit(id, defaultUserId, lastUserId, authUsers, authReaders, authRoles); //authRoles);
        });
    };

    var doGetData = function (id) {
        var reqCount = 2,
            approveData, authData;
        var submitDone = function () {
            reqCount--;
            if (reqCount <= 0) {
                initPage(id, approveData, authData);
            }
        };
        $.ajax({
            url: urls.getDefApprove,
            dataType: 'json',
            type: 'post',
            data: {
                id: id,
                rnd: (new Date).getTime()
            },
            success: function (res) {
                if (res.success) {
                    approveData = res.model;
                    submitDone();
                }
                else {
                    alert(res.message || '读取审批人失败！', goBack);
                }
            },
            error: function () {
                alert('读取审批人错误！', goBack);
            }
        });
        $.ajax({
            url: urls.getAuth,
            dataType: 'json',
            type: 'post',
            data: {
                id: id,
                rnd: (new Date).getTime()
            },
            success: function (res) {
                if (res.success) {
                    authData = res.model;
                    submitDone();
                }
                else {
                    alert(res.message || '读取授权人失败！', goBack);
                }
            },
            error: function () {
                alert('读取授权人错误！', goBack);
            }
        });
    };
    var run = function () {
        var id = util.getParam('id');
        if (id === '') { 
            alert('参数错误！', goBack); 
            return; 
        }

        doGetData(id);
    };

    module.exports = {
        run: run
    };
});