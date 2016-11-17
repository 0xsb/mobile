package cmcc.mobile.admin.service;

import java.util.Map;

import org.activiti.engine.task.Task;

/**
 * 
 * @author chenghaotian
 *
 */
public interface ProcessService {
	/**
	 * 回收任务至上一个节点
	 * 
	 * @param taskId
	 *            任务Id
	 * @param opinion
	 *            意见
	 * @param opinion
	 * @param params 
	 * @param companyId
	 */
	void recovery(Task task, String opinion, Map<String, Object> params, Map<String, Object> msgInfo);

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
	void reassign(Task task, String assignee, String opinion, Map<String, Object> variables,
			Map<String, Object> msgInfo);

	/**
	 * 拒绝当前任务,任务所属分支会结束，上级分支不受影响，多实例情况下只有一条分支会被结束，其他不受影响
	 * 
	 * @param taskId
	 *            任务id
	 * @param opinion
	 *            意见
	 * @param variables
	 *            任务相关变量
	 * @return
	 */
	Task reject(String taskId, String opinion, Map<String, Object> variables);

	/**
	 * 撤销任务,仅在第一步时可用，结束当前流程
	 * 
	 * @param taskId
	 *            任务id
	 * @param opinion
	 *            意见
	 * @return
	 */
	String cancel(String taskId, String opinion);

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
	Map<String, Object> getProcessStartedByMe(String taskId);

}
