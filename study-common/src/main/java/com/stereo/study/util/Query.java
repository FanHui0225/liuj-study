package com.stereo.study.util;

import java.io.Serializable;
import java.util.List;

public class Query<T> implements Serializable {

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private Integer pageIndex;

    private Integer pageSize = DEFAULT_PAGE_SIZE;

    private Integer offset = 0;

    private String sortField = "create_time";

    private Boolean sortDesc = true;

    private Integer pageTotal;

    private Integer totalRecord;

    private List<T> results;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Boolean getSortDesc() {
        return sortDesc;
    }

    public void setSortDesc(Boolean sortDesc) {
        this.sortDesc = sortDesc;
    }

    public void setPageIndexAndPageSize(Integer pageIndex, Integer pageSize) {
        if (pageIndex <= 0)
            pageIndex = 1;
        else
            this.pageIndex = pageIndex;
        if (pageSize <= 0) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else
            this.pageSize = pageSize;
        offset = pageSize * (pageIndex - 1);
    }

    public void setTotalCount(Long count) {
        if (count == null || count == 0) {
            this.pageTotal = 0;
            this.totalRecord = 0;
        } else {
            long totalRecord = count;
            int pageSize = this.pageSize;
            long totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize
                : totalRecord / pageSize + 1;
            this.pageTotal = (int) totalPage;
            this.totalRecord = (int) totalRecord;
        }
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public List<T> getResults() {
        return results;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
