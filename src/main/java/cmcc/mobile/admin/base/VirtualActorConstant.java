package cmcc.mobile.admin.base;

/**
 *	虚拟角色常量定义
 * @author renlinggao
 * @Date 2016年10月11日
 */
public class VirtualActorConstant {
	
	public static final String ASSUMED_ROLE = "$$$";//自选角色
	public static final String PROMOTER_ROLE = "$$!";//发起人角色
	public static final String DEPT_LEADER_ROLE = "$$#";//部门领导角色
	public static final String SUPERIOR_DEPT_LEADER_ROLE="$$@"; //上级部门领导
	
	public static final String ACT_PROMOTER_ROLE = "$$$"; //activiti的发为人角色
	public static final String ACT_UNDER_DEPT_PEOPLE_ROLE = "$$%";//部门下的所有人员角色
	
	public static final String BATCH_PROMOTER_ROLE = "$$$";//批量发起发起人
}
