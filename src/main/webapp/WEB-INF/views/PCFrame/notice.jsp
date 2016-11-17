<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="../common/head-public.jsp"%>
<%@ include file="../common/ie.jsp"%>
<%@ include file="../common/config.jsp"%>

<link rel="stylesheet" href="/mobile/assets/dep/zTree/css/zTreeStyle/zTreeStyle.css"/>
<link href="/mobile/assets/dep/umeditor/themes/default/css/umeditor.min.css" type="text/css" rel="stylesheet">
    <style>
        .org {
            border: 1px solid #cccccc;
        }

        .dept-tree {
            overflow: auto;
            height: 300px;
        }
        .form-control:focus {
            box-shadow: none;
        }
		.deleteFile{
			cursor:pointer;
		}
    </style>
</head>
<body>
<div id="wrapper">
    <!-- page content -->
    <div id="page-wrapper" class="page-wrapper">
        <!-- screen title -->
        <c:set var="screenTitle" value="公告管理"/>
        <div class="page-heading">
            <h2>
                <span class="type">公告</span>管理
            </h2>
        </div>
        <!-- end screen title -->
        <!-- page main -->
        <div class="wrapper wrapper-content">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="panel-title">历史<span class="type">公告</span>
                    </div>
                </div>
                <div class="panel-body">
                    <form id="search" class="form-filter form-inline">
                        <div class="form-group">
                            消息类型：
                            <select id="ntype" class="form-control" name="type">
                                <!--<option value="">全部</option>
                                <option value="1">通知公告</option>
                                <option value="2">图片新闻</option>
                                <option value="3">文字新闻</option>-->
                            </select>
                        </div>
                        <div class="form-group">
                            <input type="text" class="form-control" name="title" placeholder="标题或内容">
                        </div>
                        <button type="submit" class="btn btn-default">搜索</button>
                        <button type="reset" class="btn btn-default" data-do="reset">重置搜索条件</button>
                    </form>
                    <hr/>
                    <div class="btn-group" role="group">
                        <a href="javascript:void(0);" class="btn btn-default" data-do="create:notice">
                            <span class="fa fa-plus" aria-hidden="true"></span>
                            新建<span class="type">公告</span>
                        </a>
                    </div>
                    <br><br>
                    <table id="datatable" class="datatable table table-bordered table-hover">
                        <thead>
                            <tr role="col-headers">
                                <th>序号</th>
                                <th>标题</th>
                                <th>消息类型</th>
                                <!--                                 <th>内容摘要</th> -->
                                <th>发送时间</th>
                                <th>发送状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody role="items">
                            <script type="text/html" id="tmpl-item">
                                <td width="50">{{model.index}}</td>
                                <td width="200">{{model.title}}</td>
                                <td width="80">
                                    <# if(model.typeId == 1){ #>通知公告<# }else if(model.typeId ==2 ){ #>
                                        图片新闻
                                    <# }else if(model.typeId == 3){ #>
                                        文字新闻
                                    <# } #>
                                </td>
                                <!--<td>&nbsp;</td>-->
                                <td width="160">{{model.createTime}}</td>
                                <td width="60">{{model.statusText}}</td>
                                <td class="col-actions" width="200">
                                    <a href="javascript:void(0);" data-do="edit">编辑</a>
                                    <a href="${contextPath}/moblicApprove/newsDetail.do?id={{model.id}}">详情</a>
                                    <# if(model.sort == 0) { #>
                                        <a href="javascript:void(0);" data-do="stick">取消置顶</a>
                                    <# } else { #>
                                        <a href="javascript:void(0);" data-do="stick">置顶</a>
                                    <# } #>
                                    <a href="javascript:void(0);" data-do="delete">删除</a>
                                </td>
                            </script>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- end page main -->
    </div>
    <!-- end page content -->
</div>

