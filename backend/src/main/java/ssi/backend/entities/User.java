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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @NotNull
    @Lob
    @Column(name = "userType", nullable = false)
    private String userType;

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

    @Column(name = "status")
    private String status = "INACTIVE";

    @Size(max = 255)
    @Column(name = "phoneNumber ")
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "bankAccount")
    private String bankAccount;

    @Size(max = 255)
    @Column(name = "bankName")
    private String bankName;

    @Size(max = 255)
    @Column(name = "nationalIdNumber")
    private String nationalIdNumber;

    @Size(max = 255)
    @Column(name = "nationalIdPhotoFront")
    private String nationalIdPhotoFront;

    @Size(max = 255)
    @Column(name = "nationalIdPhotoBack")
    private String nationalIdPhotoBack;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 255)
    @Column(name = "note")
    private String note;

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