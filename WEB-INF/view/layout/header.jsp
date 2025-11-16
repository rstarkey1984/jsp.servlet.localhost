<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@
taglib prefix="c" uri="jakarta.tags.core" %> <%@ taglib prefix="fn"
uri="jakarta.tags.functions" %>

<div class="site-header">
  <div class="site-header-inner">
    <!-- 왼쪽: 로고/사이트명 -->
    <div class="site-header-left">
      <a href="${pageContext.request.contextPath}/board/list" class="site-logo">
        MySite
      </a>
      <nav class="site-nav">
        <a
          href="${pageContext.request.contextPath}/board/list"
          class="site-nav-link"
        >
          게시판
        </a>
        <!-- 필요하면 메뉴 더 추가 -->
        <%--
        <a
          href="${pageContext.request.contextPath}/about"
          class="site-nav-link"
        >
          About
        </a>
        --%>
      </nav>
    </div>

    <!-- 오른쪽: 로그인 / 유저 정보 -->
    <div class="site-header-right">
      <!-- 로그인 안 된 경우 -->
      <c:if test="${empty sessionScope.id}">
        <a
          href="${pageContext.request.contextPath}/user/login"
          class="btn btn-outline-light"
        >
          로그인
        </a>
        <a
          href="${pageContext.request.contextPath}/user/register"
          class="btn btn-primary-light"
        >
          회원가입
        </a>
      </c:if>

      <!-- 로그인 된 경우 -->
      <c:if test="${not empty sessionScope.id}">
        <span class="user-info">
          <span class="user-avatar">
            ${fn:substring(sessionScope.id, 0, 1)}
          </span>
          <span class="user-name"> ${sessionScope.id} 님 </span>
        </span>
        <a
          href="${pageContext.request.contextPath}/user/logout"
          class="btn btn-outline-light"
        >
          로그아웃
        </a>
      </c:if>
    </div>
  </div>
</div>
