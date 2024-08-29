{
        "id":"1",
        "title": "understanding the covert narcissist",
        "content": "Covert narcissists often fly under the radar, making their traits harder to identify than those of their overt counterparts.",
        "author": "Dr. ABC",
        "createdAt": "2024-01-04 10:30:23"
}


Sure! Below is the completed `renderBlogDiv` function which will loop over the `blogsList` and create div blocks for each blog post. It appends the generated HTML to the element with the ID `blogs-parent-div`. I've incorporated the structure you provided in the example.

```javascript
function renderBlogDiv() {
    // It uses blogsList
    try {
        if (isDataFetched) {
            console.log(`>> blog data is now available`);

            // Get the parent div where blog posts will be appended
            const blogsParentDiv = document.getElementById("blogs-parent-div");
            blogsParentDiv.innerHTML = ''; // Clear previous content if any
            
            // Loop over items of blogsList to create div blocks
            blogsList.forEach(post => {
                // Create a new div element for each blog post
                const blogPostDiv = document.createElement('div');
                blogPostDiv.className = 'blog-post';
                blogPostDiv.id = `blog-post-${post.id}`;

                // Construct inner HTML for the blog post
                blogPostDiv.innerHTML = `
                    <h2>${post.title}</h2>
                    <p>
                        <strong>${post.author}</strong> posted on ${new Date(post.createdAt).toLocaleDateString()}
                    </p>
                    <hr>
                    <p>
                        ${post.content.length > 100 ? post.content.substring(0, 100) + '...' : post.content}
                        ${post.content.length > 100 ? `<a href="/blog/${post.id}">read more</a>` : ''}
                    </p>
                `;

                // Append the blog post div to the parent div
                blogsParentDiv.appendChild(blogPostDiv);
            });
        } else {
            console.log(`>> no blog data available | keep showing skeleton loading`);
            // You could add code here to show a loading skeleton in the UI if required
        }
    } catch (err2) {
        console.log(`.. error while rendering blocks: ${err2}`);
    }
}
```

### Explanation:
1. **Check Data Availability**: The function checks if `isDataFetched` is `true` to ensure that there is data available to render.
2. **Get Parent Div**: It retrieves the parent div element where all the blog posts will be appended (`blogs-parent-div`).
3. **Clear Existing Content**: It clears any previous content to avoid appending new posts over old ones.
4. **Loop Through Blogs**: It iterates over each item in `blogsList` and creates a new div for each blog post.
5. **Construct Inner HTML**: It constructs the inner HTML for each blog post, including the title, author, creation date, content (clipped to 100 characters), and a "read more" link if the content exceeds 100 characters.
6. **Append to Parent Div**: Finally, each constructed blog post div is appended to the parent div in the DOM.

This approach ensures that each blog post is rendered correctly according to the provided specifications. If there is no data, you can add some logic to show a loading skeleton or message, as indicated in the comments.



document.addEventListener('DOMContentLoaded', () => {
    // Call getBlogData when the DOM content is fully loaded
    getBlogData();
});

// Event listener for the custom event 'blog-data-available'
document.addEventListener('blog-data-available', () => {
    // Call renderBlogDiv after a setTimeout of 1 second
    setTimeout(() => {
        renderBlogDiv();
    }, 1000); // 1000 milliseconds = 1 second
});