<%@ page language="java"
    contentType="text/plain; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.time.LocalDateTime,java.util.*,jakarta.servlet.http.*,java.io.*,java.nio.charset.StandardCharsets"%>
<%!
    // ===== 유틸리티 (서블릿의 메서드를 JSP 선언부로 이식) =====
    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }
    private static int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    /** 바디 미리보기(텍스트 우선, 실패 시 바이너리 헥사). 기본 2048바이트 */
    private static String readBodyPreview(HttpServletRequest req) throws IOException {
        return readBodyPreview(req, 2048);
    }
    private static String readBodyPreview(HttpServletRequest req, int maxBytes) throws IOException {
        // 요청 인코딩 지정(없으면 UTF-8)
        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        }
        // 문자 스트림 우선 시도
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            int total = 0;
            char[] buf = new char[512];
            int n;
            while ((n = br.read(buf)) != -1 && total < maxBytes) {
                int toAppend = Math.min(n, maxBytes - total);
                sb.append(buf, 0, toAppend);
                total += toAppend;
            }
            if (br.read() != -1) sb.append("\n...[truncated]");
            return sb.toString();
        } catch (IllegalStateException e) {
            // 이미 Reader 사용됐거나 바이너리인 경우 InputStream으로 헥사 출력
            StringBuilder hex = new StringBuilder();
            try (ServletInputStream is = req.getInputStream()) {
                int total = 0;
                byte[] b = new byte[512];
                int n;
                while ((n = is.read(b)) != -1 && total < maxBytes) {
                    int toAppend = Math.min(n, maxBytes - total);
                    for (int i = 0; i < toAppend; i++) {
                        hex.append(String.format("%02x ", b[i]));
                    }
                    total += toAppend;
                }
                if (is.read() != -1) hex.append("...[truncated]");
            }
            return "(binary) " + hex.toString();
        }
    }
