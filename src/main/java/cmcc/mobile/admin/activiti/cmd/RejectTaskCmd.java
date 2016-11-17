package cmcc.mobile.admin.activiti.cmd;

import java.util.Map;

import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelGatewayActivityBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import cmcc.mobile.admin.base.ActivitiConstant;

public class RejectTaskCmd implements Command<Void> {

	private String taskId;
	private String reason;
	private Map<String, Object> variables;

	public RejectTaskCmd(String taskId, String reason, Map<String, Object> variables) {
		this.taskId = taskId;
		this.reason = reason;
		this.variables = variables;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		TaskEntity taskEntity = commandContext.getTaskEntityManager().findTaskById(taskId);
		if (taskEntity == null) {
			throw new RuntimeException("未查询到任务");
		}

		ExecutionEntity execution = taskEntity.getExecution();
		ActivityImpl activity = taskEntity.getExecution().getActivity();
		ActivityBehavior behavior = activity.getActivityBehavior();

		ExecutionEntity parent = execution.getParent();
		ActivityBehavior parentBehavior = null;
		if (parent != null && parent.getActivity() != null) {
			parentBehavior = parent.getActivity().getActivityBehavior();
		}

		taskEntity.setVariablesLocal(variables);
		execution.destroyScope(reason);

		try {
			if (behavior instanceof MultiInstanceActivityBehavior) {
				// 多实例任务(不包括多实例子流程)
				MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) behavior;
				multiInstanceActivityBehavior.completed(execution);
			} else if (parentBehavior != null && parentBehavior instanceof ParallelGatewayActivityBehavior) {
				// 当前任务处在的并行分支中
				throw new RuntimeException("当前节点暂不支持拒绝");
			} else {
				execution.getProcessInstance().setVariableLocal(ActivitiConstant.PROCESS_STATUS,
						ActivitiConstant.PROCESS_STATUS_REFUSE);
				// 普通流程、(多实例)子流程、等等
				execution.end();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return null;
	}

}
