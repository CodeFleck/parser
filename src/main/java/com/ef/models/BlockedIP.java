package com.ef.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BlockedIP {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String ip;
    private String comment;

    public BlockedIP() {
    }

    public BlockedIP(String ip, String comment) {
        this.ip = ip;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "BlockedIP{" + "id=" + id + ", ip='" + ip + '\'' + ", comment='" + comment + '\'' + '}';
    }
}
