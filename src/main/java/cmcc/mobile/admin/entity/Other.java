package cmcc.mobile.admin.entity;

/**
 * 存用户留言
 * @author Administrator
 *
 */
public class Other {
	
	private long id;
	private String createTime;
	private String message;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
