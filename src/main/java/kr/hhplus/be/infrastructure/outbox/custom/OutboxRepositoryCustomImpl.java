package kr.hhplus.be.infrastructure.outbox.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.entity.QPaymentOutbox;
import kr.hhplus.be.domain.outbox.enumtype.OutboxStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OutboxRepositoryCustomImpl implements OutboxRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PaymentOutbox> findUnSuccessedEvents(int limit) {
        QPaymentOutbox paymentOutbox = QPaymentOutbox.paymentOutbox;

        return queryFactory
                .selectFrom(paymentOutbox)
                .where(paymentOutbox.status.ne(OutboxStatus.SUCCESS)) // SUCCESS가 아닌 이벤트 조회
                .orderBy(paymentOutbox.createdAt.asc()) // 오래된 것부터 가져오기
                .limit(limit) // 최대 N개만 가져오기
                .fetch();
    }
}
