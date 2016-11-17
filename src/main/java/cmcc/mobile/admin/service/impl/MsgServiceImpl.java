package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.MsgCompanyMapper;
import cmcc.mobile.admin.dao.MsgSendMapper;
import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.entity.MsgCompany;
import cmcc.mobile.admin.entity.MsgSend;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.service.MsgService;

/**
 * 短信消息接口实现类
 * 
 * @author renlinggao
 * @Date 2016年8月29日
 */
@Service
public class MsgServiceImpl implements MsgService {

	@Autowired
	private MsgCompanyMapper msgCompanyMapper;

	@Autowired
	private MsgSendMapper msgSendMapper;

	@Autowired
	private TotalUserMapper totalUserMapper;

	@Override
	public void sendMsgBatch(List<String> userIds, String content) {
		List<MsgSend> sendList = new ArrayList<MsgSend>();
		for (String userId : userIds) {
			TotalUser user = totalUserMapper.selectByPrimaryKey(userId);
			if (user != null) {
				MsgSend send = new MsgSend();
				send.setContent(content);
				send.setInserttime(new Date());
				send.setMobile(user.getMobile());
				send.setStatus(0);
				send.setType("99");
				sendList.add(send);
			}
		}
		if (!sendList.isEmpty()) {
			msgSendMapper.insertBatch(sendList);
		}
	}

	@Override
	public void sendMsg(String userId, String content) {
		TotalUser user = totalUserMapper.selectByPrimaryKey(userId);
		if (user != null) {
			MsgSend send = new MsgSend();
			send.setContent(content);
			send.setInserttime(new Date());
			send.setMobile(user.getMobile());
			send.setStatus(0);
			send.setType("99");
			msgSendMapper.insertSelective(send);
		}
	}

	@Override
	public void checkdSendMsg(String userId, String content) {
		TotalUser user = totalUserMapper.selectByPrimaryKey(userId);
		if (user != null) {
			String companyId = user.getCompanyId();
			boolean canSend = checkSendCompany(companyId);
			if (canSend) {
				MsgSend send = new MsgSend();
				send.setContent(content);
				send.setInserttime(new Date());
				send.setMobile(user.getMobile());
				send.setStatus(0);
				send.setType("99");
				msgSendMapper.insertSelective(send);
			}
		}

	}

	@Override
	public void checkdSendMsgBatch(List<String> userIds, String content) {
		if (userIds != null && userIds.size() > 0) {
			TotalUser user = totalUserMapper.selectByPrimaryKey(userIds.get(0));
			String companyId = user != null ? user.getCompanyId() : null;
			// 判断该公司是否能发短信
			boolean canSend = checkSendCompany(companyId);
			if (!canSend)
				return;
			List<MsgSend> sendList = new ArrayList<MsgSend>();
			for (String uId : userIds) {
				TotalUser u = totalUserMapper.selectByPrimaryKey(uId);
				if (u != null) {
					// 初始化一条短信
					MsgSend send = new MsgSend();
					send.setContent(content);
					send.setInserttime(new Date());
					send.setMobile(u.getMobile());
					send.setStatus(0);
					send.setType("99");
					sendList.add(send);
				}
			}
			if (!sendList.isEmpty()) {
				msgSendMapper.insertBatch(sendList);
			}
		}

	}

	@Override
	public void sendByMobile(String mobile, String content) {
		MsgSend send = new MsgSend();
		send.setContent(content);
		send.setInserttime(new Date());
		send.setMobile(mobile);
		send.setStatus(0);
		send.setType("99");
		msgSendMapper.insertSelective(send);
	}

	@Override
	public void sendBatchByMobile(List<String> mobiles, String content) {
		List<MsgSend> sendList = new ArrayList<MsgSend>();
		for (String mobile : mobiles) {
			MsgSend send = new MsgSend();
			send.setContent(content);
			send.setInserttime(new Date());
			send.setMobile(mobile);
			send.setStatus(0);
			send.setType("99");
			sendList.add(send);
		}
		if (!sendList.isEmpty()) {
			msgSendMapper.insertBatch(sendList);
		}

	}

	@Override
	public void checkdSendByMobile(String mobile, String content, String companyId) {
		boolean canSend = checkSendCompany(companyId);
		if(canSend){
			sendByMobile(mobile, content);
		}
	}

	@Override
	public void checkdSendBatchByMobile(List<String> mobiles, String content, String companyId) {
		boolean canSend = checkSendCompany(companyId);
		if(canSend){
			sendBatchByMobile(mobiles, content);
		}
	}

	/**
	 * 验证公司是否是短信白名单
	 * 
	 * @param companyId
	 * @return
	 */
	private boolean checkSendCompany(String companyId) {
		if (StringUtils.isNotEmpty(companyId)) {
			// 获取白名单公司
			List<MsgCompany> companys = msgCompanyMapper.getMsgCompanyByCompanyId(companyId);
			if (companys != null && companys.size() > 0) {
				return true;
			}
		}
		return false;
	}
}
