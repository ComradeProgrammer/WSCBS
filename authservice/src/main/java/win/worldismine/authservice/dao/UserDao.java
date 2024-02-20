package win.worldismine.authservice.dao;

import org.apache.ibatis.annotations.*;
import win.worldismine.authservice.entity.User;

import java.util.List;

@Mapper
public interface UserDao {
    @Select(value = "SELECT * FROM users")
    List<User> listAllUser();

    @Select(value = "SELECT * FROM users where username = #{username}")
    User findUserByUserName(@Param("username") String username);

    @Insert("INSERT INTO  users VALUES (#{username},#{password})")
    void createUser(User user);
    @Update("UPDATE users SET password=#{password} WHERE username = #{username}")
    void updateUserByUserName(User user);

    @Delete("DELETE  FROM  users WHERE 1=1")
    void deleteAll();
}
