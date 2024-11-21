package kr.co._icia.finalProject.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import kr.co._icia.finalProject.dto.BestCheckForm;
import kr.co._icia.finalProject.dto.writeReviewForm;
import kr.co._icia.finalProject.entity.BestCheck;
import kr.co._icia.finalProject.entity.Board;
import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.Plans;
import kr.co._icia.finalProject.repository.BestCheckRepository;
import kr.co._icia.finalProject.repository.BoardRepository;
import kr.co._icia.finalProject.repository.MemberRepository;
import kr.co._icia.finalProject.repository.PlanRepository;

@Service
public class BoardService {
  @Autowired
  private HttpSession session;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PlanRepository planRepository;

  @Autowired
  private BestCheckRepository bestCheckRepository;

  public ArrayList<Long> findAllPlanId() {
    List<Board> reviewCheckList = boardRepository.findAll();
    ArrayList<Long> pIdList = new ArrayList<>();
    for (int i = 0; i < reviewCheckList.size(); i++) {
      pIdList.add(reviewCheckList.get(i).getPlan().getId());
    }
    System.out.println(pIdList);
    return pIdList;
  }

  public void registReview(writeReviewForm writeReviewForm) throws Exception {
    MultipartFile[] FileList = writeReviewForm.getBprofile();
    String fileNameList = null;
    for (MultipartFile bFile : FileList) {
      if (!bFile.isEmpty()) {

        System.out.println(FileList);
        String originalFilename = bFile.getOriginalFilename();
        System.out.println("업로드한 파일명 : " + originalFilename);
        String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        uuid = uuid.substring(0, 10);
        int suffixIndex = originalFilename.lastIndexOf(".");
        fileNameList = uuid + originalFilename.substring(suffixIndex);
        System.out.println("새 파일명 :" + fileNameList);

        String savePath = "C:\\final_workspace\\finalProject\\src\\main\\resources\\static\\imgs\\boardImg";

        File file = new File(savePath, fileNameList);
        bFile.transferTo(file);
        String tempFileName = writeReviewForm.getBprofilename();
        if (tempFileName == null) {
          writeReviewForm.setBprofilename(fileNameList);
        } else {
          writeReviewForm.setBprofilename(tempFileName + "," + fileNameList);// 제목,내용,종류,작성자,번호 ,파일명 담겨진다.
        }
      }

    }
    Object loginId = session.getAttribute("loginId");

    Members members = memberRepository.findById((Long) loginId).orElse(null);
    Plans plans = planRepository.findById((writeReviewForm.getId())).orElse(null);
    Board board = new Board();
    board.setFilename(writeReviewForm.getBprofilename());
    board.setPlan(plans);
    board.setMember(members);
    LocalDateTime date = LocalDateTime.now();
    String time = date.format(DateTimeFormatter.ofPattern("yyyy년MM월dd일 HH시"));
    board.setBoarddate(time);

    boardRepository.save(board);
  }

  public List<Board> findAll() {
    return boardRepository.findAll();
  }

  public Board findPlansId(Long planId) {
    return boardRepository.findByPlanId(planId);
  }

  public void registBest(BestCheckForm bestCheckForm) {
    BestCheck bestCheck = new BestCheck();
    Board bestBoard = boardRepository.findById(bestCheckForm.getBoardId()).orElse(null);
    System.out.println(bestBoard);
    Members checkedMember = memberRepository.findById(bestCheckForm.getMemberId()).orElse(null);
    System.out.println(checkedMember);
    bestCheck.setBoard(bestBoard);
    bestCheck.setMember(checkedMember);
    bestCheck.setBest(bestCheckForm.getBest() + 1);
    bestCheckRepository.save(bestCheck);
  }

  public List<BestCheck> BoardCheck(Long memberId) {
    List<BestCheck> memberCheck = bestCheckRepository.findByBoardId(memberId);
    System.out.println("memberCheck : " + memberCheck);
    return memberCheck;
  }
  public BestCheck memberCheckByBoardId(Long memberId,Long boardId) {
    BestCheck memberCheck = bestCheckRepository.findByMemberIdAndBoardId(memberId,boardId);
    System.out.println("memberCheck : " + memberCheck);
    return memberCheck;
  }
}
