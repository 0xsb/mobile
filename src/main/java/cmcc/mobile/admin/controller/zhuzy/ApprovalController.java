package cmcc.mobile.admin.controller.zhuzy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ApprovalAuthorityMapper;
import cmcc.mobile.admin.entity.ApprovalData;
import cmcc.mobile.admin.entity.ApprovalRunManage;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.MsgCompany;
import cmcc.mobile.admin.entity.Other;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.entity.UserApprovalType;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.iApprovalService;
import cmcc.mobile.admin.vo.ProcessInfoVo;

@Controller
@RequestMapping("/approval")
public class ApprovalController extends BaseController {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private iApprovalService approvalService;
	@Autowired
	private ApprovalAuthorityMapper approvalAuthorityMapper;

	/**
	 * 获取待办信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("getInfoCount")
	@ResponseBody
	public JsonResult getInfoCount(HttpServletRequest request, String wyyId) {
		JsonResult jsonresult = new JsonResult();
		String userId = request.getSession().getAttribute("userId").toString();
		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
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
	 * 获取默认流程 置顶流程
	 * 
	 * @return
	 */
	@RequestMapping("getDefaultProcess")
	@ResponseBody
	public JsonResult getDefaultProcess(HttpServletRequest request,
			@RequestParam(value = "wyyId", defaultValue = "wyy0001") String wyyId) {
		Map<String, Object> map1 = new HashMap<>();
		Map<String, Object> map2 = new HashMap<>();
		String userId = request.getSession().getAttribute("userId").toString();
		String companyId = request.getSession().getAttribute("companyId").toString();
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
				map.put("default", collectTypeList);
			} else { // 默认
				map.put("default", defaultApprovalList);
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
	 * 根据流程类型获取表单信息
	 * 
	 * @param typeid
	 *            流程类型ID
	 * @return
	 */
	@RequestMapping("getApprovalTableInfo")
	@ResponseBody
	public JsonResult reportdata(String typeId, HttpServletRequest request) {

		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		JsonResult jsonresult = new JsonResult();
		List<ApprovalTableConfigDetails> approvaldetailslist = new ArrayList<ApprovalTableConfigDetails>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			HashMap<String, String> hashmap = new HashMap<String, String>();
			hashmap.put("id", typeId);
			hashmap.put("companyId", companyId);
			ApprovalType approvalType = new ApprovalType();
			approvalType = approvalService.getApprovalTypeById2(hashmap);

			String typename = approvalType.getName();
			String icon = approvalType.getIcon();
			String table_config_id = approvalType.getApprovalTableConfigId();
			approvaldetailslist = approvalService.getApprovalDetailsInfoById(table_config_id, companyId, getMobile());
			map.put("typename", typename);
			map.put("icon", icon);
			map.put("forminfo", approvaldetailslist);
			jsonresult.setModel(map);
			jsonresult.setSuccess(true);

		} catch (Exception e) {
			logger.info(e);
			jsonresult.setSuccess(false);
			jsonresult.setMessage("服务器错误");
		}
		return jsonresult;
	}

	/**
	 * 
	 * @param message
	 * @return
	 * 
	 */
	@RequestMapping("addOther")
	@ResponseBody
	public JsonResult addOther(String message) {
		SimpleDateFormat sfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JsonResult json = new JsonResult();
		Other other = new Other();
		other.setMessage(message);
		other.setCreateTime(sfmt.format(new Date()));
		json = approvalService.addOther(other);
		return json;
	}

