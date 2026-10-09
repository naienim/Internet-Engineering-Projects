package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.List;
import java.util.Scanner;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SaveArticle {

    private String path;
    private String body;

    public SaveArticle(String path, String body) {
        this.path = path;
        this.body = body;
    }

    public byte[] pageBody() {
        String title = "";
        String summary = "";
        String articleBody = "";
        List<Article> references = new ArrayList<>();

        String[] params = body.split("&");

        for (String p : params) {
            String[] pair = p.split("=");
            if (pair.length != 2)
                continue;
            String key = pair[0];
            String value;
            try {
                value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
            }
            catch (Exception e) {
                value = pair[1];
            }

            if (key.equals("title")) {
                title = value;
            }
            else if (key.equals("summary")) {
                summary = value;
            }
            else if (key.equals("body")) {
                articleBody = value;
            }
            else if (key.equals("ref")) {
                Article refArticle = ArticleRepository.getArticleByTitle(value);
                if (refArticle != null) {
                    references.add(refArticle);
                }
            }
        }

        InputStream is = getClass().getClassLoader().getResourceAsStream("styles/saveArticleStyle.css");
        String css = new Scanner(is).useDelimiter("\\A").next();

        try {
            Article article = new Article(title, summary, articleBody, references);
            ArticleRepository.addArticle(article);

            StringBuilder html = new StringBuilder();
            html.append("<html>");
            html.append("<head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<title>Article Saved</title>");
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
            html.append("<div class='alert alert-success'>");
            html.append("Article <strong>").append(title).append("</strong> was saved successfully.");
            html.append("</div>");
            html.append("<a href='HomePage' class='btn btn-primary'>Go to Home</a>");
            html.append("<a href='AddArticlePage' class='btn'>Add Another Article</a>");
            html.append("</div>");
            html.append("</body>");
            html.append("</html>");

            return html.toString().getBytes();
        }
        catch (Exception e) {
            StringBuilder html = new StringBuilder();
            html.append("<html>");
            html.append("<head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<title>Error Saving Article</title>");
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
            html.append("<div class='alert alert-error'>");
            html.append("Error: ").append(e.getMessage());
            html.append("</div>");
            html.append("<a href='AddArticlePage' class='btn btn-primary'>Back to Add Article</a>");
            html.append("<a href='HomePage' class='btn'>Go to Home</a>");
            html.append("</div>");
            html.append("</body>");
            html.append("</html>");

            return html.toString().getBytes();
        }
    }
}
