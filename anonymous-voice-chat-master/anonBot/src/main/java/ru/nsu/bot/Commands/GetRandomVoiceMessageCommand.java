package ru.nsu.bot.Commands;

import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nsu.bot.AnonymousService;

import java.util.Map;

public class GetRandomVoiceMessageCommand extends AnonymizerCommand {
    private final AnonymousService anonymousService;

    public GetRandomVoiceMessageCommand(AnonymousService anonymousService) {
        super("listen", "catch the bottle from the strangers...");
        this.anonymousService = anonymousService;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Map.Entry<User, Voice> randomVoice = anonymousService.getRandomMessage(message.getFrom());
        if (randomVoice == null) {
            message.setText("Oops. Here's no strangers yet.");
            super.processMessage(absSender, message, arguments);
        } else {
            try {
                SendVoice sendVoice = new SendVoice(message.getChatId().toString(), new InputFile(randomVoice.getValue().getFileId()));
                sendVoice.setCaption(anonymousService.getUserEmoji(randomVoice.getKey()).getUnicode());
                absSender.execute(sendVoice);
            } catch (TelegramApiException e) {
                throw new IllegalArgumentException("Can't send voice message to user: " + message.getFrom());
            }
        }
    }
}
