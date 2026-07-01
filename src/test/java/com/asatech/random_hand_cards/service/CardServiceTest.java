package com.asatech.random_hand_cards.service;

import com.asatech.random_hand_cards.dto.CardDrawResult;
import com.asatech.random_hand_cards.model.Card;
import com.asatech.random_hand_cards.model.Rank;
import com.asatech.random_hand_cards.model.Suit;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class CardServiceTest {

    private CardService cardService;

    @BeforeEach
    void setUp(){
        // Initialisation fraîche avant chaque test
        cardService = new CardService();
    }


    /* ====================================================================================
     * TESTS DE GÉNÉRATION DU PAQUET (generateDeck)
     * ==================================================================================== */

    /**
     * Règle métier : Un paquet standard doit contenir exactement 52 cartes uniques.
     */

    @Test
    void generateDeck_ShouldReturn52UniqueCard(){
        //Act
        List<Card>  deck = cardService.generateDeck();
        // Assert
        assertThat(deck).hasSize(52);
        // Vérifier qu'il n'y a pas de doublons
        assertThat(deck).doesNotHaveDuplicates();
    }

    /* ====================================================================================
     * TESTS DE MÉLANGE (shuffleDeck)
     * ==================================================================================== */

    /**
     * Règle de sécurité : Impossible de mélanger un paquet inexistant (null).
     */

    @Test
    void shuffle_ShouldThrowException_WhenDeckIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> cardService.shuffleDeck(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ne peut pas être null.");
    }

    /**
     * Règle métier : Le mélange doit réorganiser les cartes sans en ajouter,
     * en supprimer ou les modifier.
     */
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



    /* ====================================================================================
     * TESTS DE DISTRIBUTION (drawHand)
     * ==================================================================================== */

    /**
     * Règle de sécurité : Impossible de piocher dans un paquet non instancié.
     */
    @Test
    void drawHand_ShouldThrowException_WhenDeckIsNull() {

        // Act & Assert
        assertThatThrownBy(() -> cardService.drawHand(null, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ne peut pas être vide ou null");
    }


    /**
     * Règle de sécurité : Impossible de piocher dans un paquet vide (0 carte).
     */
    @Test
    void drawHand_ShouldThrowException_WhenDeckIsEmpty() {
        // Arrange : création d'une liste vide
        List<Card> emptyDeck = List.of();

        // Act & Assert
        assertThatThrownBy(() -> cardService.drawHand(emptyDeck, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ne peut pas être vide ou null");
    }

    /**
     * Règle métier : Le nombre de cartes à piocher doit être cohérent (pas de valeur nulle ou négative).
     */
    @Test
    void drawHand_ShouldThrowException_WhenCountIsZeroOrNegative() {
        // Arrange
        List<Card> deck = List.of(
                new Card(Suit.COEUR, Rank.AS),
                new Card(Suit.PIQUE, Rank.ROI)
        );

        // Act & Assert pour 0
        assertThatThrownBy(() -> cardService.drawHand(deck, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("doit être strictement positif");

        // Act & Assert pour un nombre négatif
        assertThatThrownBy(() -> cardService.drawHand(deck, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("doit être strictement positif");
    }

    /**
     * Règle métier : Impossible de piocher plus de cartes que le paquet n'en contient.
     */
    @Test
    void drawHand_ShouldThrowException_WhenDeckIsTooSmall() {
        List<Card> smallDeck = List.of(new Card(Suit.COEUR, Rank.AS));

        assertThatThrownBy(() -> cardService.drawHand(smallDeck, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pas assez de cartes");
    }




    /**
     * Règle métier : Cas nominal, le nombre exact de cartes demandées doit être retourné.
     */
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

    /* ====================================================================================
     * TESTS DE TRI (sortHand)
     * ==================================================================================== */

    /**
     * Règle de sécurité : Impossible de trier une main non instanciée.
     */

    @Test
    void sortHand_ShouldThrowException_WhenCardListIsNull(){

        // Act & Assert
        // On passe explicitement 'null' à la place de la liste
        assertThatThrownBy(() -> cardService.sortHand(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ne peut pas être null ou vide");
    }
    /**
     * Règle métier : Le tri s'effectue obligatoirement dans l'ordre défini par les Enums
     * (d'abord la Couleur, puis la Valeur au sein de la même couleur).
     */
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

    /* ====================================================================================
     * TEST D'ORCHESTRATION (processRandomHand)
     * ==================================================================================== */

    /**
     * Règle métier globale : Le flux complet doit s'exécuter sans erreur et renvoyer
     * un DTO contenant le paquet complet, la main aléatoire et la main triée.
     */
    @Test
    void processRandomHand_ShouldReturnCompleteResult() {
        // Exécution de la méthode principale
        CardDrawResult result = cardService.processRandomHand();

        // 1. Vérification de la non-nullité
        assertNotNull(result, "Le résultat ne doit pas être null");

        // 2. Vérification des tailles des listes
        assertEquals(52, result.fullDeck().size(), "Le paquet complet doit contenir 52 cartes");
        assertEquals(10, result.randomHand().size(), "La main aléatoire doit contenir 10 cartes");
        assertEquals(10, result.sortedHand().size(), "La main triée doit contenir 10 cartes");

        // 3. Vérification de la cohérence des données (les mêmes cartes sont présentes dans les deux mains)
        assertTrue(result.sortedHand().containsAll(result.randomHand()),
                "La main triée doit contenir exactement les mêmes cartes que la main aléatoire");

        // 4. Vérification que la main triée est bien dans un ordre différent (ou du moins triée selon les règles)
        // Note : Il est statistiquement quasi-impossible qu'une main de 10 cartes tirées au hasard soit déjà parfaitement triée.
        assertNotEquals(result.randomHand(), result.sortedHand(),
                "La main triée ne doit pas être strictement identique à la main brute aléatoire");
    }



}
