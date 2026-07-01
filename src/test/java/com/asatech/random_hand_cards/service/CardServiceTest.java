package com.asatech.random_hand_cards.service;

import com.asatech.random_hand_cards.model.Card;
import com.asatech.random_hand_cards.model.Rank;
import com.asatech.random_hand_cards.model.Suit;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CardServiceTest {

    private CardService cardService;

    @BeforeEach
    void setUp(){
        // Initialisation fraîche avant chaque test
        cardService = new CardService();
    }

    @Test
    void generateDeck_ShouldReturn52UniqueCard(){
        //Act
        List<Card>  deck = cardService.generateDeck();
        // Assert
        assertThat(deck).hasSize(52);
        // Vérifier qu'il n'y a pas de doublons
        assertThat(deck).doesNotHaveDuplicates();
    }

    @Test
    void shuffleDeck_ShouldChangerOrderButKeepSameCards(){
        //Arrange
        List<Card> originalDeck = cardService.generateDeck();

        //Act
        List<Card> shuffledDeck = cardService.shuffleDeck(originalDeck);

        // Assert
        assertThat(shuffledDeck).hasSize(52);
        assertThat(shuffledDeck).containsExactlyInAnyOrderElementsOf(originalDeck);
        assertThat(shuffledDeck).isNotEqualTo(originalDeck);

    }

    @Test
    void drawHand_ShouldReturnRequestedNumberOfCards(){

        // Arrange
        List<Card> deck = cardService.generateDeck();
        int handSize = 10;

        // Act
        List<Card> hand = cardService.drawHand(deck,handSize);

        // Assert
        assertThat(hand).hasSize(handSize);
    }

    @Test
    void drawHand_ShouldThrowException_WhenNotEnoughCards(){

        // Arrange
        List<Card> smallDeck = List.of(
                new Card(Suit.COEUR, Rank.AS),
                new Card(Suit.PIQUE, Rank.ROI)
        );

        // Act & Assert
        // On s'attend à ce qu'une exception soit levée si on demande 10 cartes sur un paquet de 2
        assertThatThrownBy(() -> cardService.drawHand(smallDeck, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pas assez de cartes");
    }

    @Test
    void sortHand_ShouldSortBySuitThenRank(){

        // Arrange : On crée une main en désordre
        List<Card> unsortedHand = List.of(
                new Card(Suit.PIQUE, Rank.DIX),
                new Card(Suit.COEUR, Rank.AS),
                new Card(Suit.CARREAU, Rank.ROI),
                new Card(Suit.COEUR, Rank.ROI),
                new Card(Suit.PIQUE, Rank.DEUX)
        );

        // Act
        List<Card> sortedHand = cardService.sortHand(unsortedHand);

        // Assert
        assertThat(sortedHand).extracting("suit","rank").containsExactly(
                Tuple.tuple(Suit.CARREAU,Rank.ROI),
                Tuple.tuple(Suit.COEUR,Rank.AS),
                Tuple.tuple(Suit.COEUR,Rank.ROI),
                Tuple.tuple(Suit.PIQUE,Rank.DEUX),
                Tuple.tuple(Suit.PIQUE,Rank.DIX)
        );

    }

}
