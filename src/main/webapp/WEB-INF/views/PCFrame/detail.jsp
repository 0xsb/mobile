<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/taglibs.jsp"%>
<%@ include file="../common/vars.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<link rel="shortcut icon" type="image/x-icon" href="${contextPath}/favicon.ico">
<script type="text/javascript">
String.prototype.html = function(encode) {
    var replace = ["&#39;", "'", "&quot;", '"', "&nbsp;", " ", "&gt;", ">", "&lt;", "<", "&amp;", "&", "&yen;", "¥"];
    if (encode) {
        replace.reverse();
    }
    for (var i = 0, str = this; i < replace.length; i += 2) {
        str = str.replace(new RegExp(replace[i], 'g'), replace[i + 1]);
    }
    return str;
};
</script>
<title><c:out value="${message.title}"/></title>
<link rel="stylesheet" type="text/css" href="${contextPath}/assets/src/css/page-article.css">
<!--[if lt IE 9]><link rel="stylesheet" type="text/css" href="${contextPath}/assets/src/css/page-article-ie.css"><![endif]-->
</head>
<body class="zh_CN mm_appmsg" ontouchstart="" style="background-color: #fbfbfd">
<c:choose>
    <c:when test="${!empty error}">
    	<div class="rich_media" align="center" style="margin-top: 80px">
    		<span style="font-family: serif;">${error }</span>
    	</div>
    </c:when>
    <c:otherwise>
	    <div class="rich_media">
	        <div class="rich_media_inner">
	            <div id="js_content" id="page-content">
	                <div class="rich_media_area_primary">
	                    <h2 class="rich_media_title" id="activity-name">
	                        <c:out value="${message.title}"/>
	                    </h2>
	                    <div class="rich_media_meta_list">
	                        <span class="rich_media_meta rich_media_meta_text rich_media_meta_nickname">创建：<c:out value="${message.userName}"/></span><br/>
	                        <em id="post-date" class="rich_media_meta rich_media_meta_text">发布：${time}</em>
	                        <em id="post-date" class="rich_media_meta rich_media_meta_text">阅读：${message.readCount }</em>
	                    </div>
	                    <div class="rich_media_thumb_wrp" id="media">
	                        <script>
	                        (function() {
	                            var cover = "<c:out value="${message.picurl}"/>";
	                            document.write('<div style="text-align:center;"><img class="rich_media_thumb" style="max-width:100%" id="js_cover" src="' + cover + '" onerror="this.parentNode.removeChild(this)" data-backsrc=' + cover + '  data-src="' + cover + '" /></div>');
	                        })();
	                        </script>
	                    </div>
	                    <div class="rich_media_title"></div>
	                    <div class="rich_media_title">
	                    	<span class="rich_media_meta rich_media_meta_text rich_media_meta_nickname">摘要：<c:out value="${message.detail}"/></span>
	                    </div>
	                    
	                    <div class="rich_media_content " id="js_content">
	                        <c:out value="${message.content}" escapeXml="false"/>
	                    </div>
	                    <script type="text/javascript">
	                    if ("" == 1 && document.getElementById('js_content')) {
	                        document.getElementById('js_content').addEventListener("selectstart", function(e) {
	                            e.preventDefault();
	                        });
	                    }
	                    </script>
	                </div>
	            </div>
	        </div>
	    </div>
    </c:otherwise>
</c:choose>
</body>
</html>