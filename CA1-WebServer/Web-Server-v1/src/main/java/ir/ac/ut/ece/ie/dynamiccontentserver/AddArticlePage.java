package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.List;

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

        html.append("<html>");
        html.append("<head><title>Add Article</title></head>");
        html.append("<body>");
        html.append("<h1>Add New Article</h1>");
        html.append("<form method='POST' action='SaveArticle'>");
        html.append("Title:<br>");
        html.append("<input type='text' name='title'><br><br>");
        html.append("Summary:<br>");
        html.append("<textarea name='summary' rows='4' cols='50'></textarea><br><br>");
        html.append("Body:<br>");
        html.append("<textarea name='body' rows='10' cols='50'></textarea><br><br>");
        html.append("References:<br>");
        if (articles.isEmpty()) {
            html.append("No articles found.<br><br>");
        }
        else {
            for (Article a : articles) {
                html.append("<input type='checkbox' name='ref' value='");
                html.append(a.getTitle());
                html.append("'> ");
                html.append(a.getTitle());
                html.append("<br>");
            }
        }
        html.append("<br>");
        html.append("<input type='submit' value='Submit'>");
        html.append("</form>");
        html.append("<br>");
        html.append("<a href='HomePage'>Back to Home</a>");
        html.append("</body>");
        html.append("</html>");

        return html.toString().getBytes();
    }
}
