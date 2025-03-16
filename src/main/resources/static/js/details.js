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
    axios.get(`/api/posts/${postId}`)
        .then(response => {
            const post = response.data.data;

            document.getElementById("category").innerText = post.category;
            document.getElementById("title").innerText = post.title;
            document.getElementById("authorNickname").innerText = post.authorNickname;
            document.getElementById("content").innerText = post.content;
            const createdAt = new Date(post.postTimeInfo.createdAt);
            const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;
            document.getElementById("createdAt").innerText = formattedCreatedAt;
        })
        .catch(error => {
        });
}

function loadPostDetailsWithAccessToken(postId, accessToken) {
    axios.get(`/api/posts/${postId}`, {
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
            const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;
            document.getElementById("createdAt").innerText = formattedCreatedAt;

            if (post.isMine) {
                const editPost = document.getElementById("editPost");
                editPost.style.display = 'block';
                editPost.setAttribute('href', `/home`);
            }
        })
        .catch(error => {
        });
}