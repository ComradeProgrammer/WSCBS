package win.worldismine.urlservice.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import win.worldismine.urlservice.model.URLObject;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@ActiveProfiles("test")
class UrlDaoTest {
    @Autowired
    UrlDao urlDao;

    @BeforeEach
    void before() {
        urlDao.deleteAll();
    }

    @AfterEach
    void after() {
        urlDao.deleteAll();

    }

    @Test
    public void testUrlDao() {
        assertNull(urlDao.getUrlObjById("non-exist"));
        urlDao.deleteUrlById("non-exist");

        urlDao.createUrlObj(new URLObject("id1", "url1", "creator1"));
        urlDao.createUrlObj(new URLObject("id2", "url2", "creator2"));
        urlDao.createUrlObj(new URLObject("id3", "url3", "creator3"));
        urlDao.createUrlObj(new URLObject("id4", "url1", "creator1"));
        urlDao.createUrlObj(new URLObject("id5", "url2", "creator2"));
        urlDao.createUrlObj(new URLObject("id6", "url3", "creator3"));
        assertNotNull(urlDao.getUrlObjById("id1"));
        assertEquals(urlDao.getUrlObjById("id2").getUrl(), "url2");
        assertEquals(urlDao.getUrlObjById("id3").getCreator(), "creator3");

        assertEquals(urlDao.listUrlObjsByUser("creator1").size(), 2);
        assertEquals(urlDao.listUrlObjsByUser("creator2").size(), 2);
        assertEquals(urlDao.listUrlObjsByUser("creator3").size(), 2);
        urlDao.updateUrlObjById(new URLObject("id2", "new-url2", "creator2"));
        assertEquals(urlDao.getUrlObjById("id2").getUrl(), "new-url2");

        urlDao.deleteUrlById("id1");
        assertEquals(urlDao.listUrlObjsByUser("creator1").size(), 1);
        urlDao.deleteUrlByUser("creator2");
        assertEquals(urlDao.listUrlObjsByUser("creator2").size(), 0);

    }

}