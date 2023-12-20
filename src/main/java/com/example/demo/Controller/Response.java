package com.example.demo.Controller;

public class Response {
    private int status = 0;
    private String msg;
    private Object data = null;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setState(Boolean v) {
        if (v) {
            this.status = 200;
        } else {
            this.status = 0;
        }
    }
}
