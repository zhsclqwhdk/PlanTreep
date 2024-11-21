package kr.co._icia.finalProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestCheckForm{
    private Long best = (long) 0;
    private Long memberId; // Member의 ID만 포함
    private Long boardId; // Board의 ID만 포함
}