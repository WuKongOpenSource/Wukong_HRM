package com.kakarote.core.entity;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kakarote.core.config.WebConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangzhiwei
 * 默认分页
 */
public class BasePage<T> implements IPage<T> {

    private static final long serialVersionUID = 8545996863226528798L;

    /**
     * 查询数据列表
     */
    private List<T> list;

    /**
     * 总数
     */
    private long totalRow;

    /**
     * 每页显示条数，默认 15
     */
    private long pageSize;

    /**
     * 当前页
     */
    private long pageNumber;

    /**
     * 排序字段信息
     */
    private List<OrderItem> orders;

    /**
     * 自动优化 COUNT SQL
     */
    private boolean optimizeCountSql;


    /**
     * 额外数据
     */
    private Object extraData;


    public BasePage() {
        this(null, null);
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public BasePage(Long current, Long size) {
        this(current, size, 0L);
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     * @param total   总行数
     */
    public BasePage(Long current, Long size, Long total) {
        this(current, size, total, new ArrayList<>());
    }

    /**
     * @param current 当前页
     * @param size    每页显示条数
     * @param total   总行数
     * @param list    数据列表
     */
    public BasePage(Long current, Long size, Long total, List<T> list) {
        if (current == null || current < 0L) {
            current = 1L;
        }
        if (size == null || size < 0L) {
            size = 15L;
        }
        if (total == null || total < 0L) {
            total = 0L;
        }

        this.pageNumber = current;
        this.pageSize = size;
        this.totalRow = total;
        this.orders = new ArrayList<>();
        this.optimizeCountSql = true;
        this.list = list;
    }


    @Override
    @JsonIgnore
    public List<T> getRecords() {
        return this.list;
    }

    public List<T> getList() {
        return this.list;
    }

    @JsonSerialize(using = WebConfig.NumberSerializer.class)
    public Long getTotalRow() {
        return this.totalRow;
    }

    @JsonSerialize(using = WebConfig.NumberSerializer.class)
    public Long getTotalPage() {
        if (getSize() == 0) {
            return 0L;
        }
        long pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        return pages;
    }

    @JsonSerialize(using = WebConfig.NumberSerializer.class)
    public Long getPageSize() {
        return this.pageSize;
    }

    @JsonSerialize(using = WebConfig.NumberSerializer.class)
    public Long getPageNumber() {
        return this.pageNumber;
    }

    public boolean isFirstPage() {
        return this.pageNumber == 1L;
    }

    public boolean isLastPage() {
        return getTotal() == 0 || this.pageNumber >= getTotalPage();
    }

    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public BasePage<T> setRecords(List<T> records) {
        this.list = records;
        return this;
    }

    @Override
    @JsonIgnore
    public long getTotal() {
        return this.totalRow;
    }

    @Override
    public BasePage<T> setTotal(long total) {
        this.totalRow = total;
        return this;
    }

    @Override
    @JsonIgnore
    public long getSize() {
        return this.pageSize;
    }

    @Override
    public BasePage<T> setSize(long size) {
        this.pageSize = size;
        return this;
    }

    @Override
    @JsonIgnore
    public long getCurrent() {
        return this.pageNumber;
    }

    @Override
    public BasePage<T> setCurrent(long current) {
        this.pageNumber = current;
        return this;
    }


    @Override
    public List<OrderItem> orders() {
        return orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    @Override
    public boolean optimizeCountSql() {
        return optimizeCountSql;
    }


    public BasePage<T> setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
        return this;
    }

    /**
     * 类型转换 通过beanCopy
     *
     * @param clazz 转换后的类
     * @param <R>   R
     * @return BasePage
     */
    public <R> BasePage<R> copy(Class<R> clazz) {
        return copy(obj -> BeanUtil.copyProperties(obj, clazz));
    }

    /**
     * 类型转换 通过beanCopy
     *
     * @param <R> R
     * @return BasePage
     */
    public <R> BasePage<R> copy(Function<? super T, ? extends R> mapper) {
        BasePage<R> basePage = new BasePage<>(getCurrent(), getSize(), getTotal());
        basePage.setRecords(getRecords().stream().map(mapper).collect(Collectors.toList()));
        return basePage;
    }

    @Override
    @JsonIgnore
    public long getPages() {
        return getTotalPage();
    }

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }
}
