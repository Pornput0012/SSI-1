package ssi.backend.entities;

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
    @Column(name = "sellers_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nickname", nullable = false)
    private String nickname;

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
    @Column(name = "fullname", nullable = false, length = 40)
    private String fullname;

    @Size(max = 255)
    @NotNull
    @Column(name = "mobileNumber", nullable = false)
    private String mobileNumber;

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
    @Column(name = "nationalIdNumber", nullable = false)
    private String nationalIdNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "nationalIdPhotoFront", nullable = false)
    private String nationalIdPhotoFront;

    @Size(max = 255)
    @NotNull
    @Column(name = "nationalIdPhotoBack", nullable = false)
    private String nationalIdPhotoBack;

    @NotNull
    @ColumnDefault("'INACTIVE'")
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    @Column(name = "createdOn", nullable = false)
    private Instant createdOn;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedOn", nullable = false)
    private Instant updatedOn;

}