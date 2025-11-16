<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@
taglib prefix="c" uri="jakarta.tags.core" %> <%@ taglib prefix="fn"
uri="jakarta.tags.functions" %>

<head>
  <meta charset="UTF-8" />

  <!-- 모바일 반응형 필수 -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />

  <!-- 브라우저 호환성 -->
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />

  <!-- 기본 제목 (각 페이지에서 덮어쓰기 가능) -->
  <title>
    <c:choose> <c:when test="${not empty pageTitle}"> ${pageTitle} </c:when>
    <c:otherwise> MyBoard </c:otherwise> </c:choose>
  </title>

  <!-- 공통 CSS -->
  <link
    rel="stylesheet"
    href="${pageContext.request.contextPath}/assets/css/style.css"
  />
</head>
