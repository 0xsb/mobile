define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var alert = require('component/Alert');

    var urls = {
        index: CONTEXT_PATH + '/moblicApprove/toPcFrame.do',
        SMS: CONTEXT_PATH + '/user/createCode.do',
        login: CONTEXT_PATH + '/user/verifyMobile.do',
        getCompany: CONTEXT_PATH + '/approvals/showCompany.do',
        selectCompany: CONTEXT_PATH + '/approvals/selectCompany.do',
        checkCode: CONTEXT_PATH + '/user/verifyImageCode.do',
        checkCode2: CONTEXT_PATH + '/OA/imageCheck.do',
        loginbypass: CONTEXT_PATH + '/OA/login.do'
    };

    var createCompanys = function (list) {
        return _.map(list, function (item, index) {
            return '<option value="' + item.userId + '" ' + (index === 0 ? 'selected' : '') + '>'
                + item.companyName + '</option>';
        }).join('');
    };
    var toSelectCompany = function (mobile, lastForm) {
        var $form = $('#form-company');
        var $company = $('#company-select');

        $.ajax({
            url: urls.getCompany,
            type: 'get',
            dataType: 'json',
            data: {
                mobile: mobile,
            },
            success: function (res) {
                if (res.success) {
                    $company.html(createCompanys(res.model));
                    lastForm.hide();
                    $('.login-tabs').hide();
                    $form.show();
                }
                else {
                    alert(res.message || '读取组织架构信息错误！').delay(3);
                }
            },
            error: function () {
                alert('读取组织架构信息错误！').delay(3);
            },
        });

        var pending = false;

        $form.on('submit', function (e) {
            e.preventDefault();
            if (pending) { return false; }

            var userId = $company.val();
            $.ajax({
                url: urls.selectCompany,
                type: 'post',
                dataType: 'json',
                data: {
                    userId: userId
                },
                beforeSend: function () {
                    pending = true;
                },
                success: function (res) {
                    if (res.success) {
                        location.href = urls.index;
                    }
                    else {
                        alert(res.message || '载入公司信息出错！').delay(3);
                    }
                },
                error: function () {
                    alert('载入公司信息出错！').delay(3);
                },
                complete: function () {
                    pending = false;
                }
            });
        });
    };

    var run = function () {
        var $form = $('#form-login');
		var $form2 = $('#form-pass');
        var $sendSMS = $('.btn-sendSMS');
        var $validImg = $('.valid-img');
		var $validImg2 = $('.valid-img2');
        var pending = false;
        var imageCodeUrl = $validImg.attr('src');
        var imageCodeUrl2 = $validImg2.attr('src');

        var countDown = 0, cdTimer;
        var startCountDown = function (seconds) {
            countDown = seconds;
            $sendSMS.addClass('countdown');
            cdTimer = setInterval(function () {
                countDown--;
                if (countDown > 0) {
                    $sendSMS.text(countDown + 's后重新获取');
                }
                else {
                    $sendSMS.text('获取验证码');
                    $sendSMS.removeClass('countdown');
                    clearInterval(cdTimer);
                }
            }, 1000);
        };

        var checkValidCode = function ($Img,code,callback) {
            $.ajax({
                url: urls.checkCode,
                dataType: 'json',
                type: 'post',
                data: { imageCode: code },
                beforeSend: function () {
                    pending = true;
                },
                success: function (res) {
                    if (res.success) {
                        callback();
                    }
                    else {
                        alert(res.message || '验证码验证失败');
                        $Img.click();
                    }
                },
                error: function () {
                    alert('无法效验验证码，请稍后再试');
                    $Img.click();
                },
                complete: function () {
                    pending = false;
                }
            });
        };
        $sendSMS.on('click', function () {
            if (countDown > 0 || pending) return;
            var mobile = $('#loginName').val(),
                imageCode = $('#imageCode').val();
            if (!(/^[1][3,4,5,7,8][0-9]{9}$/.test(mobile))) {
                alert('请输入正确的手机号码！').delay(3); return;
            }
            if ($.trim(imageCode) === '') {
                alert('请输入验证码！').delay(3); return;
            }
            checkValidCode($validImg,imageCode, function () {
                $.ajax({
                    url: urls.SMS,
                    type: 'post',
                    dataType: 'json',
                    data: {
                        mobile: mobile
                    },
                    beforeSend: function () {
                        pending = true;
                    },
                    success: function (res) {
                        if (res.success) {
                            startCountDown(60);
                        }
                        else {
                            alert(res.message).delay(3);
                            $validImg.click();
                        }
                    },
                    error: function () {
                        alert('发生错误!').delay(3);
                        $validImg.click();
                    },
                    complete: function () {
                        pending = false;
                    }
                });
                $('#btn-login').prop('disabled', false);
            });
        });
        $validImg.on('click', function () {
            $validImg.attr('src', imageCodeUrl + '?rnd=' + (new Date).getTime());
        });
        $validImg2.on('click', function () {
            $validImg2.attr('src', imageCodeUrl2 + '?rnd=' + (new Date).getTime());
        });
        $form.on('submit', function (e) {
            e.preventDefault();
            if (pending) { return false; }

            var mobile = $('#loginName').val(),
                validCode = $('#validcode').val();
            if (!(/^[1][3,4,5,7,8][0-9]{9}$/.test(mobile))) {
                alert('请输入正确的手机号码！').delay(3); return;
            }
            if ($.trim(validCode) === '') {
                alert('请输入动态密码！').delay(3); return;
            }

            $.ajax({
                url: urls.login,
                type: 'post',
                dataType: 'json',
                data: {
                    mobile: mobile,
                    verifyCode: validCode
                },
                beforeSend: function () {
                    pending = true;
                },
                success: function (res) {
                    if (res.success) {
                        //location.href = urls.index;
                        toSelectCompany(mobile, $form);
                    }
                    else {
                        alert(res.message || '发生错误!').delay(3);
                        if (res.model >= 3) {
                            //alert('短信验证码错误超过3次，请重新获取!');
                            $('#btn-login').prop('disabled', true);
                            $validImg.click();
                            $('#imageCode').val('');
                        }
                    }
                },
                error: function () {
                    alert('发生错误!').delay(3);
                },
                complete: function () {
                    pending = false;
                }
            });

            return false;
        });
        $form2.on('submit', function (e) {
            e.preventDefault();
            if (pending) { return false; }

            var mobile = $('#loginName2').val(),
                imageCode = $('#imageCode2').val(),
                validCode = $('#password').val();
            if (!(/^[1][3,4,5,7,8][0-9]{9}$/.test(mobile))) {
                alert('请输入正确的手机号码！').delay(3); return;
            }
            if ($.trim(imageCode) === '') {
                alert('请输入验证码！').delay(3); return;
            }
            if ($.trim(validCode) === '') {
                alert('请输入账号密码！').delay(3); return;
            }
        	$.ajax({
                url: urls.loginbypass,
                type: 'post',
                dataType: 'json',
                data: {
                    mobile: mobile,
                    password: validCode,
                    imageCode: imageCode
                },
                beforeSend: function () {
                    pending = true;
                },
                success: function (res) {
                    if (res.success) {
                        toSelectCompany(mobile, $form2);
                    }
                    else {
                        alert(res.message || '发生错误!').delay(3);
                        $validImg2.click();
                        $('#imageCode2').val('');
                    }
                },
                error: function () {
                    alert('发生错误!').delay(3);
                },
                complete: function () {
                    pending = false;
                }
            });
        });
        $form.show();

        $('.login-tab').on('click', function () {
            var toForm = $(this).data('form');

            $('.form-login').hide();
            $('#form-' + toForm).show();

            $('.login-tab').removeClass('active');
            $(this).addClass('active');
        });
    };

    module.exports = {
        run: run
    };
});
