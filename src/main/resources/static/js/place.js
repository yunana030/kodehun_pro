document.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".yuna-view-image-container");
    console.log("container:", container); // ← container가 잘 선택되는지 확인
    if (!container) return;

    const images = container.querySelectorAll(".yuna-view-image");
    console.log("images:", images); // ← images NodeList 확인
    const prevBtn = container.querySelector(".yuna-view-prev");
    const nextBtn = container.querySelector(".yuna-view-next");
    console.log("prevBtn, nextBtn:", prevBtn, nextBtn); // ← 버튼 확인

    let currentIndex = 0;

    if (images.length <= 1) {
        prevBtn.style.display = "none";
        nextBtn.style.display = "none";
        return;
    }

    nextBtn.addEventListener("click", () => {
        images[currentIndex].classList.remove("active");
        currentIndex = (currentIndex + 1) % images.length;
        images[currentIndex].classList.add("active");
    });

    prevBtn.addEventListener("click", () => {
        images[currentIndex].classList.remove("active");
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        images[currentIndex].classList.add("active");
    });
});

//좋아요,즐겨찾기
document.addEventListener("DOMContentLoaded", () => {
    const likeBtn = document.querySelector(".btn-like");
    const favBtn = document.querySelector("#favoriteBtn");

    // --- 좋아요 토글 (클라이언트용) ---
    likeBtn.addEventListener("click", async () => {
        const icon = likeBtn.querySelector("i");
        icon.classList.toggle("fa-solid");
        icon.classList.toggle("fa-regular");
        icon.classList.toggle("active");

        // 실제 서버 호출 필요하면 여기에 axios.post(`/place/${window.placeId}/like`) 추가 가능
    });

    // --- 즐겨찾기 상태 로드 ---
    async function loadFavoriteStatus() {
        try {
            const response = await axios.get(`/place/${window.placeId}/favorite/status`);
            const { favorited, loginRequired } = response.data;

            if (loginRequired) {
                favBtn.classList.remove("btn-secondary");
                favBtn.classList.add("btn-outline-secondary");
                favBtn.innerText = "즐겨찾기";
                return;
            }

            if (favorited) {
                favBtn.classList.remove("btn-outline-secondary");
                favBtn.classList.add("btn-secondary");
                favBtn.innerText = "즐겨찾기 ✔";
            } else {
                favBtn.classList.remove("btn-secondary");
                favBtn.classList.add("btn-outline-secondary");
                favBtn.innerText = "즐겨찾기";
            }
        } catch (error) {
            console.error("즐겨찾기 상태 로드 실패", error);
        }
    }

    // --- 즐겨찾기 클릭 ---
    favBtn.addEventListener("click", async () => {
        try {
            await axios.post(`/place/${window.placeId}/favorite`);
            await loadFavoriteStatus(); // 서버 반영 후 상태 갱신
        } catch (error) {
            if (error.response?.status === 401) {
                alert("로그인 후 이용 가능합니다.");
            } else {
                alert("즐겨찾기 처리 중 오류가 발생했습니다.");
                console.error(error);
            }
        }
    });

    // 초기 로드
    loadFavoriteStatus();
});

