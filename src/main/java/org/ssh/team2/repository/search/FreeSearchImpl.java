package org.ssh.team2.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.ssh.team2.domain.Free;
import org.ssh.team2.domain.QFree;

import java.util.List;

@Repository
public class FreeSearchImpl extends QuerydslRepositorySupport implements FreeSearch {
    public FreeSearchImpl() { super(Free.class); }

    @Override
    public Page<Free> searchAll(String[] type, String keyword, Pageable pageable) {
        QFree qfree = QFree.free;
        JPQLQuery<Free> query = from(qfree);

        if(type != null && type.length > 0 && keyword != null) {
            BooleanBuilder builder = new BooleanBuilder();
            for (String types : type) {
                if (types.contains("t")) builder.or(qfree.title.containsIgnoreCase(keyword));
                if (types.contains("c")) builder.or(qfree.content.containsIgnoreCase(keyword));
                if (types.contains("w")) builder.or(qfree.member.username.containsIgnoreCase(keyword));
            }
            query.where(builder);
        }
        query.where(qfree.id.gt(0));
        this.getQuerydsl().applyPagination(pageable, query);
        List<Free> list = query.fetch();
        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);
    }
}
