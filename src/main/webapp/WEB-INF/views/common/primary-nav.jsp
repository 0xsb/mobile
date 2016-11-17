<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div class="navbar-default navbar-static-side" role="navigation">
 
    <ul class="primary-nav metismenu">
        <shiro:hasPermission name="company">
        <li>
            <a href="${contextPath}/web/company/list.htm"><i class="fa fa-cubes"></i> <span class="nav-label">企业管理</span></a>
        </li>
        </shiro:hasPermission>
        <shiro:hasPermission name="admin">
        <li>
            <a href="javascript:void(0);"><i class="fa fa-user-secret"></i> <span class="nav-label">管理员管理</span> <span class="fa arrow"></span></a>
            <ul class="nav-second-level collapse">
            	<shiro:hasAnyRoles name="admin,customerManager">
                <li><a href="${contextPath}/web/system/companyAdmin.htm">企业管理员</a></li>
                </shiro:hasAnyRoles>
                <shiro:hasAnyRoles name="admin,areaManager">
                <li><a href="${contextPath}/web/system/areaAdmin.htm">区域管理员</a></li>
                </shiro:hasAnyRoles>
                <shiro:hasAnyRoles name="admin,customerManager">
                <li><a href="${contextPath}/web/system/customManager.htm">客户经理</a></li>
                </shiro:hasAnyRoles>
            </ul>
        </li>
        </shiro:hasPermission>
        <shiro:hasPermission name="role">
        <li>
            <a href="${contextPath}/web/authority/role.htm"><i class="fa fa-users"></i> <span class="nav-label">角色管理</span></a>
        </li>
        </shiro:hasPermission>
      <shiro:hasPermission name="staff">
        <li>
            <a href="javascript:void(0);"><i class="fa fa-users"></i> <span class="nav-label">员工管理</span> <span class="fa arrow"></span></a>
            <ul class="nav-second-level collapse">
                <li>
                    <a href="${contextPath}/web/system/contacts.htm">通讯录</a>
                </li>
                <li>
                    <a href="${contextPath}/web/system/pendingUser.htm">待审核</a>
                </li>
            </ul>
        </li>
          </shiro:hasPermission>
          
          
                <shiro:hasPermission name="companyInfo">
        <li>
            <a href="${contextPath}/web/system/companyInfo.htm"><i class="fa fa-wrench"></i> <span class="nav-label">企业信息</span></a>
        </li>
        </shiro:hasPermission>
        
        
              <shiro:hasPermission name="jobManagement">
        <li>
            <a href="${contextPath}/web/company/position.htm"><i class="fa fa-map-marker"></i> <span class="nav-label">职位管理</span></a>
        </li>
      </shiro:hasPermission>
      
        <li>
            <a href="javascript:void(0);"><i class="fa fa-comments"></i> <span class="nav-label">公告管理</span> <span class="fa arrow"></span></a>
            <ul class="nav-second-level collapse">
                <li>
                    <a href="${contextPath}/web/notice.htm">公告管理</a>
                </li>
                <li>
                    <a href="${contextPath}/web/system/news.htm">滚动消息</a>
                </li>
                <%--<li>
                     <a href="${contextPath}/web/pendingNotice.htm">待审核</a> 
                </li>--%>
            </ul>
        </li>
        
        <shiro:hasPermission name="app">
        <li>
            <a href="javascript:void(0);"><i class="fa fa-cubes"></i> <span class="nav-label">应用管理</span> <span class="fa arrow"></span></a>
            <ul class="nav-second-level collapse">
                <li><a href="${contextPath}/web/apply/internal.htm">内部应用</a></li>
                <shiro:hasAnyRoles name="companyManager">
                <c:if test="${! sessionScope.isSuper}">
                <li><a href="${contextPath}/web/apply/external.htm">外部应用</a></li>
                </c:if>
                </shiro:hasAnyRoles>
            </ul>
        </li>
        </shiro:hasPermission>
        <shiro:hasPermission name="report">
        <li>
            <a href="javascript:void(0);"><i class="fa fa-bar-chart"></i> <span class="nav-label">统计管理</span> <span class="fa arrow"></span></a>
            <ul class="nav-second-level collapse">
                <li><a href="${contextPath}/web/system/report.htm?view=realtime">实时分析</a></li>
                <li><a href="${contextPath}/web/system/report.htm?view=trend">趋势分析</a></li>
                <li><a href="${contextPath}/web/system/report.htm?view=activeUser">活跃用户</a></li>
                <li><a href="${contextPath}/web/system/report.htm?view=keepUser">留存用户</a></li>
                <li><a href="${contextPath}/web/system/report.htm?view=payAnaly">付费用户</a></li>
                <li><a href="${contextPath}/web/system/report.htm?view=area">地域分布</a></li>
                <li><a href="${contextPath}/web/system/report.htm?view=terminalDi">终端分布</a></li>
            </ul>
        </li>
        </shiro:hasPermission>
        <shiro:hasPermission name="upgrade">
        <li>
            <a href="javascript:void(0);"><i class="fa fa-cubes"></i> <span class="nav-label">版本升级</span> <span class="fa arrow"></span></a>
            <ul class="nav-second-level collapse">
                <li><a href="${contextPath}/web/system/version.htm">版本升级</a></li>
            </ul>
        </li>
         </shiro:hasPermission>
        <shiro:hasPermission name="area">
        <li>
            <a href="${contextPath}/web/system/area.htm"><i class="fa fa-map-marker"></i> <span class="nav-label">区域管理</span></a>
        </li>
        </shiro:hasPermission>
    </ul>
</div>