%>
<%
    // ===== (0) 공통 응답 메타 =====
    response.setCharacterEncoding("UTF-8");
    response.setHeader("X-Example", "LifeCycleJsp");
    // 테스트 편의: 세션 기본 속성
    if (session.getAttribute("visitedAt") == null) {
        session.setAttribute("visitedAt", LocalDateTime.now().toString());
    }

    // ===== (1) 쿼리로 쿠키 설정 (?setCookie=key:value) =====
    String cookieParam = request.getParameter("setCookie");
    if (cookieParam != null && cookieParam.contains(":")) {
        String[] kv = cookieParam.split(":", 2);
        Cookie c = new Cookie(kv[0], kv[1]);
        c.setPath("/");
        c.setHttpOnly(true);
        // c.setSecure(true); // HTTPS 환경에 맞게
        c.setMaxAge(60 * 10); // 10분
        response.addCookie(c);
    }

    // ===== (2) 특수 동작: redirect / error =====
    String action = request.getParameter("action");
    if ("redirect".equalsIgnoreCase(action)) {
        String to = request.getParameter("to");
        response.sendRedirect(to != null ? to : (request.getContextPath() + "/life.jsp"));
        return;
    } else if ("error".equalsIgnoreCase(action)) {
        int code = parseIntOrDefault(request.getParameter("code"), 400);
        String msg = request.getParameter("msg");
        if (msg != null) response.sendError(code, msg);
        else response.sendError(code);
        return;
    }

    // ===== (3) 본문 출력 구성 (서블릿과 동일한 섹션) =====
    StringBuilder outBuf = new StringBuilder(8_192);

    outBuf.append("=== Java Servlet Request/Response Deep-Dive (JSP Edition) ===\n\n");

    // Time
    outBuf.append("# Time\n");
    outBuf.append("now: ").append(LocalDateTime.now()).append('\n');
    outBuf.append('\n');

    // Request Line
    outBuf.append("# Request Line\n");
    outBuf.append("method           : ").append(request.getMethod()).append('\n');
    outBuf.append("protocol         : ").append(request.getProtocol()).append('\n');
    outBuf.append("scheme           : ").append(request.getScheme()).append('\n');
    outBuf.append("requestURI       : ").append(request.getRequestURI()).append('\n');
    outBuf.append("requestURL       : ").append(request.getRequestURL()).append('\n');
    outBuf.append("queryString      : ").append(nullToEmpty(request.getQueryString())).append('\n');
    outBuf.append("contextPath      : ").append(request.getContextPath()).append('\n');
    outBuf.append("servletPath      : ").append(request.getServletPath()).append('\n');
    outBuf.append("pathInfo         : ").append(nullToEmpty(request.getPathInfo())).append('\n');
    outBuf.append("pathTranslated   : ").append(nullToEmpty(request.getPathTranslated())).append('\n');
    outBuf.append('\n');

    // Endpoints
    outBuf.append("# Endpoints\n");
    outBuf.append("serverName       : ").append(request.getServerName()).append('\n');
    outBuf.append("serverPort       : ").append(request.getServerPort()).append('\n');
    outBuf.append("localAddr        : ").append(request.getLocalAddr()).append('\n');
    outBuf.append("localName        : ").append(request.getLocalName()).append('\n');
    outBuf.append("localPort        : ").append(request.getLocalPort()).append('\n');
    outBuf.append("remoteAddr       : ").append(request.getRemoteAddr()).append('\n');
    outBuf.append("remoteHost       : ").append(request.getRemoteHost()).append('\n');
    outBuf.append("remotePort       : ").append(request.getRemotePort()).append('\n');
    outBuf.append('\n');

    // Headers
    outBuf.append("# Headers\n");
    Enumeration<String> hnames = request.getHeaderNames();
    while (hnames.hasMoreElements()) {
        String h = hnames.nextElement();
        outBuf.append(h).append(": ").append(request.getHeader(h)).append('\n');
    }
    outBuf.append('\n');

    // Content/Locale
    outBuf.append("# Content/Locale\n");
    outBuf.append("contentType      : ").append(nullToEmpty(request.getContentType())).append('\n');
    outBuf.append("contentLength    : ").append(request.getContentLength()).append('\n');
    outBuf.append("characterEncoding: ").append(nullToEmpty(request.getCharacterEncoding())).append('\n');
    outBuf.append("Locales (Accept-Language): ");
    StringJoiner locJoiner = new StringJoiner(", ");
    Enumeration<Locale> locs = request.getLocales();
    while (locs.hasMoreElements()) locJoiner.add(locs.nextElement().toLanguageTag());
    outBuf.append(locJoiner.length() == 0 ? "(none)" : locJoiner.toString()).append('\n');
    outBuf.append('\n');

    // Parameters
    outBuf.append("# Parameters\n");
    Map<String,String[]> pmap = request.getParameterMap();
    if (pmap.isEmpty()) {
        outBuf.append("(no parameters)\n");
    } else {
        for (Map.Entry<String,String[]> e : pmap.entrySet()) {
            outBuf.append(e.getKey()).append(" = ");
            StringJoiner sj = new StringJoiner(", ");
            for (String v : e.getValue()) sj.add(v);
            outBuf.append('[').append(sj.toString()).append(']').append('\n');
        }
    }
    outBuf.append('\n');

    // Cookies
    outBuf.append("# Cookies\n");
    Cookie[] cookies = request.getCookies();
    if (cookies == null || cookies.length == 0) {
        outBuf.append("(no cookies)\n");
    } else {
        for (Cookie c : cookies) {
            outBuf.append(c.getName()).append('=').append(c.getValue())
                  .append(" ; Path=").append(c.getPath())
                  .append(" ; HttpOnly=").append(c.isHttpOnly())
                  .append(" ; MaxAge=").append(c.getMaxAge())
                  .append('\n');
        }
    }
    outBuf.append('\n');

    // 방문 카운터 (세션 유지 예시)
    Integer visit = (Integer) session.getAttribute("sess.visit");
    if (visit == null) visit = 0;
    session.setAttribute("sess.visit", ++visit);

    // Session
    outBuf.append("# Session\n");
    HttpSession sess = request.getSession(true);
    outBuf.append("sessionId        : ").append(sess.getId()).append('\n');
    outBuf.append("isNew            : ").append(sess.isNew()).append('\n');
    outBuf.append("creationTime     : ").append(sess.getCreationTime()).append('\n');
    outBuf.append("lastAccessedTime : ").append(sess.getLastAccessedTime()).append('\n');
    outBuf.append("maxInactiveInterval(s): ").append(sess.getMaxInactiveInterval()).append('\n');
    outBuf.append("attr.visitedAt   : ").append(String.valueOf(sess.getAttribute("visitedAt"))).append('\n');
    outBuf.append("sess.visit       : ").append(session.getAttribute("sess.visit")).append('\n');
    outBuf.append('\n');

    // Body preview
    outBuf.append("# Body (first 2048 bytes if present)\n");
    if (request.getContentLength() > 0 || request.getHeader("Transfer-Encoding") != null) {
        outBuf.append(readBodyPreview(request)).append('\n');
    } else {
        outBuf.append("(no body)\n");
    }
    outBuf.append('\n');

    // Response tips
    outBuf.append("# Response Tips\n");
    outBuf.append("- response.setStatus(int), setHeader(String,String), addCookie(Cookie)\n");
    outBuf.append("- response.sendRedirect(url) for redirect\n");
    outBuf.append("- response.sendError(code[, message]) for error\n");
    outBuf.append("- response.getWriter() for text, response.getOutputStream() for binary\n");

    // 실제 출력
    out.print(outBuf.toString());
%>
