package org.ssh.team2.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.ssh.team2.domain.Place;
import org.ssh.team2.domain.PlaceCategory;
import org.ssh.team2.domain.QPlace;

import java.util.List;

@Repository
public class PlaceSearchImpl extends QuerydslRepositorySupport implements PlaceSearchRepository {
    public PlaceSearchImpl() {
        super(Place.class);
    }

//    builder -> Querydsl에서 where 조건을 유연하게 쓰기 위해 사용하는 객체
    @Override
    public Page<Place> searchAll(String type, String keyword,
                                 PlaceCategory category, Pageable pageable) {

        QPlace qPlace = QPlace.place;
        JPQLQuery<Place> query = from(qPlace);
        //if밖에 놓으면 항상 생성되므로 keyword가 없어도 존재하게
        BooleanBuilder builder = new BooleanBuilder();

//        검색어와 카테고리가 같이 동작할 때 별도로 필요한 부분
        if(category != null) {
            builder.and(qPlace.category.eq(category));
        }

//        검색어 필터
        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder searchBuilder = new BooleanBuilder();

            if (type == null || type.isEmpty() || type.equals("all")) { // '선택안함' 눌렀을 떄
                searchBuilder.or(qPlace.placename.containsIgnoreCase(keyword))
                        .or(qPlace.content.containsIgnoreCase(keyword))
                        .or(qPlace.sido.containsIgnoreCase(keyword))
                        .or(qPlace.member.username.containsIgnoreCase(keyword));
            } else {
                switch (type) {
                    case "pn":
                        searchBuilder.or(qPlace.placename.containsIgnoreCase(keyword));
                        break;
                    case "c":
                        searchBuilder.or(qPlace.content.containsIgnoreCase(keyword));
                        break;
                    case "u":
                        searchBuilder.or(qPlace.member.username.containsIgnoreCase(keyword));
                        break;
                    case "s":
                        searchBuilder.or(qPlace.sido.containsIgnoreCase(keyword));
                        break;
                    default:
                        searchBuilder.or(qPlace.placename.containsIgnoreCase(keyword))
                                .or(qPlace.content.containsIgnoreCase(keyword))
                                .or(qPlace.sido.containsIgnoreCase(keyword))
                                .or(qPlace.member.username.containsIgnoreCase(keyword));
                        break;
                }
            }
            builder.and(searchBuilder); //조건들이 더해지면 최종적으로 합침
        }
        query.where(builder);   //조건 적용함!

        this.getQuerydsl().applyPagination(pageable, query);    //페이징, Querydsl이 pageable 객체를 쿼리에 적용
        List<Place> list = query.fetch();   //실제 DB에서 맞는 결과 리스트를 찾아옴
        long count = query.fetchCount();    //조건에 맞는 전체 레코드 수를 조회
        return new PageImpl<>(list, pageable, count);
    }
}
