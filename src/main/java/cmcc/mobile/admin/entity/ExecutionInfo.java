package cmcc.mobile.admin.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.apache.commons.lang3.StringUtils;

import cmcc.mobile.admin.base.ActivitiConstant;

/**
 * 组装流程历史节点工具类
 * 
 * @author chenghaotian
 *
 */
public class ExecutionInfo {

	/** 当前为分支时节点是executionId,为任务节点时是taskId */
	private String id;

	/** 是否为任务节点 */
	private boolean isTask;

	/* 当前对象是否为只包含分支的容器分支 */
	private boolean isScope = true;

	/** 父节点id，当前为任务节点时是任务的executionId，当前为分支节点时是分支的parentId */
	private String parentId;

	/** 任务信息，如果在初始化时选择了queryTaskInfo选项则会查询 */
	private HistoricTaskInstance taskInfo;

	/** 分支的序列，只在分支节点上有数据，记录分支下的子分支和任务节点，按创建时间排列 */
	private List<ExecutionInfo> sequee = new ArrayList<>();
	/** 分支的子分支，只在分支节点上有数据 */
	private Map<String, ExecutionInfo> children = new HashMap<>();

	/** 内部使用，保存所有节点数据，所有节点共用一个对象 */
	private Map<String, ExecutionInfo> executionList__;

	private ExecutionInfo(String id) {
		this.id = id;
		this.isTask = false;
		executionList__ = new HashMap<>();
		executionList__.put(id, this);
	}

	private ExecutionInfo() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isTask() {
		return isTask;
	}

	public boolean isScope() {
		return isScope;
	}

	public HistoricTaskInstance getTaskInfo() {
		return taskInfo;
	}

	public String getParentId() {
		return parentId;
	}

	public ExecutionInfo getParent() {
		return executionList__.get(parentId);
	}

	public List<ExecutionInfo> getSequee() {
		return sequee;
	}

	public Map<String, ExecutionInfo> getChildren() {
		return children;
	}

	// 获取子节点相关方法

	public ExecutionInfo findChild(String id) {
		return executionList__.get(id);
	}

	public ExecutionInfo getLastChildren() {
		if (sequee.isEmpty()) {
			return null;
		}
		return sequee.get(sequee.size() - 1);
	}

	public ExecutionInfo getPervious() {
		ExecutionInfo parent = this.getParent();
		if (parent == null || parent.isScope()) {
			return null;
		}

		int index = parent.getSequee().indexOf(this);
		if (index > 0) {
			return parent.getSequee().get(index - 1);
		}
		return null;
	}

	public List<ExecutionInfo> getChildExecutions() {
		List<ExecutionInfo> result = new ArrayList<>();
		if (!this.isTask()) {
			result.add(this);
		}
		if (!this.getChildren().isEmpty()) {
			for (ExecutionInfo child : this.getChildren().values()) {
				result.addAll(child.getChildExecutions());
			}
		}
		return result;
	}

	// 组装数据相关方法

	/**
	 * 生成当前流程实例分支详细信息，包含未完成和已完成的任务(驳回、撤销的任务和分支不会计算在内)
	 * 
	 * @param historyService
	 * @param managementService
	 * @param processInstanceId
	 * @param queryTaskInfo
	 *            是否查询历史任务详情,可以通过{@link ExecutionInfo.getTaskInfo()}获取
	 * @return
	 */
	public static ExecutionInfo getExecutionInfo(HistoryService historyService, ManagementService managementService,
			String processInstanceId, boolean queryTaskInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ")
				.append(managementService.getTableName(HistoricVariableInstance.class))
				.append(" hiVar ");
		sb.append("WHERE hiVar.PROC_INST_ID_ = #{processInstanceId} ");
		sb.append("AND (hiVar.NAME_ = #{variableName} OR hiVar.NAME_ = #{variableName2}) ");
		sb.append("ORDER BY hiVar.CREATE_TIME_ ASC ");
		// 根据变量名和流程实例id查询历史变量,按创建时间升序排列
		List<HistoricVariableInstance> variables = historyService.createNativeHistoricVariableInstanceQuery()
				.sql(sb.toString())
				.parameter("variableName", ActivitiConstant.VARIABLE_EXECUTION_ANCHOR)
				.parameter("variableName2", ActivitiConstant.VARIABLE_EXECUTION_ANCHOR_ADDITIONAL)
				.parameter("processInstanceId", processInstanceId)
				.list();

		// 查询历史任务
		Map<String, HistoricTaskInstance> taskMap = null;
		if (queryTaskInfo) {
			List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
					.processInstanceId(processInstanceId)
					.list();
			taskMap = new HashMap<>();
			for (HistoricTaskInstance historicTaskInstance : tasks) {
				if (StringUtils.equals(historicTaskInstance.getDeleteReason(),
						ActivitiConstant.TASK_DELETE_REASON_CANCELED)) {
					continue;
				}
				taskMap.put(historicTaskInstance.getId(), historicTaskInstance);
			}
		}
		return getExecutionInfo(variables, taskMap);
	}

