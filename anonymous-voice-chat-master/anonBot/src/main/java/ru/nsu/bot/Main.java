package ru.nsu.bot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            AnonymizerChat.getInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
