package cmcc.mobile.admin.controller.chenght;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.SupervisionService;
import cmcc.mobile.admin.service.WorkFlowService;

/**
 * 
 * @author chenghaotian
 *
 */
@Controller
@RequestMapping("process")
public class ProcessController extends BaseController {

	@Autowired
	private WorkFlowService workFlowService;

	@Autowired
	private SupervisionService supervisionService;

	/**
	 * 回收任务至上一个节点
	 * 
	 * @param taskId
	 *            任务Id
	 * @return
	 */
	@RequestMapping("recovery")
	@ResponseBody
	public JsonResult recovery(String thirdDealId, String taskId, String opinion, String model) {
		JsonResult result = new JsonResult(true, null, null);
		String companyId = getCompanyId();
		String wyyId = getWyydId();
		Map<String, Object> params = JSONObject.parseObject(model, new TypeReference<Map<String, Object>>() {
		});
		workFlowService.recovery(thirdDealId, taskId, opinion, params, companyId, wyyId);
		// result.setModel(taskId);
		return result;
	}

	/**
	 * 重新指派任务
	 * 
	 * @param taskId
	 *            任务Id
	 * @param opinion
	 *            意见
	 * @param userId
	 *            新用户Id
	 * @param paramters
	 *            当前任务节点已输入数据
	 * @return
	 */
	@RequestMapping("reassign")
	@ResponseBody
	public JsonResult reassign(String thirdDealId, String taskId, String userId, String opinion, String model) {
		JsonResult result = new JsonResult(true, null, null);
		String companyId = getCompanyId();
		String wyyId = getWyydId();
		Map<String, Object> params = JSONObject.parseObject(model, new TypeReference<Map<String, Object>>() {
		});
		workFlowService.reassign(thirdDealId, taskId, userId, opinion, params, companyId, wyyId);
		// result.setModel(taskId);
		return result;
	}

	/**
	 * 拒绝当前任务,任务所属分支会结束，上级分支不受影响，多实例情况下只有一条分支会被结束，其他不受影响
	 * 
	 * @param taskId
	 *            任务id
	 * @param opinion
	 *            意见
	 * @param variables
	 *            任务相关变量
	 */
	@RequestMapping("reject")
	@ResponseBody
	public JsonResult reject(String thirdDealId, String taskId, String opinion, String model) {
		JsonResult result = new JsonResult(true, null, null);
		String companyId = getCompanyId();
		Map<String, Object> params = JSONObject.parseObject(model, new TypeReference<Map<String, Object>>() {
		});
		try {
			workFlowService.reject(thirdDealId, taskId, opinion, params, companyId);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 撤销任务,仅在第一步时可用，结束当前流程
	 * 
	 * @param taskId
	 *            任务id
	 * @param opinion
	 *            意见
	 */
	@RequestMapping("cancel")
	@ResponseBody
	public JsonResult cancel(String thirdDealId, String taskId, String opinion) {
		JsonResult result = new JsonResult(true, null, null);
		String companyId = getCompanyId();
		try {
			workFlowService.cancel(thirdDealId, taskId, opinion, companyId);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 查询我发起的某条流程的详细信息
	 * 
	 * @param taskId
	 *            任务id
	 * @return tasksList {@link cmcc.mobile.admin.entity.ActivitiHistoryTask
	 *         任务信息}列表，根据完成时间升序排列<br/>
	 *         tasksFormsData 最后一个完成的节点上的所有任务详细信息(List<Map<String,Object>)
	 * 
	 */
	@RequestMapping("processStartedByMe")
	@ResponseBody
	public JsonResult processStartedByMe(String taskId) {
		JsonResult result = new JsonResult(true, null, null);
		try {
			MultipleDataSource.setDataSourceKey(null);
			Map<String, Object> model = workFlowService.getProcessStartedByMe(taskId);
			result.setModel(model);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 跳转到自定义表单页面
	 * 
	 * @param formType
	 * @return
	 */
	@RequestMapping("/toCustomForm")
	public String toCustomForm(String formType) {
		switch (formType) {
		case "requestExtension":
			// 任务督办申请延期界面
			return "forward:/process/toRequestExtension.do";
		case "distributionExtension":
			// 公文流转分发按钮逻辑
			return "forward:/workflow/toDistributionExtension.do";
		default:
			return "";
		}
	}

	@RequestMapping("toRequestExtension")
	@ResponseBody
	public JsonResult toRequestExtension(String taskId, String extensionTaskKey) {
		Map<String, Object> requestExtensionForm = supervisionService.getRequestExtensionForm(taskId, extensionTaskKey);
		JsonResult result = new JsonResult(true, null, requestExtensionForm);
		return result;
	}

	@RequestMapping("requestExtension")
	@ResponseBody
	public JsonResult requestExtension(String taskId, String model, String opinion) {
		JsonResult result = new JsonResult(true, null, null);
		String userId = getUserId();
		String companyId = getCompanyId();
		String wyyId = getWyydId();
		supervisionService.requestExtension(taskId, userId, model, opinion, companyId, wyyId);
		result.setModel(taskId);
		return result;
	}

	@RequestMapping("getSupversionList")
	@ResponseBody
	public JsonResult getSupversionList() throws ParseException {
		String companyId = getCompanyId();
		String dbName = (String) request.getSession().getAttribute("DynamicDbName");
		MultipleDataSource.setDataSourceKey(dbName);
		List<Map<String, Object>> supversionList = supervisionService.getSupversionList(companyId);
		JsonResult result = new JsonResult(true, null, supversionList);
		return result;
	}

}
