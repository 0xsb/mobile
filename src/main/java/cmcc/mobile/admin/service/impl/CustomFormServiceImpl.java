package cmcc.mobile.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.base.VirtualActorConstant;
import cmcc.mobile.admin.dao.AdminRoleMapper;
import cmcc.mobile.admin.dao.ApprovalAuthorityMapper;
import cmcc.mobile.admin.dao.ApprovalMostTypeMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigDetailsMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigMapper;
import cmcc.mobile.admin.dao.ApprovalTypeMapper;
import cmcc.mobile.admin.dao.UserApprovalTypeMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.AdminRole;
import cmcc.mobile.admin.entity.ApprovalAuthority;
import cmcc.mobile.admin.entity.ApprovalMostType;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.CustomFormService;
import cmcc.mobile.admin.util.DateUtil;
import cmcc.mobile.admin.util.PropertiesUtil;
import cmcc.mobile.admin.vo.ApprovalTableConfigVo;

/**
 *
 * @author renlinggao
 * @Date 2016年5月5日
 */
@Service("customFormService")
public class CustomFormServiceImpl implements CustomFormService {

	@Autowired
	ApprovalMostTypeMapper approvalMostTypeMapper;

	@Autowired
	ApprovalTableConfigMapper approvalTableConfigMapper;

	@Autowired
	ApprovalTypeMapper approvalTypeMapper;

	@Autowired
	ApprovalTableConfigDetailsMapper approvalTableConfigDetailsMapper;

	@Autowired
	UserMapper userMapper;
	@Autowired
	AdminRoleMapper roleMapper;
	@Autowired
	UserApprovalTypeMapper userApprovalType;

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ApprovalAuthorityMapper authorityMapper;

	@Override
	public boolean addCustomForm(String companyId, Integer scene, String mostTypeKey, String name, String icon,
			String des, List<ApprovalTableConfigDetails> control, String userId) {
		String py = UUID.randomUUID().toString();// CustomFormUtil.convertLower(name);
		ApprovalType approvalType = approvalTypeMapper.selectByPrimaryKey(py);
		if (approvalType != null)
			return false;

		// 插入流程表
		String atcId = UUID.randomUUID().toString();
		ApprovalTableConfig atc = new ApprovalTableConfig();
		atc.setId(atcId);
		atc.setApprovalTypeId(py);
		atc.setStatus("0");
		atc.setUserId("1");
		atc.setDate(DateUtil.getDateStr(new Date()));
		approvalTableConfigMapper.insert(atc);
		// 插入流程类型表
		ApprovalType at = new ApprovalType();
		at.setDes(des);
		at.setIcon(icon);
		at.setName(name);
		at.setId(py);
		at.setStatus("1");
		at.setApprovalMostTypeId(mostTypeKey);
		at.setApprovalTableConfigId(atc.getId() + "");
		at.setThirdApprovalStartLink(PropertiesUtil.getAppByKey("TYPE_LINK") + py);
		at.setScene(scene);
		at.setCompanyId(companyId);
		at.setIsDefault("1");
		at.setCreateUserId(userId);
		approvalTypeMapper.insert(at);

		ApprovalAuthority record = new ApprovalAuthority();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		record.setId(py);
		record.setCreateTime(sdf.format(new Date()));
		record.setCompanyId(companyId);
		ApprovalMostType type = approvalMostTypeMapper.selectByPrimaryKey(mostTypeKey);
		record.setWyyId(type.getWyyId());
		authorityMapper.insertSelective(record);
		String mingxifId = null;
		// 插入表单数据
		if (control != null) {
			for (ApprovalTableConfigDetails atcd : control) {
				String atcdId = UUID.randomUUID().toString();

				if ("TableField".equals(atcd.getControlId())) {
					mingxifId = atcdId;
				}

				if (new Integer(atcd.getSequence()) % 100 > 0) {
					atcd.setPreviousId(mingxifId);
				}
				atcd.setId(atcdId);
				atcd.setApprovalTableConfigId(atc.getId() + "");
				approvalTableConfigDetailsMapper.insert(atcd);
			}
		}

		return true;
	}

