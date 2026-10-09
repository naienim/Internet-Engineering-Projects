package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.List;
import java.net.URLDecoder;
import java.util.ArrayList;
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
            String value = "";
            try {
                value = URLDecoder.decode(pair[1], "UTF-8");
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

        try {
            Article article = new Article(title, summary, articleBody, references);
            ArticleRepository.addArticle(article);
            return ("<html><body>" + "<h2>Article Saved</h2>" + "<a href='HomePage'>Go to Home</a>" + "</body></html>").getBytes();
        }
        catch (Exception e) {
            return ("<html><body>" + "<h2>Error: " + e.getMessage() + "</h2>" + "<a href='AddArticlePage'>Back</a>" + "</body></html>").getBytes();
        }
    }
}
