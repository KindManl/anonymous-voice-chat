package ru.nsu.bot.Commands;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.nsu.bot.AnonymousService;
import ru.nsu.bot.AnonymousUser;

@Slf4j
public class StartCommand extends AnonymizerCommand {

    private final AnonymousService anonymousService;

    public StartCommand(AnonymousService anonymousService) {
        super("start", "connect to chat");
        this.anonymousService = anonymousService;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        if (anonymousService.addAnonymous(new AnonymousUser(message.getFrom(), message.getChat()))){
            message.setText("Hello, " + message.getFrom().getUserName() + ". " + "You've been added to chat. Wait for the voice messages!");
        } else {
            message.setText("You've been already registered!");
        }
        super.processMessage(absSender, message, arguments);
    }
}
