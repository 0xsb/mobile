package cmcc.mobile.admin.controller.wubj;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vaadin.data.util.filter.And;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.CustomerMapper;
import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.entity.UserApprovalType;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.iApprovalService;
import cmcc.mobile.admin.util.PropertiesUtil;
import cmcc.mobile.admin.vo.CompanyVo;
 

@Controller
@RequestMapping("/appApproval")
public class AppInterfaceController extends BaseController{
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private iApprovalService approvalService;
	@Autowired
	private TotalUserMapper totalUserMapper ;
	@Autowired
	private CustomerMapper customerMapper ;
	/**
	 * 获取待办信息条数
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("getInfoCount")
	@ResponseBody
	public JsonResult getInfoCount(HttpServletRequest request, String userId,String companyId,String wyyId) {
		if(wyyId == null || wyyId.equals("")){
			wyyId = "wyy0001" ;
		}
		JsonResult jsonresult = new JsonResult();
		MultipleDataSource.setDataSourceKey("");
		request.getSession().setAttribute("userId", userId);
		request.getSession().setAttribute("companyId", companyId);
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		ThirdApprovalDeal record = new ThirdApprovalDeal();
		record.setUserId(userId);
		record.setNodeStatus("1");
		record.setStatus("1");
		record.setWyyId(wyyId);
		try {
			List<ThirdApprovalDeal> list = approvalService.getDealInfo(record);
			jsonresult.setModel(list.size());
			jsonresult.setSuccess(true);
		} catch (Exception e) {
			logger.info(e);
			jsonresult.setSuccess(false);
			jsonresult.setMessage("服务器错误");
		}
		return jsonresult;
	}

	/**
	 *跳转代办已办待阅我的已办H5页面
	 */
	@RequestMapping("getApproval")
	@ResponseBody
	public JsonResult getApproval(HttpServletRequest request ,String type,String userId,String companyId,String mobile){
		Customer customer = customerMapper.selectByCompanyId(companyId) ;
		String companyName = customer.getCustomerName() ;
		String DynamicDbName = customer.getDbname() ;
		if (StringUtils.isNotEmpty(type)) {
			String approvalLink = "mobile/appApproval/toAppWorkflowList.do?"+"type="+type+"&mobile="+mobile+"&userId="+userId+"&companyId="+companyId+"&companyName="+companyName+"&DynamicDbName="+DynamicDbName;		
			return new JsonResult(true,"获取成功",approvalLink) ;
		}	
		return new JsonResult(false,"参数错误",null) ;
	}
	
	/**
	 * 
	 * 
	 * 审批列表页
	 * 
	 * @return
	 */
	@RequestMapping("/toAppWorkflowList")
	public String toAppWorkflowList(String mobile,String userId,String companyId,String companyName,String DynamicDbName) {
		request.getSession().setAttribute("mobile", mobile);	
		request.getSession().setAttribute("userId",userId) ;
		request.getSession().setAttribute("companyId",companyId) ;
		request.getSession().setAttribute("companyName",companyName) ;
		request.getSession().setAttribute("DynamicDbName",DynamicDbName) ;	
		return "microApp/mobile/workflowList";
	}
	
	/**
	 * 切换集团
	 */
	@RequestMapping("/cutCompany")
	@ResponseBody
	public JsonResult cutCompany(HttpServletRequest request , String mobile){
 	if(StringUtils.isNotEmpty(mobile)){
		request.getSession().setAttribute("mobile", mobile);	
		MultipleDataSource.setDataSourceKey("");
		 List<CompanyVo> list = totalUserMapper.selectByMobilel(mobile) ;
			for (int i = 0; i < list.size(); i++) {
				CompanyVo companyVo = list.get(i) ;
				companyVo.setPassword(null);
			}
		 return new JsonResult(true,"获取成功！",list) ;
		}
		return new JsonResult(false,"参数错误！",null) ;
	}

