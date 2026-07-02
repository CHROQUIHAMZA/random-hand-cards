package com.asatech.random_hand_cards.exception;

import com.asatech.random_hand_cards.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erreur 400 (Bad Request) : Intercepte les violations de règles métier.
     * Déclenchée par notre programmation défensive (ex: taille de paquet invalide).
     * On transmet le message d'erreur d'origine car il contient l'explication
     * métier précise pour aider à corriger sa requête.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex , HttpServletRequest request, Model model){
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        model.addAttribute("apiError",apiError);
        return "error";
    }

    /**
     * Erreur 404 (Not Found) : Gère les cas où l'utilisateur tape une URL qui n'existe pas.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoResourceFoundException ex, HttpServletRequest request, Model model) {

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "La page que vous recherchez n'existe pas.",
                request.getRequestURI()
        );

        model.addAttribute("apiError", apiError);
        return "error"; // On réutilise ta belle page d'erreur existante !
    }


    /**
     * Erreur 500 (Internal Server Error) : Filet de sécurité (Catch-all).
     * Intercepte tous les bugs techniques inattendus (ex: NullPointerException).
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex , HttpServletRequest request,Model model){
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Une erreur interne inattendue est survenue.",
                request.getRequestURI()
        );
        model.addAttribute("apiError",apiError);
        return "error";
    }
}
