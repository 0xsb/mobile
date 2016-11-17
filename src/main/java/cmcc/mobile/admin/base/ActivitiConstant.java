package cmcc.mobile.admin.base;

import java.text.SimpleDateFormat;

/**
 *
 * @author renlinggao
 * @Date 2016年7月26日
 */
public class ActivitiConstant {
	public static final String FORM_ID = "__formId"; // 流程表单的id
	public static final String FORM_RELATION = "__formRelation"; // 表单是否继承上一个表单
	public static final String ALL_FORM_DETAIL = "__allForms"; // 所有表单
	public static final String TASKS_CONFIG = "__tasksConfig";// 所有节点的配置
	public static final String NEXT_NODE = "__nextTaskName";// 下一个节点
	public static final String NEXT_BUTTON = "__button";// 节点的按钮组

	public static final String TASK_IS_CLICK = "__isClick";//当前任务的按钮是否点击过

	public static final String NO_SUCCESS_FROM_ID = "__formNoSuccessId";// 不能被继承的表单

	public static final String CONFIG_SUCCESS = "__success";// 是否继承上一个表单
	public static final String CONFIG_WRITABLE = "__writable";// 是否可修改

	public static final String TASK_DELETE_REASON_COMPLETED = "200";
	public static final String TASK_DELETE_REASON_CANCELED = "300";
	public static final String TASK_DELETE_REASON_REASSIGNED = "400";
	public static final String TASK_DELETE_REASON_REJECTED = "500";
	public static final String TASK_DELETE_REASON_RECOVERED = "600";
	public static final String TASK_DELETE_REASON_DELETED = "deleted";

	public static final String PREVIOUS_TASK = "__previousTask";

	public static final String PROCESS_STATUS = "__process_status";// 流程状态

	public static final int PROCESS_STATUS_DRAFT = 0;// 起草状态
	public static final int PROCESS_STATUS_CIRCULATION = 1;// 流转状态
	public static final int PROCESS_STATUS_COMPLETE = 2;// 完成状态
	public static final int PROCESS_STATUS_REFUSE = 3;// 拒绝状态
	public static final int PROCESS_STATUS_REVOKE = 9;// 起草人撤销

	/** 记录流程分支信息变量名 */
	public static final String VARIABLE_EXECUTION_ANCHOR = "___executionParentId";

	public static final String VARIABLE_EXECUTION_ANCHOR_ADDITIONAL = "___executionAdditional";

	/** 下一个任务办理人变量名,变量保存在分支上 */
	public static final String VARIABLE_NEXT_TASK_ASSIGNEE = "___assignee";

	/** 当前任务相关信息变量名，包括companyId，wyyId等,变量保存在分支上 */
	public static final String VARIABLE_NEXT_TASK_MSG_INFO = "___dealMsgInfo";

	/** 待办表id变量名 */
	public static final String VARIABLE_TASK_THIRD_DEAL_ID = "___thirdDealId";

	public static final String VARIABLE_FORM_DATA = "formData";
	
	public static final String VARIABLE_PARAMTER_DATA = "paramterData";

	/** activiti时间格式(ISO 8601 不包含时区) */
	public static SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public static String GATEWAY_CONDITION_VARIABLE_NAME = "___nextTask";// 主动条件的判断变量
}
