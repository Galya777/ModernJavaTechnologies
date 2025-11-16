package bg.sofia.uni.fmi.mjt.newsfeed.model;

import java.util.Objects;

public class Source {
    private final String id;
    private final String name;

    public Source(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return Objects.equals(id, source.id) &&
               Objects.equals(name, source.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Source{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
