package com.ivangochev.raceratingapi.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


public class CookieUtils {
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
//    public static final String REFRESH_TOKEN_EXPIRATION_TIME;
    public static Cookie generateCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setSecure(Boolean.TRUE);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }
    public static String getCookieFromRequest(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
