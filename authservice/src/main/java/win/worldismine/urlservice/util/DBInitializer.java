package win.worldismine.urlservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import win.worldismine.urlservice.dao.UserDao;

@Component
public class DBInitializer implements CommandLineRunner {
    @Autowired
    private UserDao userDa0;
    public void run(String... args) {
        userDa0.createTableIfNotExist();
    }
}