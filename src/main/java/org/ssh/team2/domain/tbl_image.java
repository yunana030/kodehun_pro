package org.ssh.team2.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "place")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class tbl_image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int ord;    //이미지 순서
    private String filename;
    private String uuid;
    private boolean image;  //이미지 넣었는지 안 넣었는지!

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id") // FK → tbl_place.id
    private Place place;

}
