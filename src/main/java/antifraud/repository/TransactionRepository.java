package antifraud.repository;

import antifraud.model.transaction.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByCardNumberAndDateOfTransactionBetween(
            String cardNumber,
            LocalDateTime from,
            LocalDateTime to
    );

    List<TransactionEntity> findAllByOrderByIdAsc();

    List<TransactionEntity> findByCardNumberOrderByIdAsc(String cardNumber);
}