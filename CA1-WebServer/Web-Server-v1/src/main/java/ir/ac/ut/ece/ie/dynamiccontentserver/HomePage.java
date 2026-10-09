package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.*;
import java.util.List;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HomePage {

    private String path;
    private String body;

    public HomePage(String path, String body) {
        this.path = path;
        this.body = body;
    }

    public byte[] pageBody() {

        String query = "";
        String sortBy = "refs";
        if (path.contains("?")) {
            String queryString = path.substring(path.indexOf("?") + 1);
            String[] params = queryString.split("&");
            for (String p : params) {
                String[] pair = p.split("=");
                if (pair.length != 2)
                    continue;
                if (pair[0].equals("query")) {
                    try {
                        query = URLDecoder.decode(pair[1], "UTF-8").toLowerCase();
                    }
                    catch (Exception e) {
                        query = pair[1].toLowerCase();
                    }
                }
                else if (pair[0].equals("sortBy")) {
                    sortBy = pair[1];
                }
            }
        }

        List<Article> articles = ArticleRepository.getAllArticles();
        if (sortBy.equals("date")) {
            articles.sort((a, b) -> b.getPublishTime().compareTo(a.getPublishTime()));
        }
        else {
            articles.sort((a, b) -> {
                int countA = ArticleRepository.countReferencesTo(a);
                int countB = ArticleRepository.countReferencesTo(b);
                return Integer.compare(countB, countA);
            });
        }

        List<Article> titleMatches = new ArrayList<>();
        List<Article> summaryMatches = new ArrayList<>();

        for (Article a : articles) {

            String title = a.getTitle().toLowerCase();
            String summary = a.getSummary().toLowerCase();

            if (query.isEmpty()) {
                titleMatches.add(a);
            }
            else if (title.contains(query)) {
                titleMatches.add(a);
            }
            else if (summary.contains(query)) {
                summaryMatches.add(a);
            }
        }

        List<Article> results = new ArrayList<>();
        results.addAll(titleMatches);
        results.addAll(summaryMatches);

        StringBuilder html = new StringBuilder();

        html.append("<html><header><title>HomePage</title></header><body>");
        html.append("<h1>Articles</h1>");
        html.append("<form method='GET' action='HomePage'>");
        html.append("<input type='text' name='query' placeholder='Search...' value='" + query + "'>");
        html.append("<input type='hidden' name='sortBy' value='" + sortBy + "'>");
        html.append("<input type='submit' value='Search'>");
        html.append("</form>");
        html.append("<br>");

        String nextSortMode = sortBy.equals("date") ? "refs" : "date";
        String buttonText = sortBy.equals("date") ? "Sort by References" : "Sort by Date";
        String currentModeText = sortBy.equals("date") ? "Date" : "References";
        html.append("Sorting by: <b>").append(currentModeText).append("</b><br><br>");
        html.append("<form method='GET' action='HomePage'>");
        html.append("<input type='hidden' name='query' value='" + query + "'>");
        html.append("<input type='hidden' name='sortBy' value='" + nextSortMode + "'>");
        html.append("<button type='submit'>" + buttonText + "</button>");
        html.append("</form>");
        html.append("<hr>");

        if (results.isEmpty()) {
            html.append("<p>No articles found.</p>");
            html.append("<hr>");
        }
        else {
            for (Article a : results) {
                int refCount = ArticleRepository.countReferencesTo(a);
                String encodedTitle = "";
                try {
                    encodedTitle = URLEncoder.encode(a.getTitle(), "UTF-8");
                }
                catch (Exception e) {
                    encodedTitle = a.getTitle();
                }

                html.append("<h3>");
                html.append("<a href='ArticlePage?title=" + encodedTitle + "'>");
                html.append(a.getTitle());
                html.append("</a>");
                html.append(" (Referenced by " + refCount + ")");
                html.append("</h3>");
                html.append("<p>");
                html.append(a.getSummary());
                html.append("</p>");
                html.append("<hr>");
            }
        }

        html.append("<br>");
        html.append("<a href='AddArticlePage'>Add Article</a>");
        html.append("</body></html>");

        return html.toString().getBytes();
    }
}
