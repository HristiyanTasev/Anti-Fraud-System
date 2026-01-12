package antifraud.repository;

import antifraud.model.ip.SuspiciousIpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspiciousIpRepository extends JpaRepository<SuspiciousIpEntity, Long> {
    boolean existsByIp(String ip);
    void deleteByIp(String ip);
    List<SuspiciousIpEntity> findAllByOrderByIdAsc();
}
