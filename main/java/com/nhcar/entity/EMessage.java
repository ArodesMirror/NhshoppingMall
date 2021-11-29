package com.nhcar.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "message")
public class EMessage {
    @DatabaseField(generatedId = true)
    private int mid;
    @DatabaseField(columnName = "mtitle")
    private String mtitle;
    @DatabaseField(columnName = "mcontent")
    private String mcontent;
    @DatabaseField(columnName = "mdate")
    private Date mdate;

    public EMessage() {
    }

    public EMessage(int mid, String mtitle, String mcontent, Date mdate) {
        this.mid = mid;
        this.mtitle = mtitle;
        this.mcontent = mcontent;
        this.mdate = mdate;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getMcontent() {
        return mcontent;
    }

    public void setMcontent(String mcontent) {
        this.mcontent = mcontent;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }
}
