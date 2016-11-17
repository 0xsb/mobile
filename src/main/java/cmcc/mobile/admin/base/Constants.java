package cmcc.mobile.admin.base;

public class Constants {
	public static final String PARAMETERS_NOT_FORMAT = "请求参数格式不合法。";
	public static final String PARAMETERS_DATE_WRONG = "时间类型不合法。";
	public static final String PARAMETERS_LACKING = "请求参数不完整。";
	public static final String SERVICE_HBASE_QUERY_FAILED = "当前网络不好，请稍后再试。";
	public static final String SC_NULL_DATA = "目标数据缺失。";
	public static final String PARAMETERS_NOT_EXIST = "无效的用户编码";
	public static final String SP_UID_LACK = "请求参数不完整,用户ID缺失。";
	public static final String SP_PAR_LACK = "请求参数不完整,参与人缺失。";
	public static final String SP_TIME_LACK = "请求参数不完整,时间参数缺失。";
	public static final String SP_PARUID_LACK = "请求参数不完整,参与人ID缺失。";
	public static final String SP_TASKTITLE_LACK = "请求参数不完整,任务标题缺失。";

	/**
	 * 公司管理员
	 */
	public static final String COMPANY_MANAGER = "companyManager";
	/**
	 * 区域经理
	 */
	public static final String AREA_MANAGER = "areaManager";
	/**
	 * 客户经理
	 */
	public static final String CUSTOMER_MANAGER = "customerManager";
	/**
	 * admin管理员
	 */
	public static final String ADMIN_MANAGER = "admin";

	/**
	 * 公司管理员
	 */
	public static final Integer COMPANY_ADMIN = 1;
	public static final Integer AREA_ADMIN = 3;
	public static final Integer CUSTOMER_ADMIN = 2;

	public static final Integer USER_CAN_USE = 1;
	public static final Integer USER_NOT_REG_IM = -1; // 用户导入未注册小溪
	public static final Integer USER_NOT_VALIDATE = -2;// app注册待审核
	public static final Integer COMPANY_CANT_USE = 0; // 企业不可用
	public static final Integer COMPANY_WAIT_VALIDATE = 2;// 企业待审核
	public static final Integer COMPANY_DELETED = 3;// 已删除

	/**
	 * 普通公告类型 1
	 */
	public static final Long COMMON_MESSAGE = 1L;
	/**
	 * 滚动消息类型 2
	 */
	public static final Long SCOLL_MESSAGE = 2L;

	/**
	 * 消息必达立即发送
	 */
	public static final Integer CREATE_TYPE_NOW = 1;
	/**
	 * 消息必达定时发送
	 */
	public static final Integer CREATE_TYPE_WAIT = 2;

	/**
	 * 消息必达应用推送
	 */
	public static final Integer PUSH_BY_APP = 1;
	/**
	 * 消息必达短信推送
	 */
	public static final Integer PUSH_BY_SMS = 2;
	/**
	 * 消息必达电话回拨推送
	 */
	public static final Integer PUSH_BY_TEL = 3;

	/**
	 * 
	 */
	public static final Integer MESSAGE_STATUS_DELETE = 4;

	/**
	 * 已接收
	 */
	public static final Integer RECEIVE_STATUS_1 = 1;
	/**
	 * 已删除
	 */
	public static final Integer RECEIVE_STATUS_2 = 2;
	/**
	 * 已确认
	 */
	public static final Integer RECEIVE_STATUS_3 = 3;

	/**
	 * 未确认 1
	 */
	public static final Integer CONFIRM_STATUS_1 = 1;

	/**
	 * 已确认 2
	 */
	public static final Integer CONFIRM_STATUS_2 = 2;
}
