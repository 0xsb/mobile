package cmcc.mobile.admin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class ProcessUtil {

	private static final FlowNode ExclusiveGateway = null;

	public static boolean isCondition(String el, Map<String, Object> vars) {
		ExpressionFactory factory = new ExpressionFactoryImpl();
		SimpleContext context = new SimpleContext();
		for (String key : vars.keySet()) {
			context.setVariable(key, factory.createValueExpression(vars.get(key), Object.class));
		}
		ValueExpression e = factory.createValueExpression(context, el, boolean.class);
		return (Boolean) e.getValue(context);
	}

	public static UserTask getNextTask(BpmnModel bpmnModel, FlowNode flowElement, Map<String, Object> variables) {
		List<FlowNode> flowNodes = getNextTask(bpmnModel, flowElement, variables, false);
		if (flowNodes.isEmpty()) {
			return null;
		}
		return (UserTask) flowNodes.get(0);
	}

	/**
	 * 获取当前节点的下一个用户任务
	 * 
	 * @param bpmnModel
	 *            流程定义模型
	 * @param flowElement
	 *            当前节点
	 * @param variables
	 *            相关流程变量
	 * @return
	 */
	public static List<FlowNode> getNextTask(BpmnModel bpmnModel, FlowNode flowElement, Map<String, Object> variables,
			boolean checkGateWay) {
		ArrayList<FlowNode> flowNodes = new ArrayList<>();

		Process mainProcess = bpmnModel.getMainProcess();
		List<SequenceFlow> outgoingFlows = flowElement.getOutgoingFlows();

		boolean SelectDestination = false;
		if (checkGateWay && StringUtils.isNotEmpty(flowElement.getDocumentation())) {
			JSONObject config = JSONObject.parseObject(flowElement.getDocumentation());
			SelectDestination = config.getBooleanValue("SelectDestination");
		}

		// 遍历所有外出流向
		for (SequenceFlow sequenceFlow : outgoingFlows) {
			String expression = sequenceFlow.getConditionExpression();
			FlowNode destination = (FlowNode) bpmnModel.getFlowElement(sequenceFlow.getTargetRef());
			if (SelectDestination) {
				flowNodes.add(destination);
				continue;
			}
			// 不满足条件时跳过
			if (StringUtils.isNotEmpty(expression) && !ProcessUtil.isCondition(expression, variables)) {
				continue;
			}

			if (destination instanceof UserTask) {
				// 目标为用户任务
				flowNodes.add(destination);
				break;
			} else if (destination instanceof ExclusiveGateway) {
				// 目标为排他网关
				flowNodes.addAll(getNextTask(bpmnModel, destination, variables, checkGateWay));
				break;
			} else if (destination instanceof SubProcess) {
				SubProcess subProcess = (SubProcess) destination;
				List<StartEvent> list = mainProcess.findFlowElementsInSubProcessOfType(subProcess, StartEvent.class);
				if (!list.isEmpty()) {
					flowNodes.addAll(getNextTask(bpmnModel, list.get(0), variables, checkGateWay));
					break;
				}
			} else if (destination instanceof EndEvent) {
				EndEvent endEvent = (EndEvent) destination;
				FlowElementsContainer parent = mainProcess.findParent(endEvent);
				if (parent != null && parent instanceof SubProcess) {
					flowNodes.addAll(getNextTask(bpmnModel, (SubProcess) parent, variables, checkGateWay));
					break;
				}
			}
		}
		return flowNodes;
	}

}
