package org.igetwell.web;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 基础类控制器
 */
public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();

    protected static ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();

    protected static ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();

    @ModelAttribute
    private void setReqAndResp(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        this.session.set(session);
        this.request.set(request);
        this.response.set(response);
    }
}
