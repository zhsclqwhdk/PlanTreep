package kr.co._icia.finalProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import kr.co._icia.finalProject.entity.Board;
import kr.co._icia.finalProject.entity.ShareContent;
import kr.co._icia.finalProject.service.BoardService;
import kr.co._icia.finalProject.service.ShareContentService;

@Controller
public class HomeController {

	@Autowired
	private HttpSession session;

	@Autowired
	private ShareContentService shareContentService;

	@Autowired
	private BoardService boardService;

	@GetMapping("/")
	public String getHome(Model model) {
		System.out.println("home");
		Long loginId = (Long) session.getAttribute("loginId");
		List<Board> boardList = boardService.findAll();
		model.addAttribute("boardList", boardList);
		if (loginId != null) {
			List<ShareContent> receivePlanList = shareContentService.findSharePlanList(loginId);
			model.addAttribute("shareContentCnt", receivePlanList.size());

		}
		return "home";
	}

}
