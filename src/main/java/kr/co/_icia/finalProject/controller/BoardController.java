package kr.co._icia.finalProject.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co._icia.finalProject.dto.BestCheckForm;
import kr.co._icia.finalProject.dto.writeReviewForm;
import kr.co._icia.finalProject.entity.BestCheck;
import kr.co._icia.finalProject.entity.Board;
import kr.co._icia.finalProject.entity.Node;
import kr.co._icia.finalProject.entity.PlanDayDetail;
import kr.co._icia.finalProject.entity.PlanNodeBridge;
import kr.co._icia.finalProject.entity.Plans;
import kr.co._icia.finalProject.service.BoardService;
import kr.co._icia.finalProject.service.PlanService;

@Controller
@RequestMapping("/boards")
public class BoardController {
	@Autowired
	private BoardService boardService;

	@Autowired
	private PlanService planService;

	@Autowired
	private HttpSession session;

	@GetMapping("/popUpReview")
	public String popUpReview(@RequestParam("id") Long Id, Model model, RedirectAttributes ra) {
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다");
			return "redirect:/member/loginFormPage";
		}
		Plans plan = planService.findById(Id);
		model.addAttribute("title", plan.getPname());
		return "/board/planReview";
	}

	@GetMapping("/ReviewCheck")
	@ResponseBody
	public List<Long> ReviewCheck() {
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			return null;
		}
		List<Long> planId = boardService.findAllPlanId();
		return planId;
	}

	@PostMapping("/writeReview")
	@ResponseBody
	public Long writeReview(@RequestParam("paramId") Long paramId, writeReviewForm writeReviewForm)
			throws IllegalStateException, IOException {
		try {
			boardService.registReview(writeReviewForm);
			System.out.println("board : " + boardService.findPlansId(paramId).getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long boardId = boardService.findPlansId(paramId).getId();
		return boardId;

	}

	@GetMapping("/loadPlan")
	@ResponseBody
	public Plans loadPlan(Model model, @RequestParam("id") Long Id) {
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			return null;
		}
		Plans Plan = planService.findById(Id);
		model.addAttribute("Plan", Plan);
		return Plan;
	}

	@GetMapping("/boardDetail/{planid}/{boardid}")
	public String detailPlanByReview(@PathVariable("planid") Long planId, @PathVariable("boardid") Long boardid,
			Model model, RedirectAttributes ra) {
		System.out.println(planService.findById(planId));
		Plans plan = planService.findById(planId);

		List<PlanDayDetail> planDetailList = planService.findDetailsByPlanId(plan.getId());
		List<String> dateList = new ArrayList<>();
		for (PlanDayDetail planDetail : planDetailList) {
			String detailDate = planDetail.getDetailDate();
			dateList.add(detailDate);
		}

		Board board = boardService.findPlansId(planId);
		List<String> fileNameList = new ArrayList<>(); // 파일 이름 목록을 저장할 리스트를 생성합니다.
		String fileName = board.getFilename(); // 각 리뷰 체크의 프로필 이름을 가져옵니다.
		if (fileName != null) {

			String[] AlldateList = fileName.split(","); // 파일 이름을 쉼표로 분리합니다.
			for (int i = 0; i < AlldateList.length; i++) {
				fileNameList.add(AlldateList[i]); // 분리된 각 파일 이름을 리스트에 추가합니다.
			}
			System.out.println(fileNameList); // 파일 이름 목록을 콘솔에 출력합니다.
		}
		model.addAttribute("fileNameList", fileNameList); // 모델에 파일 이름 목록을 추가합니다.

		model.addAttribute("board", board); // 모델에 리뷰 체크 목록을 추가합니다.

		model.addAttribute("boardid", boardid);
		model.addAttribute("dateList", dateList);
		model.addAttribute("planid", plan.getId());
		return "/board/boardDetail";

	}

	@GetMapping("/detailLoad")
	@ResponseBody
	public HashMap<String, Object> detailLoad(@RequestParam("id") Long id, @RequestParam("date") String date) {

		Plans plan = planService.findById(id);

		List<Node> nodeList = new ArrayList<>();
		String pathPoints = null;
		List<String> dateList = new ArrayList<>();

		List<PlanDayDetail> planDetailList = planService.findDetailsByPlanId(plan.getId());
		for (PlanDayDetail planDetail : planDetailList) {
			String detailDate = planDetail.getDetailDate();
			System.out.println(detailDate);
			if (date.equals(detailDate)) {
				pathPoints = planDetail.getPathjson();

				List<PlanNodeBridge> nodeBridgeList = planDetail.getNodeId();
				nodeBridgeList.sort(Comparator.comparing(PlanNodeBridge::getId)); // getId() 메서드를 사용하여 정렬
				
				for (PlanNodeBridge nodeBridge : nodeBridgeList) {
					Node node = nodeBridge.getNodeId();
					nodeList.add(node);
				}
			}
			dateList.add(detailDate);
		}

		HashMap<String, Object> response = new HashMap<>();
		response.put("nodeList", nodeList);
		response.put("pathPoints", pathPoints);

		return response;
	}

	// 후기에 좋아요 등록 기능
	@PostMapping("/Best")
	@ResponseBody
	public String registBest(@RequestParam("Paramid") Long planId, RedirectAttributes ra) {
		// 로그인 안되어있으면 로그인 페이지로 이동
		Long memberId = (Long) session.getAttribute("loginId");
		if (memberId == null) {
			return "회원없음";
		}
		Plans plans = planService.findById(planId);
		BestCheckForm bestCheckForm = new BestCheckForm();
		bestCheckForm.setMemberId(memberId);
		Long boardId = boardService.findPlansId(planId).getId();
		bestCheckForm.setBoardId(boardId);

		BestCheck memberCheck = boardService.memberCheckByBoardId(memberId, boardId);
		if (memberCheck == null) {
			boardService.registBest(bestCheckForm);
			return "111";
		} else {
			return "이미 누른 회원입니다.";
		}

	}

	@GetMapping("/BestCheck")
	@ResponseBody
	public String checkBest(@RequestParam("Paramid") Long planId, @RequestParam("boardId") Long boardId,
			RedirectAttributes ra) {
		Long memberId = (Long) session.getAttribute("loginId");
		BestCheck memberCheck = boardService.memberCheckByBoardId(memberId, boardId);
		if (memberCheck != null) {
			return "N";
		}
		return "Y";
	}

	@GetMapping("/LikeFind")
	@ResponseBody
	public String LikeFind(@RequestParam("boardId") Long boardId,RedirectAttributes ra) {
		List<BestCheck> likeList = boardService.BoardCheck(boardId);
		int totalLikes = 0;

		// 좋아요 수만 합산
		for (BestCheck like : likeList) {
			totalLikes += like.getBest();
		}

		return String.valueOf(totalLikes); // 총 좋아요 수만 반환
	}
}
