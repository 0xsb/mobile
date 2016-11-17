package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.MsgCompany;

public interface MsgCompanyMapper {
	
	List<MsgCompany> getMsgCompanyByCompanyId(String companyId);
	
}
