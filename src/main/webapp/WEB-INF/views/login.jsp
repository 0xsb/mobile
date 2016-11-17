<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="./common/head-public.jsp"%>
<%@ include file="./common/ie.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/src/css/login.css" />
</head>
<body>
<!-- header -->
<%@ include file="./common/header-login.jsp"%>
<!-- end header -->
<form id="form-login" class="form-login" method="post" action="${contextPath}/checkPass.do" style="display: none;">
    <h2 class="form-login-heading">管理登录</h2>
    <div class="alert-login alert alert-danger hide" role="alert"></div>
    <label for="inputUsername" class="sr-only">手机号</label>
    <input type="text" id="inputUsername" name="username" class="form-control" placeholder="手机号" autofocus>
    <label for="inputPassword" class="sr-only">密码</label>
    <input type="password" id="inputPassword" name="password" class="form-control" placeholder="密码">
    <button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
</form>
<form id="form-company" class="form-login" method="post" action="${contextPath}/checkLogin.do" style="display: none;">
    <h2 class="form-login-heading">管理登录</h2>
    <div class="alert-company alert alert-danger hide" role="alert"></div>
    <label for="inputPassword" class="sr-only">公司组织</label>
    <select id="company" name="company" class="form-control"></select>
    <button class="btn btn-lg btn-primary btn-block" type="submit">确定</button>
</form>
<script>seajs.use('page/login', function(page){ page.run(); });</script>
</body>
</html>
