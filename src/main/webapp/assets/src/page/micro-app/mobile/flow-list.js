define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    require('jquery-util');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');
    
    var urls = {
    	getnews: CONTEXT_PATH + '/approval/getProcessInfoList.do',
        contacts: CONTEXT_PATH + '/users/getUserByDeptId.do?deptId='
    };
    var curid='',curname='';
    var goBack = function () {
        var backUrl = getIndexPage();
        location.href = backUrl;
    };

    var getColor = function (str) {
        if (!str || str.length === 0) { return '#dddddd'; }
        var code = 0, temp = 0, codeStr = '', i = 0;
        for (i = 0; i < str.length; i++) {
            code += str.charCodeAt(i);
        }
        for (i = 0; i < 3; i++) {
            temp = code % 192;
            codeStr += ('00' + temp.toString(16)).slice(-2);
            code = Math.round(code * temp / 127);
        }
        return '#' + codeStr;
    };
    var subname = function (name, length) {
        name = name || '';
        if (!length || length <= 0) {
            length = 2;
        }
        return name.slice(-1 * length);
    };
    var itemProcess = function (list) {
        _.each(list, function (item) {
            item.colorCode = getColor(item.userName);
            item.subName = subname(item.userName);
        });
        return list;
    };
    var defaultSize =15;
    var pageMax=1;
    var query = {
            pageNo: 1,
            pageCount: 1,
            pageSize: defaultSize,
            search: '',
            wyyId: util.getParam('wyyId')
        };
    /**
     * 以下关于分页栏
    */
    var changePage=function(p)
    {
    	var currentPage = p;
    	if(currentPage<1)changePage=1;
    	else if(currentPage>pageMax)currentPage=pageMax;
    	creatPageBar($(".page-bar"), currentPage, pageMax, "window.changePage");
    	if(currentPage==query.pageNo)return;
    	//加载新页数据
    	query.pageNo=currentPage;
    	initList('flow');
    };
    window.changePage=changePage;
    var creatPageBar=function($pageBox, pageCurrent, pageMax, funName) 
    {
    	if ($pageBox.length == 0)
    		$pageBox = document.body;
    	var pageHtml = "";
    	if (pageCurrent > 5)
    		pageHtml = "<a href='javascript:void(0);' class='page_item' style='width:50px;' onclick='"
    				+ funName + "(1)'>首页</a>";
    	if (pageCurrent > 1)
    		pageHtml += "<a href='javascript:void(0);' class='page_item' onclick='"
    				+ funName + "(" + (pageCurrent - 1) + ")'><</a>";
    	for (var p = 1; p <= pageMax; p++) {
    		if (p == pageCurrent)
    			pageHtml += "<a class='page_item_on' onclick='"
    					+ funName + "(" + p + ")'>" + p + "</a>";
    		else if (p < pageCurrent - 5) {
    			if (pageHtml.indexOf("left_more") == -1)
    				pageHtml += "left_more";
    		} else if (p > pageCurrent + 5) {
    			if (pageHtml.indexOf("right_more") == -1)
    				pageHtml += "right_more";

    		} else
    			pageHtml += "<a href='javascript:void(0);' class='page_item' onclick='"
    					+ funName + "(" + p + ")'>" + p + "</a>";
    	}
    	if (pageCurrent < pageMax)
    		pageHtml += "<a href='javascript:void(0);' class='page_item' onclick='"
    				+ funName + "(" + (pageCurrent + 1) + ")'>></a>";
    	if (pageCurrent < pageMax - 5)
    		pageHtml += "<a href='javascript:void(0);' class='page_item'style='width:50px;' onclick='"
    				+ funName + "(" + pageMax + ")'>末页</a>";
    	pageHtml = pageHtml.replace("left_more",
    			"<a href='javascript:void(0);' class='page_item_empty'>..</a>");
    	pageHtml = pageHtml.replace("right_more",
    			"<a href='javascript:void(0);' class='page_item_empty'>..</a>");
    	$pageBox.html(pageHtml);
    };
    /*
     *获取url中参数
     */
     var getRequest=function(key) 
     {
         var url = location.search; //获取url中"?"符后的字串
         var theRequest = new Object();
         if (url.indexOf("?") != -1) {
             var str = url.substr(1);
             strs = str.split("&");
             for (var i = 0; i < strs.length; i++) {
                 theRequest[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]);
             }
         }
         return theRequest[key];
     }
    var getNews= function (query, type, callback) {
    	var t=getRequest('t');
    	var typeString='';
    	if(t==0)t=1;
    	$(".lv1").attr('class','txl-menu lv1');
    	$("#t"+t).attr('class','txl-menu lv1 active');
    	var types=['','dwsp','wysp','wdfq','dwyl'];
        $.ajax({
            url: urls.getnews,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {
            	type:types[t],
                page: query.pageNo,
                pageSize: query.pageSize,
                term: query.search,
                rnd: (new Date).getTime(),
                wyyId: query.wyyId
            },
            beforeSend: function () {
                //loader.show();
            },
            success: function (res) {
            	//alert(JSON.stringify(res));
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    callback(res.model, query, type);
                }
                else {
                    util.alert(res.message || '获取新闻列表出错！');
                }
            },
            error: function () {
                util.alert('获取新闻列表出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
    };
    var approvalItemRender = template($('#tmpl-approvalItem').html());
    var renderNews = function (model, query, type,count) {
    	var uls=$('.sp-list');
    	var html = [];
    	uls.html('');
        if (model.total === 0)
        {
        	uls.html('<div class="-no -gray">暂无相关记录</div>');
        	return;
        }
        pageMax=model.pages;
        var list =model.list,html='';
        _.each(list, function(model) {
            var url = model.link;
            url += '&type=' + type;
            model.url = url;
            uls.append(approvalItemRender({
                model: model
            }));
        });
        changePage(query.pageNo);
    };
    var initList = function (type) {
    	if(type=='flow')
    		getNews(query, type, renderNews);
    };
    var run = function () {
        initList('flow');
    };

    module.exports = { run: run };
});