	@Override
	public boolean editCustomForm(String mostTypeKey, String id, String name, String icon, String des,
			List<ApprovalTableConfigDetails> control) {
		ApprovalType at = approvalTypeMapper.selectByPrimaryKey(id);

		if (at != null) {
			// 获取原来的流程配置
			ApprovalTableConfig atc = approvalTableConfigMapper.selectByPrimaryKey(at.getApprovalTableConfigId());

			atc.setStatus("2"); // 设置原来的状态为删除
			approvalTableConfigMapper.updateByPrimaryKeySelective(atc);

			atc.setDate(DateUtil.getDateStr(new Date()));
			String newConfigId = UUID.randomUUID().toString();
			atc.setId(newConfigId);
			atc.setStatus("0");// 设置原来的状态为启用
			// 复制原来的配置插入数据库
			approvalTableConfigMapper.insert(atc);

			String mingxifId = null;
			// 插入所有的表单控件到控件表
			for (ApprovalTableConfigDetails atcd : control) {
				String atcdId = UUID.randomUUID().toString();
				if ("TableField".equals(atcd.getControlId())) {
					mingxifId = atcdId;
				}
				if (new Integer(atcd.getSequence()) % 100 > 0) {
					atcd.setPreviousId(mingxifId);
				}

				atcd.setId(atcdId);
				atcd.setApprovalTableConfigId(newConfigId);
				approvalTableConfigDetailsMapper.insert(atcd);
			}

			at.setApprovalTableConfigId(newConfigId); // 设置新表单配置关联
			at.setName(name);
			at.setDes(des);
			at.setIcon(icon);
			at.setApprovalMostTypeId(mostTypeKey);
			approvalTypeMapper.updateByPrimaryKeySelective(at);
			return true;
		}
		return false;
	}

	private List<User> getDefUser(String str) {
		List<User> users = new ArrayList<User>();

		if (StringUtils.isNotEmpty(str)) {// 解析字符串加入user集合
			String userIds[] = str.split(",");
			for (String id : userIds) {
				User user = new User();
				if ((VirtualActorConstant.ASSUMED_ROLE.equals(id))) {// 当时站位时
																		// 设置用户名
					user.setUserName(VirtualActorConstant.ASSUMED_ROLE);
					user.setId(VirtualActorConstant.ASSUMED_ROLE);
				} else if (VirtualActorConstant.PROMOTER_ROLE.equals(id)) {
					user.setUserName(VirtualActorConstant.PROMOTER_ROLE);
					user.setId(VirtualActorConstant.PROMOTER_ROLE);
				} else if (VirtualActorConstant.DEPT_LEADER_ROLE.equals(id)) {
					user.setUserName(VirtualActorConstant.DEPT_LEADER_ROLE);
					user.setId(VirtualActorConstant.DEPT_LEADER_ROLE);
				} else if (VirtualActorConstant.SUPERIOR_DEPT_LEADER_ROLE.equals(id)) {
					user.setUserName(VirtualActorConstant.SUPERIOR_DEPT_LEADER_ROLE);
					user.setId(VirtualActorConstant.SUPERIOR_DEPT_LEADER_ROLE);
				} else {
					user = userMapper.selectByPrimaryKey(id);
				}
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public boolean setDefApprovalUsers(ApprovalTableConfig approvalTableConfig) {
		ApprovalType approvalType = approvalTypeMapper.selectByPrimaryKey(approvalTableConfig.getId());
		if (approvalType == null)
			return false;
		ApprovalTableConfig a = approvalTableConfigMapper.selectByPrimaryKey(approvalType.getApprovalTableConfigId());
		approvalTableConfig.setId(a.getId());
		if (a != null) {
			approvalTableConfigMapper.updateByPrimaryKeySelective(approvalTableConfig);
			return true;
		}
		return false;
	}

	@Override
	public ApprovalTableConfigVo getDefApprovalUsers(String id) {
		ApprovalType approvalType = approvalTypeMapper.selectByPrimaryKey(id);
		ApprovalTableConfig approvalTableConfig = approvalTableConfigMapper
				.selectByPrimaryKey(approvalType.getApprovalTableConfigId());
		List<User> defUserList = getDefUser(approvalTableConfig.getDefaultApprovalUserIds());
		ApprovalTableConfigVo approvalTableConfigVo = new ApprovalTableConfigVo(approvalTableConfig);
		approvalTableConfigVo.setDefUserList(defUserList);
		if (approvalTableConfig.getLastUserId() != null)
			approvalTableConfigVo.setLastUser(userMapper.selectByPrimaryKey(approvalTableConfig.getLastUserId()));
		return approvalTableConfigVo;
	}

	@Override
	public boolean stopWorkFlow(String id) {
		ApprovalType at = approvalTypeMapper.selectByPrimaryKey(id);
		if (at != null) {
			if ("1".equals(at.getStatus())) {
				int x = userApprovalType.deleteByUserIdAndTypeId(id);
				at.setStatus("2");
			} else {
				at.setStatus("1");
			}
			approvalTypeMapper.updateByPrimaryKeySelective(at);
			return true;
		}
		return false;
	}
}