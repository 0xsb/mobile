package cmcc.mobile.admin.vo;

import java.util.Date;

public class UnionMessageVo {
	/** 接入系统ID */
	private String app_id;
	/** 接入系统ID */
	private String app_pwd;

	/** 消息类型：0表示待办，1表示已办，2表示待阅，3表示已阅，4表示草稿，5表示已建 */
	private String msg_type;

	/** 应用系统中实际消息id */
	private String app_msg_id;
	/** 应用系统中实际消息流程实例id */
	private String instance_id;

	/** 起草(发送)人OA账号或者工号 */
	private String sender;
	/** 起草(发送)人中文姓名 */
	private String sender_name;
	/** 接收人OA账号或者工号 */
	private String receiver;
	/** 接收人中文姓名 */
	private String receiver_name;

	/** 标题 */
	private String msg_title;
	/** 流程名称 */
	private String doc_type;
	/** 环节名称 */
	private String msg_status;
	/** 待办信息打开时需要的链接地 */
	private String msg_url;
	/** 紧急程度 */
	private String urgent_level;
	/** 置顶字段 */
	private Date stick;

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getApp_pwd() {
		return app_pwd;
	}

	public void setApp_pwd(String app_pwd) {
		this.app_pwd = app_pwd;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public String getApp_msg_id() {
		return app_msg_id;
	}

	public void setApp_msg_id(String app_msg_id) {
		this.app_msg_id = app_msg_id;
	}

	public String getInstance_id() {
		return instance_id;
	}

	public void setInstance_id(String instance_id) {
		this.instance_id = instance_id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiver_name() {
		return receiver_name;
	}

	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}

	public String getMsg_title() {
		return msg_title;
	}

	public void setMsg_title(String msg_title) {
		this.msg_title = msg_title;
	}

	public String getDoc_type() {
		return doc_type;
	}

	public void setDoc_type(String doc_type) {
		this.doc_type = doc_type;
	}

	public String getMsg_status() {
		return msg_status;
	}

	public void setMsg_status(String msg_status) {
		this.msg_status = msg_status;
	}

	public String getMsg_url() {
		return msg_url;
	}

	public void setMsg_url(String msg_url) {
		this.msg_url = msg_url;
	}

	public String getUrgent_level() {
		return urgent_level;
	}

	public void setUrgent_level(String urgent_level) {
		this.urgent_level = urgent_level;
	}

	public Date getStick() {
		return stick;
	}

	public void setStick(Date stick) {
		this.stick = stick;
	}

}
