package cmcc.mobile.admin.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.support.RestGatewaySupport;

import com.alibaba.fastjson.JSONArray;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.base.VirtualActorConstant;
import cmcc.mobile.admin.dao.ActivitiRoleGroupMapper;
import cmcc.mobile.admin.dao.ActivitiRoleMapper;
import cmcc.mobile.admin.dao.OrganizationMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.ActivitiRole;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.ActivitiRoleService;
import cmcc.mobile.admin.vo.ActivitiUserVo;
import cmcc.mobile.admin.vo.GroupVo;

@Service
public class ActivitiRoleServiceImpl implements ActivitiRoleService {
	@Autowired
	private ActivitiRoleMapper roleMapper;
	@Autowired
	private ActivitiRoleGroupMapper roleGroupMapper;
	@Autowired
	private OrganizationMapper organizationMapper ;
	@Autowired
	private UserMapper userMapper ;
	/**
	 * activiti流程流转
	 * __groupsType是否按组分割标识（0.不按组分割，1.按组分割）。不按组分割不需要查询上下级关系，按组分割需要查询上下级。
	 * candidateUsers用户集合
	 * candidateGroups群组，角色集合
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JsonResult getNextNode(String userId, String companyId, boolean isBelong, List<Map<String, Object>> list) {
		Map<String, Object> user = new HashMap<>();
		Map<String, Object> map2 = new HashMap<>();
		Map<String, Object> map3 = new HashMap<>();
		List<ActivitiUserVo> lists = new ArrayList<ActivitiUserVo>() ;
		// 如果有虚拟角色 先添加到这个集合
		List<ActivitiUserVo> temRoleVoList = new ArrayList<ActivitiUserVo>() ;
		// 如果是领导发起的角色
		List<ActivitiUserVo> laderRoleVoList = new ArrayList<ActivitiUserVo>() ;
		// 获取发起人所在的部门
		Organization org = roleMapper.selectByOrg(userId) ;

	
		if (org == null) {
			return new JsonResult(false, "该用户没有组织信息,请联系管理员加入相应的组织", org);
		}
		//获取集团所有人员
		List<User> userList = userMapper.selectByCompanyId(org.getCompanyId()) ;
		//查询发起人不在部门的人员信息
		map2.put("orgId", org.getId()) ;
		map2.put("companyId",org.getCompanyId());
		List<User> userByOrg = userMapper.selectAllByOrgCom(map2) ;
		map2.clear(); 
		List<Organization> listOrg = organizationMapper.selectByOrg(org.getId()) ;
		String fullname = org.getOrgFullname() ;
		for (int i = 0; i < list.size(); i++) {
			map2 = list.get(i);
			if (isBelong == true) {
				//不按组分割
				if (Integer.parseInt(map2.get("__groupsType").toString()) == 0) {
					if (map2.get("candidateGroups") != null) {
						List<GroupVo> groups = JSONArray.parseArray((String) map2.get("candidateGroups"),GroupVo.class);
						String[] arr = new String[groups.size()];
						for (int y = 0; y < groups.size(); y++) {
							String id = groups.get(y).getId();
							if (VirtualActorConstant.ACT_PROMOTER_ROLE.equals(id)) {// 如果是发起人虚拟角色
								ActivitiUserVo vo = new ActivitiUserVo();
								vo.setUserId(VirtualActorConstant.ACT_PROMOTER_ROLE);
								vo.setUserName("发起人");
								temRoleVoList.add(vo);
							}else if(VirtualActorConstant.ACT_UNDER_DEPT_PEOPLE_ROLE.equals(id)){	
								//将同个部门的人员信息封装
								for(int j=0 ; j<userByOrg.size() ; j++){
									ActivitiUserVo vo = new ActivitiUserVo();
									vo.setUserId(userByOrg.get(j).getId()) ;
									vo.setUserName(userByOrg.get(j).getUserName()) ;
									vo.setOrgId(org.getId());
									vo.setOrgName(org.getOrgName());
									vo.setFullName(org.getOrgFullname());
									vo.setCompanyId(companyId);
									laderRoleVoList.add(vo) ;
								}
								//将下级所有人员信息封装
								List<Organization> listOrganization = getOrg(listOrg) ;
								for(int x=0 ;x<listOrganization.size();x++){
									String orgId = listOrganization.get(x).getId() ;
									for(int z=0 ;z<userList.size() ; z++){										
										if(userList.get(z).getOrgId().equals(orgId)){
											ActivitiUserVo vo = new ActivitiUserVo();
											vo.setUserId(userList.get(z).getId()) ;
											vo.setUserName(userList.get(z).getUserName()) ;
											vo.setOrgId(orgId);
											vo.setOrgName(listOrganization.get(x).getOrgName());
											vo.setFullName(listOrganization.get(x).getOrgFullname());
											vo.setCompanyId(companyId);
											laderRoleVoList.add(vo) ;
										}
									}
								}												
							}
							arr[y] = id;
						}
//						//如果角色数组长度不为0 ,则遍历角色数组如果角色中关联了组织就查询角色上的组织下的人员
//						if(arr.length!=0){
//							for (int j = 0; j < arr.length; j++) {
//								ActivitiRole activitiRole = roleMapper.selectByRoleId(arr[j]) ;
//								if(activitiRole.getText2()!=null || activitiRole.getText2().equals("")){
//									lists = roleGroupMapper.selectUserByOrg(activitiRole.getText2()) ;
//									break ;
//								}
//							}
//						}
//						if(lists.size()==0){
						user.put("roleId", arr);
						lists = roleGroupMapper.selectByUserId(user);
//						}
						//把虚拟角色的用户和查询出来的用户合并
						lists.addAll(temRoleVoList);
						//把发起人是领导的下级人员合并
						lists.addAll(laderRoleVoList) ;
						map3.put("users", lists);
						map3.put("Task", map2.get("id"));
						return new JsonResult(true, "查询成功！", map3);
					} else if (map2.get("candidateUsers") != null) {
						List<GroupVo> groups = JSONArray.parseArray((String) map2.get("candidateUsers"), GroupVo.class);
						String[] arr = new String[groups.size()];
						for (int y = 0; y < groups.size(); y++) {
							arr[y] = groups.get(y).getId();
						}
						user.put("userId", arr);
						lists = roleGroupMapper.selectByUserOrg(user);
						map3.put("users", lists);
						map3.put("Task", map2.get("id"));
						return new JsonResult(true, "查询成功！", map3);
					}

				} 
				//按组分割
				else if (Integer.parseInt(map2.get("__groupsType").toString()) == 1) {
					if (map2.get("candidateGroups") != null) {
						List<GroupVo> groups = JSONArray.parseArray((String) map2.get("candidateGroups"),GroupVo.class);
						String[] arr = new String[groups.size()];
						for (int y = 0; y < groups.size(); y++) {
							arr[y] = groups.get(y).getId();
						}
						//如果角色数组长度不为0 ,则遍历角色数组如果角色中关联了组织就查询角色上的组织下的人员
						if(arr.length!=0){
							for (int j = 0; j < arr.length; j++) {
								ActivitiRole activitiRole = roleMapper.selectByRoleId(arr[j]) ;
								if(activitiRole.getText2()!=null || activitiRole.getText2().equals("")){
									lists = roleGroupMapper.selectUserByOrg(activitiRole.getText2()) ;
									break ;
								}
							}
						}
						if(lists.size()==0){
						//获取所有角色下的人员信息
						user.put("roleId", arr);
						List<ActivitiUserVo> usrole = roleGroupMapper.selectByListUser(user) ;
						//查询一切可能的人员信息
						for (int j = 0; j < usrole.size(); j++) {
							//如果fullname相同说明同一部门有人员符合则把结果放到list中
							if (fullname.equals(usrole.get(i).getFullName())) {
								lists.add(usrole.get(j)) ;
							}
							//查询上级所有符合条件的部门人员信息放到list
							//将所有的fullName将‘/’隔开的部门名称组装成数组
							String[] full = fullname.split("/") ;
							String[] fname = usrole.get(j).getFullName().split("/") ;
							//userId的fullName的数组长度大于人员信息里的部门数组长度，即查上级。
							if(full.length>fname.length){
								//循环遍历userId的fullName数组，并从0到K依次截取之前的部门，如果和人员信息里的fuName相同即符合情况的人员信息放到list中返回
								for (int k = 0; k < full.length; k++) {
									if(fullname.substring(0, fullname.indexOf("/",fullname.indexOf(".")+k)).equals(fname)){
										lists.add(usrole.get(j)) ;
									}
								}
							}
															
						}
						}
						map3.put("users", lists);
						map3.put("Task", map2.get("id"));
						return new JsonResult(true, "查询成功！", map3);
					} else if (map2.get("candidateUsers") != null) {
						List<GroupVo> groups = JSONArray.parseArray((String) map2.get("candidateUsers"), GroupVo.class);
						String[] arr = new String[groups.size()];
						for (int y = 0; y < groups.size(); y++) {
							arr[y] = groups.get(y).getId();
						}
						user.put("userId", arr);
						// user.put("orgId", orgId) ;
						List<ActivitiUserVo> usr = roleGroupMapper.selectByUserOrg(user);
						for (int j = 0; j < usr.size(); j++) {
							//如果fullname相同说明同一部门有人员符合则把结果放到list中
							if (fullname.equals(usr.get(i).getFullName())) {
								lists.add(usr.get(j)) ;
							}
							//查询上级所有符合条件的部门人员信息放到list
							//将所有的fullName将‘/’隔开的部门名称组装成数组
							String[] full = fullname.split("/") ;
							String[] fname = usr.get(j).getFullName().split("/") ;
							//userId的fullName的数组长度大于人员信息里的部门数组长度，即查上级。
							if(full.length>fname.length){
								//循环遍历userId的fullName数组，并从0到K依次截取之前的部门，如果和人员信息里的fuName相同即符合情况的人员信息放到list中返回
								for (int k = 0; k < full.length; k++) {
									if(fullname.substring(0, fullname.indexOf("/",fullname.indexOf(".")+k)).equals(fname)){
										lists.add(usr.get(j)) ;
									}
								}
							}																
						}
						map3.put("users", lists);
						map3.put("Task", map2.get("id"));
						return new JsonResult(true, "查询成功！", map3);
					}
				}
			} else {
				if (map2.get("candidateGroups") != null) {
					List<GroupVo> groups = JSONArray.parseArray((String) map2.get("candidateGroups"), GroupVo.class);
					String[] arr = new String[groups.size()];
					for (int y = 0; y < groups.size(); y++) {
						arr[y] = groups.get(y).getId();
					}
					user.put("roleId", arr);
					user.put("userId", userId);
					lists = roleGroupMapper.selectByUserId(user);
					if (lists == null || lists.size() == 0) {
						map3.put("users", null);
						map3.put("Task", null);
					} else {
						map3.put("users", lists);
						map3.put("Task", map2.get("id"));
						return new JsonResult(true, "查询成功！", map3);
					}

				}else if (map2.get("candidateUsers") != null) {
					List<GroupVo> groups = JSONArray.parseArray((String) map2.get("candidateUsers"), GroupVo.class);
					String[] arr = new String[groups.size()];
					for (int y = 0; y < groups.size(); y++) {
						arr[y] = groups.get(y).getId();
					}
					user.put("userId", arr);
					// user.put("orgId", orgId) ;
					lists = roleGroupMapper.selectByUserOrg(user);
					map3.put("users", lists);
					map3.put("Task", map2.get("id"));
					return new JsonResult(true, "查询成功！", map3);

				}
			}
		}
		return new JsonResult(true, "流程选择错误！", null);

	}

	@Override
	public JsonResult getRole(String companyId) {
		List<ActivitiRole> list = roleMapper.selectCompany(companyId);
		if (list.size() == 0) {
			return new JsonResult(false, "没有角色信息", null);
		}
		return new JsonResult(true, "查询成功！", list);
	}
	
	public List<Organization> getOrg(List<Organization> list) {
		if(list.size()!=0){
			getParent(list);
		}
		return list;
	}
	//迭代查询下级部门
	private void getParent(List<Organization> organization){
		
		for(int i = 0 ; i<organization.size() ; i++){
			List<Organization> organ = organizationMapper.findByParentId(organization.get(i).getId());
			if(organ.size()!=0){
			organization.addAll(organ) ;
			getParent(organ);
			}
			
		}
		
	}
	
}
