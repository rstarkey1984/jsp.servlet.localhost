// /assets/js/board.js

(function () {
  const { ref, reactive, computed, watch } = Vue;

  /**
   * 게시판(목록/페이징/글쓰기/수정/삭제) 로직
   * - jsonFetch : 공통 fetch 헬퍼
   * - loading   : 로딩 상태 ref
   * - loginUserId : 로그인한 유저 id (ref)
   */
  function useBoard(jsonFetch, loading, loginUserId) {
    // 게시판 목록 / 페이징
    const boards = ref([]);
    const expandedBoardId = ref(null);
    const page = ref(1);
    const size = ref(10);
    const boardError = ref("");

    // 글쓰기 / 수정 폼
    const boardForm = reactive({
      idx: null,
      title: "",
      content: "",
    });
    const boardMode = ref("create"); // 'create' | 'edit'
    const formError = ref("");
    const formSuccess = ref("");

    const hasNext = computed(() => {
      return boards.value.length === size.value;
    });

    // 게시판 목록 조회
    const fetchBoards = async () => {
      loading.value = true;
      boardError.value = "";
      try {
        const url = `/api/board?page=${page.value}&size=${size.value}`;
        const { body } = await jsonFetch(url, { method: "GET" });

        if (!body.success) {
          boardError.value = body.message || "목록 로딩 실패";
          boards.value = [];
          return;
        }

        boards.value = body.data || [];
      } catch (e) {
        boardError.value = "서버 오류: " + e.message;
      } finally {
        loading.value = false;
      }
    };

    const toggleContent = (idx) => {
      if (expandedBoardId.value === idx) {
        expandedBoardId.value = null;
      } else {
        expandedBoardId.value = idx;
      }
    };

    const prevPage = () => {
      if (page.value <= 1) return;
      page.value--;
    };

    const nextPage = () => {
      if (!hasNext.value) return;
      page.value++;
    };

    const changeSize = () => {
      page.value = 1;
    };

    // 글 수정 시작
    const startEdit = (b) => {
      boardMode.value = "edit";
      boardForm.idx = b.idx;
      boardForm.title = b.title || "";
      boardForm.content = b.content || "";
      formError.value = "";
      formSuccess.value = "";
    };

    // 수정 취소
    const cancelEdit = () => {
      boardMode.value = "create";
    };

    // 글 등록/수정
    const submitBoard = async () => {
      formError.value = "";
      formSuccess.value = "";

      if (!loginUserId.value) {
        formError.value = "로그인이 필요합니다.";
        return;
      }

      if (!boardForm.title || !boardForm.content) {
        formError.value = "제목/내용을 모두 입력하세요.";
        return;
      }

      loading.value = true;
      try {
        if (boardMode.value === "create") {
          const { body } = await jsonFetch("/api/board", {
            method: "POST",
            body: JSON.stringify({
              title: boardForm.title,
              content: boardForm.content,
            }),
          });

          if (!body.success) {
            formError.value = body.message || "등록 실패";
            return;
          }

          formSuccess.value = "게시글이 등록되었습니다.";

          // 폼 초기화
          boardForm.title = "";
          boardForm.content = "";

          // 1페이지로 이동 후 목록 새로고침
          page.value = 1;
          await fetchBoards();
        } else {
          const { body } = await jsonFetch("/api/board/" + boardForm.idx, {
            method: "PUT",
            body: JSON.stringify({
              title: boardForm.title,
              content: boardForm.content,
            }),
          });

          if (!body.success) {
            formError.value = body.message || "수정 실패";
            return;
          }

          formSuccess.value = "게시글이 수정되었습니다.";
          await fetchBoards();
          cancelEdit();
        }
      } catch (e) {
        formError.value = "서버 오류: " + e.message;
      } finally {
        loading.value = false;
      }
    };

    // 삭제
    const deleteBoard = async (idx) => {
      if (!loginUserId.value) {
        alert("로그인이 필요합니다.");
        return;
      }
      if (!confirm("정말 삭제하시겠습니까?")) return;

      loading.value = true;
      try {
        const { body } = await jsonFetch("/api/board/" + idx, {
          method: "DELETE",
        });

        if (!body.success) {
          boardError.value = body.message || "삭제 실패";
          return;
        }

        await fetchBoards();
      } catch (e) {
        boardError.value = "서버 오류: " + e.message;
      } finally {
        loading.value = false;
      }
    };

    // page / size 바뀔 때마다 목록 자동 로딩
    watch(
      [page, size],
      () => {
        fetchBoards();
      },
      { immediate: true }
    );

    // boardMode가 create가 되면 폼 초기화
    watch(boardMode, (mode) => {
      if (mode === "create") {
        boardForm.idx = null;
        boardForm.title = "";
        boardForm.content = "";
        formError.value = "";
        formSuccess.value = "";
      }
    });

    return {
      boards,
      expandedBoardId,
      page,
      size,
      boardError,
      boardForm,
      boardMode,
      formError,
      formSuccess,
      hasNext,
      fetchBoards,
      prevPage,
      nextPage,
      changeSize,
      startEdit,
      cancelEdit,
      submitBoard,
      deleteBoard,
      toggleContent,
    };
  }

  window.useBoard = useBoard;
})();
