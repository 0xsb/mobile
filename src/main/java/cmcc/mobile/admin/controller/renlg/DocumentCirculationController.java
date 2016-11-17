package cmcc.mobile.admin.controller.renlg;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.service.DocumentCirculationService;

/**
 * 公文传阅控制层
 * 
 * @author renlinggao
 * @Date 2016年9月19日
 */
@Controller
@RequestMapping("document")
public class DocumentCirculationController extends BaseController {

	@Autowired
	private DocumentCirculationService documentCirculationService;

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
		Map<String, Object> users = documentCirculationService.agree(userId, taskId, params, opinion);
		result.setModel(users);
		return result;
	}

	/**
	 * 选择下一步任务
	 * 
	 * @param taskId
	 * @param model
	 * @param nextAssignee
	 * @return
	 */
	@RequestMapping("selectNextTask")
	@ResponseBody
	public JsonResult selectNextTask(String taskId, String nextTaskDefId) {
		String userId = getUserId();
		JsonResult result = new JsonResult(true, null, null);
		Map<String, Object> users = documentCirculationService.selectTask(taskId, nextTaskDefId, userId);
		result.setModel(users);
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
		Map resultMap = documentCirculationService.startProcess(companyId, userId, key, params, nextAssignee, next,
				getWyydId());
		result.setModel(resultMap);
		return result;
	}
	
	/**
	 * 查询历史流程全局的变量
	 * @param taskId
	 * @return
	 */
	@RequestMapping("getHisValues")
	@ResponseBody
	public JsonResult getHisValues(String taskId){
		JsonResult result = new JsonResult(true,null,null);
		Map<String, Object> processValues = documentCirculationService.getHisProcessValues(taskId);
		result.setModel(processValues);
		return result;
	}

}
