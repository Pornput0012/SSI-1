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
@Table(name = "buyers")
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyers_id")
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nickname", nullable = false)
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
    @Column(name = "fullname", nullable = false, length = 40)
    private String fullName;


    @ColumnDefault("'INACTIVE'")
    @Column(name = "status")
    private String status;

    @JsonIgnore
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdOn", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Instant createdOn;

    @JsonIgnore
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "updatedOn", insertable = false, updatable = false)
    private Instant updatedOn;

}