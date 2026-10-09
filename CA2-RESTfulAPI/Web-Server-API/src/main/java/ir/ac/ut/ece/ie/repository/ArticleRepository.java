package ir.ac.ut.ece.ie.repository;

import java.util.List;
import ir.ac.ut.ece.ie.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitleIgnoreCase(String title);
}
