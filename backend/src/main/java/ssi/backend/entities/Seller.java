package ssi.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sellers")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sellers_id")
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nickName", nullable = false)
    private String nickName;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 40)
    @NotNull
    @Column(name = "fullName", nullable = false, length = 40)
    private String fullName;

    @Size(max = 255)
    @NotNull
    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "bankAccountNumber", nullable = false)
    private String bankAccountNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "bankName", nullable = false)
    private String bankName;

    @Size(max = 255)
    @NotNull
    @Column(name = "idCardNumber", nullable = false)
    private String idCardNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "idCardImageFront", nullable = false)
    private String idCardImageFront;

    @Size(max = 255)
    @NotNull
    @Column(name = "idCardImageBack", nullable = false)
    private String idCardImageBack;


    @Column(name = "status")
    private String status = "INACTIVE";

    @JsonIgnore
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    @Column(name = "createdOn", insertable = false, updatable = false)
    private Instant createdOn;

    @JsonIgnore
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "updatedOn", insertable = false, updatable = false)
    private Instant updatedOn;

}