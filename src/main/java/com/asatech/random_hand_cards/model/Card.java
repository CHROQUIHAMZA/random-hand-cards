package com.asatech.random_hand_cards.model;

public record Card(Suit suit, Rank rank) {

    /**
     * Génère le chemin de l'image associée à la carte.
     * Cette méthode sera utilisée par la vue Thymeleaf pour afficher les ressources fournies.
     */

    public String getImagePath(){

        // Exemple de format généré : "/img/cards/AS_COEUR.png"
        return "/img/cards/" + rank.name() + "_" + suit().name() + ".png";
    }
}
