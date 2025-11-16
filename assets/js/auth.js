// /assets/js/auth.js

(function () {
  // Vue 3 CDN에서 제공하는 전역 객체
  const { ref, reactive, watch, onMounted } = Vue;

  /**
   * 로그인 / 회원가입 로직을 묶어놓은 Composition 함수
   * - jsonFetch : 공통 fetch 헬퍼
   * - loading   : 로딩 상태 ref
   */
  function useAuth(jsonFetch, loading) {
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

    // 로그인
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

        // 로그인 성공
        loginUserId.value = loginForm.id;
        loginForm.password = "";
      } catch (e) {
        loginError.value = "서버 오류: " + e.message;
      } finally {
        loading.value = false;
      }
    };

    // 로그아웃
    const logout = () => {
      loginUserId.value = null;
    };

    // 회원가입 (성공 시 자동 로그인)
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

        // 회원가입 성공 → 자동 로그인
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

    // loginUserId ↔ localStorage 동기화
    watch(loginUserId, (newId) => {
      if (newId) {
        localStorage.setItem("loginUserId", newId);
      } else {
        localStorage.removeItem("loginUserId");
      }
    });

    // 마운트 시 localStorage에서 로그인 복원
    onMounted(() => {
      const saved = localStorage.getItem("loginUserId");
      if (saved) {
        loginUserId.value = saved;
      }
    });

    return {
      authMode,
      loginForm,
      loginUserId,
      loginError,
      registerForm,
      registerError,
      registerSuccess,
      login,
      logout,
      register,
    };
  }

  // 전역에 노출 (app.js에서 사용)
  window.useAuth = useAuth;
})();
