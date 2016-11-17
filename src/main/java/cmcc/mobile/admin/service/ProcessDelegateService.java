package cmcc.mobile.admin.service;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.runtime.Job;

/**
 * 
 * @author chenghaotian
 *
 */
public interface ProcessDelegateService {

	/**
	 * 任务监听器： <br/>
	 * 在大部分普通任务创建时调用<br/>
	 * 获取分支中上的上个节点相关信息,设置执行人并添加待办
	 * 
	 * @param task
	 */
	void assignByVarOnTaskCreated(DelegateTask task);

	/**
	 * 多实例Collection表达式： <br/>
	 * 修改上个节点设置的办理人变量格式后返回给activiti(["user1","user2","user3"]-->[["user1"],[
	 * "user2"],["user3"]])
	 * 
	 * @param execution
	 * @return
	 */
	List<String> getAssigneeOnMultiInstanceCreated(DelegateExecution execution);

	/**
	 * sequenceFlow执行监听器： <br/>
	 * 在多实例任务结束时调用，查询汇总节点的办理人并设置变量到上级分支
	 * 
	 * @param execution
	 */
	void setAssigneeOnMultiInstanceTaked(DelegateExecution execution);

	/**
	 * sequenceFlow执行监听器 <br/>
	 * 设置下个任务执行人的变量值为前一个任务的执行人<br/>
	 * (例如:Task1(userA)->Task2(userB)->Task3(),
	 * 在task2完成后的设置分支的nextAssignee的值为userA)
	 * 
	 * @param execution
	 */
	void setAssigneeWithPerviousOnTaskTaked(DelegateExecution execution);

	void triggerTimeEvent(Job job, DelegateExecution execution);

	void triggerTimeEventEnd(Job job, DelegateExecution execution);
	
}
