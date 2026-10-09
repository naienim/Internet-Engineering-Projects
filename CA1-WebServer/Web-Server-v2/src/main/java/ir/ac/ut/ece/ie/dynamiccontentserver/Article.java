package ir.ac.ut.ece.ie.dynamiccontentserver;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Article {

    private String title;
    private String summary;
    private String body;
    private Date publishDate;
    private LocalDateTime publishTime;
    private List<Article> references;

    public Article(String title, String summary, String body, List<Article> references) {
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.publishDate = new Date();
        this.publishTime = LocalDateTime.now();
        this.references = references;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getBody() {
        return body;
    }

    public Date getPublishDate() { return publishDate; }

    public void setPublishDate(Date publishDate) { this.publishDate = publishDate; }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }

    public List<Article> getReferences() {
        return references;
    }

    public void addReference(Article article) {
        if (article != null) {
            references.add(article);
        }
    }
}
