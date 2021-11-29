package com.nhcar.entity;

import java.util.List;

public class ECarNewResult {
    private int pageSize;
    private int dataCount;
    private int start;
    private int pageCount;
    private int page;
    private List<ECarNews> dataResult;

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<ECarNews> getDataResult() {
        return dataResult;
    }

    public void setDataResult(List<ECarNews> dataResult) {
        this.dataResult = dataResult;
    }
}