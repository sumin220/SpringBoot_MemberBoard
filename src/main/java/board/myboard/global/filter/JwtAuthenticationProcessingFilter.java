package board.myboard.global.filter;

import board.myboard.domain.member.entity.Member;
import board.myboard.domain.member.repository.MemberRepository;
import board.myboard.global.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    /**
     * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행x, 바로 튕기기
     * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저 정보 저장 후 필터 계속 진행
     */

    private final JwtService jwtService;

    private final MemberRepository memberRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    //사용자에게 부여될 권한을 다른 권한으로 매핑 / 아무런 변경 없이 입력받은 권한은 그대로 반환 = 매핑할 필요가 없다.

    private final String NO_CHECK_URL = "/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return; //안해주면 아래로 내려가서 계속 필터를 진행해버림
        }

        String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null); //2

        if (refreshToken != null) {
            checkRefreshToeknAndReIssueAccessToken(response, refreshToken);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);//4
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresent(

                accessToken -> jwtService.extractUsername(accessToken).ifPresent(

                        username -> memberRepository.findByUsername(username).ifPresent(

                                member -> saveAuthentication(member)
                        )
                )
        );

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Member member) {
        UserDetails user = User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authoritiesMapper.mapAuthorities(user.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext(); //5
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void checkRefreshToeknAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {

        memberRepository.findByRefreshToken(refreshToken).ifPresent(
                member -> jwtService.sendAccessToken(response, jwtService.createAccessToken(member.getUsername()))
        );
    }
}
