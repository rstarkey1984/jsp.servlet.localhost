// Vue 3 Composition API
const { createApp, ref, computed } = Vue;

const app = createApp({
  setup() {
    // ==========================
    // 1. 상태(state) - 더미 데이터
    // ==========================

    // 🔹 실제 API 대신, 연습용 고정 데이터
    const boards = ref([
      { idx: 1, title: "첫 번째 글입니다." },
      { idx: 2, title: "두 번째 글입니다." },
      { idx: 3, title: "세 번째 글입니다." },
    ]);

    const page = ref(1);
    const size = ref(10);
    const loading = ref(false);
    const boardError = ref("");

    // 현재 boards 개수와 size를 비교해서 "다음 페이지가 있는 것처럼"만 처리
    const hasNext = computed(() => {
      return boards.value.length === size.value;
    });

    // ==========================
    // 2. 자식 컴포넌트가 쏘는 이벤트 처리 (부모 메서드)
    // ==========================

    const handleChangePage = (newPage) => {
      console.log("change-page 이벤트:", newPage);
      page.value = newPage;

      // ※ 1단계에서는 API 호출 없이, 값만 바꿔봄
      //    2단계/3단계에서 여기서 fetchBoards()를 붙일 예정
    };

    const handleChangeSize = (newSize) => {
      console.log("change-size 이벤트:", newSize);
      size.value = newSize;
      page.value = 1;
    };

    const handleEdit = (board) => {
      alert(`수정 버튼 클릭: idx=${board.idx}, title=${board.title}`);
      // 나중에: 여기서 "수정 폼 컴포넌트" 열기
    };

    const handleDelete = (idx) => {
      const ok = confirm(`정말 ${idx}번 글을 삭제하시겠습니까? (연습용 alert)`);
      if (ok) {
        // 연습용으로 boards에서 바로 제거
        boards.value = boards.value.filter((b) => b.idx !== idx);
      }
    };

    return {
      boards,
      page,
      size,
      hasNext,
      loading,
      boardError,
      handleChangePage,
      handleChangeSize,
      handleEdit,
      handleDelete,
    };
  },
});

// ==========================
// 3. 전역 컴포넌트 등록
// ==========================

app.component("board-list-card", {
  template: "#board-list-card-template",

  // 부모가 내려주는 데이터 (읽기 전용)
  props: {
    boards: {
      type: Array,
      required: true,
    },
    page: {
      type: Number,
      required: true,
    },
    size: {
      type: Number,
      required: true,
    },
    hasNext: {
      type: Boolean,
      required: true,
    },
    loading: {
      type: Boolean,
      required: true,
    },
    error: {
      type: String,
      default: "",
    },
  },

  // 자식이 부모에게 알릴 수 있는 이벤트 목록
  emits: ["change-page", "change-size", "edit", "delete"],

  setup(props, { emit }) {
    // 이전/다음 페이지 버튼
    const prevPage = () => {
      if (props.page <= 1) return;
      emit("change-page", props.page - 1);
    };

    const nextPage = () => {
      if (!props.hasNext) return;
      emit("change-page", props.page + 1);
    };

    // 셀렉트 박스에서 페이지 크기 변경
    const onChangeSize = (event) => {
      const newSize = Number(event.target.value);
      emit("change-size", newSize);
    };

    // 수정/삭제 버튼
    const onEdit = (b) => {
      emit("edit", b);
    };

    const onDelete = (idx) => {
      emit("delete", idx);
    };

    return {
      prevPage,
      nextPage,
      onChangeSize,
      onEdit,
      onDelete,
    };
  },
});

app.mount("#app");
