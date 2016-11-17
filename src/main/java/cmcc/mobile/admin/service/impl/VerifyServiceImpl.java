package cmcc.mobile.admin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.MsgSendMapper;
import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.dao.VerifyMobileMapper;
import cmcc.mobile.admin.entity.MsgSend;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.VerifyMobile;
import cmcc.mobile.admin.service.VerifyService;
@Service
public class VerifyServiceImpl implements VerifyService{
	
	@Autowired
	private VerifyMobileMapper verifymobileMapper;
	
	@Autowired
	private TotalUserMapper totaluserMapper;
	
	@Autowired
	private MsgSendMapper msgsendMapper;

	@Override
	public int insert(VerifyMobile verifyMobile) {
		
		return verifymobileMapper.insert(verifyMobile);
	}

	@Override
	public List<TotalUser> selectByMobile(String mobile) {
		
		return totaluserMapper.selectByMobile(mobile);
	}

	@Override
	public VerifyMobile selcectByMobileAndCode(VerifyMobile verifyMobile) {
		
		return verifymobileMapper.selcectByMobileAndCode(verifyMobile);
	}

	@Override
	public int insertSelective(MsgSend record) {
		
		return msgsendMapper.insertSelective(record);
	}

	@Override
	public VerifyMobile selcectByMobile(VerifyMobile verifyMobile) {
		
		return verifymobileMapper.selcectByMobile(verifyMobile);
	}

	@Override
	public int updateStasusById(Integer id) {
		
		return verifymobileMapper.updateStasusById(id);
	}

	@Override
	public List<MsgSend> selectByMsgSendMobile(String mobile) {
		
		return msgsendMapper.selectByMsgSendMobile(mobile);
	}

}
