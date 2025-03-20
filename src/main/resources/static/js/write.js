document.addEventListener("DOMContentLoaded", function() {
    const accessToken = localStorage.getItem('AccessToken');

    if (accessToken) {
        axios.post('/api/token/validate', null, {
            headers: {
                'AccessToken': accessToken
            }})
            .then(response => {
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

// 게시글 작성
document.getElementById('writeForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const accessToken = localStorage.getItem('AccessToken');
    const categoryValue = document.getElementById("categoryValue").value;

    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;

    const writeData = {};

    writeData.title = title;
    writeData.content = content;
    writeData.category = categoryValue;

    axios.post('/api/posts', writeData, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            window.confirm("작성이 완료됐습니다.");
            window.location.href = `/view/posts/${categoryValue}`;
        })
        .catch(error => {
            alert("제목은 1~50자 이내, 내용은 1자 이상어이야 합니다.");
        });
});