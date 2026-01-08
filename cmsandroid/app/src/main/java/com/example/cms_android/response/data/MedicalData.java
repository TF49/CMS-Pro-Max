package com.example.cms_android.response.data;

import java.util.List;
import com.example.cms_android.model.Medical;

public class MedicalData {
    private List<Medical> data;
    private int total;
    private int page;
    private int size;
    private int pages;

    public MedicalData() {
    }

    public List<Medical> getData() {
        return data;
    }

    public void setData(List<Medical> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}