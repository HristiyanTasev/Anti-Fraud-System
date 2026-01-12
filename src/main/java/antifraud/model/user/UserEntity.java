package antifraud.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private UserRoleEntity role;
    @Column(nullable = false)
    private boolean isEnabled = false;


    public UserEntity(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public UserEntity(String name, String username, String password, UserRoleEntity role, boolean isEnabled) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isEnabled = isEnabled;
    }
}
