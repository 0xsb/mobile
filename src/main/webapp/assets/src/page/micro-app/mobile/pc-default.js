define(function(require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    require('jquery-util');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');

    var urls = {
    	getmail: CONTEXT_PATH + '/web/getEmailUrl.do',
        getList: CONTEXT_PATH + '/approval/getProcessInfoList.do',
        getnews: CONTEXT_PATH + '/web/notice/findAll.do',
        indexPage: CONTEXT_PATH + '/moblicApprove/toIndex.do',
        dailyPage: CONTEXT_PATH + '/moblicApprove/toDailys.do',
        taskPage: CONTEXT_PATH + '/moblicApprove/toTasks.do',
        welfarePage: CONTEXT_PATH + '/moblicApprove/toWelfale.do',
        personlink: CONTEXT_PATH + '/LinkUser/queryListLinkUser.do',
        getlinkType: CONTEXT_PATH + '/LinkType/queryListLinkTypes.do',
        deletelink: CONTEXT_PATH + '/LinkUser/deleteLinkUser.do'
    };

    var getIndexPage = function() {
        var url = '';
        var wyyId = util.getParam('wyyId');
        wyyId = wyyId || 'wyy0001';
        switch (wyyId) {
            case 'wyy0001':
                url = urls.indexPage;
                break;
            case 'wyy0002':
                url = urls.dailyPage;
                break;
            case 'wyy0003':
                url = urls.taskPage;
                break;
            case 'wyy0004':
                url = urls.welfarePage;
                break;
            default:
                url = urls.indexPage;
                break;
        }

        return url;
    };
    var showNew=function(timestr){
    	var dt=new Date(timestr.replace(/-/g, "/")),nt=new Date();
        var df = nt.getTime() - dt.getTime();
        var days = parseInt(df / (1000 * 60 * 60 * 24));
        var str='';
//        if(days<=1)str='<img class="newImg" src="/mobile/assets/src/css/img/new.jpg"/>';
        return str;
    };
    var goBack = function() {
        var backUrl = getIndexPage();
        location.href = backUrl;
    };
    var getColor = function(str) {
        if (!str || str.length === 0) {
            return '#dddddd';
        }
        var code = 0,
            temp = 0,
            codeStr = '',
            i = 0;
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
    var subname = function(name, length) {
        name = name || '';
        if (!length || length <= 0) {
            length = 2;
        }
        return name.slice(-1 * length);
    };
    var itemProcess = function(list) {
        _.each(list, function(item) {
            item.colorCode = getColor(item.userName);
            item.subName = subname(item.userName);
        });
        return list;
    };

    function openWindow(url) {
        var wleft = (window.screen.availWidth - 930) / 2 + 'px';
        window.open(url, 'newwindow', 'scrollbars=1,width=900px,height=640px,left=' + wleft);
    }
    
    window.openWindow = openWindow;
    
    //var listRender = template($('#tmpl-approvals').html());
    //var nodataRender = template($('#tmpl-nodata').html());
    var typeMap = {
        'dwsp': {
            name: '我的待办',
            type: 'dwsp'
        },
        'wdfq': {
            name: '我的发起',
            type: 'wdfq'
        },
        'dwyl': {
            name: '我的待阅',
            type: 'dwyl'
        },
        'wysp': {
            name: '我的已办',
            type: 'wysp'
        }
    };
    var defaultSize = 5;

    var getList = function(query, type, callback) {
        $.ajax({
            url: urls.getList,
            type: 'post',
            dataType: 'json',
            Global: false,
            data: {
                type: type,
                page: query.pageNo,
                pageSize: query.pageSize,
                term: query.search,
                rnd: (new Date).getTime(),
                wyyId: query.wyyId
            },
            beforeSend: function() {
                //loader.show();
            },
            success: function(res) {
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    callback(res.model, query, type);
                } else {
                    util.alert(res.message || '获取审批列表出错！');
                }
            },
            error: function() {
                util.alert('获取审批列表出错！');
            },
            complete: function() {
                //loader.hide();
            }
        });
    };
    var openEmail=function(){
    	$.ajax({
            url: urls.getmail,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {},
            beforeSend: function () {
            },
            success: function (res) {
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    window.open(res.model.emailUrl);
                }
                else {
                    util.alert(res.message || '获取设置出错！');
                }
            },
            error: function () {
                util.alert('获取设置出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
    };
    window.openEmail=openEmail;
    var getLinkType = function(){
		$.ajax({
            url: urls.getlinkType,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {},
            beforeSend: function () {
                //loader.show();
            },
            success: function (res) {
            	var master = res.model;
            	tab_containers = '<div class="tab_content" style="display: none;"></div>';
            	if(master != null){
            		$.each(master,function(i,item){
    		        	tabsList = '<li typeId="'+item.id+'" class="tabsList">'+item.typeName+'</li><img class="tabsList_img" src="/mobile/assets/src/css/img/lineTo.jpg">';
    		        	$(".tabsLeft").append(tabsList);
    		        	$(".tab_container").append(tab_containers);
    		        })
    		        $(".tab_content").eq(0).css("display","block");
    	            getLinks();
            	}
            },
            error: function () {
                alert('获取设置出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
	}
    var tabsList = '',tab_containers = '',tabsLinks = '';
    // var linkItemRender = template($('#tmpl-linkItem').html());
    var setData = {
    		_ajax :function(typeId,_index){
    			$.ajax({
                    url: urls.personlink,
                    type: 'post',
                    dataType: 'json',
                    Global:false,
                    data: {typeId:typeId},
                    success: function (res) {
                        var masterUrl = res.model;
                    	tabsLinks = '';
//                    	console.log(masterUrl)
                    	if(masterUrl != null){
                    		$.each(masterUrl,function(i,item){	
        		        		tabsLinks += '<div><a class="addLinks" resourceIds="'+item.id+'" href="http://'+item.url+'" target="_blank">'+item.context+'</a><img class="imgDelete" src="/mobile/assets/src/css/img/delete.png" /></div>';
        			        })
                    	}
    			        $(".tab_content").eq(_index).html(tabsLinks);
                        setData.imgDelete()
                    },
                    error: function () {
                        util.alert('获取设置出错！');
                    }
                });
    		},
    		imgDelete : function(){
    			$(".imgDelete").click(function(){
    				var sourcrId = $(this).siblings().attr("resourceids");
    				$(this).parent().remove();
    				$.ajax({
                    url: urls.deletelink,
                    type: 'post',
                    dataType: 'json',
                    Global:false,
                    data: {id:sourcrId},
                    success: function (res) {
                    },
                    error: function () {
                        util.alert('获取设置出错！');
                    }
                });
    			})
    		}
    }
    var getLinks=function(){
    	setData._ajax($(".tabsList").eq(0).attr("typeid"),0);
    	$(".tabsList").eq(0).addClass("tabsList_active");
        $(".tabsList").hover(function(){
        	$(this).siblings().removeClass("tabsList_active");
        	$(this).addClass("tabsList_active");
            setData._ajax($(this).attr("typeid"),$(this).index());
        	$(".tab_content").eq($(this).index()).show();
        	$(".tab_content").eq($(this).index()).siblings().hide();
        },function(){})
        $(".tab_content").hover(function(){
        	$(this).show();
        	$(this).siblings().hide();
        },function(){})
    };
    
    var getNews = function(query, type, callback) {
        var _type=type,
        _data={
                type: _type,
                pageNo: 1,
                pageSize: 5
            };
        if(type==1)_data.pageSize=10;
        $.ajax({
            url: urls.getnews,
            type: 'post',
            dataType: 'json',
            Global: false,
            data:_data,
            beforeSend: function() {
                //loader.show();
            },
            success: function(res) {
//            	console.log(res)
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    callback(res.model, query, type);
                } else {
                    util.alert(res.message || '获取新闻列表出错！');
                }
            },
            error: function() {
                util.alert('获取新闻列表出错！');
            },
            complete: function() {
                //loader.hide();
            }
        });
    };
    var approvalItemRender = template($('#tmpl-approvalItem').html());
    
    var renderList = function(model, query, type) {
        var html = [];
        if (type == 'dwsp') {
            $('#info-count').text(model.total);
            if (model.total == 0) {
                $('#info-count').hide();
            } else {
                $('#info-count').show();
            }
        }
        if (model.total === 0) {
            // 无数据进行提示
            html = '<div class="-no -gray">暂无相关记录</div>';
        } else {
            // 显示数据
            var list = model.list;
            _.each(list, function(model) {
                var url = model.link;
                url += '&type=' + type;
                model.url = url;
                model.date=model.arriveDate.substring(0,10);
                html.push(approvalItemRender({
                    model: model
                }));
            });
        }
        $('.task-list').html(html);
    };
    var renderGG = function(model, query, type) {
        $('#news-0').hide();
        $('#news-1').hide();
        $('#gonggao-0').show();
        $('#gonggao-1').show();
        $('#gonggao-0').html('');
        $('#gonggao-1').html('');
        var uls = [$('#gonggao-0'), $('#gonggao-1')];
        if (model.length == 0) {
            uls[0].html('<div class="-no -gray">暂无相关记录</div>');
            return;
        }
        var list = model,
            html = '';
        for (var i = 0; i < list.length; i++) {
            var item = list[i];
            var tt = item.title;
            if (tt.length > 19) tt = tt.substr(0, 18) + '..';
            tt+=showNew(item.createTime);
            html = '<a href="javascript:window.openWindow(\'/mobile/moblicApprove/newsDetail.do?t='+type+'&id='+item.id+'\');">' + tt + '</a>';
            uls[i % 2].append(html);
//            $('#news-1').append(html);
        }
    };
    var renderNews = function(model, query, type) {
        $('#gonggao-0').hide();
        $('#gonggao-1').hide();
        $('#news-0').show();
        $('#news-1').show();
        $('#news-1').html('');
        if (model.length == 0) {
            $('#news-1').html('<div class="-no -gray">暂无相关记录</div>');
            return;
        }
        var list = model,
            html = '';
        for (var i = 0; i < list.length; i++) {
            var item = list[i];
            var tt = item.title;
            if (tt.length > 20) tt = tt.substr(0, 21) + '..';
            tt+=showNew(item.createTime);
            html = '<a class="newlist" href="javascript:window.openWindow(\'/mobile/moblicApprove/newsDetail.do?t='+type+'&id='+item.id+'\');">' + tt + '</a>';
            $('#news-1').append(html);
//            $(".newlist").click(function(){
//            	$(this).children(".newImg").remove();
//            })
        }
    };
    var renderPicNews = function(model, query, type) {
        $('#gonggao-0').hide();
        $('#gonggao-1').hide();
        $('#news-0').show();
        $('#news-1').show();
        //$('#news-1').html('');
        $(".imgList").html('');
        $(".infoList").html('');
        $(".indexList").html('');
        var imgList='',infoList='',indexList='',imgListLeft = '';
        var list = model;
        for (var i = 0; i < list.length; i++) {
            var item = list[i];
            var tt = item.title;
            var cls='',cls2='';
            if(i==0){cls='infoOn';cls2='indexOn';}
            if (tt.length > 14) tt = tt.substr(0, 13) + '..';
            imgList+='<li><a href="javascript:window.openWindow(\'/mobile/moblicApprove/newsDetail.do?t='+type+'&id='+
            item.id+'\');"><img src="'+item.picurl+'" width="400px" height="200px"></a></li>';
            infoList+='<li class="'+cls+'">'+tt+'</li>';
            indexList+='<li class="'+cls2+'">'+(i+1)+'</li>';
            imgListLeft+='<a class="newlist" href="javascript:window.openWindow(\'/mobile/moblicApprove/newsDetail.do?t='+type+'&id='+item.id+'\');">' + tt + '</a>';
        }
        $(".imgList").html(imgList);
        $(".infoList").html(infoList);
        $(".indexList").html(indexList);
        $("#news-1").append(imgListLeft);
      //轮播动画
        setData.curIndex = 0, //当前index
            imgLen = $(".imgList li").length; //图片总数
        // 定时器自动变换2.5秒每次
        var autoChange = setInterval(function() {
            if (setData.curIndex < imgLen - 1) {
                setData.curIndex++;
            } else {
                setData.curIndex = 0;
            }
            //调用变换处理函数
            changeTo(setData.curIndex);
        }, 2500);
        //左箭头滑入滑出事件处理
        $("#prev").hover(function() {
            //滑入清除定时器
            clearInterval(autoChange);
        }, function() {
            //滑出则重置定时器
            autoChangeAgain();
        });
        //左箭头点击处理
        $("#prev").click(function() {
            //根据setData.curIndex进行上一个图片处理
            setData.curIndex = (setData.curIndex > 0) ? (--setData.curIndex) : (imgLen - 1);
            changeTo(setData.curIndex);
        });
        //右箭头滑入滑出事件处理
        $("#next").hover(function() {
            //滑入清除定时器
            clearInterval(autoChange);
        }, function() {
            //滑出则重置定时器
            autoChangeAgain();
        });
        //右箭头点击处理
        $("#next").click(function() {
            setData.curIndex = (setData.curIndex < imgLen - 1) ? (++setData.curIndex) : 0;
            changeTo(setData.curIndex);
        });
        //对右下角按钮index进行事件绑定处理等
        $(".indexList").find("li").each(function(item) {
            $(this).hover(function() {
                clearInterval(autoChange);
                changeTo(item);
                // setData.curIndex = item;
            }, function() {
                autoChangeAgain();
            });
        });
        //清除定时器时候的重置定时器--封装
        function autoChangeAgain() {
            autoChange = setInterval(function() {
                if (setData.curIndex < imgLen - 1) {
                    setData.curIndex++;
                } else {
                    setData.curIndex = 0;
                }
                //调用变换处理函数
                changeTo(setData.curIndex);
            }, 2500);
        }

        function changeTo(num) {
            var goLeft = num * 400;
            $(".imgList").animate({
                left: "-" + goLeft + "px"
            }, 500);
            $(".infoList").find("li").removeClass("infoOn").eq(num).addClass("infoOn");
            $(".indexList").find("li").removeClass("indexOn").eq(num).addClass("indexOn");
        }
    };
    var bindEvents = function() {
        $('.tab1').on('click', function(e) {
            $('.tab1').attr('class', 'tab tab1');
            $(e.target).attr('class', 'tab tab1 active');
            initPage($(e.target).attr('id'));
        });
        $('.morenews').on('click', function(e) {
         //    setData.curIndex = 0;
         //    console.log(setData.curIndex);
        	// $(".tab1").each(function(){
        	//     if($(this).attr('class').indexOf('active')>-1)
        	//     {
        	//     	var _id=$(this).attr('id');
        	//     	// if(_id=='gonggao')
        	//     	// 	initPage(_id);
        	//     	// else{
        	//     		initPage('news');
        	//     		initPage('pics');
        	//     	// }
        	//     }
        	// });
            location.href=location.href;
        });
        $('#my-approval .tabs').on('click', '.tab', function(event) {
            var $this = $(this);
            if ($(this).hasClass('active')) return false;

            var id = $this.attr('id');
            $this.siblings('.active').removeClass('active').end().addClass('active');

            initPage(id);
        }).on('click', '[data-do="refresh"]', function() {
            var id = $(this).siblings('.active').attr('id');
            initPage(id);
        });
    };
    var query = {
        pageNo: 1,
        pageCount: 1,
        pageSize: defaultSize,
        search: '',
        wyyId: util.getParam('wyyId')
    };
    var initPage = function(type) {
    	//alert(type);
        if (type == 'news') {
            getNews(query, 3, renderNews);
        }
        if(type=='pics'){
        	getNews(query, 2, renderPicNews);
        } 
        if (type == 'gonggao') {
            getNews(query, 1, renderGG);
        } else {
            getList(query, type, renderList);
        }
    };
    var run = function(mod) {
        $(document).on('click', '[data-target="popwin"]', function() {
            var url = $(this).data('href');
            openWindow(url);
        });
        if (mod == undefined) {
            initPage('dwsp');
            var mod=Math.floor(Math.random()*2);
            $(".tab1").attr('class','tab tab1 ');
//            if(mod==1){
            	$("#news").attr('class','tab tab1 active');
            	initPage('news');
                initPage('pics');
//            }else{
//            	$("#gonggao").attr('class','tab tab1 active');
//            	initPage('gonggao');
//            }
//            $("#news").attr('class','tab tab1 active');
//        	initPage('news');
//            initPage('pics');
            bindEvents();
        } else {
            //实现指定刷新
            initPage(mod);
        }
        getLinkType();
        $("#news").on("click",function(){
        	$("#news").attr('class','tab tab1 active');
        	initPage('news');
            initPage('pics');
        })
    };
    module.exports = {
        run: run
    };
});