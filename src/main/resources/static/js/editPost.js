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


                axios.get(`/api/posts/id/${postId}`)
                    .then(response => {
                        const post = response.data.data;

                        document.getElementById("category").innerText = post.category;
                        document.getElementById("authorNickname").innerText = post.authorNickname;
                        const createdAt = new Date(post.postTimeInfo.createdAt);
                        const updatedAt = new Date(post.postTimeInfo.updatedAt);

                        if (createdAt !== updatedAt) {
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
        window.confirm("로그인이 필요한 페이지입니다.");
        window.location.href = "/home";
    }
});

// 게시글 수정
document.getElementById('editForm').addEventListener('submit', function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const accessToken = localStorage.getItem('AccessToken');
    const postId = document.getElementById("postId").value;

    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;

    const editData = {};

    if (title !== beforeTitle) {
        editData.title = title;
    }
    if (content !== beforeContent) {
        editData.content = content;
    }

    axios.put(`/api/posts/${postId}`, editData, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            window.confirm("수정이 완료됐습니다.");
            window.location.href = `/posts/${postId}`;
        })
        .catch(error => {
            alert("제목은 1~50자 이내, 내용은 1자 이상어이야 합니다.");
        });
});