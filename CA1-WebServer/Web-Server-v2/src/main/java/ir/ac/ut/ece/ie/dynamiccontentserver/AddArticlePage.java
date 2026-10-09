package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.List;
import java.util.Scanner;
import java.io.InputStream;

public class AddArticlePage {

    private String path;
    private String body;

    public AddArticlePage(String path, String body) {
        this.path = path;
        this.body = body;
    }

    public byte[] pageBody() {
        List<Article> articles = ArticleRepository.getAllArticles();
        StringBuilder html = new StringBuilder();
        InputStream is = getClass().getClassLoader().getResourceAsStream("styles/addArticlePageStyle.css");
        String css = new Scanner(is).useDelimiter("\\A").next();

        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Add Article</title>");
        html.append("<style>").append(css).append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='navbar'>");
        html.append("<div><strong>UT Article Platform</strong></div>");
        html.append("<div>");
        html.append("<a href='HomePage'>Home</a>");
        html.append("<a href='AddArticlePage'>Add Article</a>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class='container'>");
        html.append("<h1>Add New Article</h1>");
        html.append("<form method='POST' action='SaveArticle'>");
        html.append("<label for='title'>Title</label>");
        html.append("<input type='text' id='title' name='title'>");
        html.append("<label for='summary'>Summary</label>");
        html.append("<textarea id='summary' name='summary' rows='4'></textarea>");
        html.append("<label for='body'>Body</label>");
        html.append("<textarea id='body' name='body' rows='10'></textarea>");
        html.append("<label>References</label>");
        if (articles.isEmpty()) {
            html.append("<p>No articles found.</p>");
        }
        else {
            html.append("<div class='references-list'>");
            for (Article a : articles) {
                html.append("<div>");
                html.append("<input type='checkbox' name='ref' value='");
                html.append(a.getTitle());
                html.append("' id='ref_").append(a.getTitle()).append("'> ");
                html.append("<label for='ref_").append(a.getTitle()).append("'>");
                html.append(a.getTitle());
                html.append("</label>");
                html.append("</div>");
            }
            html.append("</div>");
        }
        html.append("<br><br>");
        html.append("<input type='submit' class='btn btn-primary' value='Submit'>");
        html.append("</form>");
        html.append("<a href='HomePage' class='btn btn-link'>Back to Home</a>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString().getBytes();
    }
}
