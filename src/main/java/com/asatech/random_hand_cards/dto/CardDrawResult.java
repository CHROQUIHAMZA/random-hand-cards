package com.asatech.random_hand_cards.dto;

import com.asatech.random_hand_cards.model.Card;

import java.util.List;

public record CardDrawResult(
        List<Card> fullDeck,
        List<Card> randomHand,
        List<Card> sorteHand
) {
}
