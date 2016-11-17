package cmcc.mobile.admin.vo;

import java.util.List;

import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.User;

/**
 *
 * @author renlinggao
 * @Date 2016年5月8日
 */
public class ApprovalTableConfigVo extends ApprovalTableConfig {

	public ApprovalTableConfigVo() {

	}

	public ApprovalTableConfigVo(ApprovalTableConfig approvalTableConfig) {
		this.setId(approvalTableConfig.getId());
		this.setDate(approvalTableConfig.getDate());
		this.setDefaultApprovalUserIds(approvalTableConfig.getDefaultApprovalUserIds());
		this.setStatus(approvalTableConfig.getStatus());
		this.setApprovalTypeId(approvalTableConfig.getApprovalTypeId());
		this.setLastUserId(approvalTableConfig.getLastUserId());
		this.setUserId(approvalTableConfig.getUserId());
		this.setLastDealWay(approvalTableConfig.getLastDealWay());
	}

	private List<User> defUserList;
	private User lastUser;

	public List<User> getDefUserList() {
		return defUserList;
	}

	public void setDefUserList(List<User> defUserList) {
		this.defUserList = defUserList;
	}

	public User getLastUser() {
		return lastUser;
	}

	public void setLastUser(User lastUser) {
		this.lastUser = lastUser;
	}

}
