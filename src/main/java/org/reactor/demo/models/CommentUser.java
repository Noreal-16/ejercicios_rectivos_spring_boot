package org.reactor.demo.models;

public class CommentUser {

    Users users;
    Comments comment;

    public CommentUser(Users users, Comments comment) {
        this.users = users;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return
                "users=" + users +
                        ", comment=" + comment;
    }
}
