package com.Schedule.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.Schedule.dto.Schedule;
import com.Schedule.service.ScheduleService;
import com.Schedule.util.MyUtil;


import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private MyUtil myUtil;

	@RequestMapping(value = "/")
	public String index() {
		return "/index";
	}

	// get방식으로 데이터가 들어올 때, 화면을 보여주는 기능
	@RequestMapping(value = "/created", method = RequestMethod.GET)
	public String created() {
		return "bbs/created";
	}

	// 등록하기
	@RequestMapping(value = "/created", method = RequestMethod.POST)
	public String createdOK(Schedule schedule, HttpServletRequest request, Model model) {
		try {
			int maxNum = scheduleService.maxNum();

			schedule.setNum(maxNum + 1); // num컬럼에 들어가 값을 1만큼 증가시켜준다.

			scheduleService.insertData(schedule);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/list";
	}

	// 리스트 페이지 보여주기
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String list(Schedule schedule, HttpServletRequest request, Model model) {

		try {
			String pageNum = request.getParameter("pageNum"); // 바뀌는 페이지번호
			int currentPage = 1; // 현재페이지 번호(디폴트값은 1)

			if (pageNum != null) {
				currentPage = Integer.parseInt(pageNum);
			}

			String searchKey = request.getParameter("searchKey"); // 검색키워드(subject, content, name)
			String searchValue = request.getParameter("searchValue");

			if (searchValue == null) {
				searchKey = "title"; // 검색키워드의 디폴트는 title
				searchValue = ""; // 검색어의 디폴트는 빈문자열
			} else {
				if (request.getMethod().equalsIgnoreCase("GET"))
					;
				{
					searchValue = URLDecoder.decode(searchValue, "UTF-8");
				}
				// get방식으로 request가 왔다면
			}
			// 1. 전체 게시물의 갯수를 가져온다.(페이징처리에 필요)
			int dataCount = scheduleService.getDataCount(searchKey, searchValue);

			// 2. 페이징 처리를 한다.(준비단계)
			int numPerPage = 5; // 페이지 당 보여줄 데이터리스트의 갯수
			int totalPage = myUtil.getPageCount(numPerPage, dataCount); // 페이지의 전체갯수를 구한다.

			if (currentPage > totalPage)
				currentPage = totalPage;

			int start = (currentPage - 1) * numPerPage + 1; // 1 6 11 16...
			int end = currentPage * numPerPage; // 5 10 15 20...

			// 3. 전체 게시물 리스트를 가져온다.
			List<Schedule> lists = scheduleService.getLists(searchKey, searchValue, start, end);

			// 4. 페이징 처리를 한다.
			String param = "";

			// 검색어가 있다면
			if (searchValue != null && !searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8"); // 컴퓨터의 언어로 인코딩
			}

			String listUrl = "/list";

			// list?searchKey=name&searchValue=춘식
			if (!param.equals(""))
				listUrl += "?" + param;

			String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);

			String articleUrl = "/article?pageNum=" + currentPage;

			if (!param.equals("")) {
				articleUrl += "&" + param;
			}
			
			model.addAttribute("lists", lists);
			model.addAttribute("articleUrl", articleUrl); // 상세페이지로 이동하기 위한 URL
			model.addAttribute("pageIndexList", pageIndexList); // ◀이전 6 7 8 9 10 다음▶
			model.addAttribute("dataCount", dataCount); // 전체 게시물의 갯수
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "bbs/list";
	}

	// 수정페이지 보여주기
	@RequestMapping(value = "/updated", method = RequestMethod.GET)
	public String updated(HttpServletRequest request, Model model){
		try {
			int num = Integer.parseInt(request.getParameter("num"));
			String pageNum = request.getParameter("pageNum");
			String searchKey = request.getParameter("searchKey");
			String searchValue = request.getParameter("searchValue");

			if (searchValue != null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}

			Schedule schedule = scheduleService.getReadData(num);
			if (schedule == null) {
				return "redirect:/list?pageNum=" + pageNum;
			}

			String param = "pageNum=" + pageNum;

			// 검색어가 있다면
			if (searchValue != null && !searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8"); // 컴퓨터의 언어로 인코딩
			}

			model.addAttribute("schedule", schedule);
			model.addAttribute("pageNum", pageNum);
			model.addAttribute("params", param);
			model.addAttribute("searchKey", searchKey);
			model.addAttribute("searchValue", searchValue);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "bbs/updated";
	
	}

	// 수정하기
	@RequestMapping(value = "/updated_ok", method = RequestMethod.POST)
	public String updated_ok(Schedule schedule, HttpServletRequest request, Model model) {
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		String param = "?pageNum=" + pageNum;

		try {
			schedule.setContent(schedule.getContent().replaceAll("<br/>", "\r\n"));
			
			scheduleService.updateData(schedule);

			// 검색어가 있다면
			if (searchValue != null && !searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8"); // 컴퓨터의 언어로 인코딩
			}

		} catch (Exception e) {
			e.printStackTrace();
			
			
			try {
				param += "&errorMessage=" + URLEncoder.encode("게시글 수정 중 에러가 발생했습니다.", "UTF-8");
			} catch (UnsupportedEncodingException e1) {
			
				e1.printStackTrace();
			}
		}

		return "redirect:/list" + param;
	
	}

	// 디스플레이 화면 보여주기
	@RequestMapping(value = "/article", method = RequestMethod.GET)
	public String article(HttpServletRequest request, Model model) {
		try {
			int num = Integer.parseInt(request.getParameter("num"));
			String pageNum = request.getParameter("pageNum");
			String searchKey = request.getParameter("searchKey");
			String searchValue = request.getParameter("searchValue");

			if (searchValue != null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}


			// 2. 게시물 데이터 가져오기
			Schedule schedule = scheduleService.getReadData(num);
			if (schedule == null) {
				return "redirect:/list?pageNum=" + pageNum;
			}
			// 게시글의 라인 수를 구한다.
			int lineSu = schedule.getContent().split("\n").length;

			String param = "pageNum=" + pageNum;

			// 검색어가 있다면
			if (searchValue != null && !searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8"); // 컴퓨터의 언어로 인코딩
			}

			model.addAttribute("schedule", schedule);
			model.addAttribute("params", param);
			model.addAttribute("lineSu", lineSu);
			model.addAttribute("pageNum", pageNum);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "게시글을 불러오는 중 에러가 발생했습니다.");
		}

		return "bbs/article";
	}

	// 삭제하기
	@RequestMapping(value = "/deleted_ok", method = { RequestMethod.GET })
	public String deleteOK(HttpServletRequest request, Model model) {
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		String param = "?pageNum=" + pageNum;

		try {
			scheduleService.deleteData(num);

			// 검색어가 있다면
			if (searchValue != null && !searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8"); // 컴퓨터의 언어로 인코딩
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			try {
				param += "&errorMessage=" + URLEncoder.encode("게시글 삭제 중 에러가 발생했습니다.", "UTF-8");
			} catch (UnsupportedEncodingException e1) {
			
				e1.printStackTrace();
			}
		}

		return "redirect:/list" + param;
	
	}
}