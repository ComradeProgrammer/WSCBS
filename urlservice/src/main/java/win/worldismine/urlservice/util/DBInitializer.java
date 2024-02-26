package win.worldismine.urlservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import win.worldismine.urlservice.dao.UrlDao;

@Component
public class DBInitializer implements CommandLineRunner {
    @Autowired
    private UrlDao urlDao;
    public void run(String... args) {
        urlDao.createTableIfNotExist();
    }
}