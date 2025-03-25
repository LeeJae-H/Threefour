document.addEventListener("DOMContentLoaded", function() {
    const category = document.getElementById("category").value;
    const page = document.getElementById("page").value;
    const writePostButton = document.getElementById("writePostButton");

    writePostButton.href = `/view/posts/${category}/write`;
    loadPosts(category, page);
});

function loadPosts(category, page) {
    axios.get(`/api/posts/list/all/${category}`, {
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
                        <td>${post.postId}</td>
                        <td><a href="/view/posts/${post.postId}/details" style="text-decoration: none;">${post.title}</a></td>
                        <td>${post.nickname}</td>
                        <td>${formattedCreatedAt}</td>
                    </tr>
                    `;
                tbody.innerHTML += row;
            });

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
                    <a class="page-link" href="/view/posts/${category}/${pageInt - 1}">Previous</a>
                </li>  
                `;
            }

            // 페이지 번호들
            const startPage = Math.floor((pageInt - 1) / 10) * 10 + 1;
            const endPage = Math.min(startPage + 9, totalPages);
            for (let i = startPage; i <= endPage; i++) {
                ul.innerHTML += `
                <li class="page-item ${parseInt(i) === pageInt ? 'active' : ''}">
                    <a class="page-link" href="/view/posts/${category}/${i}">${i}</a>
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
                    <a class="page-link" href="/view/posts/${category}/${pageInt + 1}">Next</a>
                </li>
                `;
            }
        })
        .catch(error => {
        });
}