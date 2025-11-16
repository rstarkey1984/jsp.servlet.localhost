<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@
taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
  <head>
    <c:set var="pageTitle" value="MySite - 가입완료" />
    <%@ include file="/WEB-INF/view/layout/head.jsp" %>
  </head>
  <body>
    <%@ include file="/WEB-INF/view/layout/header.jsp" %>
    <div class="main-content">
      <div class="wrapper">
        <div class="card register_form">
          <div class="title">가입 완료 🎉</div>
          <div class="subtitle">
            <b>${sessionScope.id}</b> 님, 환영합니다!<br />
            이제 서비스를 이용하실 수 있어요.
          </div>

          <button type="button" onclick="location.href='/board/list'">
            게시판으로 가기
          </button>
        </div>
      </div>
    </div>

    <%@ include file="/WEB-INF/view/layout/footer.jsp" %>
  </body>
</html>
