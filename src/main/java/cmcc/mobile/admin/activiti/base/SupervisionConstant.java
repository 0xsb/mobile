package cmcc.mobile.admin.activiti.base;

public class SupervisionConstant {
	/** 申请延期表单，督办名称变量名 */
	public static final String PARAMTER_NAME = "supName";

	/** 申请延期表单，督办开始时间变量 */
	public static final String PARAMTER_START_TIME = "supSTime";

	/** 申请延期表单，督办结束时间变量 */
	public static final String PARAMTER_END_TIME = "supETime";

	/** 申请延期表单，申请延期时间变量 */
	public static final String PARAMTER_EXTENSION_TIME = "supExTime";

	/** 主办用户表单变量 */
	public static final String PARAMTER_MAJOR_USER = "majorUser";

	/** 协办用户表单变量 */
	public static final String PARAMTER_MINOR_USER = "minorUser";

	/** 提前提醒时间，目前为提前的小时数 */
	public static final String PARAMTER_COUNTDOWN_TIME = "countDownTime";

	/** 督办完成状态，在督办开始时创建变量并设为0，督办结束时设为1 */
	public static final String VARIABLE_SUPERVISION_COMPLETED = "__supCompleted";

	/** 多人任务和督办任务的用户信息 */
	public static final String VARIABLE_MULTIINSTANCE_COLLECTION = "__multiInstanceCollection";

}
