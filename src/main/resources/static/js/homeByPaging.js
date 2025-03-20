document.addEventListener("DOMContentLoaded", function() {
    const page = document.getElementById("page").value;
    loadPosts(page);

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
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const loginData = new FormData();
    loginData.append('email', email);
    loginData.append('password', password);

    axios.post('/login', loginData, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }})
        .then(response => {
            // AccessToken, RefreshToken을 localStorage에 저장
            const accessToken = response.headers['accesstoken'];
            const refreshToken = response.headers['refreshtoken'];
            localStorage.setItem('AccessToken', accessToken);
            localStorage.setItem('RefreshToken', refreshToken);

            window.location.href = "/";
        })
        .catch(error => {
            alert('이메일 또는 비밀번호가 일치하지 않습니다.');
        });
});

// 로그아웃
document.getElementById("logoutForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const refreshToken = localStorage.getItem("RefreshToken");

    if (refreshToken) {
        axios.post("/logout", null, {
            headers: {
                "RefreshToken": refreshToken
            }})
            .then(response => {
                localStorage.removeItem("AccessToken");
                localStorage.removeItem("RefreshToken");

                window.location.href = "/";
            })
            .catch(error => {
                localStorage.removeItem("AccessToken");
                localStorage.removeItem("RefreshToken");

                window.location.href = "/";
            });
    } else {
        localStorage.removeItem("AccessToken");
        localStorage.removeItem("RefreshToken");

        window.location.href = "/";
    }
});

function loadPosts(page) {
    axios.get("/api/posts/list/all", {
        params: {
            page: page,
            size: 15
        }})
        .then(response => {
            const posts = response.data.data.postSummaryList;
            const totalPages = response.data.data.totalPages;

            // 게시글 목록
            const tbody = document.querySelector("table tbody");
            posts.forEach((post) => {
                const createdAt = new Date(post.createdAt);
                const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;
                const row = `
                    <tr>
                        <td>${post.id}</td>
                        <td><a href="/view/posts/${post.id}/details" style="text-decoration: none;">${post.title}</a></td>
                        <td>${post.author.nickname}</td>
                        <td>${formattedCreatedAt}</td>
                    </tr>
                    `;
                tbody.innerHTML += row;
            });

            // 페이지 번호
            const pageInt = parseInt(page);
            const ul = document.querySelector("ul");
            ul.innerHTML = '';

            // Previous 버튼
            if (pageInt <= 10) {
                ul.innerHTML += `
                <li class="page-item disabled">
                    <a class="page-link">Previous</a>
                </li>  
                `;
            } else {
                ul.innerHTML += `
                <li class="page-item">
                    <a class="page-link" href="/view/home/${pageInt - 1}">Previous</a>
                </li>  
                `;
            }

            // 페이지 번호들
            const startPage = Math.floor((pageInt - 1) / 10) * 10 + 1;
            const endPage = Math.min(startPage + 9, totalPages);
            for (let i = startPage; i <= endPage; i++) {
                ul.innerHTML += `
                <li class="page-item ${parseInt(i) === pageInt ? 'active' : ''}">
                    <a class="page-link" href="/view/home/${i}">${i}</a>
                </li>
                `;
            }

            // Next 버튼
            if (endPage >= totalPages) {
                ul.innerHTML += `
                <li class="page-item disabled">
                    <a class="page-link">Next</a>
                </li>
                `;
            } else {
                ul.innerHTML += `
                <li class="page-item">
                    <a class="page-link" href="/view/home/${pageInt + 1}">Next</a>
                </li>
                `;
            }
        })
        .catch(error => {
        });
}