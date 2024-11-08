package br.upe.persistence;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    @Column(unique = true)
    private Long cpf;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    @Column(length = 60)
    private String password;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public @NotNull Long getCpf() {
        return this.cpf;
    }

    public void setCpf(@NotNull Long cpf) {
        this.cpf = cpf;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

}
