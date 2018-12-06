package com.rosato.polimi.cardgame.exceptions;

public class NotYourTurnException extends Exception {
    public NotYourTurnException() {super();}

    public NotYourTurnException(String message) {
        super(message);
    }
}
