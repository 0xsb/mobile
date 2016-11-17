define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    require('jquery-util');
    var util = require('component/mobile/util');
    var loader = require('component/mobile/loader');
    
    var urls = {
        getList: CONTEXT_PATH + '/users/getAllDept.do',
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

    var defaultSize =20;
    var getList = function (query, type, callback) {
        $.ajax({
            url: urls.getList,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {
                type: type,
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
                    util.alert(res.message || '获取部门列表出错！');
                }
            },
            error: function () {
                util.alert('获取部门列表出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
    };
    var bindDptEvents=function(){
    	$('.lv0').on('click',function (e) {
    		var id=$(e.target).attr('id');
    		curname=$(e.target).text();
    		$('.lv0').attr('class','txl-menu lv0');
    		$(e.target).attr('class','txl-menu lv0 active');
    		getContactsById(id);
    		$('#sub-'+id).toggle();
        });
    	$('.lv1').on('click',function (e) {
    		var id=$(e.target).attr('id');
    		curname=$(e.target).text();
    		$('.lv1').attr('class','txl-menu lv1');
    		$(e.target).attr('class','txl-menu lv1 active');
    		getContactsById(id);
    		$('#sub-'+id).toggle();
        });
    	
    };
    var renderDpt = function (model, query, type) {
    	//console.log(JSON.stringify(model));
    	//alert(model.length)
    	var jModel={};
    	var subm=function(parid){
    		//子菜单
    		var tmps=[];
    		var styStr='display:block;';
        	for(var j=0;j<model.length;j++)	
        	{
        		var dpt=model[j];
        		//alert("son:"+JSON.stringify(dpt));
        		//alert(dpt.previousId);
        		if(dpt.previousId==parid)
        		{
        			if($("#sub-"+parid).length>0)
        			{
        				$("#sub-"+parid).append("<div class='txl-menu lv1' id='"+dpt.id+"'>"+
        	        	dpt.orgName+"</div><div class='sub' id='sub-"+dpt.id+"' style='"+styStr+"'></div>");
        				styStr='display:none;';
        			}
        			else
        			{
        				tmps.push(dpt);
        			}
        		}
        	}
        	//处理异常数据
        	for(var k=0;k<tmps.length;k++)	
        	{
        		var dpt=tmps[k];
        		if(dpt.previousId==parid)
        		{
        			if($("#sub-"+parid).length>0)
        			{
        				$("#sub-"+parid).append("<div class='txl-menu lv1' id='"+dpt.id+"'>"+
        	        	dpt.orgName+"</div><div class='sub' id='sub-"+dpt.id+"'></div>");
        			}
        		}
        	}
    	};
    	//生成父级
    	var styStr='display:block;';
    	for(var i=0;i<model.length;i++)	
    	{
    		var fathor=model[i];
    		if(fathor.previousId==null||fathor.previousId=='')
    		{
    			$("#dpt-list").append("<div class='txl-menu lv0' id='"+fathor.id+"'>"+
    			fathor.orgName+"</div><div class='sub' id='sub-"+fathor.id+"' style='"+styStr+"'></div>");
    			styStr='display:none;';
    			if(curid=='')
    			{
    				curid=fathor.id;
    				curname=fathor.orgName;
    			}
    		}
    		subm(fathor.id);
    	}
    	//alert($('.lv0:first').attr('id'));
		//$('.lv0:first').show();
    	//默认通讯录
    	$('#'+curid).attr('class',$('#'+curid).attr('class')+' active');
    	getContactsById(curid);
    	//事件绑定
    	bindDptEvents();
    };
    var getContactsById=function(id){
    	//alert(id);
    	$.ajax({
            url: urls.contacts+id,
            type: 'post',
            dataType: 'json',
            Global:false,
            data: {
            },
            beforeSend: function () {
                //loader.show();
            },
            success: function (res) {
            	//alert(JSON.stringify(res));
                if (res.success) {
                    if (typeof res.model === 'string') res.model = JSON.parse(res.model);
                    initContacts(res.model);
                }
                else {
                    util.alert(res.message || '获取通讯录列表出错！');
                }
            },
            error: function () {
                util.alert('获取通讯录列表出错！');
            },
            complete: function () {
                //loader.hide();
            }
        });
    };
    var initContacts=function(list){
    	$(".contacts").html('');
    	for(var i=0;i<list.length;i++)
    	{
    		var user=list[i];
    		var pst=user.post==null?'--':user.post;
    		var num=user.workNumber==null?'--':user.workNumber;
    		$(".contacts").append('<tr><td>'+user.userName+'</td>'+
    		'<td>&nbsp;'+curname+'</td>'+
			'<td>&nbsp;'+pst+'</td>'+
			'<td>'+user.mobile+'</td>'+
			'<td>&nbsp;'+num+'</td></tr>');
    	}
    };
    var query = {
            pageNo: 1,
            pageCount: 1,
            pageSize: defaultSize,
            search: '',
            wyyId: util.getParam('wyyId')
        };
    var initPage = function (type) {
        getList(query, type, renderDpt);
    };
    var run = function () {
        initPage('txl');
    };

    module.exports = { run: run };
});
