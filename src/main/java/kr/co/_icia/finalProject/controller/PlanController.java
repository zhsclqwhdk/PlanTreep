package kr.co._icia.finalProject.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co._icia.finalProject.dto.MemberJoinForm;
import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.Node;
import kr.co._icia.finalProject.entity.PlanDayDetail;
import kr.co._icia.finalProject.entity.PlanNodeBridge;
import kr.co._icia.finalProject.entity.Plans;
import kr.co._icia.finalProject.entity.ShareContent;
import kr.co._icia.finalProject.service.BoardService;
import kr.co._icia.finalProject.service.MemberService;
import kr.co._icia.finalProject.service.PlanService;
import kr.co._icia.finalProject.service.ShareContentService;

@Controller
public class PlanController {

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private PlanService planService;

	@Autowired
	private HttpSession session;

	@Autowired
	private MemberService memberService;

	@Autowired
	private ShareContentService shareService;
	@Autowired
	private BoardService boardService;

	@GetMapping("plan")
	public String movePlanPage(RedirectAttributes ra, Model model) {
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다");
			return "redirect:/member/loginFormPage";
		}
		List<Plans> planList = planService.findPlanList(checkLogin);

		model.addAttribute("planList", planList);

		httpSession.removeAttribute("newPlanId");
		return "chooseDate";
	}

	@GetMapping("map")
	public String moveMapPage(@RequestParam("result") String result, @RequestParam("title") String title, Model model)
			throws Exception {
		Members loginMember = memberService.findById((Long) session.getAttribute("loginId"));
		System.out.println(result);
		ArrayList<String> dateList = new ArrayList<>();
		String[] AlldateList = result.split(",");
		for (int i = 0; i < AlldateList.length; i++) {
			dateList.add(AlldateList[i]);
		}

		Plans plans = planService.registPlan(AlldateList, title, loginMember);

		session.setAttribute("newPlanId", plans.getId());
		model.addAttribute("loginX", loginMember.getMemberX());
		model.addAttribute("loginY", loginMember.getMemberY());
		model.addAttribute("loginAddr", loginMember.getRoadAddress());

		model.addAttribute("plans", plans);
		model.addAttribute("dateList", dateList);
		model.addAttribute("pTitle", title);

		return "chooseMap";
	}

	@GetMapping("/popUpSharePlanList")
	public String popUpSharePlanList(Model model) {
		Long loginId = (Long) session.getAttribute("loginId");
		List<ShareContent> shareList = shareService.findSharePlanList_All(loginId);
		model.addAttribute("shareList", shareList);
		return "popUpSharePlanList";
	}

	@GetMapping("/sharePlace")
	@ResponseBody
	public List<Node> sharePlace(@RequestParam("planid") String planid) {

		return planService.shareNode(Long.parseLong(planid));
	}

	// 내 일정 보기
	@GetMapping("/viewPlan")
	public String viewPlanPage(Model model, RedirectAttributes ra) {
		System.out.println("내 일정 보기 페이지 이동");
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다");
			return "redirect:/member/loginFormPage";
		}
		Long loginId = (Long) session.getAttribute("loginId");
        List<ShareContent> shareList = shareService.findSharePlanList_All(loginId);
        System.out.println(shareList);
        model.addAttribute("shareList", shareList);
		if (loginId != null) {
			List<Plans> myPlanList = planService.findPlanList(loginId);
			model.addAttribute("myPlanList", myPlanList);
		}
		List<Members> memberList = memberService.findAll();
		model.addAttribute("memberList", memberList);
		return "socket/myPlanView";
	}

	// 회원 아이디 검색으로 회원 찾기
    @GetMapping("/searchMember")
    @ResponseBody
    public List<MemberJoinForm> searchMember(@RequestParam("mnickname") String mnickname) {
        List<Members> members = memberService.findByMnickname(mnickname);
        if (members == null) {
            return new ArrayList<>();
        }
        List<MemberJoinForm> memberList = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            MemberJoinForm mf = new MemberJoinForm();
            mf.setMid(members.get(i).getMid());
            mf.setMpw(members.get(i).getMpw());
            mf.setMname(members.get(i).getMname());
            mf.setMnickname(members.get(i).getMnickname());
            memberList.add(mf);
        }
        System.out.println(memberList);
        return memberList;
    }
	// 일정 공유 기능
	@GetMapping("/sharePlanToMember")
	public String sharePlanToMember(@RequestParam("mid") String[] targetMid, @RequestParam("planid") Long[] planId) {
		// planIdArray
		shareService.sharePlanToMember(targetMid, planId);

		return "redirect:/viewPlan";
	}

	// 공유받은 일정 확인
//	@GetMapping("/receivePlanView")
//	public String receivePlanView(Model model, RedirectAttributes ra) {
//		Long checkLogin = (Long) session.getAttribute("loginId");
//		if (checkLogin == null) {
//			ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다");
//			return "redirect:/member/loginFormPage";
//		}
//		Long loginId = (Long) session.getAttribute("loginId");
//		List<ShareContent> shareList = shareService.findSharePlanList_All(loginId);
//		System.out.println(shareList);
//		model.addAttribute("shareList", shareList);
//		return "socket/receivePlanView";
//	}

	// 공유되었던 일정인지 확인하는 기능
	@GetMapping("/shareCheckByReceiverId")
	@ResponseBody
	public List<Long> shareCheckByReceiverId(@RequestParam("receiveMemberId") String receiveMid) {
		Long sendId = (Long) session.getAttribute("loginId");
		Members sendMembers = memberService.findById(sendId);
		Members receiveMembers = memberService.findBymid(receiveMid);
		List<Long> planIdList = new ArrayList<>();
		List<ShareContent> shareContent = shareService.findByReceiveMemberIdAndSendMemberId(receiveMembers,
				sendMembers);
		for (ShareContent sc : shareContent) {
			Long planId = sc.getPlanId().getId();
			planIdList.add(planId);
		}
		return planIdList;
	}

	@GetMapping("/planDetail/{planid}")
	public String planDetailByReview(@PathVariable("planid") Long planId, Model model, RedirectAttributes ra) {
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다");
			return "redirect:/member/loginFormPage";
		}
		System.out.println("플랜 : " + planService.findById(planId));
		Plans plan = planService.findById(planId);

		List<PlanDayDetail> planDetailList = planService.findDetailsByPlanId(plan.getId());
		List<String> dateList = new ArrayList<>();
		for (PlanDayDetail planDetail : planDetailList) {
			String detailDate = planDetail.getDetailDate();
			dateList.add(detailDate);
		}

		model.addAttribute("dateList", dateList);
		model.addAttribute("planid", plan.getId());
		return "/board/planDetail";

	}

	@GetMapping("/detailLoad")
	@ResponseBody
	public HashMap<String, Object> detailLoadPlan(@RequestParam("id") Long id, @RequestParam("date") String date) {

		Plans plan = planService.findById(id);

		List<Node> nodeList = new ArrayList<>();
		String pathPoints = null;
		List<String> dateList = new ArrayList<>();

		List<PlanDayDetail> planDetailList = planService.findDetailsByPlanId(plan.getId());
		for (PlanDayDetail planDetail : planDetailList) {
			String detailDate = planDetail.getDetailDate();
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
}