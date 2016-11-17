package cmcc.mobile.admin.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import cmcc.mobile.admin.entity.ActivitiHistoryTask;

/**
 *
 * @author renlinggao
 * @Date 2016年7月28日
 */
public interface WorkFlowService {
	/**
	 * 打开表单的返回给前端的信息
	 * 
	 * @param userId
	 * @param key
	 * @return
	 */
	public Map<String, Object> getStartNodeInfo(String companyId, String userId, String key);

	/**
	 * 提交表单
	 * 
	 * @param userId
	 * @param key
	 * @param params
	 * @param nextAssignee
	 * @return
	 */
	public Map<String, Object> startProcess(String companyId, String userId, String key, Map<String, Object> params,
			String nextAssignee, String next, String wyyId);

	/**
	 * 按钮逻辑(同意 提交)
	 */
	public Map<String, Object> agree(String userId, String taskId, Map<String, Object> params, String opinionTxt);

	/**
	 * 获取表单信息
	 * 
	 * @param taskId
	 * @return
	 */
	public Map<String, Object> taskDetails(String taskId);

	/**
	 * 流程选择人员
	 * 
	 * @param taskId
	 * @param nextAssignee
	 * @return
	 */
	public boolean selectUser(String dealId, String userId, String taskId, String nextAssignee, String companyId,
			String wyyId);

	/**
	 * 获取一个集合的用户提交的数据和表单
	 * 
	 * @param tasks
	 * @param tasksConfig
	 *            开始节点的流程配置
	 * @return
	 */
	public List<Map<String, Object>> getTasksFormsData(List<String> tasks, ProcessDefinition processDefinition);

	public String addAgentMessage(String userId, String startUserId, String approvalName, String companyId,
			String processInstanceId, String taskId, String wyyId);

	public String addAgentMessage(Task task, ProcessDefinition ProcessDefinition, String companyId, String wyyId);

	public String addStartMessage(String userId, String startUserId, String approvalName, String companyId,
			String processInstanceId, String taskId, String wyyId);

	public void updateStartMessage(String processInstanceId, String status);

	public void updateDealMessage(String id, String processInstanceId, String nodeStatus, String status);

	List<String> getLastHistoryTasks(String taskId, List<ActivitiHistoryTask> list);

	/**
	 * 回收任务至上一个节点
	 * 
	 * @param taskId
	 *            任务Id
	 * @param opinion
	 *            意见
	 * @param opinion
	 * @param params
	 * @return
	 */
	public void recovery(String thirdDealId, String taskId, String opinion, Map<String, Object> params,
			String companyId, String wyyId);

	/**
	 * 重新指派任务
	 * 
	 * @param taskId
	 *            任务Id
	 * @param opinion
	 *            意见
	 * @param userId
	 *            指派用户Id
	 * @return
	 */
	void reassign(String thirdDealId, String taskId, String assignee, String opinion, Map<String, Object> variables,
			String companyId, String wyyId);

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
	void reject(String thirdDealId, String taskId, String opinion, Map<String, Object> variables, String companyId);

	Map<String, Object> getProcessStartedByMe(String taskId);

	public void cancel(String thirdDealId, String taskId, String opinion, String companyId);

	/**
	 * 发文结束事件
	 * 
	 * @param execution
	 */
	public void postingEndEvent(String taskId, String typeId);

	/**
	 * 人员预约领导同意生成二维码事件
	 * 
	 * @param execution
	 */
	public void reservePersonnelTaked(DelegateTask task);
}
