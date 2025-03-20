let beforeNickname;
let validatedNickname;
let isNicknameValidated = false;

document.addEventListener("DOMContentLoaded", function() {
    const accessToken = localStorage.getItem('AccessToken');

    if (accessToken) {
        axios.post('/api/token/validate', null, {
            headers: {
                'AccessToken': accessToken
            }})
            .then(response => {
                axios.get("/api/users/my", {
                    headers: {
                        'AccessToken': accessToken
                    }})
                    .then(response => {
                        const email = response.data.data.email;
                        const nickname = response.data.data.nickname;
                        document.getElementById('email').value = email;
                        document.getElementById('nickname').value = nickname;
                        beforeNickname = nickname;
                    })
                    .catch(error => {
                        window.confirm("로그인이 필요한 페이지입니다.");
                        window.location.href = "/";
                    });
            })
            .catch(error => {
                window.confirm("로그인이 필요한 페이지입니다.");
                window.location.href = "/";
            });
    } else {
        window.confirm("로그인이 필요한 페이지입니다.");
        window.location.href = "/";
    }
});

// 내 정보 수정
document.getElementById('updateForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const accessToken = localStorage.getItem('AccessToken');

    const updateData = {};

    const password = document.getElementById("password").value;
    const password2 = document.getElementById("password2").value;
    if (password) {
        if (password !== password2) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }
        updateData.password = password;
    }

    const nickname = document.getElementById('nickname').value;
    if (nickname !== beforeNickname) {
        if (nickname !== validatedNickname) {
            alert("닉네임 확인이 필요합니다.");
            return;
        }
        updateData.nickname = nickname;
    }

    axios.put('/api/users/my', updateData, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            window.confirm("수정이 완료됐습니다.");
            window.location.href = "/";
        })
        .catch(error => {
            alert("잘못된 비밀번호입니다.");
        });

});

// 닉네임 확인
document.getElementById("nicknameButton").addEventListener("click", function (event) {
    event.preventDefault();

    const nickname = document.getElementById("nickname").value;

    axios.get("/api/users/join/validate-nickname", {
        params: {
            nickname: nickname
        }})
        .then(response => {
            alert("사용 가능한 닉네임입니다.");
            isNicknameValidated = true;
            validatedNickname = nickname;
        })
        .catch(error => {
            alert("닉네임 형식이 올바르지 않거나, 이미 사용 중인 닉네임입니다.");
        });
});