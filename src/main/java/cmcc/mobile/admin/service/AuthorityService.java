package cmcc.mobile.admin.service;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.ApprovalAuthority;

public interface AuthorityService {


	JsonResult getApproval(ApprovalAuthority authority);

	JsonResult UpdateApproval(ApprovalAuthority authority);

	JsonResult getPerson(String id);


}
