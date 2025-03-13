// 홈 화면에서 로그인 여부 확인
document.addEventListener("DOMContentLoaded", function() {
    const accessToken = localStorage.getItem('AccessToken');

    if (accessToken) {
        axios.post('/api/token/validate', null, {
            headers: {
                'AccessToken': accessToken
            }})
            .then(response => {
                const userNickname = response.data.data;
                document.getElementById('userNickname').textContent = userNickname + "님 환영합니다!";
                document.getElementById('logoutForm').style.display = 'block';
            })
            .catch(error => {
                document.getElementById('loginForm').style.display = 'block';
            });
    } else {
        document.getElementById('loginForm').style.display = 'block';
    }
});

// 로그인
document.getElementById('loginForm').addEventListener('submit', function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);

    axios.post('/login', formData, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }})
        .then(response => {
            // AccessToken, RefreshToken을 localStorage에 저장
            const accessToken = response.headers['accesstoken'];
            const refreshToken = response.headers['refreshtoken'];

            localStorage.setItem('AccessToken', accessToken);
            localStorage.setItem('RefreshToken', refreshToken);

            // 리다이렉션
            const redirectUrl = response.headers['location'];
            window.location.href = redirectUrl;
        })
        .catch(error => {
            alert('이메일 또는 비밀번호가 일치하지 않습니다.');
        });
});

// 로그아웃
document.getElementById("logoutForm").addEventListener("submit", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const refreshToken = localStorage.getItem("RefreshToken");

    if (refreshToken) {
        axios.post("/logout", null, {
            headers: {
                "RefreshToken": refreshToken
            }})
            .then(response => {
                localStorage.removeItem("AccessToken");
                localStorage.removeItem("RefreshToken");

                // 리다이렉션
                const redirectUrl = response.headers['location'];
                window.location.href = redirectUrl;
            })
            .catch(error => {
                localStorage.removeItem("AccessToken");
                localStorage.removeItem("RefreshToken");

                // 리다이렉션
                const redirectUrl = response.headers['location'];
                window.location.href = redirectUrl;
            });
    } else {
        localStorage.removeItem("AccessToken");
        localStorage.removeItem("RefreshToken");
    }
});