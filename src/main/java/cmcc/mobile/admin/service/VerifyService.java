package cmcc.mobile.admin.service;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.MsgSend;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.VerifyMobile;

public interface VerifyService {

	int insert(VerifyMobile verifyMobile);
	
	List<TotalUser> selectByMobile(String mobile);
	
	 VerifyMobile selcectByMobileAndCode(VerifyMobile verifyMobile);
	 
	 int insertSelective(MsgSend record);
	 
	 VerifyMobile selcectByMobile(VerifyMobile verifyMobile);
	 
	 int updateStasusById(Integer id);
	 
	 List<MsgSend> selectByMsgSendMobile(String mobile);
}
