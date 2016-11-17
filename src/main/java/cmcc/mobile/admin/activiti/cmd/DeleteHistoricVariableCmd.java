package cmcc.mobile.admin.activiti.cmd;

import java.util.List;

import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;

public class DeleteHistoricVariableCmd implements Command<Void> {

	private List<HistoricVariableInstance> variables;

	public DeleteHistoricVariableCmd(List<HistoricVariableInstance> variables) {
		this.variables = variables;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		for (HistoricVariableInstance historicVariableInstance : variables) {
			commandContext.getDbSqlSession().delete((HistoricVariableInstanceEntity)historicVariableInstance);
		}
		return null;
	}

}
