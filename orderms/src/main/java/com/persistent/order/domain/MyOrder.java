package com.persistent.order.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A MyOrder.
 */
@Entity
@Table(name = "my_order")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MyOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cust_id")
    private Integer custId;

    @Column(name = "prod_cnt")
    private Integer prodCnt;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCustId() {
        return custId;
    }

    public MyOrder custId(Integer custId) {
        this.custId = custId;
        return this;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public Integer getProdCnt() {
        return prodCnt;
    }

    public MyOrder prodCnt(Integer prodCnt) {
        this.prodCnt = prodCnt;
        return this;
    }

    public void setProdCnt(Integer prodCnt) {
        this.prodCnt = prodCnt;
    }

    public String getStatus() {
        return status;
    }

    public MyOrder status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyOrder myOrder = (MyOrder) o;
        if (myOrder.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, myOrder.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MyOrder{" +
            "id=" + id +
            ", custId='" + custId + "'" +
            ", prodCnt='" + prodCnt + "'" +
            ", status='" + status + "'" +
            '}';
    }
}
