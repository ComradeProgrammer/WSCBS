package win.worldismine.urlservice.dao;

import org.apache.ibatis.annotations.*;
import win.worldismine.urlservice.model.URLObject;

import java.util.List;

@Mapper
public interface UrlDao {

    @Select("select * from urls where id=#{id}")
    URLObject getUrlObjById(@Param("id") String id);

    @Update("update urls set url=#{url} where id=#{id}")
    void updateUrlObjById(URLObject obj);

    @Insert("insert into urls values (#{id},#{url},#{creator})")
    void createUrlObj(URLObject obj);

    @Delete("delete from urls where id=#{id}")
    void deleteUrlById(@Param("id") String id);

    @Delete("delete from urls where 1=1")
    void deleteAll();

    @Select("select * from urls where creator=#{creator}")
    List<URLObject> listUrlObjsByUser(@Param("creator") String creator);

    @Delete("delete from urls where  creator=#{creator}")
    void deleteUrlByUser(@Param("creator") String creator);

    @Update("""
            CREATE TABLE IF NOT EXISTS urls(
                id VARCHAR(256) PRIMARY KEY NOT NULL UNIQUE ,
                url VARCHAR(256) NOT NULL,
                creator VARCHAR(256) NOT NULL
            );""")
    void createTableIfNotExist();

}
