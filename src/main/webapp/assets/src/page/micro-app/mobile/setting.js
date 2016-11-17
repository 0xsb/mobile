define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    require('jquery-util');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');
    var alert = require('component/Alert');

    var urls = {
    	getmail: CONTEXT_PATH + '/web/getEmailUrl.do',
        setmail: CONTEXT_PATH + '/web/updateEmailUrl.do',
        setword: CONTEXT_PATH + '/OA/updatePassword.do',
        getlinkType: CONTEXT_PATH + '/LinkType/queryListLinkTypes.do',
        getlink: CONTEXT_PATH + '/LinkResouces/queryLinkResiuces.do',
        upTolink: CONTEXT_PATH + '/LinkUser/insertLinkUser.do'
    };
    var curid='',curname='',resetLinksData = '';
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
     /**
      * 链接格式
      */
   var isUrl=function(str_url)
   {
          var strRegex = "^((https|http|ftp|rtsp|mms)?://)"
          + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
          + "(([0-9]{1,3}\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
          + "|" // 允许IP和DOMAIN（域名）
          + "([0-9a-z_!~*'()-]+\.)*" // 域名- www.
          + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\." // 二级域名
         + "[a-z]{2,6})" // first level domain- .com or .museum
         + "(:[0-9]{1,4})?" // 端口- :80
         + "((/?)|" // a slash isn't required if there is no file name
         + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
         var re=new RegExp(strRegex);
 		//re.test()
         if (re.test(str_url)){
             return (true);
         }else{
             return (false);
         }
    }
    var getSetting= function (query, t, callback) {
    	var _url='';
    	if(t==2)_url=urls.getmail;
    	else return;
        $.ajax({
            url: _url,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {},
            beforeSend: function () {
                //loader.show();
            },
            success: function (res) {
            	//alert(JSON.stringify(res));
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    callback(res.model, query, t);
                }
                else {
                    alert(res.message || '获取设置出错！');
                }
            },
            error: function () {
                alert('获取设置出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
    };
    var setEmail=function(_emailUrl){
    	$.ajax({
            url: urls.setmail,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {emailUrl:_emailUrl},
            beforeSend: function () {
                //loader.show();
            },
            success: function (res) {
                if (res.success) {
                	alert('操作成功！').delay(1);
                }
                else {
                    util.alert(res.message || '设置邮箱出错！');
                }
            },
            error: function () {
                util.alert('设置邮箱出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
    };
    var approvalItemRender = template($('#tmpl-mailItem').html());
    var renderMailForm = function (model, query, type) {
    	var uls=$('#mail-list');
    	uls.html('');
    	var mails=[{index:1,name:'移动139',url:'http://mail.10086.cn/'},
    	           {index:2,name:'网易邮箱',url:'http://mail.163.com/'},
    	           {index:3,name:'搜狐邮箱',url:'http://mail.sohu.com/'},
    	           {index:4,name:'腾讯邮箱',url:'http://mail.qq.com/'},
    	           {index:5,name:'126邮箱',url:'http://www.126.com/'},
    	           {index:6,name:'新浪邮箱',url:'http://mail.sina.com.cn/'}];

        _.each(mails, function (item) {
            item.space ='';
            uls.append(approvalItemRender({
                model: item
            }));
        });


        var mail = model ? model.emailUrl : '';
        if(mail=='')$("#mymail").val(mails[0].url);
        else $("#mymail").val(mail);

        $(".s-radio").on("click", 'input[name="mailradio"]',function(e){
        	$("#mymail").val($(this).val());
        });
        $(".submitmail").on("click",function(){
        	if($("#mymail").val()=='')
        	{
        		alert('邮箱地址没有填写').delay(1);
        		return;
        	}
        	else if(!isUrl($("#mymail").val())){
        		alert('邮箱地址格式错误').delay(1);
        		return;
        	}
        	setEmail($("#mymail").val());
        });
    };
    var resetPassword = function(){
    	$(".resetPassword").click(function(){
    		if($(".f-ls-passwordNews").val() === $(".f-ls-passwordNew").val()){
        		$.ajax({
                    url: urls.setword,
                    type: 'post',
                    dataType: 'json',
                    Global:false,
                    data: {oldPassword:$(".f-ls-password").val(),password:$(".f-ls-passwordNews").val()},
                    success: function (res) {
                        if(res.message === "错误原因：旧密码错误"){
                        	alert(res.message);
                        }else{
                        	alert("修改成功");
                        }
                    },
                    error: function () {
                        util.alert('获取设置出错！');
                    }
                });
        	}else{
        		alert("新密码填写不一致")
        	}
    	})
    }
    var setFu = {
    		changeRe :'',
    		resourcesIdsMore : '',
    		resourcesIds: {
    			resourcesIds: []
    		},
    		addLinks : function(){
    			$(".addLinks").click(function(){
    				if(setFu.resourcesIds.resourcesIds.indexOf($(this).attr("resourcesId")) == -1){
    					tabsLinks = '<p class="addLinksY" resourcesId="'+$(this).attr("resourcesId")+'" target="_blank">'+$(this).text()+'</p>';
        				$(".tab_containersNew").append(tabsLinks);
        				setFu.resourcesIds.resourcesIds.push($(this).attr("resourcesId"));
    				}
    				setFu.addLinksY();//删除链接
    			})
    		},
    		addLinksY : function(){
    			$(".addLinksY").click(function(){
    				$(this).remove();
    				setFu.resourcesIdsMore = setFu.resourcesIds.resourcesIds.toString();
    				setFu.resourcesIdsMore = setFu.resourcesIdsMore.replace(new RegExp($(this).attr("resourcesId")),"");
    				setFu.resourcesIds.resourcesIds = setFu.resourcesIdsMore.split(",")
    			})
    		},
    		resetLinks : function(){
    			$(".resetLinks").click(function(){
        			$.ajax({
        	            url: urls.upTolink,
        	            type: 'post',
        	            dataType: 'json',
        	            Global:false,
        	            data: setFu.resourcesIds,
        	            success: function (res) {
        	            	if(setFu.resourcesIds.resourcesIds.length != 0){
        	            		alert("保存设置成功")
        	            	}
        	            },
        	            error: function () {
        	                alert('获取设置出错！');
        	            }
        	        });
    			})
    		},
    		_ajax : function(typeIds,_index){
    			$.ajax({
                    url: urls.getlink,
                    type: 'post',
                    dataType: 'json',
                    Global:false,
                    data: {typeId:typeIds},
                    success: function (res) {
                    	var masterUrl = res.model;
                    	tabsLinks = '';
                    	$.each(masterUrl,function(i,item){	
    		        		tabsLinks += '<p class="addLinks" resourcesId="'+item.id+'" addSrc="'+item.url+'" target="_blank">'+item.context+'</p>';
    			        })
    			        $(".tab_content").eq(_index).html(tabsLinks);
    			    	setFu.addLinks();//添加常用链接
                    },
                    error: function () {
                        alert('获取设置出错！');
                    }
                });
    		}
    }
    var tabsList = '',tab_containers = '',tabsLinks = '';
	var getLinkType = function(){
		$.ajax({
            url: urls.getlinkType,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {},
            success: function (res) {
            	var master = res.model;
            	getLinks(master);
            },
            error: function () {
                alert('获取设置出错！');
            }
        });
	}
    var getLinks=function(master){//常用链接设置
        tab_containers = '<div class="tab_content tab_contentSet" style="display: none;"></div>';
        if(master != null){
        	$.each(master,function(i,item){
            	tabsList = '<li typeId="'+item.id+'" class="tabsList">'+item.typeName+'</li>';
            	$(".tabsLeft").append(tabsList);
            })
            for(var i = 0,j = master.length;i<j;i++){
                $(".tab_container").append(tab_containers);
            };
            $(".tab_content").eq(0).css("display","block");
    		setFu._ajax($(".tabsList").eq(0).attr("typeid"),0);
            $(".tabsList").click(function(){
            	var _index = $(this).index();
        		setFu._ajax($(this).attr("typeId"),_index);
            	$(".tab_content").eq($(this).index()).show();
            	$(".tab_content").eq($(this).index()).siblings().hide();
            })
        }
    };
    var initList = function (type) {
    	var t=getRequest('t');
    	if(t==0)t=1;
    	$(".lv1").attr('class','txl-menu lv1');
    	$("#t"+t).attr('class','txl-menu lv1 active');
    	$("#setting-"+t).show();
    	if(t==1)
    		getSetting(query, t, renderMailForm);
    	else if(t==2)
    		getSetting(query, t, renderMailForm);
    	resetPassword();//修改密码
    	getLinkType();//获取公司链接Type
    	setFu.resetLinks();	
    };
    var run = function () {
        initList('setting');
    };

    module.exports = { run: run };
});
