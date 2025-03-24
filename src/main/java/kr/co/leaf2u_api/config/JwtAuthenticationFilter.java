package kr.co.leaf2u_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.leaf2u_api.account.AccountRepository;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.member.JwtTokenProvider;
import kr.co.leaf2u_api.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);  // 요청에서 토큰 추출

        if (token != null && jwtTokenProvider.validateToken(token)) {  // ⬅️ validateToken 사용

            // 유효한 토큰일 경우, SecurityContext에 설정 (인증 로직 추가 가능)
            SecurityContextHolder.getContext().setAuthentication(null);

            /* memberIdx, savingAccountIdx 추출 - 문경미 */
            String email = jwtTokenProvider.getEmailFromToken(token);
            Optional<Member> member = memberRepository.findByEmail(email);
            Long memberIdx = member.get().getIdx();
            TokenContext.setMemberIdx(memberIdx);

            Optional<Account> account = accountRepository.findAccountByMember(memberIdx);
            Long savingAccountIdx = null;
            if (account.isPresent()) {
                savingAccountIdx = account.get().getIdx();
                TokenContext.setSavingAccountIdx(savingAccountIdx);
            }
        }

        filterChain.doFilter(request, response);
    }

}
