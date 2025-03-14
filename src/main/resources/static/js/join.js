let isNicknameValidated = false;
let validatedNickname;
let isEmailValidated = false;

// 회원가입
document.getElementById("joinForm").addEventListener("submit", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const nickname = document.getElementById("nickname").value;

    const joinData = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        nickname: nickname
    };

    if (nickname != validatedNickname) {
        alert("닉네임 확인이 필요합니다.");
    } else {
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
    }
});

// 이메일 인증번호 발송
document.getElementById("emailButton").addEventListener("click", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const email = document.getElementById("email").value;
    const emailNumberButton = document.getElementById("emailNumberButton");

    axios.post("/api/users/send-email", null, {
        params: {
            email: email
        }})
        .then(response => {
            alert("인증번호를 발송하였습니다.");
            document.getElementById("email").disabled = true;
            document.getElementById("emailButton").disabled = true;
            emailNumberButton.removeAttribute("disabled");
        })
        .catch(error => {
            alert("이메일 형식이 올바르지 않거나, 이미 사용 중인 이메일입니다.");
        });
});

// 이메일 인증번호 확인
document.getElementById("emailNumberButton").addEventListener("click", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const emailData = {
        email: document.getElementById("email").value,
        authNumber: document.getElementById("emailNumber").value
    };

    axios.post("/api/users/validate-email", emailData)
        .then(response => {
            alert("인증에 성공했습니다.");
            document.getElementById("emailNumber").disabled = true;
            document.getElementById("emailNumberButton").disabled = true;
            isEmailValidated = true;
            activateJoinButton();
        })
        .catch(error => {
            alert("인증에 실패했습니다.");
        });
});

// 닉네임 확인
document.getElementById("nicknameButton").addEventListener("click", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const nickname = document.getElementById("nickname").value;

    axios.get("/api/users/validate-nickname", {
        params: {
            nickname: nickname
        }})
        .then(response => {
            alert("사용 가능한 닉네임입니다.");
            isNicknameValidated = true;
            validatedNickname = nickname;
            activateJoinButton();
        })
        .catch(error => {
            alert("닉네임 형식이 올바르지 않거나, 이미 사용 중인 닉네임입니다.");
        });
});

// 회원가입 버튼 활성화 여부 확인
function activateJoinButton() {
    const joinButton = document.getElementById("joinButton");
    if (isNicknameValidated && isEmailValidated) {
        joinButton.removeAttribute("disabled");
    }
}