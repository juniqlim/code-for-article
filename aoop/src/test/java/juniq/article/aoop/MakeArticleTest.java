package juniq.article.aoop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MakeArticleTest {
    MakeArticle makeArticle = new MakeArticle();

    @Test
    void makeDataStructureArticle() {
        MakeArticle.DataStructureArticle article = makeArticle.makeDataStructureArticle("How to train your dragon", "Ever wonder how?");
        assertEquals("how-to-train-your-dragon", article.getSlug());
    }

    @Test
    void makeMutableObjectArticle() {
        MakeArticle.MutableObjectArticle article = makeArticle.makeMutableObjectArticle("How to train your dragon", "Ever wonder how?");
        assertEquals("how-to-train-your-dragon", article.getSlug());
    }

    @Test
    void immutableObjectArticle() {
        MakeArticle.ImmutableObjectArticle article = makeArticle.immutableObjectArticle("How to train your dragon", "Ever wonder how?");
        assertEquals("how-to-train-your-dragon", article.slug().value());
        assertEquals(new MakeArticle.SluggedString("How to train your dragon"), article.slug());
    }

    @Test
    void immutableObjectDependencyFreeArticle() {
        MakeArticle.ImmutableObjectDependencyFreeArticle article = makeArticle.immutableObjectDependencyFreeArticle("How to train your dragon", "Ever wonder how?");
        assertEquals("how-to-train-your-dragon", article.slug());
    }
}