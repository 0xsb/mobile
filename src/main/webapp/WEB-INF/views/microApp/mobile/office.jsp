<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../../common/head-mobile.jsp"%>
<style>
.office-t{
	height:40px;
	line-height:40px;
	color:#fff;
	background-color:#007FC6;
	text-indent:10px;
}
.grid-panel2 {
  background-color: white;
}
.grid-panel2 > a {
  float: left;
  display: block;
  width: 25%;
  height: 6.75rem;
  /* height: 33.33vw; */
  padding: 0.75rem;
  border-style: solid;
  border-color: #ddd;
  border-width: 0 1px 1px 0;
}
.grid-panel2 > a span {
  line-height: 1.5rem;
  display: block;
  width: 100%;
  text-align: center;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  color: #999999;
}
.grid-panel2 > a img {
  height: 3rem;
  width: 3rem;
  margin: .5rem auto;
  display: block;
}
</style>
</head>
<body>
<div class="office-t">公司公文</div>
<div id="grid-panel-1" class="grid-panel2 clearfix">
	<a href="/mobile/moblicApprove/toPcForm.do?flowId=ACT_0e059a96-9412-4985-9484-cd79a6ba819c&wyyId=wyy0001">
	<img src="/mobile/assets/src/css/img/formicons/icon_lxsq.png" alt="公文发文">
	<span>公司发文</span>
	</a>
	<a class="add" href="javascript:;">
	<img src="/mobile/assets/src/css/img/mobile/h_add.png" alt="更多">
	<span>更多</span>
	</a>
</div>
<div class="office-t">移动审批</div>
<div id="grid-panel-2" class="grid-panel2 clearfix"></div>
<script type="text/html" id="tmpl-approvals">
    <# _.each(list, function (item) { #>
        <# if (item.scene == 3) { #>
            <a href="${contextPath}/moblicApprove/toForm-e3.do?flowId={{item.id}}&from=index&wyyId={{wyyId}}">
        <# } else { #>
            <a href="${contextPath}/moblicApprove/toForm.do?typeId={{item.id}}&from=index&wyyId={{wyyId}}">
        <# } #>
            <img src="${contextPath}/assets/src/css/img/formicons/{{item.icon}}.png" alt="{{item.name}}">
            <span>{{item.name}}</span>
        </a>
    <# }) #>
    <a class="add" href="${contextPath}/moblicApprove/toApprovalList.do?wyyId={{wyyId}}">
        <img src="${contextPath}/assets/src/css/img/mobile/h_add.png" alt="更多">
        <span>更多</span>
    </a>
</script>

<script>
    seajs.use('page/micro-app/mobile/office', function (module) {
        module.run();
    });
</script>
</body>
</html>
