package org.ssh.team2.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageRequestDTO {
    @Builder.Default
    private int page=1;
    @Builder.Default
    private int size=5;
    private String link;
    private String type;
    private String keyword;

    public String[] getTypes(){
        if(type==null || type.isEmpty()){
            return null;
        }
        return type.split(""); //twc=>types[0]="t"
    }
    public Pageable getPageable(String...props){
        return PageRequest.of(Math.max(this.page - 1, 0), size, Sort.by(props).descending());
    }
    public String getLink(){
        if(link==null){
            StringBuilder builder=new StringBuilder();
            builder.append("page="+this.page);
            builder.append("&size="+this.size);
            if(type!=null && type.length()>0){
                builder.append("&type="+type);
            }
            if(keyword!=null && keyword.length()>0){
                builder.append("&keyword="+keyword);
            }
            link=builder.toString();
        }
        return link;
    }
}
