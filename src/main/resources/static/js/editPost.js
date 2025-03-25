let beforeTitle;
let beforeContent;

document.addEventListener("DOMContentLoaded", function() {
    const accessToken = localStorage.getItem('AccessToken');
    const postId = document.getElementById("postId").value;

    if (accessToken) {
        axios.post('/api/token/validate', null, {
            headers: {
                'AccessToken': accessToken
            }})
            .then(response => {
                axios.get(`/api/posts/${postId}/details`)
                    .then(response => {
                        const post = response.data.data;

                        document.getElementById("category").innerText = post.category;
                        document.getElementById("authorNickname").innerText = post.nickname;
                        const createdAt = new Date(post.postTimeInfo.createdAt);
                        const updatedAt = new Date(post.postTimeInfo.updatedAt);

                        if (createdAt.getTime() !== updatedAt.getTime()) {
                            const formattedUpdatedAt = `${updatedAt.getFullYear()}/${(updatedAt.getMonth() + 1).toString().padStart(2, '0')}/${updatedAt.getDate().toString().padStart(2, '0')} ${updatedAt.getHours().toString().padStart(2, '0')}:${updatedAt.getMinutes().toString().padStart(2, '0')}`;
                            document.getElementById("createdAt").innerText = `(수정됨) ${formattedUpdatedAt}`;
                        } else {
                            const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;
                            document.getElementById("createdAt").innerText = formattedCreatedAt;
                        }

                        document.getElementById('title').value = post.title;
                        document.getElementById('content').value = post.content;
                        beforeTitle = post.title;
                        beforeContent = post.content;
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

// 게시글 수정
document.getElementById('editForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const accessToken = localStorage.getItem('AccessToken');
    const postId = document.getElementById("postId").value;

    const editData = {};

    const title = document.getElementById("title").value;
    if (title !== beforeTitle) {
        editData.title = title;
    }

    const content = document.getElementById("content").value;
    if (content !== beforeContent) {
        editData.content = content;
    }

    axios.put(`/api/posts/${postId}`, editData, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            window.confirm("수정이 완료됐습니다.");
            window.location.href = `/view/posts/${postId}/details`;
        })
        .catch(error => {
            alert("제목은 1~50자 이내, 내용은 1자 이상어이야 합니다.");
        });
});