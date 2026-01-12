package antifraud.controller;

import antifraud.model.card.dto.StolenCardDtoIn;
import antifraud.model.card.dto.StolenCardDtoOut;
import antifraud.model.ip.dto.SuspiciousIpDtoIn;
import antifraud.model.ip.dto.SuspiciousIpDtoOut;
import antifraud.model.transaction.dto.TransactionDtoIn;
import antifraud.model.transaction.dto.TransactionDtoOut;
import antifraud.model.transaction.dto.TransactionFeedbackDtoIn;
import antifraud.model.transaction.dto.TransactionFeedbackDtoOut;
import antifraud.service.TransactionService;
import antifraud.validation.card.ValidLuhn;
import antifraud.validation.ip.ValidIpV4;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // TRANSACTION FEEDBACK/HISTORY SECTION
    @PostMapping("/transaction")
    public ResponseEntity<TransactionDtoOut> processTransaction(@RequestBody @Valid TransactionDtoIn transactionDtoIn) {

        return ResponseEntity.ok(this.transactionService.processTransaction(transactionDtoIn));
    }

    @PutMapping("/transaction")
    public ResponseEntity<TransactionFeedbackDtoOut> updateTransactionFeedback(
            @RequestBody @Valid TransactionFeedbackDtoIn transactionDtoIn) {
        return ResponseEntity.ok(this.transactionService.updateTransactionFeedback(transactionDtoIn));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionFeedbackDtoOut>> getTransactionsHistory() {
        return ResponseEntity.ok(this.transactionService.getTransactionHistory());
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<TransactionFeedbackDtoOut>> getTransaction(@PathVariable @NotBlank @ValidLuhn String number) {
        return ResponseEntity.ok(this.transactionService.getTransactionHistoryByCardNumber(number));
    }

    // IP SECTION
    @PostMapping("/suspicious-ip")
    public ResponseEntity<SuspiciousIpDtoOut> saveSuspiciousIp(@RequestBody @Valid SuspiciousIpDtoIn suspiciousIpDtoIn) {
        return ResponseEntity.ok(this.transactionService.saveSuspiciousIp(suspiciousIpDtoIn));
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<Map<String, String>> deleteSuspiciousIp(@PathVariable @NotBlank @ValidIpV4 String ip) {
        return ResponseEntity.ok(this.transactionService.deleteSuspiciousIp(ip));
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspiciousIpDtoOut>> getSuspiciousIps() {
        return ResponseEntity.ok(this.transactionService.getIps());
    }

    // CARD SECTION
    @PostMapping("/stolencard")
    public ResponseEntity<StolenCardDtoOut> saveStolenCard(@RequestBody @Valid StolenCardDtoIn stolenCardDtoIn) {
        return ResponseEntity.ok(this.transactionService.saveStolenCard(stolenCardDtoIn));
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<Map<String, String>> deleteStolenCard(@PathVariable @NotBlank @ValidLuhn String number) {
        return ResponseEntity.ok(this.transactionService.deleteStolenCard(number));
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCardDtoOut>> getStolenCards() {
        return ResponseEntity.ok(this.transactionService.getStolenCards());
    }
}
