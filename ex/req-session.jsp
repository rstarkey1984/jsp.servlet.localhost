<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- 한 페이지에서 입력/처리/출력을 모두 수행 (JSTL/EL 없이 스크립틀릿만 사용) --%>
<%!
    // 간단 HTML 이스케이프 (XSS 방지)
    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    // POST 한글 파라미터 인코딩
    if ("POST".equalsIgnoreCase(request.getMethod())) {
        request.setCharacterEncoding("UTF-8");
    }

    // 세션 초기화 옵션
    if ("1".equals(request.getParameter("resetSession"))) {
        session.invalidate();
        // 새 세션 획득
        session = request.getSession(true);
    }

    // 폼 파라미터 수집
    String name  = request.getParameter("name");
    String age   = request.getParameter("age");
    String memo  = request.getParameter("memo");
    String[] hobbies = request.getParameterValues("hobby"); // checkbox 다중

    // request 영역에 보관 (이번 요청에서만 사용)
    request.setAttribute("req.name",  name);
    request.setAttribute("req.age",   age);
    request.setAttribute("req.memo",  memo);
    request.setAttribute("req.hobby", hobbies);

    // session 영역에 보관 (브라우저가 닫히거나 invalidate 전까지 유지)
    if (name != null && name.trim().length() > 0)  session.setAttribute("sess.name",  name.trim());
    if (age  != null && age.trim().length() > 0)   session.setAttribute("sess.age",   age.trim());
    if (memo != null && memo.trim().length() > 0)  session.setAttribute("sess.memo",  memo.trim());
    if (hobbies != null && hobbies.length > 0)     session.setAttribute("sess.hobby", hobbies);

    // 방문 카운터 (세션 유지 예시)
    Integer visit = (Integer) session.getAttribute("sess.visit");
    if (visit == null) visit = 0;
    session.setAttribute("sess.visit", ++visit);

    // 편의용 꺼내기
    String sName  = (String) session.getAttribute("sess.name");
    String sAge   = (String) session.getAttribute("sess.age");
    String sMemo  = (String) session.getAttribute("sess.memo");
    String[] sHobby = (String[]) session.getAttribute("sess.hobby");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Request / Session 데모 (JSTL 없음)</title>
