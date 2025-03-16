document.addEventListener("DOMContentLoaded", function() {
    const category = document.getElementById("category").value;
    const writePostButton = document.getElementById("writePostButton");
    writePostButton.href = `/posts/write/${category}`;
    const page = document.getElementById("page").value;
    loadPosts(category, page);
});

function loadPosts(category, page) {
    axios.get(`/api/posts/category/${category}`, {
        params: {
            page: page,
            size: 15
        }})
        .then(response => {
            const posts = response.data.data.postSummaryList;
            const tbody = document.querySelector("table tbody");

            posts.forEach((post) => {
                const createdAt = new Date(post.createdAt);
                const formattedCreatedAt = `${createdAt.getFullYear()}/${(createdAt.getMonth() + 1).toString().padStart(2, '0')}/${createdAt.getDate().toString().padStart(2, '0')} ${createdAt.getHours().toString().padStart(2, '0')}:${createdAt.getMinutes().toString().padStart(2, '0')}`;
                const row = `
                    <tr>
                        <td>${post.id}</td>
                        <td><a href="/posts/${post.id}" style="text-decoration: none;">${post.title}</a></td>
                        <td>${post.authorNickname}</td>
                        <td>${formattedCreatedAt}</td>
                    </tr>
                    `;
                tbody.innerHTML += row;
            });

            const pageInt = parseInt(page);
            const totalPages = response.data.data.totalPages;
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
                    <a class="page-link" href="/posts/category/${category}/${pageInt - 1}">Previous</a>
                </li>  
                `;
            }

            // 페이지 번호들
            const startPage = Math.floor((pageInt - 1) / 10) * 10 + 1;
            const endPage = Math.min(startPage + 9, totalPages);
            for (let i = startPage; i <= endPage; i++) {
                ul.innerHTML += `
                <li class="page-item ${parseInt(i) === pageInt ? 'active' : ''}">
                    <a class="page-link" href="/posts/category/${category}/${i}">${i}</a>
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
                    <a class="page-link" href="/posts/category/${category}/${pageInt + 1}">Next</a>
                </li>
                `;
            }
        })
        .catch(error => {
        });
}