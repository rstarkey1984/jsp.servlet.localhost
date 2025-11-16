<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <c:set var="pageTitle" value="MySite - 게시판 목록" />
    <%@ include file="/WEB-INF/view/layout/head.jsp" %>
  </head>
  <body>
    <%@ include file="/WEB-INF/view/layout/header.jsp" %>
    <div class="main-content">
      <div class="wrapper">
        <div class="card">
          <div class="title">게시판</div>
          <div class="subtitle">게시글 목록</div>

          <!-- Flash 메시지 -->
          <c:if test="${not empty sessionScope.flash_success}">
            <div class="helper" style="color: #16a34a">
              ${sessionScope.flash_success}
            </div>
            <c:remove var="flash_success" scope="session" />
          </c:if>

          <c:if test="${not empty sessionScope.flash_error}">
            <div class="error-box">${sessionScope.flash_error}</div>
            <c:remove var="flash_error" scope="session" />
          </c:if>

          <!-- 목록 -->
          <c:choose>
            <c:when test="${empty list}">
              <div class="helper">등록된 게시글이 없습니다.</div>
            </c:when>
            <c:otherwise>
              <div class="table-container" style="margin-bottom: 14px">
                <table
                  style="
                    width: 100%;
                    border-collapse: collapse;
                    font-size: 0.85rem;
                  "
                >
                  <thead>
                    <tr>
                      <th
                        style="
                          text-align: left;
                          padding: 6px 4px;
                          border-bottom: 1px solid #e5e7eb;
                        "
                      >
                        번호
                      </th>
                      <th
                        style="
                          text-align: left;
                          padding: 6px 4px;
                          border-bottom: 1px solid #e5e7eb;
                        "
                      >
                        작성자
                      </th>
                      <th
                        style="
                          text-align: left;
                          padding: 6px 4px;
                          border-bottom: 1px solid #e5e7eb;
                        "
                      >
                        제목
                      </th>
                      <th
                        style="
                          text-align: right;
                          padding: 6px 4px;
                          border-bottom: 1px solid #e5e7eb;
                        "
                      >
                        작성일
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="b" items="${list}">
                      <tr>
                        <td
                          style="
                            padding: 6px 4px;
                            border-bottom: 1px solid #f3f4f6;
                            width: 40px;
                          "
                        >
                          ${b.idx}
                        </td>
                        <td
                          style="
                            padding: 6px 4px;
                            border-bottom: 1px solid #f3f4f6;
                            width: 80px;
                          "
                        >
                          <c:out value="${b.fk_user_id}" />
                        </td>
                        <td
                          style="
                            padding: 6px 4px;
                            border-bottom: 1px solid #f3f4f6;
                          "
                        >
                          <a
                            href="${pageContext.request.contextPath}/board/detail?idx=${b.idx}"
                          >
                            <c:out value="${b.title}" />
                          </a>
                        </td>
                        <td
                          style="
                            padding: 6px 4px;
                            border-bottom: 1px solid #f3f4f6;
                            text-align: right;
                            white-space: nowrap;
                          "
                        >
                          <c:out value="${b.regDate}" />
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
            </c:otherwise>
          </c:choose>

          <!-- 페이징 -->
          <div class="pagination">
            <!-- 이전 버튼 -->
            <c:choose>
              <c:when test="${page > 1}">
                <a
                  class="page-link"
                  href="${pageContext.request.contextPath}/board/list?page=${page - 1}&size=${size}"
                >
                  ◀
                </a>
              </c:when>
              <c:otherwise>
                <span class="page-link disabled">◀</span>
              </c:otherwise>
            </c:choose>

            <!-- 페이지 번호들 -->
            <c:forEach var="p" begin="${startPage}" end="${endPage}">
              <c:choose>
                <c:when test="${p == page}">
                  <span class="page-link current">${p}</span>
                </c:when>
                <c:otherwise>
                  <a
                    class="page-link"
                    href="${pageContext.request.contextPath}/board/list?page=${p}&size=${size}"
                  >
                    ${p}
                  </a>
                </c:otherwise>
              </c:choose>
            </c:forEach>

            <!-- 다음 버튼 -->
            <c:choose>
              <c:when test="${page < totalPages}">
                <a
                  class="page-link"
                  href="${pageContext.request.contextPath}/board/list?page=${page + 1}&size=${size}"
                >
                  ▶
                </a>
              </c:when>
              <c:otherwise>
                <span class="page-link disabled">▶</span>
              </c:otherwise>
            </c:choose>
          </div>

          <!-- 글쓰기 버튼 -->
          <form
            action="${pageContext.request.contextPath}/board/write"
            method="get"
            style="margin-top: 12px"
          >
            <button type="submit" class="btn">글쓰기</button>
          </form>
        </div>
      </div>
    </div>

    <%@ include file="/WEB-INF/view/layout/footer.jsp" %>
  </body>
</html>