<style>
    :root { color-scheme: light dark; }
    body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, "Apple SD Gothic Neo", "Malgun Gothic", sans-serif;
           max-width: 960px; margin: 24px auto; padding: 0 16px; }
    h1 { margin: 0 0 12px; }
    .note { color: #666; font-size: 0.95rem; margin-bottom: 16px; }
    .card { border: 1px solid #d0d7de; border-radius: 12px; padding: 16px; margin-bottom: 20px; }
    .grid { display: grid; gap: 12px; grid-template-columns: 1fr 1fr; }
    label { display: block; font-weight: 600; margin-bottom: 6px; }
    input[type="text"], textarea, select { width: 100%; padding: 8px 10px; border: 1px solid #c9c9c9; border-radius: 8px; }
    .row { display: grid; grid-template-columns: 160px 1fr; padding: 8px 0; border-bottom: 1px dashed #e0e0e0; }
    .row:last-child { border-bottom: 0; }
    .key { font-weight: 600; color: #555; }
    .value { white-space: pre-wrap; }
    .actions { display: flex; gap: 8px; }
    button, .linkbtn {
        display: inline-block; border: 1px solid #1f6feb; background: #1f6feb; color: #fff;
        padding: 10px 14px; border-radius: 10px; cursor: pointer; text-decoration: none;
    }
    .ghost { background: transparent; color: #1f6feb; }
    .muted { font-size: 0.9rem; color: #777; }
    .pill { display:inline-block; padding: 2px 8px; border:1px solid #c9c9c9; border-radius:999px; margin-right:6px; font-size:.9rem;}
</style>
</head>
<body>

<h1>Request / Session 실습 (JSTL 없이)</h1>
<p class="note">폼에 값을 입력해서 전송하면, <b>request</b>와 <b>session</b>에 저장된 결과가 아래 영역에 출력됩니다.</p>

<div class="card">
    <h2>요청/세션 메타정보</h2>
    <div class="row"><div class="key">요청 URL</div><div class="value"><%= request.getRequestURL() %></div></div>
    <div class="row"><div class="key">요청 방식</div><div class="value"><%= request.getMethod() %></div></div>
    <div class="row"><div class="key">세션 ID</div><div class="value"><%= session.getId() %></div></div>
    <div class="row"><div class="key">세션 생성시각</div><div class="value"><%= new java.util.Date(session.getCreationTime()) %></div></div>
</div>

<div class="card">
    <h2>이번 요청에 저장된 값 (request scope)</h2>
    <div class="row"><div class="key">name</div><div class="value"><%= esc((String)request.getAttribute("req.name")) %></div></div>
    <div class="row"><div class="key">age</div><div class="value"><%= esc((String)request.getAttribute("req.age")) %></div></div>
    <div class="row"><div class="key">hobby</div>
        <div class="value">
            <%
                String[] reqH = (String[]) request.getAttribute("req.hobby");
                if (reqH != null && reqH.length > 0) {
                    for (String h : reqH) { %><span class="pill"><%= esc(h) %></span><% }
                } else { out.print("<span class='muted'>없음</span>"); }
            %>
        </div>
    </div>
    <div class="row"><div class="key">memo</div><div class="value"><%= esc((String)request.getAttribute("req.memo")) %></div></div>
</div>

<div class="card">
    <h2>세션에 저장된 값 (session scope)</h2>
    <div class="row"><div class="key">sess.name</div><div class="value"><%= esc((String)session.getAttribute("sess.name")) %></div></div>
    <div class="row"><div class="key">sess.age</div><div class="value"><%= esc((String)session.getAttribute("sess.age")) %></div></div>
    <div class="row"><div class="key">sess.hobby</div>
        <div class="value">
            <%
                String[] sh = (String[]) session.getAttribute("sess.hobby");
                if (sh != null && sh.length > 0) {
                    for (String h : sh) { %><span class="pill"><%= esc(h) %></span><% }
                } else { out.print("<span class='muted'>없음</span>"); }
            %>
        </div>
    </div>
    <div class="row"><div class="key">sess.memo</div><div class="value"><%= esc((String)session.getAttribute("sess.memo")) %></div></div>
    <div class="row"><div class="key">sess.visit</div><div class="value"><%= session.getAttribute("sess.visit") %></div></div>
</div>

<div class="card">
    <form method="post" class="grid" autocomplete="off">
        <div>
            <label for="name">이름</label>
            <input id="name" name="name" type="text" placeholder="홍길동" value="<%= esc(name != null ? name : (sName != null ? sName : "")) %>">
        </div>
        <div>
            <label for="age">나이</label>
            <input id="age" name="age" type="text" placeholder="25" value="<%= esc(age != null ? age : (sAge != null ? sAge : "")) %>">
        </div>
        <div style="grid-column: 1 / -1;">
            <label>취미 (다중선택)</label>
            <label><input type="checkbox" name="hobby" value="축구"> 축구</label>
            <label><input type="checkbox" name="hobby" value="농구"> 농구</label>
            <label><input type="checkbox" name="hobby" value="게임"> 게임</label>
            <label><input type="checkbox" name="hobby" value="코딩"> 코딩</label>
        </div>
        <div style="grid-column: 1 / -1;">
            <label for="memo">메모</label>
            <textarea id="memo" name="memo" rows="4" placeholder="자유롭게 입력하세요."><%= esc(memo != null ? memo : (sMemo != null ? sMemo : "")) %></textarea>
        </div>
        <div style="grid-column: 1 / -1;" class="actions">
            <button type="submit">저장 (POST)</button>
            <a class="linkbtn ghost" href="?resetSession=1">세션 초기화</a>
            <span class="muted">세션 방문횟수: <b><%= visit %></b></span>
        </div>
    </form>
</div>

</body>
</html>
