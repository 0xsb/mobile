<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="./taglibs.jsp"%>
<%@ include file="./vars.jsp"%>
<!doctype html>
<html lang="zh-cmn-Hans">
<head>
<meta charset="utf-8">
<meta name="keywords" content="">
<meta name="description" content="">
<meta name="author" content="hy">
<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
<title>
<c:choose>
<c:when test="${empty sessionScope.wyyName}">移动审批</c:when>
<c:otherwise>${sessionScope.wyyName}</c:otherwise>
</c:choose>
</title>
<link rel="shortcut icon" type="image/ico" href="${contextPath}/favicon.ico"/>
<%@ include file="./config.jsp"%>
<script src="${contextPath}/assets/dep/seajs/3.0.0/sea.js"></script>
<script src="${contextPath}/assets/dep/seajs-text/seajs-text.js"></script>
<script src="${contextPath}/assets/src/config.js"></script>
<!--<script src="${contextPath}/assets/dep/echarts/echarts.common.min.js"></script>-->
<!--<link rel="stylesheet" href="${contextPath}/assets/dep/calendar/calendar.css" />-->
<!--<link rel="stylesheet" href="${contextPath}/assets/dep/jquery-mobile/jquery.mobile-1.4.5.min.css" />-->
<link rel="stylesheet" href="${contextPath}/assets/dep/jquery-mobiscroll/mobiscroll.custom-2.5.0.min.css" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/clearJQMobile.css?v=100018" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/mobile.css?v=100018" />
