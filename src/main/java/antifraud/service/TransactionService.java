package antifraud.service;

import antifraud.exception.card.StolenCardExistsException;
import antifraud.exception.card.StolenCardNotFoundException;
import antifraud.exception.ip.SuspiciousIpExistsException;
import antifraud.exception.ip.SuspiciousIpNotFoundException;
import antifraud.exception.transaction.TransactionFeedbackConflictException;
import antifraud.exception.transaction.TransactionFeedbackExistsException;
import antifraud.exception.transaction.TransactionNotFoundException;
import antifraud.model.card.StolenCardEntity;
import antifraud.model.card.dto.StolenCardDtoIn;
import antifraud.model.card.dto.StolenCardDtoOut;
import antifraud.model.ip.SuspiciousIpEntity;
import antifraud.model.ip.dto.SuspiciousIpDtoIn;
import antifraud.model.ip.dto.SuspiciousIpDtoOut;
import antifraud.model.transaction.TransactionEntity;
import antifraud.model.transaction.TransactionLimitsEntity;
import antifraud.model.transaction.WorldRegion;
import antifraud.model.transaction.dto.TransactionDtoIn;
import antifraud.model.transaction.TransactionState;
import antifraud.model.transaction.dto.TransactionDtoOut;
import antifraud.model.transaction.dto.TransactionFeedbackDtoIn;
import antifraud.model.transaction.dto.TransactionFeedbackDtoOut;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionLimitsRepository;
import antifraud.repository.TransactionRepository;
import antifraud.util.CardMapper;
import antifraud.util.IpMapper;
import antifraud.util.TransactionMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final long DEFAULT_ALLOWED_LIMIT = 200L;
    private static final long DEFAULT_MANUAL_LIMIT = 1500L;

    private final SuspiciousIpRepository suspiciousIpRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLimitsRepository transactionLimitsRepository;

    public TransactionService(SuspiciousIpRepository suspiciousIpRepository, StolenCardRepository stolenCardRepository, TransactionRepository transactionRepository, TransactionLimitsRepository transactionLimitsRepository) {
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
        this.transactionLimitsRepository = transactionLimitsRepository;
    }

    public TransactionDtoOut processTransaction(TransactionDtoIn transactionDtoIn) {
        long amount = transactionDtoIn.getMoneyAmount();
        String ip = transactionDtoIn.getIp();
        String cardNumber = transactionDtoIn.getCardNumber();
        WorldRegion region = transactionDtoIn.getWorldRegion();
        LocalDateTime date = transactionDtoIn.getDateOfTransaction();

        TransactionLimitsEntity limits = getOrCreateLimits(cardNumber);

        boolean isIpBlacklisted = suspiciousIpRepository.existsByIp(ip);
        boolean isCardStolen = stolenCardRepository.existsByCardNumber(cardNumber);

        LocalDateTime from = date.minusHours(1);
        List<TransactionEntity> lastHour = transactionRepository
                .findByCardNumberAndDateOfTransactionBetween(cardNumber, from, date);

        int distinctOtherIps = countDistinctOtherIps(lastHour, ip);
        int distinctOtherRegions = countDistinctOtherRegions(lastHour, region);

        boolean ipCorrelationProhibited = distinctOtherIps > 2;
        boolean ipCorrelationManual = distinctOtherIps == 2;

        boolean regionCorrelationProhibited = distinctOtherRegions > 2;
        boolean regionCorrelationManual = distinctOtherRegions == 2;

        boolean amountProhibited = amount > limits.getManualLimit();
        boolean amountManual = amount > limits.getAllowedLimit() && amount <= limits.getManualLimit();

        Set<String> reasons = new TreeSet<>();

        if (amountProhibited || ((!isCardStolen && !isIpBlacklisted) && amountManual)) reasons.add("amount");
        if (isCardStolen) reasons.add("card-number");
        if (isIpBlacklisted) reasons.add("ip");
        if (ipCorrelationProhibited || ipCorrelationManual) reasons.add("ip-correlation");
        if (regionCorrelationProhibited || regionCorrelationManual) reasons.add("region-correlation");

        TransactionState result;
        if (amountProhibited || isIpBlacklisted || isCardStolen || ipCorrelationProhibited || regionCorrelationProhibited) {
            result = TransactionState.PROHIBITED;
        } else if (amountManual || ipCorrelationManual || regionCorrelationManual) {
            result = TransactionState.MANUAL_PROCESSING;
        } else {
            result = TransactionState.ALLOWED;
        }

        TransactionDtoOut out = new TransactionDtoOut();
        out.setResult(result.toString());
        out.setInfo(reasons.isEmpty() ? "none" : String.join(", ", reasons));

        TransactionEntity saved = new TransactionEntity(
                transactionDtoIn.getMoneyAmount(),
                result,
                ip,
                cardNumber,
                region,
                date
        );
        transactionRepository.save(saved);

        return out;
    }

    private int countDistinctOtherIps(List<TransactionEntity> transactions, String currentIp) {
        Set<String> ips = transactions.stream()
                .map(TransactionEntity::getIp)
                .filter(v -> v != null && !v.equals(currentIp))
                .collect(Collectors.toSet());
        return ips.size();
    }

    private int countDistinctOtherRegions(List<TransactionEntity> transactions, WorldRegion currentRegion) {
        Set<WorldRegion> regions = transactions.stream()
                .map(TransactionEntity::getWorldRegion)
                .filter(v -> v != null && !v.equals(currentRegion))
                .collect(Collectors.toSet());
        return regions.size();
    }

    @Transactional
    public TransactionFeedbackDtoOut updateTransactionFeedback(TransactionFeedbackDtoIn dtoIn) {
        TransactionEntity transaction = transactionRepository.findById(dtoIn.getTransactionId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (transaction.getFeedback() != null) {
            throw new TransactionFeedbackExistsException("Feedback already exists for this transaction");
        }

        TransactionState feedback = dtoIn.getFeedback();
        TransactionState result = transaction.getTransactionState();

        if (feedback == result) {
            throw new TransactionFeedbackConflictException("Feedback conflicts with transaction result");
        }

        TransactionLimitsEntity limits = getOrCreateLimits(transaction.getCardNumber());

        applyFeedbackToLimits(limits, result, feedback, transaction.getMoneyAmount());

        transaction.setFeedback(feedback);
        transactionRepository.save(transaction);
        transactionLimitsRepository.save(limits);

        return new TransactionFeedbackDtoOut(
                transaction.getId(),
                transaction.getMoneyAmount(),
                transaction.getIp(),
                transaction.getCardNumber(),
                transaction.getWorldRegion().name(),
                transaction.getDateOfTransaction(),
                transaction.getTransactionState().name(),
                transaction.getFeedback().name()
        );
    }

    public List<TransactionFeedbackDtoOut> getTransactionHistory() {
        List<TransactionEntity> transactions = this.transactionRepository.findAllByOrderByIdAsc();
        return TransactionMapper.transactionListToDtoList(transactions);
    }

    public List<TransactionFeedbackDtoOut> getTransactionHistoryByCardNumber(String cardNumber) {
        List<TransactionEntity> transactions = transactionRepository.findByCardNumberOrderByIdAsc(cardNumber);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("Transaction not found");
        }
        return TransactionMapper.transactionListToDtoList(transactions);
    }

    private TransactionLimitsEntity getOrCreateLimits(String cardNumber) {
        return transactionLimitsRepository.findById(cardNumber)
                .orElseGet(() -> transactionLimitsRepository.save(
                        new TransactionLimitsEntity(cardNumber, DEFAULT_ALLOWED_LIMIT, DEFAULT_MANUAL_LIMIT)
                ));
    }

    private void applyFeedbackToLimits(TransactionLimitsEntity limits,
                                       TransactionState validity,
                                       TransactionState feedback,
                                       long amount) {

        if (validity == TransactionState.ALLOWED && feedback == TransactionState.MANUAL_PROCESSING) {
            limits.setAllowedLimit(decreaseLimit(limits.getAllowedLimit(), amount));
            return;
        }
        if (validity == TransactionState.ALLOWED && feedback == TransactionState.PROHIBITED) {
            limits.setAllowedLimit(decreaseLimit(limits.getAllowedLimit(), amount));
            limits.setManualLimit(decreaseLimit(limits.getManualLimit(), amount));
            return;
        }
        if (validity == TransactionState.MANUAL_PROCESSING && feedback == TransactionState.ALLOWED) {
            limits.setAllowedLimit(increaseLimit(limits.getAllowedLimit(), amount));
            return;
        }
        if (validity == TransactionState.MANUAL_PROCESSING && feedback == TransactionState.PROHIBITED) {
            limits.setManualLimit(decreaseLimit(limits.getManualLimit(), amount));
            return;
        }
        if (validity == TransactionState.PROHIBITED && feedback == TransactionState.ALLOWED) {
            limits.setAllowedLimit(increaseLimit(limits.getAllowedLimit(), amount));
            limits.setManualLimit(increaseLimit(limits.getManualLimit(), amount));
            return;
        }
        if (validity == TransactionState.PROHIBITED && feedback == TransactionState.MANUAL_PROCESSING) {
            limits.setManualLimit(increaseLimit(limits.getManualLimit(), amount));
            return;
        }

        // If something unexpected happens:
        throw new TransactionFeedbackConflictException("Unsupported feedback transition");
    }

    private long increaseLimit(long currentLimit, long amount) {
        // ceil(0.8*current + 0.2*amount)
        BigDecimal res = BigDecimal.valueOf(currentLimit).multiply(BigDecimal.valueOf(0.8))
                .add(BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(0.2)));
        return res.setScale(0, RoundingMode.CEILING).longValueExact();
    }

    private long decreaseLimit(long currentLimit, long amount) {
        // ceil(0.8*current - 0.2*amount)
        BigDecimal res = BigDecimal.valueOf(currentLimit).multiply(BigDecimal.valueOf(0.8))
                .subtract(BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(0.2)));
        return res.setScale(0, RoundingMode.CEILING).longValueExact();
    }

    public SuspiciousIpDtoOut saveSuspiciousIp(SuspiciousIpDtoIn suspiciousIpDtoIn) {
        if (this.suspiciousIpRepository.existsByIp(suspiciousIpDtoIn.getIp())) {
            throw new SuspiciousIpExistsException("IP address already exists");
        }
        SuspiciousIpEntity suspiciousIpEntity = new SuspiciousIpEntity(suspiciousIpDtoIn.getIp());
        this.suspiciousIpRepository.save(suspiciousIpEntity);

        return new SuspiciousIpDtoOut(suspiciousIpEntity.getId(), suspiciousIpEntity.getIp());
    }

    @Transactional
    public Map<String, String> deleteSuspiciousIp(String ip) {
        if (!this.suspiciousIpRepository.existsByIp(ip)) {
            throw new SuspiciousIpNotFoundException("IP address not found");
        }

        this.suspiciousIpRepository.deleteByIp(ip);

        return Map.of("status", "IP " + ip + " successfully removed!");
    }

    public List<SuspiciousIpDtoOut> getIps() {
        List<SuspiciousIpEntity> ips = this.suspiciousIpRepository.findAllByOrderByIdAsc();
        return IpMapper.ipListToDtoList(ips);
    }

    public StolenCardDtoOut saveStolenCard(StolenCardDtoIn stolenCardDtoIn) {
        if (this.stolenCardRepository.existsByCardNumber(stolenCardDtoIn.getNumber())) {
            throw new StolenCardExistsException("Card number already exists");
        }
        StolenCardEntity stolenCardEntity = new StolenCardEntity(stolenCardDtoIn.getNumber());
        this.stolenCardRepository.save(stolenCardEntity);

        return new StolenCardDtoOut(stolenCardEntity.getId(), stolenCardEntity.getCardNumber());
    }

    @Transactional
    public Map<String, String> deleteStolenCard(String number) {
        if (!this.stolenCardRepository.existsByCardNumber(number)) {
            throw new StolenCardNotFoundException("Card number not found");
        }

        this.stolenCardRepository.deleteByCardNumber(number);
        return Map.of("status", "Card " + number + " successfully removed!");
    }

    public List<StolenCardDtoOut> getStolenCards() {
        List<StolenCardEntity> stolenCards = this.stolenCardRepository.findAllByOrderByIdAsc();
        return CardMapper.cardListToDtoList(stolenCards);
    }
}
