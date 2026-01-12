package antifraud.model.ip;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suspicious_ips")
@Getter
@Setter
@NoArgsConstructor
public class SuspiciousIpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String ip;

    public SuspiciousIpEntity(String ip) {
        this.ip = ip;
    }
}
