package org.example;

import org.example.entity.Post;
import org.example.entity.PostComment;
import org.example.entity.Users;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;


package org.example;

import java.time.LocalDateTime;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;


public class Main {

    /**
     * Используя hibernate, создать таблицы:
     * 1. Post (публикация) (id, title)
     * 2. PostComment (комментарий к публикации) (id, text, post_id)
     *
     * Написать стандартные CRUD-методы: создание, загрузка, удаление.
     *
     * Доп. задания:
     * 1. * В сущностях post и postComment добавить поля timestamp с датами.
     * 2. * Создать таблицу users(id, name) и в сущностях post и postComment добавить ссылку на юзера.
     * 3. * Реализовать методы:
     * 3.1 Загрузить все комментарии публикации
     * 3.2 Загрузить все публикации по идентификатору юзера
     * 3.3 ** Загрузить все комментарии по идентификатору юзера
     * 3.4 **** По идентификатору юзера загрузить юзеров, под чьими публикациями он оставлял комменты.
     * // userId -> List<User>
     *
     *
     * Замечание:
     * 1. Можно использовать ЛЮБУЮ базу данных (например, h2)
     * 2. Если запутаетесь, приходите в группу в телеграме или пишите мне @inchestnov в личку.
     */

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure();
        try(SessionFactory sessionFactory = configuration.buildSessionFactory()){
            //createTables(sessionFactory);
            //addDataTables(sessionFactory);
            //deletePostAndPostComments(sessionFactory);
            //allCommentsByPost(sessionFactory,2L);
            //allPostByUserId(sessionFactory,3L);
            //allCommentByUserId(sessionFactory,2L);
            //listUsersUnderCommentsUsersPostByUserId(sessionFactory,2L);

        }

    }
    // Создание таблиц с помощью session и метода createNativeQuery
    private static void createTables(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                // Создание таблицы user
                System.out.println("Creating table user...");
                session.createNativeQuery("CREATE TABLE users (" +
                        "id BIGINT PRIMARY KEY, " +
                        "name VARCHAR(256))").executeUpdate();
                System.out.println("Table user created.");

                // Создание таблицы post
                System.out.println("Creating table post...");
                session.createNativeQuery("CREATE TABLE post (" +
                        "id BIGINT PRIMARY KEY, " +
                        "title VARCHAR(255), " +
                        "date_creation TIMESTAMP, " +
                        "users_id BIGINT, " +
                        "FOREIGN KEY (users_id) REFERENCES users(id))").executeUpdate();
                System.out.println("Table post created.");

                // Создание таблицы post_comment
                System.out.println("Creating table post_comment...");
                session.createNativeQuery("CREATE TABLE post_comment (" +
                        "id BIGINT PRIMARY KEY, " +
                        "text VARCHAR(255), " +
                        "post_id BIGINT, " +
                        "users_id BIGINT, " +
                        "CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE, " +
                        "CONSTRAINT fk_user FOREIGN KEY (users_id) REFERENCES users(id) ON DELETE CASCADE)").executeUpdate();
                System.out.println("Table post_comment created.");

                tx.commit();
                System.out.println("Transaction committed.");
            } catch (Exception e) {
                tx.rollback();
                System.err.println("Transaction rolled back due to an error.");
                e.printStackTrace();
            }
        }
    }



    // Добавляем данные в таблицы
    private static void addDataTables(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                // Создаем пользователей (Users)
                Users user1 = new Users();
                user1.setId(1L);
                user1.setName("User #1");

                session.persist(user1);

                Users user2 = new Users();
                user2.setId(2L);
                user2.setName("User #2");
                session.persist(user2);

                // Создаем посты и добавляем им user
                Post post = new Post();
                post.setId(1L);
                post.setTitle("Post #1");
                post.setDateCreation(LocalDateTime.of(2023,12,12,10,10));
                post.setUsers(user1);
                session.persist(post);

                Post post1 = new Post();
                post1.setId(2L);
                post1.setTitle("Post #2");
                post1.setDateCreation(LocalDateTime.of(2024, 6, 22, 12, 00));
                post1.setUsers(user2);
                session.persist(post1);

                Post post2 = new Post();
                post2.setId(3L);
                post2.setTitle("Post #3");
                post2.setDateCreation(LocalDateTime.of(2024,10,10,22,43));
                post2.setUsers(user2);
                session.persist(post2);

                PostComment postComment = new PostComment();
                postComment.setId(1L);
                postComment.setText("comment post #1");
                postComment.setPost(post);
                postComment.setUser(user1);
                postComment.setUser(user2);
                session.persist(postComment);

                PostComment postComment1 = new PostComment();
                postComment1.setId(2L);
                postComment1.setText("comments post #2");
                postComment1.setPost(post);
                postComment1.setUser(user1);
                session.persist(postComment1);

                PostComment postComment2 = new PostComment();
                postComment2.setId(3L);
                postComment2.setText("comment post #3");
                postComment2.setPost(post2);
                postComment2.setUser(user1);
                session.persist(postComment2);



                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                System.err.println("Transaction rolled back due to an error.");
                e.printStackTrace();
            }
        }
    }

    // Удаление post и post_comment для того чтобы удалялся пост с зависимостью добавили при созднии таблицы
    // ON DELETE CASCADE
    private static void deletePostAndPostComments(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Post post = session.find(Post.class, 1L);
            session.remove(post);
            tx.commit();
        }
    }
    // Все комментарии по id поста
    private static void allCommentsByPost(SessionFactory sessionFactory, long idPost){
        try(Session session = sessionFactory.openSession()){
            Post post = session.find(Post.class,idPost);
            if (post != null) {
                // Создаем запрос на выборку комментариев для данного поста
                Query<PostComment> query = session.createQuery(
                        "SELECT pc FROM PostComment pc WHERE pc.post = :post", PostComment.class);
                query.setParameter("post", post);

                // Получаем результат запроса в виде списка комментариев
                List<PostComment> comments = query.getResultList();

                // Выводим комментарии
                for (PostComment comment : comments) {
                    System.out.println(comment);
                }
            } else {
                System.out.println("Post with id " + idPost + " not found.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching comments for post with id " + idPost);
            e.printStackTrace();
        }

    }
    // Все посты которые выставил User по его id
    private static void allPostByUserId(SessionFactory sessionFactory,long idUser){
        try (Session session = sessionFactory.openSession()) {
            Users user = session.find(Users.class, idUser);
            if (user != null) {
                List<Post> posts = user.getPosts(); // Получаем список сообщений пользователя
                for (Post post : posts) {
                    System.out.println(post); // Выводим информацию о каждом сообщении
                }
            } else {
                System.out.println("User with id " + idUser + " not found.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching posts for user with id " + idUser);
            e.printStackTrace();
        }

    }
    // Все комментарии который оставил Users по его id
    private static void allCommentByUserId(SessionFactory sessionFactory,long idUser) {
        try (Session session = sessionFactory.openSession()) {
            Users user = session.find(Users.class, idUser);
            if (user != null) {
                List<PostComment> postComments = user.getPostComments(); // Получаем список сообщений пользователя
                for (PostComment comments : postComments) {
                    System.out.println(comments); // Выводим информацию о каждом сообщении
                }
            } else {
                System.out.println("User with id " + idUser + " not found.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching posts for user with id " + idUser);
            e.printStackTrace();
        }
    }


    // Все users под которыми оставил комментариий user c id
    private static void listUsersUnderCommentsUsersPostByUserId(SessionFactory sessionFactory, long idUser) {
        try (Session session = sessionFactory.openSession()) {
            Users users = session.find(Users.class, idUser);

            if (users != null) {
                Set<Users> usersSet = new HashSet<>();

                // Запрос для получения всех комментариев пользователя
                Query<PostComment> commentQuery = session.createQuery(
                        "SELECT pc FROM PostComment pc WHERE pc.users.id = :usersId",
                        PostComment.class
                );
                commentQuery.setParameter("usersId", idUser);
                List<PostComment> postComments = commentQuery.getResultList();

                // Для каждого комментария получаем пост и его автора
                for (PostComment comment : postComments) {
                    Post post = comment.getPost();
                    if (post != null) {
                        Users postAuthor = post.getUsers(); // Получаем пользователя, написавшего пост
                        if (postAuthor != null && !postAuthor.equals(users)) {
                            usersSet.add(postAuthor); // Добавляем автора поста в множество, если он не текущий пользователь
                        }
                    }
                }

                // Вывод результатов
                System.out.println("Users who have posted under comments by User #" + idUser + ":");
                for (Users u : usersSet) {
                    System.out.println(u);
                }
            } else {
                System.out.println("User with id " + idUser + " not found.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching data for user with id " + idUser);
            e.printStackTrace();
        }
    }

}





