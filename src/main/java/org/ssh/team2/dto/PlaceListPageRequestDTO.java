package org.ssh.team2.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ssh.team2.domain.PlaceCategory;

@Setter
@Getter
@ToString
public class PlaceListPageRequestDTO extends PageRequestDTO{
    public PlaceListPageRequestDTO(){
        super();
        super.setSize(9);
    }
    public PlaceListPageRequestDTO(int page, int size){
        super.setPage(page);
        super.setSize(size);
    }
    private PlaceCategory category; // DB 조회용 enum
    private String searchFilter;    // 검색용 문자열



}