	private static ExecutionInfo getExecutionInfo(List<HistoricVariableInstance> variables,
			Map<String, HistoricTaskInstance> taskMap) {
		HistoricVariableInstanceEntity first = (HistoricVariableInstanceEntity) variables.get(0);
		ExecutionInfo root = new ExecutionInfo(first.getExecutionId());
		for (int i = 1; i < variables.size(); i++) {
			HistoricVariableInstanceEntity itemVar = (HistoricVariableInstanceEntity) variables.get(i);
			// 可选分支不计入子节点
			if (itemVar.getName().equals(ActivitiConstant.VARIABLE_EXECUTION_ANCHOR_ADDITIONAL)) {
				ExecutionInfo additionalExecution = root.executionList__.get(itemVar.getExecutionId());
				additionalExecution.getParent().children.remove(itemVar.getExecutionId());
				additionalExecution.getParent().sequee.remove(additionalExecution);
				continue;
			}

			ExecutionInfo parent = root.executionList__.get(itemVar.getTextValue());
			// 判断当前节点是任务还是分支并获取任务详情
			boolean isTask = StringUtils.isNotEmpty(itemVar.getTaskId());
			String id = isTask ? itemVar.getTaskId() : itemVar.getExecutionId();
			HistoricTaskInstance taskInfo = null;
			if (isTask) {
				parent.isScope = false;
				if (taskMap != null) {
					taskInfo = taskMap.get(id);
				}
			}
			// 插入子节点
			parent.addChild(id, isTask, taskInfo);
		}
		return root;
	}

	private void addChild(String childId, boolean isTask, HistoricTaskInstance taskInfo) {
		ExecutionInfo child = new ExecutionInfo();
		child.id = childId;
		child.parentId = this.getId();
		child.isTask = isTask;
		child.isScope = !isTask;
		child.taskInfo = taskInfo;
		child.executionList__ = this.executionList__;
		executionList__.put(childId, child);
		this.sequee.add(child);
		if (!isTask) {
			this.children.put(childId, child);
		}
	}

	// 获取前一个任务相关方法

	public List<ExecutionInfo> getPerviousTasks(String taskId) {
		ExecutionInfo child = executionList__.get(taskId);
		if (child == null || !child.isTask()) {
			throw new RuntimeException("未查询到任务信息");
		}

		return getPerviousTasks(child.getParent(), child);
	}

	private List<ExecutionInfo> getPerviousTasks(ExecutionInfo current, ExecutionInfo child) {
		List<ExecutionInfo> list = new ArrayList<>();

		ExecutionInfo pervious;
		if (child == null) {
			// 未指定当前位置，将当前分支最后一个子节点作为当前位置
			pervious = current.getLastChildren();
		} else {
			// 将当前位置前移一位
			pervious = child.getPervious();
		}

		while (pervious != null) {
			if (pervious.isTask()) {
				// 当前节点是任务,终止循环并返回
				list.add(pervious);
				return list;
			}

			// 当前节点是分支，倒推分支序列获取最后一个任务
			// TODO 临时修复
			List<ExecutionInfo> sequee = pervious.getSequee();
			if (!sequee.isEmpty()) {
				for (int i = sequee.size() - 1; i >= 0; i--) {
					ExecutionInfo item = sequee.get(i);
					if (item.isTask()) {
						list.add(item);
						return list;
					}
				}
			}
			// 获取子分支上的任务
			if (pervious.getChildren() != null) {
				for (ExecutionInfo perviousChild : pervious.getChildren().values()) {
					List<ExecutionInfo> perviousTasks = getPerviousTasks(perviousChild, null);
					if (!perviousTasks.isEmpty()) {
						list.addAll(perviousTasks);
					}
				}
			}

			// 当前节点无合适任务，继续前移一位
			if (list.isEmpty()) {
				pervious = pervious.getPervious();
			} else {
				break;
			}
		}

		// 本分支无合适数据，前往父级分支寻找
		ExecutionInfo parent = current.getParent();
		if (list.isEmpty() && parent != null) {
			list.addAll(getPerviousTasks(parent, current));
		}
		return list;
	}

	public ExecutionInfo getFirstTask() {
		for (ExecutionInfo item : sequee) {
			if (item.isTask) {
				return item;
			} else {
				ExecutionInfo firstTask = item.getFirstTask();
				if (firstTask != null) {
					return firstTask;
				}
				continue;
			}
		}
		return null;
	}
}