package ssi.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
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
    @Column(name = "buyers_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "nickname", nullable = false)
    private String nickname;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "fullname", nullable = false, length = 40)
    private String fullname;

    @JsonIgnore
    @Column(name = "createdOn", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Instant createdOn;

    @JsonIgnore
    @Column(name = "updatedOn", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    private Instant updatedOn;
}
