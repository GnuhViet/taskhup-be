package com.taskhub.project.filter;

import com.taskhub.project.core.auth.authentication.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    public static final int TOKEN_START_IDX = 7; //"Bearer "

    private final Set<String> skipUrls = Set.of(
            "/api/v1/auth/register",
            "/api/v1/auth/authenticate"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String authorHeader = request.getHeader("X-author");
        final String token;

        if (authenHeader == null || !authenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authenHeader.substring(TOKEN_START_IDX);

        try {
            JWTService.DecodedToken decodedToken = jwtService.decodeToken(token);
            if (decodedToken.getUserId() == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (authorHeader == null) {
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(decodedToken.getUserId(), null, null);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            else {
                var authorToken = jwtService.decodeToken(authorHeader);
                var userId = decodedToken.getUserId();
                var workSpaceId = authorToken.getRoleWithActions().getWorkSpaceId();
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorToken.getRoleWithActions().getActions().forEach(action -> {
                    authorities.add(new SimpleGrantedAuthority(action));
                });

                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(userId, workSpaceId, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setHeader(HttpHeaders.WARNING, e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return skipUrls.stream().anyMatch(p -> pathMatcher.match(p, request.getRequestURI()));
    }
}
