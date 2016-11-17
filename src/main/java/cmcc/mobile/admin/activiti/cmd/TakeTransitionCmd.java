package cmcc.mobile.admin.activiti.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;

public class TakeTransitionCmd implements Command<Void> {
	private String executionId;
	private String reason = "";
	private String activityId;

	public TakeTransitionCmd(String executionId, String activityId) {
		this.executionId = executionId;
		this.activityId = activityId;
	}

	public TakeTransitionCmd(String executionId, String activityId, String reason) {
		this.executionId = executionId;
		this.activityId = activityId;
		this.reason = reason;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		// 获取分支
		ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(executionId);
		// 获取目标节点
		ActivityImpl destination = executionEntity.getProcessDefinition().findActivity(activityId);
		if (executionEntity.getExecutions().isEmpty()) { // 不是跨子流程重置
			// 修改当前节点transition
			ActivityImpl activityImpl = executionEntity.getActivity();
			TransitionImpl transition = activityImpl.createOutgoingTransition();
			transition.setDestination(destination);
			executionEntity.setTransition(transition);

			TaskEntity taskEntity = commandContext.getTaskEntityManager().findTasksByExecutionId(executionId).get(0);
			// 删除当前节点任务
			executionEntity.removeTask(taskEntity);
			taskEntity.setExecution(null);
			taskEntity.setExecutionId(null);
			taskEntity.setProcessInstanceId(null);
			commandContext.getTaskEntityManager().deleteTask(taskEntity.getId(), reason, false);

			// 手动触发事件
			executionEntity.performOperation(AtomicOperation.TRANSITION_NOTIFY_LISTENER_TAKE);

			// 还原节点transition
			activityImpl.getOutgoingTransitions().remove(transition);
		} else { // 跨子流程重置
			ExecutionEntity subProcess = executionEntity.getExecutions().get(0);

			// 修改当前节点transition
			ActivityImpl activityImpl = subProcess.getActivity();
			TransitionImpl transition = activityImpl.createOutgoingTransition();
			transition.setDestination(destination);
			subProcess.setTransition(transition);

			// 清空子流程
			subProcess.destroyScope(reason);
			subProcess.performOperation(AtomicOperation.TRANSITION_DESTROY_SCOPE);

			// 还原节点transition
			activityImpl.getOutgoingTransitions().remove(transition);
		}
		return null;
	}

}
