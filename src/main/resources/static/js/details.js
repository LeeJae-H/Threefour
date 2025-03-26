document.addEventListener("DOMContentLoaded", function() {
    const accessToken = localStorage.getItem('AccessToken');
    const postId = document.getElementById("postId").value;

    if (accessToken) {
        axios.post('/api/token/validate', null, {
            headers: {
                'AccessToken': accessToken
            }})
            .then(response => {
                loadPostDetailsWithAccessToken(postId, accessToken);
                loadComments(postId);
            })
            .catch(error => {
                loadPostDetails(postId);
                loadComments(postId);
            });
    } else {
        loadPostDetails(postId);
        loadComments(postId);
    }
});

function loadPostDetails(postId) {
    axios.get(`/api/posts/${postId}/details`)
        .then(response => {
            const post = response.data.data;

            document.getElementById("category").innerText = post.category;
            document.getElementById("title").innerText = post.title;
            document.getElementById("authorNickname").innerText = post.nickname;
            document.getElementById("content").innerText = post.content;
            const createdAt = new Date(post.postTimeInfo.createdAt);
            const updatedAt = new Date(post.postTimeInfo.updatedAt);

            if (createdAt.getTime() !== updatedAt.getTime()) {
                const formattedUpdatedAt = `${updatedAt.getFullYear()}/${(updatedAt.getMonth() + 1).toString().padStart(2, '0')}/${updatedAt.getDate().toString().padStart(2, '0')} ${updatedAt.getHours().toString().padStart(2, '0')}:${updatedAt.getMinutes().toString().padStart(2, '0')}`;
                document.getElementById("createdAt").innerText = `(수정됨) ${formattedUpdatedAt}`;
            } else {
                const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;
                document.getElementById("createdAt").innerText = formattedCreatedAt;
            }
        })
        .catch(error => {
        });
}

function loadPostDetailsWithAccessToken(postId, accessToken) {
    axios.get(`/api/posts/${postId}/details`, {
        headers: {
            "AccessToken": accessToken
        }})
        .then(response => {
            const post = response.data.data;

            document.getElementById("category").innerText = post.category;
            document.getElementById("title").innerText = post.title;
            document.getElementById("authorNickname").innerText = post.nickname;
            document.getElementById("content").innerText = post.content;
            const createdAt = new Date(post.postTimeInfo.createdAt);
            const updatedAt = new Date(post.postTimeInfo.updatedAt);

            if (createdAt.getTime() !== updatedAt.getTime()) {
                const formattedUpdatedAt = `${updatedAt.getFullYear()}/${(updatedAt.getMonth() + 1).toString().padStart(2, '0')}/${updatedAt.getDate().toString().padStart(2, '0')} ${updatedAt.getHours().toString().padStart(2, '0')}:${updatedAt.getMinutes().toString().padStart(2, '0')}`;
                document.getElementById("createdAt").innerText = `(수정됨) ${formattedUpdatedAt}`;
            } else {
                const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;
                document.getElementById("createdAt").innerText = formattedCreatedAt;
            }

            if (post.isMine) {
                const editPostButton = document.getElementById("editPostButton");
                editPostButton.style.display = 'block';
                editPostButton.href = `/view/posts/${postId}/edit`;

                const deletePostButton = document.getElementById("deletePostButton");
                deletePostButton.style.display = 'block';
            }
        })
        .catch(error => {
        });
}

function loadComments(postId) {
    axios.get(`/api/comments/list/${postId}`)
        .then(response => {
            const comments = response.data.data.commentSummaryList;
            const commentsList = document.getElementById("commentsList");

            commentsList.innerHTML = "";
            comments.forEach((comment) => {
                const createdAt = new Date(comment.createdAt);
                const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;

                const row = `
                    <div class="row align-items-center border p-1 mt-2">
                        <strong class="border-end col-md-2">${comment.nickname}</strong>
                        <p class="border-end col-md-7">${comment.content}</p>
                        <small class="col-md-2">${formattedCreatedAt}</small>
                        <button class="btn btn-danger col-md-1 deleteCommentButton" data-comment-id="${comment.commentId}">삭제</button>
                    </div>
                `
                commentsList.innerHTML += row;
            })
        })
        .catch(error => {
        });
}

// 게시글 삭제
document.getElementById("deletePostButton").addEventListener("click", function (event) {
    event.preventDefault();

    const accessToken = localStorage.getItem('AccessToken');
    const postId = document.getElementById("postId").value;

    axios.delete(`/api/posts/${postId}`, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            alert("삭제가 완료됐습니다.");
            window.location.href = "/";
        })
        .catch(error => {
            alert("삭제에 실패했습니다.");
            window.location.href = "/";
        });
});

// 댓글 작성
document.getElementById("writeCommentButton").addEventListener("click", function (event) {
    event.preventDefault();

    const accessToken = localStorage.getItem('AccessToken');

    const postId = document.getElementById("postId").value;
    const commentContent = document.getElementById("commentContent").value;

    if (!commentContent) {
        alert("내용을 입력하세요.");
        return;
    }

    const writeData = {};

    writeData.postId = postId;
    writeData.content = commentContent;

    axios.post('/api/comments', writeData, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            alert("작성이 완료됐습니다.");
            document.getElementById("commentContent").value = "";
            loadComments(postId);
        })
        .catch(error => {
            alert("회원만 작성할 수 있습니다.")
            window.location.href = `/view/posts/${postId}/details`;
        });
});

// 댓글 삭제 (이벤트 위임 사용)
document.addEventListener("click", function (event) {
    if (event.target.classList.contains("deleteCommentButton")) {
        event.preventDefault();

        const commentId = event.target.getAttribute("data-comment-id");
        const accessToken = localStorage.getItem("AccessToken");

        axios.delete(`/api/comments/${commentId}`, {
            headers: {
                "AccessToken": accessToken
            }})
            .then(response => {
                alert("삭제가 완료됐습니다.");
                const postId = document.getElementById("postId").value;
                loadComments(postId);
            })
            .catch(error => {
                alert("작성자 본인만 삭제할 수 있습니다.");
            });
    }
});