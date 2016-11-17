define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    require('jquery-util');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');
    
    var urls = {
    	getnews: CONTEXT_PATH + '/web/notice/findAll.do',
        getdetail: CONTEXT_PATH + '/web/message/details.do?id='
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

    var defaultSize =10;
    var query = {
            pageNo: 1,
            pageCount: 1,
            pageSize: defaultSize,
            search: '',
            wyyId: util.getParam('wyyId')
        };
    var getNews= function (query, type, callback) {
    	//alert(urls.getnews);
    	var t=getRequest('t');
    	var typeString='';
    	if(t==1)typeString='公告';
    	else if(t==2)typeString='新闻';
    	else if(t==3)typeString='新闻';
    	$(".type").text(typeString);
        $.ajax({
            url: urls.getnews,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {
            	type:t,
                pageNo: query.pageNo,
                pageSize: query.pageSize
            },
            beforeSend: function () {
                //loader.show();
            },
            success: function (res) {
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
    var renderNews = function (model, query, type) {
    	var uls=$('#news-list');
    	uls.html('');
        if (model.total === 0)
        {
        	uls.html('<div class="-no -gray">暂无相关记录</div>');
        	return;
        }
        var list =model,html='';
        for(var i=0;i<list.length;i++)
        {
        	var item=list[i];
        	var tt=item.title;
        	if(tt.length>13)tt=tt.substr(0,12)+'..';
        	html='<a class="news-item" href="/mobile/moblicApprove/newsDetail.do?id='+item.id+'">'+tt+'</a>';
        	uls.append(html);
        }
    };
    var initList = function (type) {
    	if(type=='gonggao')
    		getNews(query, type, renderNews);
    };
    var typeString='';
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
    var renderArticle=function(model){
    	console.log(JSON.stringify(model));
    	$('#news-t').html(model.title);
    	var author=model.userName;
    	if(author==null)author='未知';
    	$('#news-author').text(author);
    	$('#time').text(model.createTime);
    	$('#real-content').html(model.content);
    	//alert(model.filePath);
    	if(model.filePath)
    	{
    		//alert(model.filePath.length);
    		var obj=eval('(' + model.filePath + ')');
    		var list=obj.data;
    		//alert(list.length);
    		for(var f=0;f<list.length;f++){
    			$("#fj-list").append('附件'+(f+1)+'：'+list[f].name+'&nbsp;&nbsp;<a href="'+list[f].path+'">下载</a><br>');
    		}
    	}
    };
    var initArticle=function(){
    	var arid=getRequest('id');
    	$.ajax({
            url: urls.getdetail+arid,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {},
            beforeSend: function () {
                //loader.show();
            },
            success: function (res) {
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    renderArticle(res.model);
                }
                else {
                    util.alert(res.message || '获取详情出错！');
                }
            },
            error: function () {
                util.alert('获取详情出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
    };
    var run = function () {
        initList('gonggao');
        initArticle();
    };
    module.exports = { run: run };
});
