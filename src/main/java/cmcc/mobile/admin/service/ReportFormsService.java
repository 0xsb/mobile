package cmcc.mobile.admin.service;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.vo.ApprovalVo;
;



public interface ReportFormsService {

	JsonResult getReport(ApprovalVo record, String userId);





}
