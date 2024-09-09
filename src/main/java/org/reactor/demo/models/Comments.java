package org.reactor.demo.models;

import java.util.ArrayList;
import java.util.List;

public class Comments {


    List<String> comments;

    public Comments() {
        this.comments = new ArrayList<>();
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    @Override
    public String toString() {
        return "Comments{" +
                "comments=" + comments +
                '}';
    }
}
