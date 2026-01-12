package antifraud.util;

import antifraud.model.transaction.TransactionEntity;
import antifraud.model.transaction.dto.TransactionFeedbackDtoOut;

import java.util.ArrayList;
import java.util.List;

public class TransactionMapper {

    public static List<TransactionFeedbackDtoOut> transactionListToDtoList(List<TransactionEntity> list) {
        List<TransactionFeedbackDtoOut> transactionFeedbackDtoOuts = new ArrayList<>();

        for (TransactionEntity transactionEntity : list) {
            transactionFeedbackDtoOuts.add(new TransactionFeedbackDtoOut(transactionEntity.getId(),
                    transactionEntity.getMoneyAmount(),
                    transactionEntity.getIp(), transactionEntity.getCardNumber(),
                    transactionEntity.getWorldRegion().name(),
                    transactionEntity.getDateOfTransaction(), transactionEntity.getTransactionState().name(),
                    transactionEntity.getFeedback() != null ? transactionEntity.getFeedback().name() : ""));
        }

        return transactionFeedbackDtoOuts;
    }
}
