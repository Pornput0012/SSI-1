package ssi.backend.entities;

import java.time.Instant;

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
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer order_item_id;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order_id;

    @Column(name = "line_no", nullable = false)
    private Integer line_no;

    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product_id;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "description", length = 100)
    private String description;

    @JsonIgnore
    @Column(name = "createdOn", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Instant createdOn;

    @JsonIgnore
    @Column(name = "updatedOn", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    private Instant updatedOn;
}