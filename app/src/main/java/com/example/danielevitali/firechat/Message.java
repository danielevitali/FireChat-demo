package com.example.danielevitali.firechat;

/**
 *
 */
public class Message {
    private String name;
    private String text;

    public Message() {
    }

    public Message(String name, String message) {
        this.name = name;
        this.text = message;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
