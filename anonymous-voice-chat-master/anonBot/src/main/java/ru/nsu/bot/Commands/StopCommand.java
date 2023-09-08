package ru.nsu.bot.Commands;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.nsu.bot.AnonymousService;

@Slf4j
public class StopCommand extends AnonymizerCommand {
    private final AnonymousService anonymousService;

    public StopCommand(AnonymousService anonymousService) {
        super("stop", "disconnect from chat");
        this.anonymousService = anonymousService;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        if (anonymousService.removeAnonymous(message.getFrom())){
            log.info("User {} removed from chat", message.getFrom().getId());
            message.setText("You've been successfully removed from chat! Bye!");
        } else {
            log.info("User {} has been already removed from chat", message.getFrom().getId());
            message.setText("You weren't in bot users. Bye!");
        }
        super.processMessage(absSender, message, arguments);
    }
}
