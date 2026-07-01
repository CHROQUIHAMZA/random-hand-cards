package com.asatech.random_hand_cards.controller;

import com.asatech.random_hand_cards.dto.CardDrawResult;
import com.asatech.random_hand_cards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/random-hand-cards")
    public String getRandomHandCards(Model model){
        CardDrawResult result = cardService.processRandomHand();

        model.addAttribute("deck",result.fullDeck());
        model.addAttribute("randomHand",result.randomHand());
        model.addAttribute("sortedHand",result.sortedHand());

        return "cards";

    }


}
