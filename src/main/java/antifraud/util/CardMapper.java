package antifraud.util;

import antifraud.model.card.StolenCardEntity;
import antifraud.model.card.dto.StolenCardDtoOut;

import java.util.ArrayList;
import java.util.List;

public class CardMapper {

    public static List<StolenCardDtoOut> cardListToDtoList(List<StolenCardEntity> list) {
        List<StolenCardDtoOut> cardDtoOuts = new ArrayList<>();
        for (StolenCardEntity cardEntity : list) {
            cardDtoOuts.add(new StolenCardDtoOut(cardEntity.getId(), cardEntity.getCardNumber()));
        }
        return cardDtoOuts;
    }
}
