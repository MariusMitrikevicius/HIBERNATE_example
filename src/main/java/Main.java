import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Sukuriame SessionFactory iš hibernate.cfg.xml
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Comment.class)
                .buildSessionFactory();

        // Atidarome sesiją
        Session session = factory.openSession(); // Naudojame openSession() vietoj getCurrentSession()

        try {
            // Pradedame transakciją
            session.beginTransaction();

            // Pridedame 10 komentarų, jei lentelė tuščia
            long count = (long) session.createQuery("select count(c) from Comment c").uniqueResult();
            if (count == 0) {
                for (int i = 1; i <= 10; i++) {
                    Comment comment = new Comment();
                    comment.setAuthor("Autorius " + i);
                    comment.setContent("Tai yra komentaras nr. " + i);
                    session.save(comment);
                }
            }

            // Užbaigiame transakciją
            session.getTransaction().commit();

            // Atspausdiname visus įrašus
            session.beginTransaction();
            List<Comment> comments = session.createQuery("from Comment", Comment.class).getResultList();
            comments.forEach(System.out::println);
            session.getTransaction().commit();

            // Atnaujiname konkretų įrašą (pvz., pakeičiame pirmo įrašo turinį)
            session.beginTransaction();
            Comment firstComment = session.get(Comment.class, 1L);
            if (firstComment != null) {
                firstComment.setContent("Atnaujintas komentaras!");
                session.update(firstComment);
            }
            session.getTransaction().commit();

        } finally {
            session.close();
            factory.close();
        }
    }
}
