package ssi.backend.entities;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer order_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User buyerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User sellerId;

    @JsonIgnore
    @Column(name = "order_date", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Instant orderDate;

    @Column(name = "shipping_address", length = 100, nullable = false)
    private String shippingAddress;

    @Column(name = "orderNote", length = 100)
    private String orderNote;

    @Column(name = "status", nullable = false)
    @ColumnDefault("'COMPLETED'")
    private String status;

    @Column(name = "totalAmount", nullable = false)
    @ColumnDefault("0")
    private Integer totalAmount;

    @JsonIgnore
    @Column(name = "createdOn", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Instant createdOn;

    @JsonIgnore
    @Column(name = "updatedOn", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    private Instant updatedOn;
}
