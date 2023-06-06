package juniq.article.aoop;

import java.util.Objects;

class MakeArticle {
    DataStructureArticle makeDataStructureArticle(String title, String content) {
        String lowerCase = title.toLowerCase();
        String slug = lowerCase.replace(" ", "-");

        DataStructureArticle article = new DataStructureArticle();
        article.setTitle(title);
        article.setContent(content);
        article.setSlug(slug);

        return article;
    }

    class DataStructureArticle {
        private String title;
        private String content;
        private String slug;

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public void setTitle(String title) {
        }

        public void setContent(String content) {
        }

        public String getSlug() {
            return slug;
        }
    }

    MutableObjectArticle makeMutableObjectArticle(String title, String content) {
        MutableObjectArticle article = new MutableObjectArticle(title, content);
        article.setSlug();

        return article;
    }

    class MutableObjectArticle {
        private String title;
        private String content;
        private String slug;

        public MutableObjectArticle(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public void setSlug() {
            String lowerCase = title.toLowerCase();
            String slug = lowerCase.replace(" ", "-");
            this.slug = slug;
        }

        public String getSlug() {
            return slug;
        }
    }

    ImmutableObjectArticle immutableObjectArticle(String title, String content) {
        return new ImmutableObjectArticle(title, content, new SluggedString(title));
    }

    class ImmutableObjectArticle {
        private final String title;
        private final String content;
        private final SluggedString sluggedString;

        public ImmutableObjectArticle(String title, String content, SluggedString sluggedString) {
            this.title = title;
            this.content = content;
            this.sluggedString = sluggedString;
        }

        public SluggedString slug() {
            return sluggedString;
        }
    }

    static class SluggedString {
        private final String raw;

        public SluggedString(String raw) {
            this.raw = raw;
        }

        String value() {
            return raw.toLowerCase().replace(" ", "-");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SluggedString slug = (SluggedString) o;
            return Objects.equals(raw, slug.raw);
        }

        @Override
        public int hashCode() {
            return Objects.hash(raw);
        }
    }

    ImmutableObjectDependencyFreeArticle immutableObjectDependencyFreeArticle(String title, String content) {
        return new ImmutableObjectDependencyFreeArticle(title, content, new Slugging().text(title));
    }

    class ImmutableObjectDependencyFreeArticle {
        private final String title;
        private final String content;
        private final String slug;

        public ImmutableObjectDependencyFreeArticle(String title, String content, String slug) {
            this.title = title;
            this.content = content;
            this.slug = slug;
        }

        public String slug() {
            return slug;
        }
    }

    static class Slugging {
        String text(String text) {
            return text.toLowerCase().replace(" ", "-");
        }
    }
}
