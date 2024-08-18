package kr.co.jhta.app.delideli.common.util;

import java.util.HashMap;
import java.util.Map;

public class PageUtil {

	public static Map<String, Object> getPageData(int totalNumber, 
				int countPerPage, int currentPage) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 총페이지수
		int totalPage = (totalNumber % countPerPage == 0) ? totalNumber / countPerPage : totalNumber / countPerPage + 1;

		// 현재 페이지의 게시물 시작번호
		int startNo = (currentPage - 1) * countPerPage ; // (3- 1)*10+1 ==> 21
		// 현재 페이지의 게시물 끝번호
		int endNo = currentPage * countPerPage; // 3*10
		// 시작 페이지 번호
		int startPageNo = currentPage - 5 <= 0 ? 1 : currentPage - 5;
		// 끝페이지 번호
		int endPageNo = startPageNo + 10 >= totalPage ? totalPage : startPageNo + 10;
		// 이전
		boolean prev = currentPage > 2 ? true : false;
		// 다음
		boolean next = currentPage + 2 >= totalPage ? false : true;

		// map.put("키", "밸류");
		map.put("currentPage", currentPage);
		map.put("totalNumber", totalNumber);
		map.put("countPerPage", countPerPage);
		map.put("totalPage", totalPage);
		map.put("startNo", startNo);
		map.put("endNo", endNo);
		map.put("startPageNo", startPageNo);
		map.put("endPageNo", endPageNo);
		map.put("prev", prev);
		map.put("next", next);

		return map;
	}

}
