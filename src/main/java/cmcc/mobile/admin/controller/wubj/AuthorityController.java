package cmcc.mobile.admin.controller.wubj;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ApprovalAuthorityMapper;
import cmcc.mobile.admin.entity.ApprovalAuthority;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.AuthorityService;

/**
 * 后台管理界面
 * @author wubj
 *
 */
@Controller
@RequestMapping("authority")
public class AuthorityController extends BaseController{
	@Autowired
	private AuthorityService authorityService ;
	@Autowired
	private ApprovalAuthorityMapper approvalAuthorityMapper ;

	
	//流程授权接口
	@RequestMapping("/getApproval")
	@ResponseBody
	public JsonResult getApproval(HttpServletRequest request,ApprovalAuthority authority){
		String companyId = request.getSession().getAttribute("companyId").toString() ;
		String userId = request.getSession().getAttribute("userId").toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;	
		if(authority.getId()!=null){
		ApprovalAuthority approval = approvalAuthorityMapper.selectByPrimaryKey(authority.getId()) ;
		if(StringUtils.isNotEmpty(companyId)&&StringUtils.isNotEmpty(userId)){
			if(approval==null){
				authority.setCreateTime(sdf.format(new Date()));
				authority.setCreateUserId(userId);
				return authorityService.getApproval(authority) ;
			}else{
				authority.setUpdateTime(sdf.format(new Date()));
//				if(authority.getUserids()!=null&&authority.getUserids().equals("")){
//					authority.setUserids(null);
//				}
//				if(authority.getRoleId()!=null&&authority.getRoleId().equals("")){
//					authority.setRoleId(null);
//				}
//				if(authority.getReportUserIds()!=null&&authority.getReportUserIds().equals("")){
//					authority.setReportUserIds(null);
//				}
				authority.setUpdateUserId(userId);
				return authorityService.UpdateApproval(authority) ;
			}
		}
		}
		return new JsonResult(false,"非法请求",null) ;
	}
	
	//获取已经授权的人员信息
	//流程授权接口
	@RequestMapping("/getPerson")
	@ResponseBody
	public JsonResult getPerson(HttpServletRequest request,String id){
		if(StringUtils.isNotEmpty(id)){
			return authorityService.getPerson(id) ;
		}
		return new JsonResult(false,"非法请求",null) ;
	}
//	//删除授权人员接口
//	@RequestMapping
//	@ResponseBody
//	public JsonResult deletePerson(HttpServletRequest request,String id){
//		if(StringUtils.isNotEmpty(id)){
//			return authorityService.deletePerson(id) ;
//		}
//		return new JsonResult(false,"非法请求",null) ;
//	}
}
