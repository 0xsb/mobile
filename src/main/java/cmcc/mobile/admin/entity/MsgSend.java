package cmcc.mobile.admin.entity;

import java.sql.Timestamp;
import java.util.Date;

/**
 *  短消息表
 * @author Administrator
 *
 */
public class MsgSend {
	
	/**
	 * V网通移动审批中您有一条新待办""，请您及时处理.
	 */
	
	    private Long id;

	    private String mobile;

	    private String content;

	    private Date inserttime;

	    private Date processTime;

	    private Integer status;

	    private String type;

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getMobile() {
	        return mobile;
	    }

	    public void setMobile(String mobile) {
	        this.mobile = mobile == null ? null : mobile.trim();
	    }

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content == null ? null : content.trim();
	    }

	    public Date getInserttime() {
	        return inserttime;
	    }

	    public void setInserttime(Date inserttime) {
	        this.inserttime = inserttime;
	    }

	    public Date getProcessTime() {
	        return processTime;
	    }

	    public void setProcessTime(Date processTime) {
	        this.processTime = processTime;
	    }

	    public Integer getStatus() {
	        return status;
	    }

	    public void setStatus(Integer status) {
	        this.status = status;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type == null ? null : type.trim();
	    }
	
}
