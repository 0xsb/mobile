<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/dep/bootstrap/3.3.6/css/bootstrap.css"/>
<link rel="stylesheet" href="${contextPath}/assets/src/css/pclogin.css" />
</head>
<body>
    <div id="compatibility">
        <div class="compatibility-logo">
            <img src="${contextPath}/assets/src/css/img/logo-login.png" alt="">
        </div>
        <div class="compatibility-text">
            移动审批PC版不支持你当前使用的浏览器<br>
            可能会导致部分图片和信息的缺失。<br>
            请下载使用最新版<a
            href="http://www.google.com/intl/zh-CN/chrome/" class="kie-setup-taoBrowser" target="_blank"
            title="谷歌浏览器">谷歌浏览器</a>或<a href="http://se.360.cn/" class="kie-setup-taoBrowser"
            target="_blank" title="360浏览器">360浏览器</a>
        </div>
    </div>

    <div id="login-box">
        <div class="login-heading"></div>
        <div class="login-tabs">
            <span class="tabcode login-tab active" data-form="login">动态码登录</span>
            <span class="tabpass login-tab" data-form="pass">密码登录</span>
        </div>
        <form id="form-login" class="form-login" method="post" style="display: none;">
            <div class="form-login-content">
                <label for="loginName">账号</label>
                <div class="control-box">
                    <input type="text" id="loginName" name="loginName" class="form-control user" placeholder="请输入手机号码" autofocus autocomplete="off">
                </div>
                <div class="control-box">
                    <input type="text" id="imageCode" name="imageCode" class="form-control validcode" placeholder="请输入验证码" autocomplete="off">
                    <img class="valid-img" src="${contextPath}/user/imageCheck.do" alt="">
                </div>
                <label for="validcode">动态码</label>
                <div class="control-box">
                    <input type="text" id="validcode" name="validcode" class="form-control validcode" placeholder="请输入动态密码" autocomplete="off">
                    <a href="javascript:" class="btn btn-sendSMS">获取动态码</a>
                </div>
                <button id="btn-login" class="btn btn-login" disabled type="submit">登录</button>
            </div>
        </form>
        <form id="form-pass" class="form-login" method="post" style="display: none;">
            <div class="form-login-content">
                <label for="loginName2">账号</label>
                <div class="control-box">
                    <input type="text" id="loginName2" name="loginName2" class="form-control user" value="13905181076" placeholder="请输入手机号码" autofocus autocomplete="off">
                </div>
                <label for="password">密码</label>
                <div class="control-box">
                    <input type="password" id="password" name="password" class="form-control validcode" value="111111" placeholder="请输入账号密码" autocomplete="off">
                </div>
                <div class="control-box">
                    <input type="text" id="imageCode2" name="imageCode2" class="form-control validcode" placeholder="请输入验证码" autocomplete="off">
                    <img class="valid-img2" src="${contextPath}/OA/imageCheck.do" alt="">
                </div>
                <button id="btn-login2" class="btn btn-login" type="submit">登录</button>
            </div>
        </form>
        <form id="form-company" class="form-login" method="post" style="display: none;">
            <div class="form-login-content">
                <label for="loginName">公司组织</label>
                <div class="control-box">
                    <select id="company-select" name="userId" class="form-control">
                    </select>
                </div>
                <button class="btn btn-login" type="submit">确定</button>
            </div>
        </form>
    </div>

    <script>
        var ua = navigator.userAgent.toLowerCase();
        if(!(/webkit/i.test(ua))) {
            document.getElementById('compatibility').style.display = 'block';
            document.getElementById('form-login').style.display = 'none';
        }
        else {
            seajs.use('page/pcframe/login', function(page) {
                page.run();
            });
        }
    </script>
</body>
</html>