	/**
	 * 待办 已办 待阅 发起
	 * 
	 * 代办 已办 搜索功能 7.14
	 * 
	 */
	@RequestMapping("getProcessInfoList")
	@ResponseBody
	public JsonResult getProcessInfoList(String type, String wyyId, String pageSize, String page, String term,
			HttpServletRequest request) {
		if (wyyId == null || wyyId.equals("")) {
			wyyId = "wyy0001";
		}
		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		JsonResult jsonresult = new JsonResult();
		String userId = request.getSession().getAttribute("userId").toString();
		List<ProcessInfoVo> list = new ArrayList<ProcessInfoVo>();
		PageInfo pageInfo = new PageInfo();

		try {
			if ("wdfq".equals(type)) { // 我的发起
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("userId", userId);
				map.put("companyId", companyId);
				map.put("wyyId", wyyId);
				if (!term.equals("")) {
					map.put("approvalName", term);
				}

				PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
				list = approvalService.getMeStartByUserId(map);// 获取我发起的列表
				pageInfo = new PageInfo(list);

			} else if ("dwsp".equals(type)) {// 待我审批
				ThirdApprovalDeal deal = new ThirdApprovalDeal();
				deal.setWyyId(wyyId);
				deal.setUserId(userId);
				deal.setStatus("1");
				deal.setNodeStatus("1");
				deal.setCompanyId(companyId);

				if (!term.equals("")) {
					deal.setApprovalName(term);
				}

				PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
				list = approvalService.getApprovalListByUserId2(deal);
				pageInfo = new PageInfo(list);

			} else if ("dwyl".equals(type)) {// 待我阅览
				ThirdApprovalDeal deal = new ThirdApprovalDeal();
				deal.setUserId(userId);
				deal.setStatus("2");// 流程完成
				deal.setNodeStatus("3");// 未阅
				deal.setWyyId(wyyId);
				if (!term.equals("")) {
					deal.setApprovalName(term);
				}

				PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
				list = approvalService.getApprovalListByUserId(deal);
				pageInfo = new PageInfo(list);
			} else if ("wysp".equals(type)) {// 我已审批
				ThirdApprovalDeal deal = new ThirdApprovalDeal();
				deal.setUserId(userId);
				deal.setNodeStatus("2");
				deal.setWyyId(wyyId);
				if (!term.equals("")) {
					deal.setApprovalName(term);
				}

				PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
				list = approvalService.getApprovalListByUserId(deal);
				pageInfo = new PageInfo(list);
			}
		} catch (Exception e) {
			logger.info(e);
			jsonresult.setSuccess(false);
			jsonresult.setMessage("服务器异常");
		}
		jsonresult.setSuccess(true);
		jsonresult.setModel(pageInfo);
		return jsonresult;
	}

