package com.persistent.order.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Orders.
 */
@Entity
@Table(name = "orders")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "product_cnt", nullable = false)
    private Integer productCnt;

    @NotNull
    @Column(name = "cust_id", nullable = false)
    private Integer custId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProductCnt() {
        return productCnt;
    }

    public Orders productCnt(Integer productCnt) {
        this.productCnt = productCnt;
        return this;
    }

    public void setProductCnt(Integer productCnt) {
        this.productCnt = productCnt;
    }

    public Integer getCustId() {
        return custId;
    }

    public Orders custId(Integer custId) {
        this.custId = custId;
        return this;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Orders orders = (Orders) o;
        if (orders.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, orders.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Orders{" +
            "id=" + id +
            ", productCnt='" + productCnt + "'" +
            ", custId='" + custId + "'" +
            '}';
    }
}
