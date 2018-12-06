package com.rosato.polimi.cardgame.exceptions;

public class GameTerminatedException extends Exception {
    public GameTerminatedException() {super();}

    public GameTerminatedException(String message) {
        super(message);
    }
}
