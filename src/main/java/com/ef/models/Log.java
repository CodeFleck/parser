package com.ef.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Log {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private LocalDateTime date;
    private String ip;
    private String request;
    private String status;
    private String userAgent;

    public Log() { }

    public Log(LocalDateTime date, String ip, String request, String status, String userAgent) {
        this.date = date;
        this.ip = ip;
        this.request = request;
        this.status = status;
        this.userAgent = userAgent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "Log{" + "id=" + id + ", date=" + date + ", ip='" + ip + '\'' + ", request='" + request + '\'' + ", status='" + status + '\'' + ", userAgent='" + userAgent + '\'' + '}';
    }
}