<script type="text/template" id="tmpl-noticeCreateModal">
    <div class="modal-dialog" style="min-width:960px;width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">新建<span class="type"></span>
                </h4>
            </div>
            <div class="modal-body">
                <form method="post" id="createform" enctype="multipart/form-data">
                    <div class="row">
                        <div class="col-sm-11 col-md-9">
                            <div class="form-group">
                                <label for="inputTypr" class="control-label">消息类型<i class="required">*</i>
                                </label>
                                <select class="form-control" name="typeId">
                                    <# if (model.ntype == '2') { #>
                                        <option value="2">图片新闻</option>
                                        <option value="3">文字新闻</option>
                                    <# } else { #>
                                        <option value="1">通知公告</option>
                                    <# } #>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="inputTitle" class="control-label">标题<i class="required">*</i>
                                </label>
                                <input type="text" class="form-control" id="inputTitle" name="title" placeholder="公告标题">
                            </div>
                            <div class="form-group">
                                <label for="inputDetail" class="control-label">摘要<i class="required">*</i>
                                </label>
                                <textarea class="form-control" id="inputDetail" name="detail" placeholder="公告摘要"></textarea>
                            </div>
                            <div class="form-group">
                                <label for="inputContent" class="control-label">正文<i class="required">*</i>
                                </label>
                                <textarea id="editor" name="content" type="text/plain"></textarea>
                            </div>
                            <div class="form-group">
                                <label class="control-label">是否发送短信</label>
                                <div class="checkbox">
                                    <label class="checkbox-inline">
                                        <input type="checkbox" name="isSend" value="1">发送
                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="inputPicfile" class="control-label">封面
                                    <# if (model.ntype != '1') { #>
                                        <i class="required">*</i>
                                    <# } #>
                                </label>
                                <input type="file" class="form-control" id="inputPicfile" name="picfile" placeholder="封面">
                            </div>
                            <div class="form-group">
                                <div id="uploadinfo"></div>
                                <br>
                                <a href="javascript:;" id="filebegin" class="control-label">上传附件</a>
                                <input id="filePath" name="filePath" type="hidden" value="">
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-primary" data-do="submit" id="save_add_btn">保存</button>
                            </div>
                        </div>
                    </div>
                </form>
                <div class="col-sm-4 col-md-3" style="display:none;">
                    <input type="file" class="form-control" id="inputDocfile" name="docfile" placeholder="附件">
                </div>
            </div>
        </div>
    </div>
</script>

<script type="text/template" id="tmpl-noticeEditModal">
    <div class="modal-dialog" style="min-width:960px;width:90%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">编辑<span class="type"></span>
                </h4>
            </div>
            <div class="modal-body">
                <form method="post" enctype="multipart/form-data">
                    <div class="row">
                        <div class="col-sm-11 col-md-9">
                            <input type="hidden" name="id" value="{{model.id}}">
                            <div class="form-group">
                                <label for="inputTypr" class="control-label">消息类型<i class="required">*</i>
                                </label>
                                <select class="form-control" name="type">
                                    <# if (model.ntype == '2') { #>
                                        <option value="2" {{model.typeId == '2' ? 'selected' : ''}}>图片新闻</option>
                                        <option value="3" {{model.typeId == '3' ? 'selected' : ''}}>文字新闻</option>
                                    <# } else { #>
                                        <option value="1" {{model.typeId == '1' ? 'selected' : ''}}>通知公告</option>
                                    <# } #>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="inputTitle" class="control-label">标题<i class="required">*</i>
                                </label>
                                <input type="text" class="form-control" id="inputTitle" name="title" value="{{model.title}}" placeholder="公告标题">
                            </div>
                            <div class="form-group">
                                <label for="inputDetail" class="control-label">摘要<i class="required">*</i>
                                </label>
                                <textarea class="form-control" id="inputDetail" name="detail" placeholder="公告摘要">{{model.detail}}</textarea>
                            </div>
                            <div class="form-group">
                                <label for="inputContent" class="control-label">正文<i class="required">*</i>
                                </label>
                                <textarea id="editor" name="content" type="text/plain">{{model.content}}</textarea>
                            </div>
                            <div class="form-group">
                                <label for="inputPicfile" class="control-label">封面
                                    <# if (model.ntype != '1') { #>
                                        <i class="required">*</i>
                                    <# } #>
                                </label>
                                <div><img src="{{model.picurl}}" onerror="this.parentNode.removeChild(this);" width="100" height="100"/></div>
                                <input type="file" class="form-control" id="inputPicfile" name="picfile" placeholder="封面" data-value="{{model.picurl}}">
                            </div>
                            <div class="form-group">
                                <div id="uploadinfo">
                                    <# var filelist=''; if(model.filePath){ var obj=eval('(' + model.filePath + ')'); var list=obj.data; for(var f=0;f<list.length;f++){ #>
                                        <div id="{{f}}">附件：{{list[f].name}}&nbsp;&nbsp;<a href="javascript:;">删除</a>
                                        </div>
                                    <# } } #>
                                </div>
                                <br>
                                <a href="javascript:;" id="filebegin" class="control-label">上传附件</a>
                                <input id="filePath" name="filePath" type="hidden" value="">
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-primary" data-do="submit">保存</button>
                            </div>
                        </div>
                    </div>
                </form>
                <div class="col-sm-4 col-md-3" style="display:none;">
                    <input type="file" class="form-control" id="inputDocfile" name="docfile" placeholder="附件">
                </div>
            </div>
        </div>
    </div>
</script>

<script type="text/template" id="tmpl-ntypes">
    <# if (ntype == '2') { #>
        <option value="2">图片新闻</option>
        <option value="3">文字新闻</option>
    <# } else { #>
        <option value="1">通知公告</option>
    <# } #>
    <!--<option value="">全部</option>-->
</script>

<script>
    UMEDITOR_HOME_URL = '${contextPath}/assets/dep/umeditor/';
</script>
<script>
    seajs.use('page/pcframe/notice', function (page) {
        page.run();
    });
</script>
</body>
</html>
