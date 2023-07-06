package com.iroshnk.nftraffle.payload;

public class PaginationResponse<T> extends Response<T> {

    private long totalRecord;
    private int page;
    private int size;

    public PaginationResponse() {
    }

    public PaginationResponse(String code, String description) {
        super(code, description);
    }

    public PaginationResponse(T data, long totalRecord, int page, int size) {
        super(data);
        this.totalRecord = totalRecord;
        this.page = page;
        this.size = size;
    }

    public PaginationResponse(String code, String description, T data, long totalRecord, int page, int size) {
        super(code, description, data);
        this.totalRecord = totalRecord;
        this.page = page;
        this.size = size;
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
