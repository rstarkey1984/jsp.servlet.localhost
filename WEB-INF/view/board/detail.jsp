<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <c:set var="pageTitle" value="MySite - 게시글 상세" />
    <%@ include file="/WEB-INF/view/layout/head.jsp" %>
  </head>

  <body>
    <%@ include file="/WEB-INF/view/layout/header.jsp" %>
    <div class="main-content">
      <div class="wrapper">
        <div class="card">
          <!-- 제목 -->
          <div
            class="title"
            style="font-size: 1.5rem; font-weight: 700; margin-bottom: 8px"
          >
            <c:out value="${board.title}" />
          </div>

          <!-- 게시글 번호 -->
          <div
            class="subtitle"
            style="font-size: 0.9rem; color: #6b7280; margin-bottom: 4px"
          >
            글 번호:
            <c:out value="${board.idx}" />
          </div>

          <!-- 작성자 (fk_user_id 있을 때만 표시) -->
          <c:if test="${not empty board.fk_user_id}">
            <div
              class="subtitle"
              style="font-size: 0.9rem; color: #6b7280; margin-bottom: 4px"
            >
              작성자:
              <c:out value="${board.fk_user_id}" />
            </div>
          </c:if>

          <!-- 작성 날짜 -->
          <div
            class="subtitle"
            style="font-size: 0.9rem; color: #6b7280; margin-bottom: 16px"
          >
            작성일:
            <c:out value="${board.regDate}" />
          </div>

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

          <!-- 내용 -->
          <div
            style="
              font-size: 0.9rem;
              line-height: 1.5;
              margin-bottom: 18px;
              white-space: pre-wrap;
            "
          >
            <div style="padding: 5px"><c:out value="${board.content}" /></div>
          </div>

          <!-- 버튼들 (목록 / 수정 / 삭제 한 줄 배치) -->
          <div class="button-row">
            <!-- 목록으로 -->
            <a
              href="${pageContext.request.contextPath}/board/list"
              class="btn btn-secondary"
            >
              목록으로
            </a>

            <!-- 수정 / 삭제는 권한 있을 때만 보이도록 -->
            <c:if
              test="${empty board.fk_user_id 
    or (not empty sessionScope.id and board.fk_user_id eq sessionScope.id)}"
            >
              <!-- 수정하기 -->
              <a
                href="${pageContext.request.contextPath}/board/edit?idx=${board.idx}"
                class="btn"
              >
                수정하기
              </a>

              <!-- 삭제 (POST) -->
              <form
                action="${pageContext.request.contextPath}/board"
                method="post"
                onsubmit="return confirm('정말 삭제하시겠습니까?');"
              >
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="idx" value="${board.idx}" />
                <button
                  type="submit"
                  class="btn btn-danger"
                  style="margin-top: 0"
                >
                  삭제하기
                </button>
              </form>
            </c:if>
          </div>
        </div>
      </div>
    </div>

    <%@ include file="/WEB-INF/view/layout/footer.jsp" %>
  </body>
</html>
