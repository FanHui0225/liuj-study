package com.stereo.study.util;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable{
    private int pageNo = 1;// 页码，默认是第一页
    private int pageSize = 10;// 每页显示的记录数，默认是15
    private int totalRecord;// 总记录数
    private int currentRecord;//当前记录
    private int totalPage;// 总页数
    private List<T> results;// 对应的当前页记录
    private int prevNo;//上一页
    private int nextNo;//下一页
    private boolean prev;
    private boolean next;

    public Page() {
    }

    public Page(int pageNo, int pageSize, int totalPage, int totalRecord, List<T> results) {
        prev = pageNo - 1 > 0 && totalPage > 1;
        if (prev) prevNo = pageNo - 1;
        next = pageNo + 1 <= totalPage;
        if (next) nextNo = pageNo + 1;
        setPageNo(pageNo);
        setPageSize(pageSize);
        setTotalPage(totalPage);
        setResults(results);
        setTotalRecord(totalRecord);
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
        // 在设置总页数的时候计算出对应的总页数，在下面的三目运算中加法拥有更高的优先级，所以最后可以不加括号。
        int totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize
                : totalRecord / pageSize + 1;
        this.setTotalPage(totalPage);
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
        currentRecord = results.size();
    }

    public int getCurrentRecord() {
        return currentRecord;
    }

    public void setCurrentRecord(int currentRecord) {
        this.currentRecord = currentRecord;
    }

    public boolean isPrev() {
        return prev;
    }

    public boolean isNext() {
        return next;
    }

    public void setPrev(boolean prev) {
        this.prev = prev;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public int getPrevNo() {
        return prevNo;
    }

    public int getNextNo() {
        return nextNo;
    }

    public void setPrevNo(int prevNo) {
        this.prevNo = prevNo;
    }

    public void setNextNo(int nextNo) {
        this.nextNo = nextNo;
    }
}
