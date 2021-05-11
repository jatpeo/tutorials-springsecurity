package cn.jpps.security.config_12;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: jiatp
 * @Date: 2020/12/2 14:57
 * @Description: P12 定制WebAuthenticationDetails
 */

public class MyWebAuthenticationDetails extends WebAuthenticationDetails {

    private boolean isPassed; //默认false

    public MyWebAuthenticationDetails(HttpServletRequest req) {
        super(req);
        String code = req.getParameter("code");
        String verify_code = (String) req.getSession().getAttribute("verify_code");
        if (code != null && verify_code != null && code.equals(verify_code)) {
            isPassed = true;
        }
    }
    public boolean isPassed() {
        return isPassed;
    }
}
