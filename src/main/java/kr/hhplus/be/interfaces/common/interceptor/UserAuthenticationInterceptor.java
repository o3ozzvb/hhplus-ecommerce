package kr.hhplus.be.interfaces.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.domain.exception.CommerceBadRequestException;
import kr.hhplus.be.domain.exception.CommerceNotFoundException;
import kr.hhplus.be.domain.exception.ErrorCode;
import kr.hhplus.be.domain.user.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class UserAuthenticationInterceptor implements AsyncHandlerInterceptor {

    private final UserRepository userRepository;

    public UserAuthenticationInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Controller 진입 전 실행.
     * return값이 true이면 Controller 정상 실행, false이면 Controller 진입 X
     * Controller 진입하지 않고 바로 응답 객체를 클라이언트에 보낸다.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("x-user-id");
        if (StringUtils.isBlank(userId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "header parameter is missing.");
            throw new CommerceBadRequestException(ErrorCode.PARAMETER_IS_MISSING);
        }

        // user 정보 조회
        Long parseUserId = Long.parseLong(userId);
        try {
            userRepository.findById(parseUserId);
        } catch (CommerceNotFoundException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "this user does not have authentication.");
            throw new CommerceNotFoundException(ErrorCode.USER_NOT_EXIST);
        }
        return true;
    }

    /**
     * Controller 실행 후 View 가 render 되기 전에 실행
     */
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /**
     * View가 정상적으로 render 된 후 실행
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

    /**
     * 비동기 요청 시에만 실행. postHandle,afterColmpletion 대신 해당 메소드 실행
     */
    @Override
    public void afterConcurrentHandlingStarted(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
    }
}
