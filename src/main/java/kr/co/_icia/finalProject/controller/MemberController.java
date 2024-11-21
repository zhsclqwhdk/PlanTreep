package kr.co._icia.finalProject.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co._icia.finalProject.dto.MemberJoinForm;
import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.ShareContent;
import kr.co._icia.finalProject.service.KakaoApiService;
import kr.co._icia.finalProject.service.MemberService;
import kr.co._icia.finalProject.service.ShareContentService;

@Controller
@RequestMapping("/member")
public class MemberController {
	@Autowired
	private MemberService memService;

	@Autowired
	private KakaoApiService kakaoApiService;

	@Autowired
	private ShareContentService shareService;

	@Autowired
	private HttpSession session;

	@GetMapping("/joinFormPage")
	public String signupPage(Model model) {
		MemberJoinForm memberJoinForm = new MemberJoinForm();
		model.addAttribute("memberJoinForm", memberJoinForm);
		return "member/joinForm";
	}

	@PostMapping("/joinFormPage")
	public String signUp(@ModelAttribute("memberJoinForm") @Valid MemberJoinForm memberJoinForm, BindingResult result)
			throws Exception {
		System.out.println(memberJoinForm);
		if (result.hasErrors()) {
			System.out.println(result.hasFieldErrors("mid"));
			return "member/joinForm";
		}
		try {
			memberJoinForm.setMdate(LocalDateTime.now());
			memService.registMember(memberJoinForm);
			return "redirect:/member/loginFormPage";

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/member/joinFormPage";
		}
	}

	@PostMapping("/KakaoJoinFormPage")
	public String kakaoSignUp(@ModelAttribute("memberJoinForm") @Valid MemberJoinForm memberJoinForm,
			BindingResult result) throws Exception {
		if (result.hasErrors()) {
			System.out.println(result.hasFieldErrors("mid"));
			return "member/kakaoJoinForm";
		}
		try {
			memberJoinForm.setMstate("K0");
			memberJoinForm.setMdate(LocalDateTime.now());
			memService.registMember(memberJoinForm);
			return "redirect:/member/loginFormPage";

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/member/KakaoJoinFormPage";
		}
	}

	@GetMapping("/idCheck")
	@ResponseBody
	public String checkId(@RequestParam("mid") String mid) {
		System.out.println(mid);
		Members member = memService.findBymid(mid);
		// MemberRepository.findByMid(mid);
		System.out.println(member);
		if (member == null) {
			return "Y";
		} else {
			return "N";
		}
	}

	@GetMapping("/NicknameCheck")
	@ResponseBody
	public String NicknameCheck(@RequestParam("mnickname") String mnickname) {
		System.out.println(mnickname);
		Members member = memService.findBymnickname(mnickname);
		// MemberRepository.findByMid(mid);
		System.out.println(member);
		if (member == null) {
			return "Y";
		} else {
			return "N";
		}
	}

	@GetMapping("/loginFormPage")
	public String memLogin() {
		System.out.println("로그인 페이지");
		return "member/loginForm";
	}

	@PostMapping("/loginFormPage")
	public String memberLogin(@RequestParam("mid") String mid, @RequestParam("mpw") String mpw, Model model,
			RedirectAttributes ra) {
		Members memberCheck = memService.findMembersByMidAndMpw(mid, mpw);
		if (memberCheck == null) {
			System.out.println("로그인 실패");
			return "redirect:/member/loginFormPage";
		} else {

			String loginMemberId = memberCheck.getMid();
			Long loginId = memberCheck.getId();
			String loginMnickname = memberCheck.getMnickname();
			System.out.println(loginMemberId);
			System.out.println(loginId);
			session.setAttribute("loginNickname", loginMnickname);
			session.setAttribute("loginMid", loginMemberId);
			session.setAttribute("loginId", loginId);

			// 상태가 0인 sharecontent를 찾아
			List<ShareContent> receivePlanList = shareService.findSharePlanList((Long) session.getAttribute("loginId"));
			// redirectAttributes로 저장
			ra.addFlashAttribute("shareContentCnt", receivePlanList.size());
			System.out.println("shareContentCnt : " + receivePlanList.size());
			ra.addFlashAttribute("receivePlanList", receivePlanList);

			return "redirect:/";
		}
	}

	@GetMapping("/logout")
	public String logoutMem() {
		session.invalidate();
		return "redirect:/";

	}

	@GetMapping("/KakaoAuthCode")
	public String getKakaoAuthCode() {
		String redirect_page = "";
		redirect_page = kakaoApiService.getKakaoAuthCode();

		return redirect_page;
	}

	@GetMapping("/Kakao")
	public String memberLoginkakao(@RequestParam("code") String code, Model model) {
		String kakaoToken = kakaoApiService.getKakaoAccessToken(code);
		Members kakaoUserInfo = kakaoApiService.getKakaoUserInfo(kakaoToken);
		String mid = null;
		if (kakaoUserInfo != null) {
			mid = kakaoUserInfo.getMid();
			Members member = memService.findBymid(mid);
			if (member == null) {

				MemberJoinForm memberJoinForm = new MemberJoinForm();
				memberJoinForm.setMid(kakaoUserInfo.getMid());
				memberJoinForm.setMname(kakaoUserInfo.getMname());
				model.addAttribute("memberJoinForm", memberJoinForm);

				return "member/kakaoJoinForm";
			} else {
				session.setAttribute("loginId", member.getId());
				session.setAttribute("loginMid", member.getMid());
				session.setAttribute("loginName", member.getMname());
				session.setAttribute("loginNickname", member.getMnickname());
				return "redirect:/";
			}

		}
		return "redirect:/";
	}

	@PostMapping("/canversLogin")
	@ResponseBody
	public String canversLogin(@RequestParam("mid") String mid, @RequestParam("mpw") String mpw) {
		System.out.println(mid);
		System.out.println(mpw);
		Members loginMember = memService.findMembersByMidAndMpw(mid, mpw);
		System.out.println(loginMember);
		if (loginMember == null) {
			return "N";
		} else {
			String loginMemberId = loginMember.getMid();
			Long loginId = loginMember.getId();
			String loginMnickname = loginMember.getMnickname();
			session.setAttribute("loginNickname", loginMnickname);
			session.setAttribute("loginMid", loginMemberId);
			session.setAttribute("loginId", loginId);
			return "Y";
		}
	}

	@GetMapping("/memInfo")
	public String memInfoPage(Model model, RedirectAttributes ra) {
		Object mid = session.getAttribute("loginMid");
		Members member = memService.findByMidCheck((String) mid);
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다");
			return "redirect:/member/loginFormPage";
		}
		model.addAttribute("form", member);

		return "/member/memInfo";
	}

	@GetMapping("/memEdit")
	public String memEditPage(Model model, RedirectAttributes ra) {
		Object mid = session.getAttribute("loginMid");
		Members member = memService.findByMidCheck((String) mid);
		Long checkLogin = (Long) session.getAttribute("loginId");
		if (checkLogin == null) {
			ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다");
			return "redirect:/member/loginFormPage";
		}

		model.addAttribute("form", member);
		return "/member/memEdit";
	}

	@PostMapping("/memEdit")
	public String memEdit(Model model, @ModelAttribute("form") @Valid MemberJoinForm form, BindingResult result)
			throws Exception {
		System.out.println(form);
		if (result.hasErrors()) {
			System.out.println(result.hasFieldErrors("mid"));
			return "/member/memEdit";
		}
		Long mid = (Long) session.getAttribute("loginId");
		memService.updateMember(form, mid);
		System.out.println(form.getMstate());
		return "redirect:/";
	}

}
