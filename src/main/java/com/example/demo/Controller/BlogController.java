package com.example.demo.Controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Value("${file.location}")
    private String uploadFilePath;
    // 创建博客
    @PostMapping("/add")
    public Response addBlog(@RequestBody Map<String,Object> obj){
        Gson goJson = new Gson();
        String tagStr = goJson.toJson(obj.get("tags"));
        String saveSql = "insert into blog(title, description, tags, content, public, createTime) values(?, ?, ?, ?, ?, ?);";
        int num = jdbcTemplate.update(
                saveSql,
                obj.get("title"),
                obj.get("description"),
                tagStr,
                obj.get("content"),
                obj.get("public"),
                obj.get("createTime")
        );
        Response res = new Response();
        if (num > 0) {
            String querySql = "select * from blog";
            List<Map<String,Object>> blogList = jdbcTemplate.queryForList(querySql);
            res.setState(true);
            res.setData(blogList.get(blogList.size() - 1));
            return res;
        }
        res.setState(false);
        res.setMsg("创建失败");
        return res;
    }

    @PostMapping("/modify")
    public Response modifyBlog(@RequestBody Map<String,Object> obj){
        String sql = "update blog set title = ?, description = ?, tags = ?, content = ?, updateTime = ?, public = ? where id = ?;";
        Response res = new Response();
        int num = jdbcTemplate.update(
                sql,
                obj.get("title"),
                obj.get("description"),
                new Gson().toJson(obj.get("tags")),
                obj.get("content"),
                obj.get("updateTime"),
                obj.get("public"),
                (int)obj.get("id")
        );
        if (num > 0) {
            res.setState(true);
            return res;
        }
        res.setState(false);
        res.setMsg("更新失败");
        return res;
    }

    @PostMapping("/like")
    public Response likeBlog(@RequestBody Blog obj){
        String sql = String.format(
                "update blog set `like` = '%d' where id = '%d';",
                (int)obj.getLike(),
                (int)obj.getId()
        );
        int num = jdbcTemplate.update(sql);
        Response res = new Response();
        if (num > 0) {
            res.setState(true);
            return res;
        }
        res.setState(false);
        res.setMsg("更新失败");
        return res;
    }

    // 获取博客列表
    @PostMapping("/list")
    public Response getBlogList(@RequestBody BlogListRequest obj){
        String querySql = "select * from blog where title like \"%\"?\"%\" order by id desc";
        String queryAllSql = "select * from blog order by id desc;";
        List<Map<String,Object>> blogList = null;
        if (obj.getSearchKey() == null) {
            blogList = jdbcTemplate.queryForList(queryAllSql);
        } else {
            blogList = jdbcTemplate.queryForList(querySql, obj.getSearchKey());
        }
        Response res = new Response();
        res.setState(true);
        List<Blog> blist = new ArrayList<>();
        blogList.forEach(m -> {
            Blog p = new Blog();
            p.setTitle((String)m.get("title"));
            p.setTags(new Gson().fromJson((String) m.get("tags"), String[].class));
            p.setLike((int)m.get("like"));
            p.setDescription((String)m.get("description"));
            p.setId((int)m.get("id"));
            blist.add(p);
        });
        res.setData(blist);
        return res;
    }

    // 获取博客详情
    @GetMapping("/detail")
    public Response getBlogDetail(@RequestParam("id") int id){
        String querySql = String.format(
                "select * from blog where id = '%d';",
                id
        );
        List<Map<String,Object>> blogList = jdbcTemplate.queryForList(querySql);
        Response res = new Response();
        if (blogList.isEmpty()) {
            res.setState(false);
            res.setData(null);
            return res;
        }
        List<Blog> blist = new ArrayList<>();
        blogList.forEach(m -> {
            Blog p = new Blog(
                (String)m.get("title"),
                new Gson().fromJson((String) m.get("tags"), String[].class),
                (int)m.get("like"),
                (String)m.get("description"),
                (String)m.get("content")
            );
            p.setId((int)m.get("id"));
            blist.add(p);
        });
        res.setState(true);
        res.setData(blist.get(0));
        return res;
    }

    @GetMapping("/delete")
    public Response deleteBlog(@RequestParam("id") int id){
        String querySql = String.format(
                "delete from blog where id = '%d';",
                id
        );
        int num = jdbcTemplate.update(querySql);
        Response res = new Response();
        if (num > 0) {
            res.setState(true);
        } else {
            res.setState(false);
            res.setMsg("删除失败");
        }
        return res;
    }

    @PostMapping("/upload_image")
    public HashMap<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        HashMap<String, Object> resMap = new HashMap<String, Object>();
        HashMap<String, Object> data = new HashMap<String, Object>();
        try {
            String fileName = new Date().getTime() + "_" + file.getOriginalFilename();
            String filePath = uploadFilePath + "/" + fileName;
            String imageUrl = String.format("http://39.99.244.38/images/%s", fileName);
            file.transferTo(new File(filePath));
            resMap.put("errno", 0);
            data.put("url", imageUrl);
            data.put("alt", fileName);
            resMap.put("data", data);
            return resMap;
        } catch (IOException e) {
            e.printStackTrace();
            resMap.put("errno", 1);
            resMap.put("message", e.getMessage());
            return resMap;
        }
    }
}
