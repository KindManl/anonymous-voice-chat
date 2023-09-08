package ru.nsu.bot.Commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class HelpCommand extends AnonymizerCommand {
    private final ICommandRegistry iCommandRegistry;

    public HelpCommand(ICommandRegistry iCommandRegistry) {
        super("help", "what this can bot do");
        this.iCommandRegistry = iCommandRegistry;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Available commands:</b>")
                        .append(System.lineSeparator());
        iCommandRegistry
                .getRegisteredCommands()
                .forEach(cmd -> sb.append("/")
                        .append(cmd.getCommandIdentifier())
                        .append(" - ")
                        .append(cmd.getDescription())
                        .append(System.lineSeparator()));
        message.setText(sb.toString());
        super.processMessage(absSender, message, arguments);
    }
}
