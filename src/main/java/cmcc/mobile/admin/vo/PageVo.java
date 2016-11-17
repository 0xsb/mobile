package cmcc.mobile.admin.vo;

/**
 * 用于分页的参数接收
 * 
 * @author zhangxs
 *
 */

public class PageVo {
	private int pageNo = 1;
	private int pageSize = 10;
	private int startRow;
	private int endRow;
	private int totalCount;
	private int pageTotal;

	public PageVo(int pageNo, int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public PageVo() {
		// TODO Auto-generated constructor stub
	}

	public int getStartRow() {
		pageNo = pageNo > 0 ? pageNo : 1;
		pageSize = pageSize > 0 ? pageSize : 20;
		return startRow = (pageNo - 1) * pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalCountAndPageTotal(int totalCount) {
		this.totalCount = totalCount;
		if (totalCount % pageSize == 0) {
			this.pageTotal = totalCount / pageSize;
		} else {
			this.pageTotal = totalCount / pageSize + 1;
		}
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return pageSize;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
