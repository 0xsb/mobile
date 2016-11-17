package cmcc.mobile.admin.vo;

import cmcc.mobile.admin.entity.ApprovalType;

public class ApprovalTypeResultVo extends ApprovalType{
	int isCollection = 0;//0表示没收藏1表示收藏

	public int getIsCollection() {
		return isCollection;
	}

	public void setIsCollection(int isCollection) {
		this.isCollection = isCollection;
	} 
			
			
}
