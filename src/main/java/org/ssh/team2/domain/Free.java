package org.ssh.team2.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_free")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Free extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private String title;
    @Column(nullable = false, length = 10000)
    private String content;
    @ColumnDefault(value = "0")
    private int readCount;
    // 조회수 증가
    public  void updateReadCount(){
        readCount = readCount+1;
    }
    // 수정 시 제목, 내용 변경
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @OneToMany(mappedBy = "free", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    private String ff0;
    private String ff1;
    private String ff2;
    private String ff3;
}
