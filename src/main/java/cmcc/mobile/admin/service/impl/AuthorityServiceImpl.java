package cmcc.mobile.admin.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ActivitiRoleMapper;
import cmcc.mobile.admin.dao.ApprovalAuthorityMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.ActivitiRole;
import cmcc.mobile.admin.entity.ApprovalAuthority;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.AuthorityService;


@Service
public class AuthorityServiceImpl implements AuthorityService{

	@Autowired
	private ApprovalAuthorityMapper approvalAuthorityMapper ;
	@Autowired
	private UserMapper usermapper ;
	@Autowired
	private ActivitiRoleMapper roleMapper ;
	//授权新增接口
	@Override
	public JsonResult getApproval(ApprovalAuthority authority) {			
		int ret = approvalAuthorityMapper.insertSelective(authority) ;
		if(ret==1){
			return new JsonResult(true,"授权成功！",null) ;
		}
		return new JsonResult(false,"授权失败",null);
	}
	
	//修改授权接口
	@Override
	public JsonResult UpdateApproval(ApprovalAuthority authority) {
		int ret = approvalAuthorityMapper.updateByPrimaryKeySelective(authority) ;
		if(ret==1){
			return new JsonResult(true,"授权成功！",null) ;
		}
		return new JsonResult(false,"授权失败",null);
	}

	//获取授权人员接口
	@SuppressWarnings("null")
	@Override
	public JsonResult getPerson(String id) {
		Map<String,Object> map = new HashMap<>() ;
		Map<String,Object> map1 = new HashMap<>() ;
		List<User> users = new ArrayList<User>() ;
		List<ActivitiRole> roles = new ArrayList<ActivitiRole>() ;
		List<User> reports = new ArrayList<User>() ;
		ApprovalAuthority approvalAuthoritie = approvalAuthorityMapper.selectByPrimaryKey(id) ;
		if(approvalAuthoritie!=null){
			if(approvalAuthoritie.getUserids()!=null){
			String[] userId = approvalAuthoritie.getUserids().split(",") ;
			map.put("userId", userId) ;
			users = usermapper.selectUser(map) ;
			}
			if(approvalAuthoritie.getRoleId()!=null){
				String[] roleId = approvalAuthoritie.getRoleId().split(",") ;
				map.clear();
				map.put("roleId", roleId) ;
				roles = roleMapper.selectByMap(map) ;
			}
			if(approvalAuthoritie.getReportUserIds()!=null){
				String[] reportUsers = approvalAuthoritie.getReportUserIds().split(",") ;
				map.clear();
				map.put("userId", reportUsers) ;
				reports = usermapper.selectUser(map) ;
			}
			map1.put("users", users) ;
			map1.put("roles", roles) ;
			map1.put("reports", reports) ;
			return new JsonResult(true,"查询成功！",map1) ;
		}else if(approvalAuthoritie.getUserids()==null&&approvalAuthoritie.getRoleId()==null) {
			return new JsonResult(true,"该流程没有授权人",null) ;
		}
		return new JsonResult(true,"该流程没有授权人",null) ;
	}

	

}
