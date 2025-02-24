package kr.hhplus.be.domain.outbox.enumtype;


public enum OutboxStatus {
    INIT,   /* 이벤트 발행 등록 */
    SUCCESS, /* 발행 성공 */
    FAIL /* 발행 실패. 카프카에 메시지를 전송했지만 실패 */
}
