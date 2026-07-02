package com.asatech.random_hand_cards.controller;

import com.asatech.random_hand_cards.dto.CardDrawResult;
import com.asatech.random_hand_cards.model.Card;
import com.asatech.random_hand_cards.model.Rank;
import com.asatech.random_hand_cards.model.Suit;
import com.asatech.random_hand_cards.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;


    /**
     * Cas nominal : Vérifie que le contrôleur répond correctement à une requête valide.
     * Il doit appeler le service, récupérer le résultat (CardDrawResult),
     * et transmettre les attributs nécessaires à la vue 'cards' avec un statut HTTP 200 (OK).
     */
    @Test
    void getCards_ShouldReturnCardsViewWithModelAttributes()throws Exception{

        // Arrange : On crée de fausses données que le Service simulé va retourner
        List<Card> mockDeck = List.of(new Card(Suit.COEUR, Rank.AS));
        List<Card> mockRandomHand = List.of(new Card(Suit.PIQUE, Rank.ROI));
        List<Card> mockSortedHand = List.of(new Card(Suit.PIQUE, Rank.ROI));

        CardDrawResult mockResult = new CardDrawResult(mockDeck, mockRandomHand, mockSortedHand);

        when(cardService.processRandomHand()).thenReturn(mockResult);

        mockMvc.perform(get("/random-hand-cards"))
                .andExpect(status().isOk()) // Vérifie que le code HTTP est 200 (Succès)
                .andExpect(view().name("cards")) // Vérifie que le contrôleur renvoie bien vers "cards.html"
                .andExpect(model().attributeExists("deck")) // Vérifie que l'attribut 'deck' est passé à Thymeleaf
                .andExpect(model().attributeExists("randomHand"))
                .andExpect(model().attributeExists("sortedHand"));


    }

    /**
     * Cas d'erreur métier (Programmation défensive) :
     * Bien que le flux web actuel n'accepte aucun paramètre utilisateur direct,
     * ce test valide la robustesse du GlobalExceptionHandler.
     * Il garantit que si un développeur réutilise les méthodes du Service avec des
     * paramètres invalides (déclenchant une IllegalArgumentException), le système
     * interceptera proprement l'erreur pour renvoyer la vue 'error' avec un statut HTTP 400.
     */
    @Test
    void getCards_ShouldReturnErrorView_WhenServiceThrowsException() throws Exception {

        // Arrange
        // Peu importe laquelle (taille négative, paquet vide...), le résultat web sera le même.
        when(cardService.processRandomHand())
                .thenThrow(new IllegalArgumentException("Message d'erreur simulé pour le test"));

        // Act & Assert : On simule l'appel HTTP
        mockMvc.perform(get("/random-hand-cards"))

                // 1. On vérifie le code HTTP (adapte selon ton GlobalExceptionHandler : isBadRequest() ou isInternalServerError())
                .andExpect(status().isBadRequest())

                // 2. On vérifie que le contrôleur a bien appelé "error.html"
                .andExpect(view().name("error"))

                // 3. On vérifie que l'objet ApiError est bien transmis à la page
                .andExpect(model().attributeExists("apiError"));
    }

    /**
     * Cas d'erreur technique : Vérifie la robustesse du système face à un plantage imprévu.
     * Si le service rencontre un bug critique (ex: NullPointerException),
     * le GlobalExceptionHandler doit sécuriser l'application et renvoyer la vue 'error'
     * avec un statut HTTP 500 (Internal Server Error) sans faire crasher le serveur.
     */
    @Test
    void getCards_ShouldReturnErrorView_WhenServiceThrowsGenericException() throws Exception {

        // Arrange : On simule un crash technique inattendu dans le service (ex: NullPointerException)
        when(cardService.processRandomHand())
                .thenThrow(new NullPointerException("Bug système simulé pour le test"));

        // Act & Assert : On appelle la route
        mockMvc.perform(get("/random-hand-cards"))

                // 1. On vérifie que le code HTTP est bien 500 (Internal Server Error)
                .andExpect(status().isInternalServerError())

                // 2. On vérifie que c'est bien la page d'erreur qui s'affiche
                .andExpect(view().name("error"))

                // 3. On vérifie que l'objet ApiError est bien transmis à la page
                .andExpect(model().attributeExists("apiError"));
    }


    /**
     * Test d'intégration : Vérifie la gestion des erreurs de navigation.
     * Simule une requête vers une URL inexistante pour s'assurer que l'application
     * répond élégamment avec une page d'erreur standardisée (404)
     * plutôt que de laisser le serveur exposer une page d'erreur brute.
     */
    @Test
    void unknownRoute_ShouldReturnErrorView_With404() throws Exception {
        // 1. Simuler une requête client vers une route inconnue
        mockMvc.perform(get("/route-qui-nexiste-pas"))
                // 2. Vérifier que le serveur renvoie bien le code HTTP 404
                .andExpect(status().isNotFound())
                // 3. Vérifier que la vue retournée est bien notre page "error"
                .andExpect(view().name("error"))
                // 4. Vérifier que notre objet ApiError est bien passé à la vue
                .andExpect(model().attributeExists("apiError"));
    }
}