	/**
	 * 获取默认流程 置顶流程
	 * 
	 * @return
	 */
	@RequestMapping("getProcessList")
	@ResponseBody
	public JsonResult getProcessList(HttpServletRequest request,
			@RequestParam(value = "wyyId", defaultValue = "wyy0001") String wyyId,String userId,String companyId,String mobile) {
		Map<String, Object> map1 = new HashMap<>();
		Map<String, Object> map2 = new HashMap<>();
		request.getSession().setAttribute("userId",userId);
		request.getSession().setAttribute("companyId",companyId) ;
		request.getSession().setAttribute("mobile",mobile) ;
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		JsonResult jsonresult = new JsonResult();
		List<ApprovalType> topApprovalList = new ArrayList<ApprovalType>(); // 置顶流程
		List<ApprovalType> defaultApprovalList = new ArrayList<ApprovalType>();// 默认流程
		List<UserApprovalType> collectList = new ArrayList<UserApprovalType>();// 收藏流程ID
		List<ApprovalType> collectTypeList = new ArrayList<ApprovalType>(); // 收藏流程
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			map1.put("companyId", companyId);
			map1.put("wyyId", wyyId);
			map1.put("userId", userId);
			topApprovalList = approvalService.getTopProcess(map1);
			defaultApprovalList = approvalService.getDefaultProcess(map1);
			if(defaultApprovalList.size()!=0){
				for(ApprovalType approvalType : defaultApprovalList){
					if(approvalType.getScene()<=2){
						approvalType.setIcon("/mobile/assets/src/css/img/formicons/"+approvalType.getIcon()+".png");
						approvalType.setProcessUrl(PropertiesUtil.getAppByKey("PROCESS_URL1")+"?typeId="+approvalType.getId()+"&from=index&wyyId="+wyyId+"&userId="+userId+"&mobile="+mobile+"&companyId="+companyId+"&DynamicDbName="+DynamicDbName);
					}else if(approvalType.getScene()>=3){
						approvalType.setIcon("/mobile/assets/src/css/img/formicons/"+approvalType.getIcon()+".png");
						approvalType.setProcessUrl(PropertiesUtil.getAppByKey("PROCESS_URL2")+"?typeId="+approvalType.getId()+"&from=index&wyyId="+wyyId+"&userId="+userId+"&mobile="+mobile+"&companyId="+companyId+"&DynamicDbName="+DynamicDbName);
					}
				}
			
				
			}
			collectList = approvalService.getCollectInfoByUserId(map1);
			if (collectList != null && collectList.size() > 0) { // 收藏
				for (int i = 0; i < collectList.size(); i++) {
					ApprovalType type = new ApprovalType();
					HashMap<String, String> hashmap = new HashMap<String, String>();
					hashmap.put("id", collectList.get(i).getApprovalTypeId());
					hashmap.put("companyId", companyId);
					hashmap.put("wyyId", wyyId);
					type = approvalService.getApprovalTypeById(hashmap);
					if (type != null)
						collectTypeList.add(type);
				}
				if(collectTypeList.size()!=0){
					for(ApprovalType approvalType : collectTypeList){
						if(approvalType.getScene()<=2){
							approvalType.setIcon("/mobile/assets/src/css/img/formicons/"+approvalType.getIcon()+".png");
							approvalType.setProcessUrl(PropertiesUtil.getAppByKey("PROCESS_URL1")+"?typeId="+approvalType.getId()+"&from=index&wyyId="+wyyId+"&userId="+userId+"&mobile="+mobile+"&companyId="+companyId+"&DynamicDbName="+DynamicDbName);
						}else if(approvalType.getScene()>=3){
							approvalType.setIcon("/mobile/assets/src/css/img/formicons/"+approvalType.getIcon()+".png");
							approvalType.setProcessUrl(PropertiesUtil.getAppByKey("PROCESS_URL2")+"?typeId="+approvalType.getId()+"&from=index&wyyId="+wyyId+"&userId="+userId+"&mobile="+mobile+"&companyId="+companyId+"&DynamicDbName="+DynamicDbName);
						}
					}
				
					
				}
				map.put("default", collectTypeList);
			} else { // 默认
				map.put("default", defaultApprovalList);
			}
			if(topApprovalList.size()!=0){
				for(ApprovalType approvalType : topApprovalList){
					if(approvalType.getScene()<=2){
						approvalType.setIcon("/mobile/assets/src/css/img/formicons/"+approvalType.getIcon()+".png");
						approvalType.setProcessUrl(PropertiesUtil.getAppByKey("PROCESS_URL1")+"?typeId="+approvalType.getId()+"&from=index&wyyId="+wyyId+"&userId="+userId+"&mobile="+mobile+"&companyId="+companyId+"&DynamicDbName="+DynamicDbName);
					}else if(approvalType.getScene()>=3){
						approvalType.setIcon("/mobile/assets/src/css/img/formicons/"+approvalType.getIcon()+".png");
						approvalType.setProcessUrl(PropertiesUtil.getAppByKey("PROCESS_URL2")+"?typeId="+approvalType.getId()+"&from=index&wyyId="+wyyId+"&userId="+userId+"&mobile="+mobile+"&companyId="+companyId+"&DynamicDbName="+DynamicDbName);
					}
				}
			
				
			}
			map.put("top", topApprovalList); // 置顶
			jsonresult.setModel(map);
			jsonresult.setSuccess(true);
		} catch (Exception e) {
			logger.info(e);
			jsonresult.setMessage("服务器错误");
			jsonresult.setSuccess(false);
		}
		return jsonresult;
	}
	
	
	/**
	 * 表单页
	 * 
	 * @return
	 */
	@RequestMapping("/toProcessForm")
	public String toForm(HttpServletRequest request,String userId,String mobile,String companyId,String DynamicDbName) {
		request.getSession().setAttribute("userId",userId);
		request.getSession().setAttribute("companyId",companyId) ;
		request.getSession().setAttribute("mobile",mobile) ;
		request.getSession().setAttribute("DynamicDbName",DynamicDbName) ;
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "microApp/mobile/form";
	}
	
	@RequestMapping("/toProcessForm-e3")
	public String toFormE3(HttpServletRequest request,String userId,String mobile,String companyId,String DynamicDbName) {
		request.getSession().setAttribute("userId",userId);
		request.getSession().setAttribute("companyId",companyId) ;
		request.getSession().setAttribute("mobile",mobile) ;
		request.getSession().setAttribute("DynamicDbName",DynamicDbName) ;
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "editionThird/form";
	}
}
