package cmcc.mobile.admin.controller.wubj;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ApprovalMapper;
import cmcc.mobile.admin.dao.CustomerMapper;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.ReportFormsService;
import cmcc.mobile.admin.vo.ApprovalVo;
/**
 * 报表 
 * @author wubj
 *
 */
@Controller
@RequestMapping("report")
public class ReportFormsController extends BaseController{
	@Autowired
	private ReportFormsService formsService ;
	@Autowired
	private ApprovalMapper dataMapper ;
	@Autowired
	protected CustomerMapper customerMapper;
	//获取表单抬头
	@RequestMapping("getReport")
	@ResponseBody
	public JsonResult getReport(HttpServletRequest request,ApprovalVo record){
		String companyId = request.getSession().getAttribute("companyId").toString() ;
		String userId = request.getSession().getAttribute("userId").toString() ;
		if(StringUtils.isEmpty(record.getUserId())){
		
		record.setUserId(userId) ;
		}
		//String dbName = customerMapper.selectByCompanyId(companyId).getDbname();
		MultipleDataSource.setDataSourceKey("business1");
		if(companyId==null){
			return new JsonResult(false , "没有集团公司" ,null) ;
		}
		record.setCompanyId(companyId) ;
		
		return formsService.getReport(record,userId);		
	}
	
	@RequestMapping("getReportList")
	@ResponseBody
	public JsonResult getReportList(HttpServletRequest request,ApprovalVo record){
		String companyId = request.getSession().getAttribute("companyId").toString() ;
		String userId = request.getSession().getAttribute("userId").toString() ;
		if(record.getStartDate().equals("")){
			record.setStartDate(null);
		}
		if(record.getEndDate().equals("")){
			record.setEndDate(null);
		}
		if(record.getDrapId().equals("")){
			record.setDrapId(null);
		}
		if(record.getDrapName().equals("")){
			record.setDrapName(null);
		}
		if(record.getUserName().equals("")){
			record.setUserName(null);
		}
		if(StringUtils.isEmpty(record.getUserId())){
		record.setUserId(userId) ;
		}
		//String dbName = customerMapper.selectByCompanyId(companyId).getDbname();
//		MultipleDataSource.setDataSourceKey("business1");
		if(companyId==null){
			return new JsonResult(false , "没有集团公司" ,null) ;
		}
		record.setCompanyId(companyId) ;
		
		return formsService.getReport(record,userId);		
	}
}
