package cmcc.mobile.admin.service;

import java.util.List;

/**
 * 发送短信接口类 
 * 1.调用时请先切换到主库 
 * 2.白名单发送短信时如果公司传null，默认查询主库totaluser表的用户公司
 * 3.上4个是用用户id，下4个是用手机号发如果要验证公司id必填
 * @author renlinggao
 * @Date 2016年8月29日
 */
public interface MsgService {
	/**
	 * 批量发送短信接口
	 * 
	 * @param userIds
	 *            用戶id
	 * @param content
	 *            短信的內容
	 */
	public void sendMsgBatch(List<String> userIds, String content);

	/**
	 * 发送一条短信
	 * 
	 * @param userId
	 * @param content
	 */
	public void sendMsg(String userId, String content);

	/**
	 * 根据公司短信白名单发送一条短信
	 * 
	 * @param userId
	 * @param content
	 */
	public void checkdSendMsg(String userId, String content);

	/**
	 * 根据公司短信白名单批量发送短信
	 * 
	 * @param userIds
	 * @param content
	 */
	public void checkdSendMsgBatch(List<String> userIds, String content);
	
	/**
	 * 发送一条短信
	 * @param mobile手机号
	 * @param content短信内容
	 */
	public void sendByMobile(String mobile, String content);
	
	/**
	 * 批量发送短信
	 * @param mobiles
	 * @param content
	 */
	public void sendBatchByMobile(List<String> mobiles, String content);
	
	/**
	 * 根据短信白名单发送一条短信
	 * @param mobile
	 * @param content
	 * @param companyId
	 */
	public void checkdSendByMobile(String mobile, String content, String companyId);
	
	/**
	 * 根据短信白名单批量发送短信
	 * @param mobiles
	 * @param content
	 * @param companyId
	 */
	public void checkdSendBatchByMobile(List<String> mobiles, String content, String companyId);
}
