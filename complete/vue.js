// Vue 3 Composition API (CDN 전역 Vue 사용)
const { createApp, ref, reactive, computed, onMounted, watch } = Vue;

createApp({
  setup() {
    // ==========================
    // 1. 상태(state)
    // ==========================

    // 인증 관련
    const authMode = ref("login"); // 'login' | 'register'

    const loginForm = reactive({
      id: "",
      password: "",
    });
    const loginUserId = ref(null);
    const loginError = ref("");

    const registerForm = reactive({
      id: "",
      password: "",
      email: "",
    });
    const registerError = ref("");
    const registerSuccess = ref("");

    // 게시판 목록 / 페이징
    const boards = ref([]);
    const expandedBoardId = ref(null); // 현재 내용 펼친 게시글 번호 (없으면 null)
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

    // 공통 로딩 상태
    const loading = ref(false);

    // ==========================
    // 2. computed
    // ==========================

    const hasNext = computed(() => {
      // 간단히: 현재 페이지의 게시글 개수가 size와 같으면 "다음 페이지가 있을 수 있다"로 판단
      return boards.value.length === size.value;
    });

    // ==========================
    // 3. 공통 JSON fetch 헬퍼
    // ==========================

    const jsonFetch = async (url, options = {}) => {
      const opt = {
        headers: {
          "Content-Type": "application/json",
        },
        ...options,
      };

      const res = await fetch(url, opt);
      const text = await res.text();

      let json;
      try {
        json = JSON.parse(text);
      } catch (e) {
        throw new Error("JSON 파싱 오류: " + text);
      }
      return { status: res.status, body: json };
    };

    // ==========================
    // 4. 로그인 / 로그아웃
    // ==========================

    const login = async () => {
      loginError.value = "";

      if (!loginForm.id || !loginForm.password) {
        loginError.value = "아이디와 비밀번호를 입력하세요.";
        return;
      }

      loading.value = true;
      try {
        const { body } = await jsonFetch("/api/user/login", {
          method: "POST",
          body: JSON.stringify({
            id: loginForm.id,
            password: loginForm.password,
          }),
        });

        if (!body.success) {
          loginError.value = body.message || "로그인 실패";
          return;
        }

        // 로그인 성공 → 상태만 변경
        // localStorage 반영은 watch(loginUserId)에서 처리
        loginUserId.value = loginForm.id;
        loginForm.password = "";
      } catch (e) {
        loginError.value = "서버 오류: " + e.message;
      } finally {
        loading.value = false;
      }
    };

    const logout = () => {
      // 로그아웃 → 상태만 변경
      // localStorage 삭제는 watch(loginUserId)에서 처리
      loginUserId.value = null;
    };

    // ==========================
    // 5. 회원가입 (성공 시 자동 로그인)
    // ==========================

    const register = async () => {
      registerError.value = "";
      registerSuccess.value = "";

      if (!registerForm.id || !registerForm.password || !registerForm.email) {
        registerError.value = "아이디 / 비밀번호 / 이메일을 모두 입력하세요.";
        return;
      }

      if (registerForm.id.length > 20) {
        registerError.value = "아이디는 최대 20자까지 가능합니다.";
        return;
      }
      if (registerForm.email.length > 45) {
        registerError.value = "이메일은 최대 45자까지 가능합니다.";
        return;
      }

      loading.value = true;
      try {
        // 🔥 endpoint: /api/user/register
        const { body } = await jsonFetch("/api/user/register", {
          method: "POST",
          body: JSON.stringify({
            id: registerForm.id,
            password: registerForm.password,
            email: registerForm.email,
          }),
        });

        if (!body.success) {
          registerError.value = body.message || "회원가입에 실패했습니다.";
          return;
        }

        // 🔥 회원가입 성공 → 자동 로그인
        // localStorage 반영은 watch(loginUserId)에서 처리
        loginUserId.value = registerForm.id;

        registerSuccess.value = body.message || "회원가입이 완료되었습니다.";

        // 폼 초기화
        registerForm.id = "";
        registerForm.password = "";
        registerForm.email = "";
      } catch (e) {
        registerError.value = "서버 오류: " + e.message;
      } finally {
        loading.value = false;
      }
    };

    // ==========================
    // 6. 게시판 목록
    // ==========================

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
        expandedBoardId.value = null; // 이미 열려 있으면 접기
      } else {
        expandedBoardId.value = idx; // 해당 글만 펼치기
      }
    };

    const prevPage = () => {
      if (page.value <= 1) return;
      page.value--;
      // fetchBoards(); // ❌ page 변화는 watch가 감지해서 호출
    };

    const nextPage = () => {
      if (!hasNext.value) return;
      page.value++;
      // fetchBoards(); // ❌
    };

    const changeSize = () => {
      page.value = 1;
      // fetchBoards(); // ❌
    };

    // ==========================
    // 7. 글쓰기 / 수정
    // ==========================

    const startEdit = (b) => {
      boardMode.value = "edit";
      boardForm.idx = b.idx;
      boardForm.title = b.title || "";
      boardForm.content = b.content || "";
      formError.value = "";
      formSuccess.value = "";
    };

    const cancelEdit = () => {
      // 모드만 create로 바꾸면,
      // 나머지 폼 초기화는 watch(boardMode)가 처리
      boardMode.value = "create";
    };

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
          // 게시글 생성
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

          // ✅ 1페이지로 이동
          page.value = 1;

          // ✅ 페이지 값이 그대로 1일 수도 있으니, 직접 목록 새로고침
          await fetchBoards();
        } else {
          // 게시글 수정
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

    // ==========================
    // 8. 삭제
    // ==========================

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

        console.log(body);
        4;
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

    // ==========================
    // 9. watch로 “상태 변화 → 부작용” 모으기
    // ==========================

    // (1) page / size가 바뀔 때마다 목록 자동 로딩
    watch(
      [page, size],
      () => {
        fetchBoards();
      },
      {
        // 컴포넌트가 처음 마운트될 때도 한 번 실행 (초기 목록 로딩)
        immediate: true,
      }
    );

    // (2) loginUserId가 바뀔 때 localStorage와 자동 동기화
    watch(loginUserId, (newId) => {
      if (newId) {
        localStorage.setItem("loginUserId", newId);
      } else {
        localStorage.removeItem("loginUserId");
      }
    });

    // (3) boardMode가 create가 되면 폼 초기화
    watch(boardMode, (mode) => {
      if (mode === "create") {
        boardForm.idx = null;
        boardForm.title = "";
        boardForm.content = "";
        formError.value = "";
        formSuccess.value = "";
      }
    });

    // ==========================
    // 10. onMounted()
    // ==========================

    onMounted(() => {
      // 새로고침 시 localStorage에서 로그인 복원
      const saved = localStorage.getItem("loginUserId");
      if (saved) {
        loginUserId.value = saved;
      }

      // 목록 로딩은 watch([page, size], ..., { immediate: true })에서 처리
    });

    // ==========================
    // 11. 반환 (템플릿에서 사용할 값들)
    // ==========================

    return {
      // 상태
      authMode,
      loginForm,
      loginUserId,
      loginError,
      registerForm,
      registerError,
      registerSuccess,
      boards,
      page,
      size,
      boardError,
      boardForm,
      boardMode,
      formError,
      formSuccess,
      loading,
      hasNext,
      expandedBoardId,

      // 메서드
      login,
      logout,
      register,
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
  },
}).mount("#app");
