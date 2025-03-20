package com.quicksand.bigdata.query.utils;

import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * AuthUtil
 *
 * @author xupei
 * @date 2022/8/1
 */
public final class AuthUtil {

    private AuthUtil() {

    }

    public static UserSecurityDetails getUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication
                && null != authentication.getPrincipal()
                && authentication.getPrincipal() instanceof UserSecurityDetails) {
            return (UserSecurityDetails) authentication.getPrincipal();
        }
        return null;
    }


}
