// /assets/js/app.js

(function () {
  const { createApp, ref } = Vue;

  /**
   * 공통 JSON fetch 헬퍼
   */
  const createJsonFetch = () => {
    return async (url, options = {}) => {
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
  };

  createApp({
    setup() {
      const loading = ref(false);
      const jsonFetch = createJsonFetch();

      // 전역에 붙어 있는 useAuth / useBoard 사용
      const auth = window.useAuth(jsonFetch, loading);
      const board = window.useBoard(jsonFetch, loading, auth.loginUserId);

      return {
        loading,
        ...auth,
        ...board,
      };
    },
  }).mount("#app");
})();
