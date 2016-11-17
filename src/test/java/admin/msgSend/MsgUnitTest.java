package admin.msgSend;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import admin.BaseTest;
import cmcc.mobile.admin.service.MsgService;

/**
 *
 * @author renlinggao
 * @Date 2016年9月13日
 */
public class MsgUnitTest extends BaseTest {
	@Resource // 自动注入,默认按名称
	private MsgService msgService;

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#sendMsgBatch(java.util.List, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚
	public void testSendMsgBatch() {
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("kftest_2016082914581436638");
		String content = "testSendMsgBatch";
		msgService.sendMsgBatch(list, content);
		
	}

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#sendMsg(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚
	public void testSendMsg() {
		msgService.sendMsg("kftest_2016082914581436638", "testSendMsg");
	}

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#checkdSendMsg(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚滚
	public void testCheckdSendMsg() {
		msgService.checkdSendMsg("kftest_2016082914581436638", "testCheckdSendMsg");
	}

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#checkdSendMsgBatch(java.util.List, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚
	public void testCheckdSendMsgBatch() {
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("kftest_2016082914581436638");
		String content = "testCheckdSendMsgBatch";
		msgService.checkdSendMsgBatch(list, content);
	}

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#sendByMobile(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚
	public void testSendByMobile() {
		msgService.sendByMobile("18268733945", "testSendByMobile");
	}

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#sendBatchByMobile(java.util.List, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚
	public void testSendBatchByMobile() {
		List<String> list = new ArrayList<>();
		list.add("18268733945");
		list.add("15050561902");
		msgService.sendBatchByMobile(list, "testSendBatchByMobile");
	}

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#checkdSendByMobile(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚
	public void testCheckdSendByMobile() {
		String companyId = "kftest";
		msgService.checkdSendByMobile("18268733945", "testCheckdSendByMobile", companyId);
	}

	/**
	 * Test method for
	 * {@link cmcc.mobile.admin.service.impl.MsgServiceImpl#checkdSendByMobile(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Transactional // 标明此方法需使用事务
	@Rollback(true) // 标明使用完此方法后事务不回滚,true时为回滚
	public void testCheckdSendBatchByMobile() {
		String companyId = "kftest";
		List<String> list = new ArrayList<>();
		list.add("18268733945");
		list.add("15050561902");
		msgService.checkdSendBatchByMobile(list, "testCheckdSendBatchByMobile", companyId);
	}
}
