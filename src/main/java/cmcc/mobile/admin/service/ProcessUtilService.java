package cmcc.mobile.admin.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.task.Task;

import cmcc.mobile.admin.entity.ActivitiHistoryTask;
import cmcc.mobile.admin.entity.DelegateMsgInfo;

/**
 * 
 * @author chenghaotian
 *
 */
public interface ProcessUtilService {

	/**
	 * 获取一个未完成的任务，如果未查询到，会抛出异常
	 * 
	 * @param taskId
	 * @return
	 */
	Task getTask(String taskId);

	Map<String, String> getStartFormData(String processDefinitionId);

	/**
	 * 根据流程定义Id和任务节点id获取当期节点的taskConfig
	 * 
	 * @param processDefinitionId
	 * @param taskDefinitionKey
	 * @return
	 */
	Map<String, Object> getTaskConfig(Map<String, String> startFormData, String taskDefinitionKey);

	Map<String, Object> getTaskConfig(String processDefinitionId, String taskDefinitionKey);

	/**
	 * 根据流程定义Id和表单id获取表单详细信息
	 * 
	 * @param processDefinitionId
	 * @param taskDefinitionKey
	 * @return
	 */
	Map<String, Object> getFormDetail(Map<String, String> startFormData, String formId);

	/**
	 * 查询当前流程下所有任务(包括已完成和正在处理的)，按开始时间升序排列
	 * 
	 * @param processInstanceId
	 * @return
	 */
	List<ActivitiHistoryTask> getHistoryTasks(String processInstanceId);

	/**
	 * 获取当前任务的下一个节点
	 * 
	 * @param taskId
	 * @return 任务节点id(taskDefinitionKey)
	 */
	String getNextTask(String taskId);

	/**
	 * 获取当前任务的前一个节点的历史任务
	 * 
	 * @param taskId
	 * @return
	 */
	List<HistoricTaskInstance> getPerviousTasks(String taskId);

	String getNextTask(DelegateExecution execution);

	void changeTimerToSleepMode(TimerEntity job, String interval);

	Map<String, Object> getServiceVariables(Map<String, Object> variables, Map<String, Object> formdetail);

	Set<String> getProcessDefinitionKeys(Map<String, Object> params);

	String formatAssigneeWildcard(String temp, DelegateMsgInfo msgInfo);

	/**
	 * 获取下一个节点，遇到由用户选择的判断网关时返回所有目标节点
	 * @param taskId
	 * @return
	 */
	Map<String, Object> getNextActivity(String taskId);

}
