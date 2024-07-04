package org.example.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "post")
    private List<PostComment> post_comments;

    @ManyToOne
    private Users users;

    public Post() {
    }

    public LocalDateTime getDateCreation(LocalDateTime now) {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<PostComment> getPost_comments() {
        return post_comments;
    }

    public void setPost_comments(List<PostComment> post_comments) {
        this.post_comments = post_comments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dateCreation=" + dateCreation +
                ", user=" + (users != null ? users.getId() : "null") + // Пример вывода идентификатора пользователя
                '}';
    }
}
