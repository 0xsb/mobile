<%@ page language="java" pageEncoding="utf-8"%>
<nav class="navbar navbar-default navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="${contextPath}/microApp/list.do">
                <span class="navbar-logo">V</span><span class="navbar-head"><c:out value="${sessionScope.companyName}"/>管理后台</span>
            </a>
        </div>
        <ul class="nav navbar-nav navbar-right">
            <li>
                <a href="${contextPath}/user/manager.do">人员管理</a>
            </li>
            <li>
                <a href="${contextPath}/microApp/list.do">微应用</a>
            </li>
            <li>
                <a href="${contextPath}/users/passwordManager.do">设置</a>
            </li>
            <li>
                <a href="${contextPath}/exit.do">退出</a>
            </li>
        </ul>
    </div>
</nav>
