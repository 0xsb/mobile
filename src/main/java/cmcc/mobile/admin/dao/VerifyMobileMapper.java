package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.VerifyMobile;

public interface VerifyMobileMapper {
	
	int insert(VerifyMobile verifyMobile);
	
    VerifyMobile selcectByMobileAndCode(VerifyMobile verifyMobile);
    
    VerifyMobile selcectByMobile(VerifyMobile verifyMobile);
    
    int updateStasusById(Integer id);
}
