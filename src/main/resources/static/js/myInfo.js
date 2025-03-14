let beforeNickname;
let isNicknameValidated = false;
let validatedNickname;

document.addEventListener("DOMContentLoaded", function() {
    const accessToken = localStorage.getItem('AccessToken');

    if (accessToken) {
        axios.post('/api/token/validate', null, {
            headers: {
                'AccessToken': accessToken
            }})
            .then(response => {


                axios.get("/users/my/info", {
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
                        document.body.innerHTML = '';
                        const userConfirmed = window.confirm("로그인이 필요한 페이지입니다.");
                        if (userConfirmed) {
                            window.location.href = "/home";
                        }
                    });


            })
            .catch(error => {
                document.body.innerHTML = '';
                const userConfirmed = window.confirm("로그인이 필요한 페이지입니다.");
                if (userConfirmed) {
                    window.location.href = "/home";
                }
            });
    } else {
        document.body.innerHTML = '';
        const userConfirmed = window.confirm("로그인이 필요한 페이지입니다.");
        if (userConfirmed) {
            window.location.href = "/home";
        }
    }
});

// 내 정보 수정
document.getElementById('updateForm').addEventListener('submit', function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const accessToken = localStorage.getItem('AccessToken');

    const password = document.getElementById('password').value;
    const nickname = document.getElementById('nickname').value;

    const updateData = {};
    if (password) {
        updateData.password = password;
    }
    if (nickname !== beforeNickname) {
        if (nickname !== validatedNickname) {
            alert("닉네임 확인이 필요합니다.");
            return;
        }
        updateData.nickname = nickname;
    }

    axios.put('/users/my/info', updateData, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            alert("수정이 완료됐습니다.");
        })
        .catch(error => {
            alert("잘못된 비밀번호입니다.");
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
        })
        .catch(error => {
            alert("닉네임 형식이 올바르지 않거나, 이미 사용 중인 닉네임입니다.");
        });
});