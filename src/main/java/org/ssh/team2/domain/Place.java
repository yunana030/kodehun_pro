package org.ssh.team2.domain;

import jakarta.persistence.*;
import lombok.*;
import org.ssh.team2.domain.BaseEntity;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.PlaceCategory;
import org.ssh.team2.domain.tbl_image;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "place")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //글 순서에 대한 번호

    @Column(nullable = false)
    private String placename;       //장소명

    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Column(nullable = false)
    private String addr;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(nullable = false)
    private String sido;    //lcode

    @Column(nullable = false)
    private int readcount;

    @Column(nullable = false)
    private int like_count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id")
    private Member member;

//    이미지 관련

    //게시글이 제거될 때 이미지도 제거되게
    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<tbl_image> images = new HashSet<>();

// 이미지 추가
    public void addImage(String uuid, String filename, boolean image) {
        tbl_image img = tbl_image.builder()
                .uuid(uuid)
                .filename(filename)
                .image(image)
                .place(this)
                .ord(this.images.size()) // 순서 지정할 때 필요!(썸네일 쓸 거니까)
                .build();
        this.images.add(img);
    }
    // 이미지 제거
    public void removeImages() {
        images.forEach(img -> img.setPlace(null));
        images.clear();
    }

    public void upReadcount() {
        readcount++;
    }
}
