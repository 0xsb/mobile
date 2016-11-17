package cmcc.mobile.admin.service;

import javax.servlet.http.HttpServletRequest;

import cmcc.mobile.admin.entity.ThirdApprovalDeal;

public interface UnionMessageService {

	boolean checkSSO(HttpServletRequest request, String token);

	void insert(ThirdApprovalDeal deal, String taskName);

	void delete(ThirdApprovalDeal deal);

	void updateState(ThirdApprovalDeal deal);

	void updateState(ThirdApprovalDeal deal, String msgType);


}
