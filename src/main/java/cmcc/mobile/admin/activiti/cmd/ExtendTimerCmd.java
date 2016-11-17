package cmcc.mobile.admin.activiti.cmd;

import java.text.ParseException;
import java.util.List;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.apache.commons.lang3.StringUtils;

import cmcc.mobile.admin.base.ActivitiConstant;

public class ExtendTimerCmd implements Command<Void> {

	private String jobId;
	private List<String> expression;

	public ExtendTimerCmd(String jobId, List<String> expression) {
		super();
		this.jobId = jobId;
		this.expression = expression;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		// XXX activiti内部处理R2/2016-10-11T12:26:00/P0Y0M0DT0H1M0.000S这样表达式的timerEntity时，
		// 会删除旧的timerEntity然后创建个新的表达式为R1/2016-10-11T12:27:00/P0Y0M0DT0H1M0.000S的timerEntity,
		// 没有合适的构造方法只能手动赋值了
		TimerEntity te = (TimerEntity) commandContext.getJobEntityManager().findJobById(this.jobId);

		TimerEntity newTimer = new TimerEntity();
		newTimer.setJobHandlerConfiguration(te.getJobHandlerConfiguration());
		newTimer.setJobHandlerType(te.getJobHandlerType());
		newTimer.setRepeat(StringUtils.join(expression, "/"));
		newTimer.setRetries(te.getRetries());
		newTimer.setEndDate(te.getEndDate());
		try {
			newTimer.setDuedate(ActivitiConstant.isoDateFormat.parse(expression.get(1)));
		} catch (ParseException e) {
			throw new RuntimeException("无法解析延期时间",e);
		}
		newTimer.setExecutionId(te.getExecutionId());
		newTimer.setProcessInstanceId(te.getProcessInstanceId());
		newTimer.setProcessDefinitionId(te.getProcessDefinitionId());
		// isExclusive = te.isExclusive;
		te.delete();
		newTimer.insert();
		return null;
	}

}
