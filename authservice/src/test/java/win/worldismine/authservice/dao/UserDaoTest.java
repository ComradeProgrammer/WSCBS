package win.worldismine.authservice.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sqlite.SQLiteException;
import win.worldismine.authservice.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDaoTest {
    @Autowired
    UserDao userDao;

    @BeforeEach
    void before() {
        userDao.deleteAll();
    }

    @AfterEach
    void after() {
        userDao.deleteAll();

    }

    @Test
    public void testUserDao() {
        User usera = new User();
        usera.setUsername("aaa");
        usera.setPasswordFromRawText("passworda");
        User userb = new User();
        userb.setUsername("bbb");
        userb.setPasswordFromRawText("passwordb");
        User userc = new User();
        userc.setUsername("ccc");
        userc.setPasswordFromRawText("passwordc");
        userDao.createUser(usera);
        userDao.createUser(userb);
        userDao.createUser(userc);

        usera = userDao.findUserByUserName("aaa");
        userb = userDao.findUserByUserName("bbb");
        userc = userDao.findUserByUserName("ccc");
        assertTrue(usera.verifyPassword("passworda"));
        assertTrue(userb.verifyPassword("passwordb"));
        assertTrue(userc.verifyPassword("passwordc"));

        usera.setPasswordFromRawText("newa");
        userDao.updateUserByUserName(usera);
        usera = userDao.findUserByUserName("aaa");
        assertTrue(usera.verifyPassword("newa"));

        //test duplicate
        User userd=userDao.findUserByUserName("ddd");
        assertNull(userd);
    }
}