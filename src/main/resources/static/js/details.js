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
            })
            .catch(error => {
                loadPostDetails(postId);
            });
    } else {
        loadPostDetails(postId);
    }
});

function loadPostDetails(postId) {
    axios.get(`/api/posts/id/${postId}`)
        .then(response => {
            const post = response.data.data;

            document.getElementById("category").innerText = post.category;
            document.getElementById("title").innerText = post.title;
            document.getElementById("authorNickname").innerText = post.authorNickname;
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
    axios.get(`/api/posts/id/${postId}`, {
        headers: {
            "AccessToken": accessToken
        }})
        .then(response => {
            const post = response.data.data;

            document.getElementById("category").innerText = post.category;
            document.getElementById("title").innerText = post.title;
            document.getElementById("authorNickname").innerText = post.authorNickname;
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
                editPostButton.href = `/posts/edit/${postId}`;

                const deletePostButton = document.getElementById("deletePostButton");
                deletePostButton.style.display = 'block';
            }
        })
        .catch(error => {
        });
}

// 게시글 삭제하기
document.getElementById("deletePostButton").addEventListener("click", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const accessToken = localStorage.getItem('AccessToken');
    const postId = document.getElementById("postId").value;

    axios.delete(`/api/posts/${postId}`, {
        headers: {
            'AccessToken': accessToken
        }})
        .then(response => {
            alert("삭제가 완료됐습니다.");
            window.location.href = "/home";
        })
        .catch(error => {
            alert("삭제에 실패했습니다.");
            window.location.href = "/home";
        });
});
