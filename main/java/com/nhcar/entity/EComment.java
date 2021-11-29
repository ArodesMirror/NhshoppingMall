package com.nhcar.entity;

import java.util.Date;

public class EComment {
    private int cid;
    private int cpid;
    private String cname;
    private String ccontent;
    private Date cdate;

    public EComment() {
    }

    public EComment(int cid, int cpid, String cname, String ccontent, Date cdate) {
        this.cid = cid;
        this.cpid = cpid;
        this.cname = cname;
        this.ccontent = ccontent;
        this.cdate = cdate;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getCpid() {
        return cpid;
    }

    public void setCpid(int cpid) {
        this.cpid = cpid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCcontent() {
        return ccontent;
    }

    public void setCcontent(String ccontent) {
        this.ccontent = ccontent;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }
}
