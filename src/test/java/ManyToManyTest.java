import static org.testng.Assert.assertEquals;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.query.NativeQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import domain.User;

public class ManyToManyTest {

    private SessionFactory sessionFactory;

    @BeforeClass
    public void setUp() throws SQLException {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Test
    public void testForCount() {
        User user1 = new User("test1");
        User user2 = new User("test2");
        User user3 = new User("test3");

        //user1关注user2、user3
        Set<User> allFollowings = new HashSet<User>();
        allFollowings.add(user2);
        allFollowings.add(user3);
        user1.setFollowing(allFollowings);

        //user2被user关注
        user2.addFollower(user3);

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(user1);
        session.save(user2);
        session.save(user3);

        tx.commit();
        session.close();

        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        NativeQuery query = session.createSQLQuery("SELECT count(*) FROM users");
        List results = query.list();
        BigInteger count = (BigInteger)results.get(0);
        assertEquals(count,BigInteger.valueOf(3));
        tx.commit();
        session.close();
    }

    @Test
    public void testForFollowing() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        NativeQuery query = session.createSQLQuery("SELECT count(*) FROM user_relations WHERE follower_id=1");
        List results = query.list();
        BigInteger count = (BigInteger)results.get(0);
        assertEquals(count,BigInteger.valueOf(2));
        tx.commit();
        session.close();
    }

    @Test
    public void testForFollowed() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        User user3 = (User)session.get(User.class, 3);
        User user4 = new User("test4");

        //user4被user3关注
        user4.addFollower(user3);

        session.save(user4);
        tx.commit();
        session.close();

        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        NativeQuery query = session.createSQLQuery("SELECT count(*) FROM user_relations WHERE followed_id=4");
        List results = query.list();
        BigInteger count = (BigInteger)results.get(0);
        assertEquals(count,BigInteger.valueOf(1));
        tx.commit();
        session.close();
    }

    @Test
    public void testForRemoveFollowing() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        User user3 = (User)session.get(User.class, 3);
        User user4 = (User)session.get(User.class, 4);

        //user3不再关注user4
        user3.removeFollowing(user4);

        session.save(user4);
        tx.commit();
        session.close();

        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        NativeQuery query = session.createSQLQuery("SELECT count(*) FROM user_relations WHERE followed_id=4");
        List results = query.list();
        BigInteger count = (BigInteger)results.get(0);
        assertEquals(count,BigInteger.valueOf(0));
        tx.commit();
        session.close();
    }
}
