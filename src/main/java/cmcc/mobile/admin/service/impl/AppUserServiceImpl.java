package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cmcc.mobile.admin.dao.OrganizationMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.AppUserService;
import cmcc.mobile.admin.vo.AppOrganizationVo;
import cmcc.mobile.admin.vo.CustomerVo;


@Service
public class AppUserServiceImpl implements AppUserService{
@Autowired
private UserMapper userMapper ;
@Autowired
private OrganizationMapper organizationMapper ;
	@Override
	public List<CustomerVo> companyOrgUser(String id,Customer customer) {
		if (null != customer) {
			List<CustomerVo> customerVos = new ArrayList<>();
			CustomerVo vo = new CustomerVo();
			BeanUtils.copyProperties(customer, vo);

			/**
			 * 获取一级部门
			 */
			List<Organization> orgs = organizationMapper.selectByCompanyId(customer.getId()) ;

			// 获取所有部门，遍历获取所有部门下员工
			List<AppOrganizationVo> orgVos = orgUsers(orgs);

			vo.setOrgList(orgVos);
			customerVos.add(vo);
			return customerVos;
		}
		return null;
	}

	/**
	 * 递归获取部门元素，包括多个部门或者人员 传入一个一级部门，递归查询下面所有部门信息
	 */
	public List<AppOrganizationVo> orgUsers(List<Organization> orgs) {
		List<AppOrganizationVo> list = new ArrayList<>();
		for (Organization tAppOrganization : orgs) {
			// 查询所有部门下人员
			AppOrganizationVo orgVo = new AppOrganizationVo();
			BeanUtils.copyProperties(tAppOrganization, orgVo);
			List<User> users = userMapper.selectAllByOrgId(tAppOrganization.getId());
			orgVo.setUsers(users);
			// 查询次级部门
			tAppOrganization.setPreviousId(tAppOrganization.getId());
			List<Organization> childOrg = organizationMapper.selectByCidandPreId(tAppOrganization);
			// 递归
			if (CollectionUtils.isNotEmpty(childOrg)) {
				List<AppOrganizationVo> childs = orgUsers(childOrg);
				orgVo.setOrgs(childs);
			}
			// 参数封装
			list.add(orgVo);
		}
		return list;
	}

	@Override
	public List<AppOrganizationVo> selectByCompanyId(String companyId) {
		Organization org = new Organization();
		org.setCompanyId(companyId);
		List<Organization> first = organizationMapper.selectByCidandPreId(org);
		List<AppOrganizationVo> allOrgs = orgUsers(first);
		return allOrgs;
	}

	

}
