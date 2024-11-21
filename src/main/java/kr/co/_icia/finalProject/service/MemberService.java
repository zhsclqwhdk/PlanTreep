package kr.co._icia.finalProject.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import kr.co._icia.finalProject.dto.MemberJoinForm;
import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.repository.MemberRepository;

@Service
public class MemberService {

  @Autowired
  private MemberRepository mRepository;

  public void registMember(MemberJoinForm memberJoinForm) throws Exception {
    memberJoinForm.setMdate(LocalDateTime.now());
	  Members member = Members.createMembers(memberJoinForm);
    MultipartFile FileList = memberJoinForm.getMprofile();
    if (!FileList.isEmpty()) {

      System.out.println(FileList);
      String originalFilename = FileList.getOriginalFilename();
      System.out.println("업로드한 파일명 : " + originalFilename);
      String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
      uuid = uuid.substring(0, 10);
      int suffixIndex = originalFilename.lastIndexOf(".");
      String boardFileName = uuid + "-" + originalFilename.substring(suffixIndex);
      System.out.println("새 파밀명 :" + boardFileName);

      String savePath = "C:\\final_workspace\\finalProject\\src\\main\\resources\\static\\imgs\\profile";

      File file = new File(savePath, boardFileName);
      FileList.transferTo(file);
      String tempFileName = memberJoinForm.getMprofilename();
      if (tempFileName == null) {
        memberJoinForm.setMprofilename(boardFileName);
      } else {
        memberJoinForm.setMprofilename(tempFileName + "," + boardFileName);// 제목,내용,종류,작성자,번호 ,파일명 담겨진다.
      }
    }
    mRepository.save(member);
  }

  public List<Members> findMembers() {
    List<Members> mList = mRepository.findAll();
    return mList;
  }

  public List<Members> findByMnickname(String mnickname) {
    List<Members> memberList = mRepository.findByMnicknameContaining(mnickname);
    return memberList;
  }

  public Members findByMidCheck(String mid) {
    Members checkMember = mRepository.findByMid(mid);
    return checkMember;
  }

  public Members findMembersByMidAndMpw(@RequestParam("mid") String mid, @RequestParam("mpw") String mpw) {
    return mRepository.findByMidAndMpw(mid, mpw);
  }

  public List<Members> findAll() {
    return mRepository.findAll();
  }

  public Members findById(Long memId) {
    return mRepository.findById(memId).orElse(null);
  }

  public String memIdCheck(String mid) {
    System.out.println("MemberService -memberIdCheck 호출");
    Members member = mRepository.findByMid(mid);
    if (member == null) {
      return "Y";
    } else {
      return "N";
    }

  }

  public Members findBymid(String receiveMid) {
    return mRepository.findByMid(receiveMid);
  }

  public Members findByMember(Members kakaoUser) {
    return mRepository.findByMid(kakaoUser.getMid());
  }

  public void updateMember(MemberJoinForm form, Long memberId) throws Exception {
    Members member = mRepository.findById(memberId).orElse(null);
    member.setMpw(form.getMpw());
    member.setMname(form.getMname());
    member.setMphone(form.getMphone());
    member.setMemail(form.getMemail());
    member.setMdomain(form.getMdomain());
    member.setJibunAddress(form.getJibunAddress());
    member.setPostcode(form.getPostcode());
    member.setRoadAddress(form.getRoadAddress());
    member.setDetailAddress(form.getDetailAddress());
    member.setExtraAddress(form.getExtraAddress());
    member.setMnickname(form.getMnickname());

    mRepository.save(member);
  }

public Members findBymnickname(String mnickname) {
	return mRepository.findBymnickname(mnickname);
}

}