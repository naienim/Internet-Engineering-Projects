package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.List;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ArticlePage {

    private String path;
    private String body;

    public ArticlePage(String path, String body) {
        this.path = path;
        this.body = body;
    }

    public byte[] pageBody() {
        String title = "";

        if (path.contains("?")) {
            String query = path.substring(path.indexOf("?") + 1);
            String[] params = query.split("&");

            for (String p : params) {
                String[] pair = p.split("=");
                if (pair.length != 2)
                    continue;
                if (pair[0].equals("title")) {
                    try {
                        title = URLDecoder.decode(pair[1], "UTF-8");
                    }
                    catch (Exception e) {
                        title = pair[1];
                    }
                }
            }
        }

        Article article = ArticleRepository.getArticleByTitle(title);
        if (article == null) {
            return ("<html><body>" + "<h2>Article not found</h2>" + "<a href='HomePage'>Back to Home</a>" + "</body></html>").getBytes();
        }

        StringBuilder html = new StringBuilder();

        html.append("<html>");
        html.append("<body>");
        html.append("<h1>").append(article.getTitle()).append("</h1>");
        html.append("<h3>Summary</h3>");
        html.append("<p>").append(article.getSummary()).append("</p>");
        html.append("<h3>Body</h3>");
        html.append("<p>").append(article.getBody()).append("</p>");
        html.append("<h3>References</h3>");
        List<Article> refs = article.getReferences();
        if (refs.isEmpty()) {
            html.append("<p>No references</p>");
        }
        else {
            html.append("<ul>");
            for (Article a : refs) {
                if (a == null) continue;
                String encodedTitle = "";
                try {
                    encodedTitle = URLEncoder.encode(a.getTitle(), "UTF-8");
                }
                catch (Exception e) {
                    encodedTitle = a.getTitle();
                }

                html.append("<li>");
                html.append("<a href='ArticlePage?title=");
                html.append(encodedTitle);
                html.append("'>");
                html.append(a.getTitle());
                html.append("</a>");
                html.append("</li>");
            }
            html.append("</ul>");
        }
        html.append("<br>");
        html.append("<a href='HomePage'>Back to Home</a>");
        html.append("</body>");
        html.append("</html>");

        return html.toString().getBytes();
    }
}