	/**
	 * 获取流程详细信息
	 */
	@RequestMapping("getApprovalInfo")
	@ResponseBody
	public JsonResult getApprovalInfo(String flowid, HttpServletRequest request, String status, String thirdId) {
		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		HashMap<String, String> hashmap = new HashMap<String, String>();
		JsonResult jsonresult = new JsonResult();
		try {
			ApprovalData approvalData = new ApprovalData();
			ApprovalTableConfig approvalTableConfig = new ApprovalTableConfig();
			ApprovalType approvalType = new ApprovalType();
			approvalData = approvalService.getApprovalDataByFlowId(flowid);
			String status2 = approvalData.getIsDefinition(); // 流程 可转发 不可转发
			String editstatus = approvalData.getStatus();
			String configId = approvalData.getApprovalTableConfigId();
			approvalTableConfig = approvalService.getApprovalTableConfigById(configId); // 获取流程配置表
			String typeId = approvalTableConfig.getApprovalTypeId();// 获取流程Id
			hashmap.put("companyId", companyId);
			hashmap.put("id", typeId);
			approvalType = approvalService.getApprovalTypeById2(hashmap);
			String typename = approvalType.getName();// 获取流程名字
			List<ApprovalTableConfigDetails> detailsList = approvalService.getApprovalDetailsInfoById(configId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("table", detailsList);
			map.put("typename", typename);
			map.put("json", JSON.parseObject(approvalData.getJsonData()));
			List<ApprovalRunManage> runList = approvalService.getRunInfoByFlowId(flowid); // 获取流程扭转数据
			map.put("processrecord", runList);
			map.put("status", status2);// 0 自由 1固定
			map.put("editstatus", editstatus);// 2 可更改 3未填写
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
	 * 开始流程
	 * 
	 * @param approvalIds
	 *            审批人
	 * @param model
	 *            表单数据
	 * @param typeId
	 *            流程ID
	 * @return
	 */
	@RequestMapping("startProcess")
	@ResponseBody
	public JsonResult startProcess(String approvalIds, String model, String typeId, String wyyId,
			String defaultApprovalIds, HttpServletRequest request, String thirdId, Long taskId,String isBatch) {
		if (wyyId == null || wyyId.equals("")) {
			wyyId = "wyy0001";
		}
		JsonResult json = new JsonResult();
		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		json = approvalService.startProcess(approvalIds, model, typeId, wyyId, defaultApprovalIds, request, thirdId,
				taskId,isBatch);
		if (json.getSuccess() == false) {
			return json;
		}
		MultipleDataSource.setDataSourceKey("");
		List<MsgCompany> list = approvalService.getMsgCompany(companyId);
		if (list != null && list.size() > 0) {
			Map<String, String> map = (Map) json.getModel();
			String mobile = map.get("mobile");
			String typeName = map.get("typeName");
			MultipleDataSource.setDataSourceKey("");
			json = approvalService.insertMsgSend(mobile, typeName);
		}
		return json;
	}

	/**
	 * 办理流程
	 * 
	 * @param flowid
	 * @param type
	 * @param message
	 * @param request
	 * @param userid
	 * @param jsondata
	 * @return
	 */
	@RequestMapping("ManageProcess")
	@ResponseBody
	public JsonResult ManageProcess(String flowid, String type, String message, String wyyId,
			HttpServletRequest request, String userid, String jsondata, String thirdId, String status, Long taskId) {
		if (wyyId == null || wyyId.equals("")) {
			wyyId = "wyy0001";
		}
		JsonResult json = new JsonResult();
		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		json = approvalService.ManageProcess(flowid, type, message, request, userid, wyyId, jsondata, thirdId, status,
				taskId);
		if (json.getSuccess() == false || json.getModel() == null) {
			return json;
		}

		MultipleDataSource.setDataSourceKey("");
		List<MsgCompany> list = approvalService.getMsgCompany(companyId);
		if (list != null && list.size() > 0) {
			Map<String, String> map = (Map) json.getModel();
			String mobile = map.get("mobile");
			String typeName = map.get("typeName");

			json = approvalService.insertMsgSend(mobile, typeName);
		}
		return json;

	}

	/**
	 * 已阅接口
	 * 
	 * @param flowId
	 * @param message
	 * @param request
	 * @return
	 */
	@RequestMapping("alRead")
	@ResponseBody
	public JsonResult alRead(String flowId, String message, String wyyId, HttpServletRequest request) {
		JsonResult jsonresult = new JsonResult();
		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		jsonresult = approvalService.alRead(flowId, message, request);
		return jsonresult;
	}

	/**
	 * 获取上次流程审批人
	 * 
	 * @param typeId
	 * @param request
	 * @return
	 */

	@RequestMapping("getLastApprovalIds")
	@ResponseBody
	public JsonResult getLastApprovalIds(String typeId, HttpServletRequest request) {
		JsonResult json = new JsonResult();
		String userId = request.getSession().getAttribute("userId").toString();
		String companyId = request.getSession().getAttribute("companyId").toString();
		MultipleDataSource.setDataSourceKey("");
		String DynamicDbName = approvalService.getCustomerById(companyId).getDbname();
		MultipleDataSource.setDataSourceKey(DynamicDbName);
		List<ApprovalTableConfig> configList = new ArrayList<ApprovalTableConfig>();
		List<ApprovalData> dataList = new ArrayList<ApprovalData>();
		List<Map<String, Object>> maplist = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<ApprovalRunManage> runList = new ArrayList<ApprovalRunManage>();
		User user = new User();
		try {
			configList = approvalService.getInfoByTypeId(typeId);
			List<String> list = new ArrayList<String>();
			if (configList != null && configList.size() > 0) {
				for (int i = 0; i < configList.size(); i++) {
					list.add(configList.get(i).getId());
				}
				map.put("userId", userId);
				map.put("list", list);
				dataList = approvalService.getInfoByApprovalTableConfigId(map);
				if (dataList != null && dataList.size() > 0) {
					String flowId = dataList.get(0).getFlowId();
					runList = approvalService.getAllInfo(flowId);
					for (int i = 1; i < runList.size(); i++) {
						map = new HashMap<String, Object>();
						String uId = runList.get(i).getUserId();
						user = approvalService.getUserById(uId);
						map.put("id", user.getId());
						map.put("name", user.getUserName());
						maplist.add(map);
					}
					json.setSuccess(true);
					json.setModel(maplist);
				} else {
					json.setSuccess(true);
					json.setModel(maplist);
				}
			} else {
				json.setSuccess(true);
				json.setMessage("获取数据失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMessage("服务器异常");
		}

		return json;
	}

	// 获取发起权限
	//获取报表权限
	@RequestMapping("/getAntuarity")
	@ResponseBody
	public JsonResult getAntuarity(HttpServletRequest request,
			@RequestParam(value = "wyyId", defaultValue = "wyy0002") String wyyId) {
		String companyId = getCompanyId();
		String userId = getUserId();

		if (StringUtils.isNotEmpty(companyId) && StringUtils.isNotEmpty(userId)) {
			return approvalService.getAntuarity(companyId, userId, wyyId);
		}
		return new JsonResult(false, "非法请求", null);
	}

}
