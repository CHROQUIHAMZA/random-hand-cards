package com.asatech.random_hand_cards.service;

import com.asatech.random_hand_cards.model.Card;
import com.asatech.random_hand_cards.model.Rank;
import com.asatech.random_hand_cards.model.Suit;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardImageTest {


    /**
     * Règle technique / Intégrité des ressources :
     * Vérifie que chaque carte générée par le code possède bien son fichier image physique correspondant.
     * Cela garantit qu'aucune erreur 404 (Image introuvable) ne se produira sur l'interface utilisateur.
     */
    @Test
    void allCardImagesShouldExistInStaticFolder(){
        for(Suit suit : Suit.values()){
            for(Rank rank : Rank.values()){

                // Arrange : Instanciation de la carte courante
                Card card = new Card(suit,rank);

                // Act : Construction du chemin attendu dans le ClassPath
                String resourcePath = "static" + card.getImagePath();
                ClassPathResource resource = new ClassPathResource(resourcePath);

                // Assert : Vérifie que le fichier existe réellement sur le disque/dans le build.
                assertTrue(resource.exists(),
                        "L'image pour la carte " + rank + " de " + suit + " est introuvable au chemin : " + resourcePath);

            }
        }
    }
}
