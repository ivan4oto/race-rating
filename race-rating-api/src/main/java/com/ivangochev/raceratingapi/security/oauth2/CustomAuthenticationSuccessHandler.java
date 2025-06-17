package com.ivangochev.raceratingapi.security.oauth2;

import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.security.TokenProvider;
import com.ivangochev.raceratingapi.security.jwt.RefreshToken;
import com.ivangochev.raceratingapi.security.jwt.RefreshTokenService;
import com.ivangochev.raceratingapi.user.UserRepository;
import com.ivangochev.raceratingapi.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.redirectUri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handle(request, response, authentication);
        super.clearAuthenticationAttributes(request);
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = redirectUri.isEmpty() ?
                determineTargetUrl(request, response, authentication) : redirectUri;
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = tokenProvider.generate(customUserDetails, Boolean.FALSE);
        long tokenExpiresAt = tokenProvider.getTokenExpirationTimestamp(Boolean.FALSE);
        response.addHeader("Access-Token-Expires-At", String.valueOf(tokenExpiresAt));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(customUserDetails.getUsername());
        Cookie accessCookie = CookieUtils.generateCookie(CookieUtils.ACCESS_TOKEN, accessToken);
        Cookie refreshCookie = CookieUtils.generateCookie(CookieUtils.REFRESH_TOKEN, refreshToken.getToken());
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}