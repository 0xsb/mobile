<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-pc.jsp"%>
<link rel="stylesheet" href="${contextPath}/assets/src/css/pcdefault.css" />
<link rel="stylesheet" href="${contextPath}/assets/src/css/newssplit.css" />
</head>
<body>
<div class="main">
    <div class="header">
        <div class="vol-avg4 -tr">
            <a href="javasript:">首页</a>
     <a href="javascript:void(0);"data-href="/mobile/moblicApprove/setting.do?t=2" target="_blank" data-target="popwin">
            设置</a>
            <a href="${contextPath}/user/quit.do">退出</a>
        </div>
    </div>
    <div class="company">
        <span class="logo -tl">
            <img src="/mobile/assets/src/css/img/logo-16.png" /></span>
        <span class="com-name">如东县农业委员会MOA信息平台</span>
    </div>
    <div class="content-block">
        <div class="content-l">
            <div class="tq-box">
                <div class="tianqi">
                    <iframe allowtransparency="true" frameborder="0" width="140" height="109" scrolling="no" src="//tianqi.2345.com/plugin/widget/index.htm?s=2&z=2&t=1&v=1&d=1&bd=0&k=000000&f=ffffff&q=0&e=1&a=1&c=54511&w=140&h=109&align=center"></iframe>
                </div>
            </div>
            <a href="javascript:void(0);" data-href="http://x.eqxiu.com/s/W4PBxzLu?eqrcode=1&from=timeline&isappinstalled=0" data-target="popwin"><img class="adver2" src="/mobile/assets/src/css/img/adv-1.jpg" /></a>
            <a href="javascript:void(0);" data-href="http://xc.workapps.cn/appcms/index" data-target="popwin"><img class="adver2" src="http://xc.workapps.cn/appcms/static/img/index/img2.png" /></a>
            <img class="adver2" src="/mobile/assets/src/css/img/adv3.png" />
            <div class="gray-box">
                <div class="box-t">
                    <span class="icon icon-cylj"></span>
                    <span class="cylj">常用链接</span>
                    <a href="javascript:void(0);" data-href="/mobile/moblicApprove/setting.do?t=4" target="_blank" data-target="popwin"><img class="setUp" src="/mobile/assets/src/css/img/setUp.jpg"/></a>
                </div>
                <div class="links" id="links">
                    <ul class="tabsLeft"> </ul>
                    <div class="tab_container"> </div>
                    <!-- <a href="http://www.sina.com.cn" target="_blank">新浪</a>
                    <span class="dot"></span>
                    <a href="http://weibo.com/" target="_blank">微博</a> -->
                </div>
                <!-- <span class="add">+ 添加链接</span> -->
            </div>
        </div>
        <div class="content-r">
            <div class="handy">
                <a href="javascript:void(0);" data-href="/mobile/moblicApprove/toOffice.do" target="_blank" data-target="popwin" class="i-handy">
                    <img class="adver" src="/mobile/assets/src/css/img/oaicons/image023.png" />
                    <span class="handy-txt">公文起草</span>
                </a>
                <a href="javascript:void(0);" data-href="/mobile/moblicApprove/toTasks.do" target="_blank" data-target="popwin" class="i-handy">
                    <img class="adver" src="/mobile/assets/src/css/img/oaicons/image053.png" />
                    <span class="handy-txt">任务督办</span>
                </a>
                <a href="javascript:void(0);" data-href="/mobile/moblicApprove/toDailys.do" target="_blank" data-target="popwin" class="i-handy">
                    <img class="adver" src="/mobile/assets/src/css/img/oaicons/image021.png" />
                    <span class="handy-txt">日志日报</span>
                </a>
                <a id="myemail" href="http://mail.10086.cn/" target="_blank" class="i-handy">
                    <img class="adver" src="/mobile/assets/src/css/img/oaicons/image001.png" />
                    <span class="handy-txt">电子邮件</span>
                </a>
                <a href="javascript:void(0);" data-href="/mobile/moblicApprove/addressList.do" target="_blank" data-target="popwin" class="i-handy">
                    <img class="adver" src="/mobile/assets/src/css/img/oaicons/image003.png" />
                    <span class="handy-txt">通讯录</span>
                </a>
                <a href="javascript:void(0);" data-href="/mobile/moblicApprove/noticeList.do?t=1" target="_blank" data-target="popwin" class="i-handy">
                    <img class="adver" src="/mobile/assets/src/css/img/oaicons/image007.png" />
                    <span class="handy-txt">公告管理</span>
                </a>
                <a href="javascript:void(0);" data-href="/mobile/moblicApprove/noticeList.do?t=2" target="_blank" data-target="popwin" class="i-handy">
                    <img class="adver" src="/mobile/assets/src/css/img/oaicons/image029.png" />
                    <span class="handy-txt">新闻管理</span>
                </a>
                <!--<a href="#" class="handy-set">设置</a>
                <span class="goarrow gleft">《</span>
                <span class="goarrow gright">》</span>-->
            </div>
            <div class="main-indent">
                <div class="gray-box lm">
                    <div class="blue-t">
                        <a id="news" class="tab tab1" href="javascript:void(0);">新闻</a>
                        <a id="gonggao" class="tab tab1" href="javascript:void(0);">公告</a>
                        <a class="more" href="javascript:void(0);" data-href="/mobile/moblicApprove/newsList.do?t=0" target="_blank" data-target="popwin">更多</a>
                        <a class="more morenews" href="javascript:void(0);">刷新</a>
                    </div>
                    <div class="content-list">
                        <div class="vol-half -tl">
                            <ul id="news-1" class="news-list">
                                <!-- <a href="#">应用号终于来了 它到底长什么样</a>
                                <a href="#">辽宁“最美野长城”被抹平？官方：确实不好看</a> --></ul>
                            <ul id="gonggao-0" class="news-list" style="display:none;">
                                <!-- <a href="#">应用号终于来了 它到底长什么样</a>
                                <a href="#">辽宁“最美野长城”被抹平？官方：确实不好看</a> --></ul>
                        </div>
                        <div class="vol-half -tr">
                            <ul id="news-0" class="split-list">
                                <div id="wrapper">
                                    <!-- 最外层部分 -->
                                    <div id="banner">
                                        <!-- 轮播部分 -->
                                        <ul class="imgList">
                                            <!-- 图片部分 -->
                                        </ul>
                                        <img src="/mobile/assets/src/css/img/lunbo/prev.png" width="20px" height="40px" id="prev">
                                        <img src="/mobile/assets/src/css/img/lunbo/next.png" width="20px" height="40px" id="next">
                                        <div class="bg"></div>
                                        <!-- 图片底部背景层部分-->
                                        <ul class="infoList">
                                            <!-- 图片左下角文字信息部分 -->
                                        </ul>
                                        <ul class="indexList">
                                            <!-- 图片右下角序号部分 -->
                                        </ul>
                                    </div>
                                </div>
                            </ul>
                            <ul id="gonggao-1" class="news-list" style="display:none;">
                                <!--<a href="#">应用号终于来了 它到底长什么样</a>
                                <a href="#">辽宁“最美野长城”被抹平？官方：确实不好看</a>--></ul>
                        </div>
                    </div>
                </div>
                <div id="my-approval" class="gray-box lm">
                    <div class="tabs blue-t">
                        <a id="dwsp" class="tab tab2 active" href="javascript:void(0);">我的待办
                            <span id="info-count" class="info-count"></span>
                        </a>
                        <a id="wysp" class="tab tab2" href="javascript:void(0);">我的已办</a>
                        <a id="wdfq" class="tab tab2" href="javascript:void(0);">我的发起</a>
                        <a id="dwyl" class="tab tab2" href="javascript:void(0);">我的待阅</a>
                        <a class="more" href="javascript:void(0);"data-href="/mobile/moblicApprove/flowList.do?t=0" target="_blank" data-target="popwin">更多</a>
                        <a class="more" href="javascript:void(0);" data-do="refresh">刷新</a>
                    </div>
                    <div class="content-list">
                        <ul class="task-list"></ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="content-bot">CopyRight@江苏移动</div>
    </div>
</div>
<script type="text/template" id="tmpl-linkItem">
<a href="{{model.link}}" target="_blank">{{model.name}}</a>
</script>
<script type="text/template" id="tmpl-approvalItem">
<a data-href="{{model.url}}" target="_blank" data-target="popwin"><span class="icon-item -tl"></span><span class="icon-date -tl">&nbsp;&nbsp;[{{model.date}}]</span><span class="icon-name -tl">{{model.userName}}</span>{{model.approvalName}}<span class="-tr">审批类&nbsp;&nbsp;审批{{model.status}}</span></a>
</script>
<script>
seajs.use('page/micro-app/mobile/pc-default', function (module) {
    module.run();
});
</script>
</body>
</html>
