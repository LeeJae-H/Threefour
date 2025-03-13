let nicknameValidated = false;

// 회원가입
document.getElementById("joinForm").addEventListener("submit", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const joinData = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        nickname: document.getElementById("nickname").value
    };

    // 회원가입 요청 보내기
    axios.post("/api/users/join", joinData)
        .then(response => {
            const userNickname = response.data.data;
            alert(userNickname + "님 환영합니다!");

            // 리다이렉션
            const redirectUrl = response.headers['location'];
            window.location.href = redirectUrl;
        })
        .catch(error => {
            alert("잘못된 비밀번호입니다.");
        });
});

// 닉네임 검증
document.getElementById("nicknameButton").addEventListener("click", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const nickname = document.getElementById("nickname").value;

    axios.get("/api/users/validate-nickname", {
        params: {
            nickname: nickname
        }})
        .then(response => {
            alert("사용 가능한 닉네임입니다.");
            nicknameValidated = true;
            activateJoinButton();
        })
        .catch(error => {
            alert("닉네임 형식이 올바르지 않거나, 이미 사용 중인 닉네임입니다.");
        });
});

// 회원가입 버튼 활성화 여부 확인
function activateJoinButton() {
    const joinButton = document.getElementById("joinButton");
    if (nicknameValidated) {
        joinButton.removeAttribute("disabled");
    }
}