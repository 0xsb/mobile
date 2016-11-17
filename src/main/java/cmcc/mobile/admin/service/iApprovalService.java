package cmcc.mobile.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.ApprovalData;
import cmcc.mobile.admin.entity.ApprovalRunManage;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.MsgCompany;
import cmcc.mobile.admin.entity.Other;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.entity.ThirdApprovalStart;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.entity.UserApprovalType;
import cmcc.mobile.admin.vo.ProcessInfoVo;

public interface iApprovalService {

	/**
	 * 获取代办数量
	 * 
	 * @param record
	 */
	public List<ThirdApprovalDeal> getDealInfo(ThirdApprovalDeal record);

	/**
	 * 获取置顶流程
	 * 
	 * @return
	 */
	List<ApprovalType> getTopProcess(Map<String, Object> map1);

	/**
	 * 获取默认流程
	 * 
	 * @return
	 */
	List<ApprovalType> getDefaultProcess(Map<String, Object> map1);

	/**
	 * 获取收藏流程
	 * 
	 * @param id
	 * @return
	 */
	List<UserApprovalType> getCollectInfoByUserId(Map<String, Object> map1);

	ApprovalType getApprovalTypeById(HashMap<String, String> map);

	ApprovalType getApprovalTypeById2(HashMap<String, String> map);
	/**
	 * 根据table_config_id 获取具体配置表单信息
	 * 
	 * @param id
	 * @return
	 */
	List<ApprovalTableConfigDetails> getApprovalDetailsInfoById(String id);

	List<ApprovalTableConfigDetails> getApprovalDetailsInfoById(String id, String companyId, String mobile);

	JsonResult addOther(Other other);

	/**
	 * 通过userId获取人员
	 * 
	 * @param id
	 * @return
	 */
	User getUserById(String id);

	/**
	 * 获取我的发起列表
	 */
	List<ProcessInfoVo> getMeStartByUserId(HashMap<String,String> map);

	/**
	 * 获取yi办列表
	 * 
	 * @param record
	 * @return
	 */
	List<ProcessInfoVo> getApprovalListByUserId(ThirdApprovalDeal record);
	
	/**
	 * 获取待办列表
	 * 
	 * @param record
	 * @return
	 */
	List<ProcessInfoVo> getApprovalListByUserId2(ThirdApprovalDeal record);

	/**
	 * 根据流程ID获取流程信息
	 */

	ApprovalData getApprovalDataByFlowId(String flowId);

	/**
	 * 根据流程配置表ID获取流程配置表
	 */

	ApprovalTableConfig getApprovalTableConfigById(String id);

	/**
	 * 根据流程ID获取流程扭转数据
	 * 
	 * @param flowId
	 * @return
	 */

	List<ApprovalRunManage> getRunInfoByFlowId(String flowId);

	/**
	 * 开始流程
	 * 
	 * @param approvalIds
	 * @param model
	 * @param typeId
	 * @param defaultApprovalIds
	 * @param wyyId 
	 * @param wyyId 
	 * @param request
	 * @param taskId 任务的id用于区分是否是批量发起
	 * @return
	 */
	public JsonResult startProcess(String approvalIds, String model, String typeId, String defaultApprovalIds,
			String wyyId, HttpServletRequest request,String thirdId,Long taskId,String isBatch);

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
	public JsonResult ManageProcess(String flowid, String type, String message,HttpServletRequest request,String wyyId,
			String userid, String jsondata,String thirdId,String status,Long taskId);

	/**
	 * 已阅
	 * 
	 * @param flowId
	 * @param message
	 * @param request
	 * @return
	 */
	@RequestMapping("alRead")
	@ResponseBody
	public JsonResult alRead(String flowId, String message, HttpServletRequest request);

	List<ApprovalTableConfig> getInfoByTypeId(String approvalTypeId);

	List<ApprovalData> getInfoByApprovalTableConfigId(HashMap<String, Object> map);

	List<ApprovalRunManage> getAllInfo(String id);

	Customer getCustomerById(String id);
	
	
	JsonResult insertMsgSend(String mobile,String typeName);
	
	 List<MsgCompany> getMsgCompany(String companyId);

	public JsonResult getAntuarity(String companyId, String userId, String wyyId);

}
