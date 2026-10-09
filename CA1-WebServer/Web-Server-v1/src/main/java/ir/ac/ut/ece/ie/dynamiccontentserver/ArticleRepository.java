package ir.ac.ut.ece.ie.dynamiccontentserver;


import java.util.*;
import java.util.List;
import java.nio.file.*;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;


public class ArticleRepository {
    private static List<Article> articles = new ArrayList<>();
    private static Path articles_dir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "articles");

    public static void addArticle(Article article) throws Exception {
        for (Article a : articles) {
            if (a.getTitle().equalsIgnoreCase(article.getTitle())) {
                throw new Exception("Article title must be unique");
            }
        }
        if (article.getTitle().isEmpty()) {
            throw new Exception("Article title cannot be empty");
        }
        if (article.getSummary().isEmpty()) {
            throw new Exception("Article summary cannot be empty");
        }
        if (article.getBody().isEmpty()) {
            throw new Exception("Article body cannot be empty");
        }
        articles.add(article);
        saveArticleToFile(article);
    }

    public static List<Article> getAllArticles() {
        return articles;
    }

    public static Article getArticleByTitle(String title) {
        for (Article a : articles) {
            if (a.getTitle().equalsIgnoreCase(title)) {
                return a;
            }
        }
        return null;
    }

    public static int countReferencesTo(Article target) {
        int count = 0;

        for (Article a : articles) {
            if (a.getReferences().contains(target)) {
                count++;
            }
        }
        return count;
    }

    private static void saveArticleToFile(Article article) throws IOException {

        if (!Files.exists(articles_dir)) {
            Files.createDirectories(articles_dir);
        }

        String fileName = URLEncoder.encode(article.getTitle(), "UTF-8") + ".txt";
        Path file = articles_dir.resolve(fileName);

        StringBuilder data = new StringBuilder();
        data.append("title=").append(URLEncoder.encode(article.getTitle(), "UTF-8"));
        data.append("&summary=").append(URLEncoder.encode(article.getSummary(), "UTF-8"));
        data.append("&body=").append(URLEncoder.encode(article.getBody(), "UTF-8"));
        data.append("&publishDate=").append(article.getPublishDate().getTime());
        data.append("&publishTime=").append(URLEncoder.encode(article.getPublishTime().toString(), "UTF-8"));
        for (Article ref : article.getReferences()) {
            data.append("&ref=").append(URLEncoder.encode(ref.getTitle(), "UTF-8"));
        }

        Files.write(file, data.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void loadArticlesFromDisk() {
        try {
            if (!Files.exists(articles_dir)) {
                return;
            }

            DirectoryStream<Path> files = Files.newDirectoryStream(articles_dir, "*.txt");
            Map<Article, List<String>> refMap = new HashMap<>();

            for (Path file : files) {
                String body = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);

                String title = "";
                String summary = "";
                String articleBody = "";
                Date publishDate = null;
                LocalDateTime publishTime = null;
                List<String> refTitles = new ArrayList<>();

                String[] params = body.split("&");
                for (String p : params) {
                    String[] pair = p.split("=", 2);
                    if (pair.length != 2)
                        continue;

                    String key = pair[0];
                    String value = URLDecoder.decode(pair[1], "UTF-8");

                    if (key.equals("title"))
                        title = value;

                    else if (key.equals("summary"))
                        summary = value;

                    else if (key.equals("body"))
                        articleBody = value;

                    else if (key.equals("ref"))
                        refTitles.add(value);

                    else if (key.equals("publishDate"))
                        publishDate = new Date(Long.parseLong(value));

                    else if (key.equals("publishTime"))
                        publishTime = LocalDateTime.parse(value);
                }

                Article article = new Article(title, summary, articleBody, new ArrayList<>());
                if (publishDate != null) article.setPublishDate(publishDate);
                if (publishTime != null) article.setPublishTime(publishTime);

                articles.add(article);

                refMap.put(article, refTitles);
            }

            for (Article article : refMap.keySet()) {
                for (String refTitle : refMap.get(article)) {
                    Article ref = getArticleByTitle(refTitle);
                    if (ref != null)
                        article.addReference(ref);
                }
            }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
