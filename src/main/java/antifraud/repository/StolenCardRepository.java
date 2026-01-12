package antifraud.repository;

import antifraud.model.card.StolenCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCardEntity, Long> {
    boolean existsByCardNumber(String number);
    void deleteByCardNumber(String number);
    List<StolenCardEntity> findAllByOrderByIdAsc();
}
