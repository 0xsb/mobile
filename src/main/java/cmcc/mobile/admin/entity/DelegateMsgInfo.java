package cmcc.mobile.admin.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cmcc.mobile.admin.base.ActivitiConstant;

/**
 * 组装分支变量，获取前一个任务相关信息工具类
 * 
 * @author chenghaotian
 *
 */
public class DelegateMsgInfo {

	private List<String> assignee;

	private String companyId;

	private String wyyId;

	private String taskId;

	private String userId;

	private String owner;

	private Map<String, Object> model;

	private static List<String> variableNames = Arrays.asList(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO,
			ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE);

	public DelegateMsgInfo(DelegateExecution execution) {
		Map<String, Object> variables = execution.getVariables(variableNames);
		init(variables);
	}

	public DelegateMsgInfo(String executionId, RuntimeService runtimeService) {
		Map<String, Object> variables = runtimeService.getVariables(executionId, variableNames);
		init(variables);
	}

	private void init(Map<String, Object> variables) {
		String msgInfoJson = Objects.toString(variables.get(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO), null);
		String assigneeJson = Objects.toString(variables.get(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE), null);

		if (StringUtils.isNotEmpty(msgInfoJson)) {
			Map<String, Object> msgInfo = JSON.parseObject(msgInfoJson, new TypeReference<Map<String, Object>>() {
			});
			companyId = Objects.toString(msgInfo.get("companyId"), null);
			wyyId = Objects.toString(msgInfo.get("wyyId"), null);
			taskId = Objects.toString(msgInfo.get("taskId"), null);
			userId = Objects.toString(msgInfo.get("userId"), null);
			owner = Objects.toString(msgInfo.get("owner"), null);
			model = JSONObject.parseObject(Objects.toString(msgInfo.get("model"), "{}"),
					new TypeReference<Map<String, Object>>() {
					});
		}

		if (StringUtils.isNotEmpty(assigneeJson)) {
			assignee = JSON.parseObject(assigneeJson, new TypeReference<List<String>>() {
			});
		}
	}

	public List<String> getAssignee() {
		return assignee;
	}

	public String getCompanyId() {
		return companyId;
	}

	public String getWyyId() {
		return wyyId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getUserId() {
		return userId;
	}

	public String getOwner() {
		return owner;
	}

	public Map<String, Object> getModel() {
		return model;
	}

}
