package kr.co._icia.finalProject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.Plans;
import kr.co._icia.finalProject.entity.ShareContent;
import kr.co._icia.finalProject.repository.MemberRepository;
import kr.co._icia.finalProject.repository.PlanRepository;
import kr.co._icia.finalProject.repository.ShareContentRepository;
import kr.co._icia.finalProject.socket.ShareToMemberHandler;

@Service
public class ShareContentService {

	@Autowired
	private ShareContentRepository contentRepository;

	@Autowired
	private HttpSession session;

	@Autowired
	private MemberRepository mRopository;

	@Autowired
	private PlanRepository pRepository;

	@Autowired
	private ShareContentRepository sRepository;

	@Autowired
	private ShareToMemberHandler shareToMemberHandler;

	public List<ShareContent> findByReceiveMemberIdAndSendMemberId(Members receiveMembers, Members sendMembers) {
	    return contentRepository.findByReceiveMemberIdAndSendMemberId(receiveMembers, sendMembers);
	  }
	
	// 일정 공유 기능
	public void sharePlanToMember(String[] targetMid, Long[] planIdList) {
		// 1. share 테이블 save;
		String sendMid = (String) session.getAttribute("loginMid");
		Members sendMemberId = mRopository.findByMid(sendMid);
		
		for (String receiveMid : targetMid) {
			Members receiveMemberId = mRopository.findByMid(receiveMid);
			for (Long planId : planIdList) {
				ShareContent shareContent = new ShareContent();
				shareContent.setSendMemberId(sendMemberId);
				shareContent.setReceiveMemberId(receiveMemberId);
				Plans plan = pRepository.findById(planId).orElse(null);
				shareContent.setPlanId(plan);

				sRepository.save(shareContent);
			}
		}
		// 2. 메세지 전송
		shareToMemberHandler.sendPlanToMember(sendMid, targetMid, planIdList);
	}

	public List<ShareContent> findSharePlanList(Long reciveMemberId) {
		List<ShareContent> contentList = contentRepository.findByMembersId(reciveMemberId);
		return contentList;
	}

	public ShareContent findById(Long scId) {
		return contentRepository.findById(scId).orElse(null);
	}

	public List<ShareContent> findSharePlanList_All(Long loginId) {
		List<ShareContent> contentList = contentRepository.findByReceiveMemberId_IdOrderByIdDesc(loginId);
		// CHECK_STATE = 1 UPDATE
		for (ShareContent shareContent : contentList) {
			shareContent.setCheckState(1);
			contentRepository.save(shareContent);
		}
		return contentList;
	}

	public ShareContent findByPlanId_Id(Long planId) {
		return contentRepository.findByPlanId_Id(planId);
	}

}