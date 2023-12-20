package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public Response login(@RequestBody User obj){
        String sql = "select * from user where name = ?";
        List<Map<String,Object>> users = jdbcTemplate.queryForList(sql,  obj.getName());
        Response res = new Response();
        if (users.isEmpty()) {
            res.setStatus(0);
            res.setMsg("当前用户不存在");
            res.setData(null);
        } else if (!users.get(0).get("password").equals(obj.getPassword())) {
            res.setStatus(0);
            res.setMsg("密码不正确");
            res.setData(null);
        } else {
            res.setStatus(200);
            res.setMsg("登录成功");
            res.setData(users.get(0));
        }
        return res;
    }

    @PostMapping("/register")
    public Response register(@RequestBody User obj){
        String sql = "select * from user where name = '" + obj.getName() + "'";
        List<Map<String,Object>> users = jdbcTemplate.queryForList(sql);
        Response res = new Response();
        if (users.isEmpty()) {
            String updateSql = String.format(
                "insert into user(name, chineseName, password) values('%s', '%s', '%s');",
                obj.getName(),
                obj.getChineseName(),
                obj.getPassword()
            );
            int num = jdbcTemplate.update(updateSql);
            if (num > 0) {
                res.setState(true);
                res.setMsg("注册成功");
                res.setData(jdbcTemplate.queryForList(sql).get(0));
            } else {
                res.setState(false);
                res.setMsg("注册失败");
            }
            return res;
        }
        res.setStatus(0);
        res.setMsg("当前用户已注册");
        res.setData(null);
        return res;
    }
}
