package org.ssh.team2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class PageResponseDTO<E> {
    private int page;  //현재페이지
    private int size; // 블록사이즈
    private int total; // 전체레코드 수
    private int start;  // 블록의 시작페이지
    private int end;   // 블록의 end 페이지
    private boolean next; //다음 페이지 여부
    private boolean prev; // 이전페이지 여부
    private List<E> dtoList; // 페이징할 dtoList

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {
        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        if(total <= 0) {
            this.start = 1;
            this.end = 1;
            this.prev = false;
            this.next = false;
            return;
        }
        int pageCountPerBlock = 3; // 한 블록당 페이지 수
        int last = (int) Math.ceil(total / (double) size); // 전체 마지막 페이지 번호

        this.end = (int) (Math.ceil(this.page / (double) pageCountPerBlock)) * pageCountPerBlock;
        this.start = this.end - (pageCountPerBlock - 1);

        this.end=end>last?last:end;
        this.prev=this.start>1;
        this.next=this.end<last;  // total> this.end*size
    }
}
