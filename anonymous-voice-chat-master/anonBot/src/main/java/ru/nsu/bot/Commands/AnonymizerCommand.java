package ru.nsu.bot.Commands;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class AnonymizerCommand implements IBotCommand {
    private final String commandIdentifier;
    private final String description;

    public AnonymizerCommand(String commandIdentifier, String description) {
        this.commandIdentifier = commandIdentifier;
        this.description = description;
    }

    @Override
    public String getCommandIdentifier() {
        return commandIdentifier;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        log.info(String.format("Message to user: '%s' ", message.getText()));
        try {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text(message.getText())
                    .build();
            sendMessage.enableHtml(true);
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new IllegalArgumentException("Command message processing error: " + e.getMessage(), e);
        }
    }
}
