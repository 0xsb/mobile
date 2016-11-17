<%@ page language="java" pageEncoding="utf-8"%>
<%--
配置项
1、系统配置，由后台输出至页面
2、seajs 配置
--%>
<script id="config">
var COMPANY_ID = '<c:out value="${sessionScope.companyId}"/>';
var COMPANY_NAME = '<c:out value="${sessionScope.companyName}"/>';
var USER_ID = '<c:out value="${sessionScope.userId}"/>';
var MOBILEPHONE = '<c:out value="${sessionScope.mobile}"/>';

var USER_NAME = '<c:out value="${sessionScope.userName}"/>';
var DEPT_NAME = '<c:out value="${sessionScope.orgName}"/>';
var WYY_ID = '<c:out value="${sessionScope.wyyId}"/>';

var CONTEXT_PATH = '<c:out value="${contextPath}"/>';
var SECRET_KEY = 0;
var SEA_CONFIG = {
    debug: true,
    base: CONTEXT_PATH + '/assets',
    map: [
    	[ /^(.*\.(?:css|js))(.*)$/i, '$1?' + 'v=100019' ]
    ],
    paths: {
        'page': 'src/page',
        'component': 'src/component',
        'util': 'src/component/util'
    },
    alias: {
        'jquery': 'dep/jquery/1.11.2/jquery.js',
        'jqueryui': 'dep/jquery-ui/jquery-ui.min.js',
        'bootstrap': 'dep/bootstrap/3.3.6/js/bootstrap-cmd',
        'underscore': 'dep/underscore/1.8.3/cmd/underscore-min',
        'backbone': 'dep/backbone/1.3.3/backbone',
        'zepto': 'dep/zepto/zepto.min.js',

        'video': 'dep/video/5.8.0/video.min',
        'unslider': 'dep/unslider/2.0/dist/js/unslider-min',
        'underscore': 'dep/underscore/1.8.3/underscore',

        'plupload': 'dep/plupload/2.1.3/js/plupload.full.min.js',
        'jquery-slimScroll': 'dep/jquery-slimscroll/1.3.7/jquery.slimscroll',
        'jquery-placeholder': 'dep/jquery-placeholder/2.1.2/jquery.placeholder.min',

        'jquery-mobile': 'dep/jquery-mobile/jquery.mobile.min',
        'jquery-mobiscroll': 'dep/jquery-mobiscroll/mobiscroll',

        'calendar' : 'dep/calendar/calendar',
        'jquery-validate': 'dep/jquery-validation/1.15.0/jquery.validate.min.js',
        'jquery-validate-additional' : 'dep/jquery-validation/1.15.0/additional-methods.min',
        'calendar': 'dep/calendar/calendar',
        'bootstrap-datetimepicker': 'dep/bootstrap-datetimepicker/js/bootstrap-datetimepicker',
        'bootstrap-datetimepicker-zh-CN': 'dep/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN',
        'bootstrap-select': 'dep/bootstrap-select/js/bootstrap-select',
        'bootstrap-select-zh-CN': 'dep/bootstrap-select/js/i18n/defaults-zh_CN',

        'wangEditor': 'dep/wang-editor/js/wangEditor.min',
        'ztree': 'dep/zTree/js/jquery.ztree.core',
        'ztree.exedit': 'dep/zTree/js/jquery.ztree.exedit',
        'ztree-excheck' : 'dep/zTree/js/jquery.ztree.excheck',
        'umeditor' : 'dep/umeditor/umeditor',
        'umeditor-config' : 'dep/umeditor/umeditor.config.js',
        'umeditor-lang' : 'dep/umeditor/lang/zh-cn/zh-cn',
        'jquery-form' : 'dep/jquery-form/jquery.form.js',
        'metisMenu' : 'dep/metisMenu/metisMenu.min',

        'md5': 'util/md5',
        'cookie': 'util/cookie',
        'parseQueryString': 'util/parseQueryString',
        'template': 'util/template',
        'walkList': 'util/walkList',
        'jquery-util': 'util/jquery-util',
        'csrf': 'util/csrf',
        'json2' : 'dep/shim/json2'
    }
};
</script>
