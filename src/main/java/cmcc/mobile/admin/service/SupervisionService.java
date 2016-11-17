package cmcc.mobile.admin.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

/**
 * 
 * @author chenghaotian
 *
 */
public interface SupervisionService {

	/**
	 * 任务监听器: <br/>
	 * 督办审核及延期审核任务创建时调用，设置候选人为任务执行人，同时添加待办消息
	 * 
	 * @param task
	 */
	void assignByCandidatesOnTaskCreated(DelegateTask task);

	/**
	 * 执行监听器:<br/>
	 * 定时器创建时调用，设置定时器的开始时间，重复次数，重复间隔时间
	 * 
	 * @param execution
	 * @param endDateVariableName
	 *            结束时间的<b>变量名</b>
	 * @param hourVariableName
	 *            提前提醒时间(小时)的<b>变量名</b>
	 * @return
	 * @throws ParseException
	 */
	String getTimerEndDate(DelegateExecution execution, String endDateVariableName, String hourVariableName)
			throws ParseException;

	/**
	 * 多实例表达式： <br/>
	 * 在多实例节点创建和<b>完成</b>时触发，第一次触发时会拆分执行者列表为多个list
	 * 
	 * @param execution
	 * @param mainVarName
	 *            主办人员的<b>变量名</b>
	 * @param subVarName
	 *            协办人员的<b>变量名</b>
	 * @return 多实例的列表，元素为用户id
	 */
	List<String> getAssigneeOnSupervisionStart(DelegateExecution execution, String mainVarName, String subVarName);

	/**
	 * 多实例表达式:<br/>
	 * 在多实例节点的每个任务完成时触发，判断主办人的任务是否完成。完成时清理其他任务的代表信息并返回true
	 * 
	 * @param execution
	 * @param mainVarName
	 * @param subVarName
	 * @return
	 */
	Boolean isSupervisionCanComplete(DelegateExecution execution, String mainVarName, String subVarName);

	/**
	 * 执行监听器:<br/>
	 * 督办任务子流程结束时触发，清理定时作业和任务，同时向上级分支传递assignee和msgInfo变量
	 * 
	 * @param execution
	 */
	void onSupervisionEnd(DelegateExecution execution);

	/**
	 * 任务监听器:<br/>
	 * 在延期审核同意时触发，重新计算并修改定时任务的到期时间
	 * 
	 * @param task
	 * @param endDateVariableName
	 * @param hourVariableName
	 * @throws ParseException
	 */
	void changeEndDate(DelegateTask task, String endDateVariableName, String hourVariableName) throws ParseException;

	/**
	 * 服务任务表达式:<br/>
	 * 在定时器触发后触发，发送短信提醒
	 * 
	 * @param execution
	 * @throws ParseException
	 */
	void notifyOnSupervisionExperied(DelegateExecution execution) throws ParseException;

	/**
	 * 获取申请延期表单
	 * 
	 * @param taskId
	 * @param extensionTaskKey
	 * @return
	 */
	Map<String, Object> getRequestExtensionForm(String taskId, String extensionTaskKey);

	/**
	 * 申请延期
	 * 
	 * @param taskId
	 * @param userId
	 * @param model
	 * @param reason
	 * @param companyId
	 * @param wyyId
	 */
	void requestExtension(String taskId, String userId, String model, String reason, String companyId, String wyyId);

	/**
	 * 任务监听器:<br/>
	 * 在任务创建时触发，自动完成任务并设置变量，变量来自分支中的msgInfo属性
	 * 
	 * @param task
	 * @throws InterruptedException
	 */
	void completeOnTaskCreated(DelegateTask task) throws InterruptedException;

	/**
	 * 督办任务公示列表
	 * 
	 * @param companyId
	 * @return
	 * @throws ParseException
	 */
	List<Map<String, Object>> getSupversionList(String companyId) throws ParseException;

}
