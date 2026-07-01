package com.asatech.random_hand_cards.service;

import com.asatech.random_hand_cards.dto.CardDrawResult;
import com.asatech.random_hand_cards.model.Card;
import com.asatech.random_hand_cards.model.Rank;
import com.asatech.random_hand_cards.model.Suit;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CardService {

    public static final int HAND_SIZE = 10;

    /**
     * Orchestre toute la logique de distribution et de tri.
     */
    public CardDrawResult processRandomHand(){
        List<Card> deck = generateDeck();
        List<Card> shuffledDeck = shuffleDeck(deck);
        List<Card> randomHand = drawHand(shuffledDeck,HAND_SIZE);
        List<Card> sortedHand = sortHand(randomHand);
        return new CardDrawResult(
                shuffledDeck,
                randomHand,
                sortedHand
        );
    }


    /**
     * 1. Génère un paquet de 52 cartes (Produit cartésien des Couleurs et Valeurs).
     */
    public List<Card> generateDeck(){
        return Arrays.stream(Suit.values())
                .flatMap(suit -> Arrays.stream(Rank.values())
                    .map(rank -> new Card(suit,rank)))
                .toList();
    }

    /**
     * 2. Mélange le paquet de façon aléatoire.
     * Note : On clone la liste pour garder la méthode pure (ne modifie pas l'entrée).
     */

    public List<Card> shuffleDeck(List<Card> deck){
        if (deck == null) {
            throw new IllegalArgumentException("Le paquet à mélanger ne peut pas être null.");
        }
        List<Card> shuffled = new ArrayList<>(deck);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * 3. Tire les N premières cartes du paquet.
     */
    public List<Card> drawHand(List<Card> deck , int count){

        if (deck == null || deck.isEmpty()) {
            throw new IllegalArgumentException("Le paquet de cartes ne peut pas être vide ou null.");
        }

        if (count <= 0) {
            throw new IllegalArgumentException("Le nombre de cartes à tirer (" + count + ") doit être strictement positif.");
        }

        if(deck.size() < count){
            throw new IllegalArgumentException("Pas assez de cartes dans le paquet pour tirer une main de " + count );
        }
        return deck.stream()
                .limit(count)
                .toList();
    }

    /**
     * 4. Trie la main : d'abord par couleur, puis par valeur.
     */
    public List<Card> sortHand(List<Card> deck){
        if (deck == null || deck.isEmpty()) {
            throw new IllegalArgumentException("La main à trier ne peut pas être null ou vide.");
        }
        return deck.stream()
                .sorted(Comparator.comparing(Card::suit).thenComparing(Card::rank))
                .toList();
    }
}
