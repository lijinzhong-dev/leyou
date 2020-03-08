package com.leyou.es.search.pojo;

import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/10
 * @Description:   用于接收前台搜索查询时传入的json数据
 * @version: 1.0
 */
public class SearchRequest {

    private String key;// 搜索条件
    private Integer page;// 当前页码

    private String sortBy;// 排序字段

    private Boolean descending;//是否降序

    private Map<String,String> filter;// 用于封装前端传过来的过滤参数(分类、品牌、规格参数)

    private static final int DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final int DEFAULT_PAGE = 1;// 默认页

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }

}
