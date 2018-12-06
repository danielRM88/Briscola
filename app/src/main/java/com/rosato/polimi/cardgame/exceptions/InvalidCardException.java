package com.rosato.polimi.cardgame.exceptions;

public class InvalidCardException extends Exception {
    public InvalidCardException() {super();}

    public InvalidCardException(String message) {
        super(message);
    }
}
