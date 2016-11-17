package cmcc.mobile.admin.controller.renlg;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.WorkFlowService;

/**
 *
 * @author renlinggao
 * @Date 2016年7月28日
 */
@Controller
@RequestMapping("workflow")
public class WorkFlowController extends BaseController {
	@Autowired
	private WorkFlowService workFlowService;

	/**
	 * 获取开始表单信息
	 * 
	 * @param key
	 * @return
	 */
	@RequestMapping("getStartForm")
	@ResponseBody
	public JsonResult getStartForm(String key) {
		JsonResult result = new JsonResult(true, null, null);
		String companyId = getCompanyId();
		String userId = getUserId();
		Map<String, Object> startTaskInfo = workFlowService.getStartNodeInfo(companyId, userId, key);
		result.setModel(startTaskInfo);
		return result;
	}

	/**
	 * 开始流程
	 * 
	 * @param key
	 * @param model
	 * @param nextAssignee
	 * @param next
	 * @return
	 */
	@RequestMapping("startProcess")
	@ResponseBody
	public JsonResult startProcess(String key, String model, String nextAssignee, String next) {
		JsonResult result = new JsonResult(true, null, null);
		String companyId = getCompanyId();
		String userId = getUserId();
		Map params = JSONObject.parseObject(model, Map.class);
		Map resultMap = workFlowService.startProcess(companyId, userId, key, params, nextAssignee, next, getWyydId());
		result.setModel(resultMap);
		return result;
	}

	/**
	 * 同意和提交按钮
	 * 
	 * @param taskId
	 * @param model
	 * @param nextAssignee
	 * @return
	 */
	@RequestMapping("agree")
	@ResponseBody
	public JsonResult agree(String taskId, String model, String opinion) {
		String userId = getUserId();
		JsonResult result = new JsonResult(true, null, null);
		Map params = JSONObject.parseObject(model, Map.class);
		Map<String, Object> users = workFlowService.agree(userId, taskId, params, opinion);
		result.setModel(users);
		return result;
	}

	/**
	 * 选择人员
	 * 
	 * @param taskId
	 * @param model
	 * @param nextAssignee
	 * @return
	 */
	@RequestMapping("selectUser")
	@ResponseBody
	public JsonResult selectUser(String thirdDealId, String taskId, String nextAssignee, String opinion) {
		String userId = getUserId();
		JsonResult result = new JsonResult(true, null, null);
		workFlowService.selectUser(thirdDealId, userId, taskId, nextAssignee, getCompanyId(), getWyydId());
		return result;
	}

	/**
	 * 流程详情
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping("taskDetail")
	@ResponseBody
	public JsonResult taskDetail(String taskId) {
		JsonResult result = new JsonResult(true, null, null);
		MultipleDataSource.setDataSourceKey(null);
		Map<String, Object> taskInfo = workFlowService.taskDetails(taskId);
		result.setModel(taskInfo);
		return result;
	}

	/**
	 * 分发按钮逻辑
	 * 
	 * @return
	 */
	@RequestMapping("toDistributionExtension")
	@ResponseBody
	public JsonResult distributionExtension(String taskId,@RequestParam(value="typeId",required=true)String typeId) {
		JsonResult result = new JsonResult(true, null, null);
		workFlowService.postingEndEvent(taskId,typeId);
		return result;
	}

}
