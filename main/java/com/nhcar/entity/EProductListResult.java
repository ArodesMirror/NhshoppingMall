package com.nhcar.entity;

import java.util.List;

public class EProductListResult {
    private int pageSize;
    private int dataCount;
    private int start;
    private int pageCount;
    private int page;
    private List<EProduct>dataResult;

    public EProductListResult() {
    }

    public EProductListResult(int pageSize, int dataCount, int start, int pageCount, int page, List<EProduct> dataResult) {
        this.pageSize = pageSize;
        this.dataCount = dataCount;
        this.start = start;
        this.pageCount = pageCount;
        this.page = page;
        this.dataResult = dataResult;

    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getDataCount() {
        return dataCount;
    }

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

    public List<EProduct> getDataResult() {
        return dataResult;
    }

    public void setDataResult(List<EProduct> dataResult) {
        this.dataResult = dataResult;
    }
}
