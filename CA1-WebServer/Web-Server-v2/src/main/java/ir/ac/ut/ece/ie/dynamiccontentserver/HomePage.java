package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.*;
import java.util.List;
import java.util.Scanner;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
        InputStream is = getClass().getClassLoader().getResourceAsStream("styles/homePageStyle.css");
        String css = new Scanner(is).useDelimiter("\\A").next();

        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>HomePage</title>");
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
        html.append("<h1>Articles</h1>");
        html.append("<form method='GET' action='HomePage' class='search-form'>");
        html.append("<input type='text' name='query' placeholder='Search...' value='").append(query).append("'>");
        html.append("<input type='hidden' name='sortBy' value='").append(sortBy).append("'>");
        html.append("<input type='submit' class='btn btn-primary' value='Search'>");
        html.append("</form>");


        String nextSortMode = sortBy.equals("date") ? "refs" : "date";
        String buttonText = sortBy.equals("date") ? "Sort by References" : "Sort by Date";
        String currentModeText = sortBy.equals("date") ? "Date" : "References";
        html.append("<div class='sort-info'>");
        html.append("Sorting by: <b>").append(currentModeText).append("</b>");
        html.append("&nbsp;&nbsp;");
        html.append("<a href='HomePage?query=").append(query).append("&sortBy=").append(nextSortMode);
        html.append("' class='btn btn-secondary'>").append(buttonText).append("</a>");
        html.append("</div>");
        html.append("<hr>");

        if (results.isEmpty()) {
            html.append("<p>No articles found.</p>");
            html.append("<hr>");
        }
        else {
            for (Article a : results) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                String formattedDate = formatter.format(a.getPublishDate());
                int refCount = ArticleRepository.countReferencesTo(a);
                String highlightedTitle = highlight(a.getTitle(), query);
                String highlightedSummary = highlight(a.getSummary(), query);
                String encodedTitle = "";
                try {
                    encodedTitle = URLEncoder.encode(a.getTitle(), "UTF-8");
                }
                catch (Exception e) {
                    encodedTitle = a.getTitle();
                }

                html.append("<div class='article-card'>");
                html.append("<h3 class='article-title'>");
                html.append("<a href='ArticlePage?title=").append(encodedTitle).append("'>");
                html.append(highlightedTitle);
                html.append("</a>");
                html.append("</h3>");
                html.append("<div class='meta'>");
                html.append("Published on ").append(formattedDate).append(" | Referenced by ").append(refCount).append(" article(s)");
                html.append("</div>");
                html.append("<p class='article-summary'>");
                html.append(highlightedSummary);
                html.append("</p>");
                html.append("</div>");

            }
        }

        html.append("<a href='AddArticlePage' class='btn footer-link'>Add Article</a>");
        html.append("</div>");
        html.append("</body></html>");


        return html.toString().getBytes();
    }

    private String highlight(String text, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return text;
        }

        String escaped = java.util.regex.Pattern.quote(searchTerm);
        return text.replaceAll("(?i)" + escaped, "<span class=\"highlight\">$0</span>");
    }

}
