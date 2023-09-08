package ru.nsu.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nsu.bot.Commands.GetRandomVoiceMessageCommand;
import ru.nsu.bot.Commands.HelpCommand;
import ru.nsu.bot.Commands.StartCommand;
import ru.nsu.bot.Commands.StopCommand;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class AnonymizerChat extends TelegramLongPollingCommandBot {

    private static AnonymizerChat instance;
    private final static BotConfig botConfig = BotConfig.getInstance();
    private final TelegramBotsApi telegramBotsApi;
    private final AnonymousService anonymousService;

    {
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error("Error in initializing bot");
            throw new IllegalArgumentException("Telegram bot initializing error");
        }
    }

    private AnonymizerChat() {
        super();
        log.info("Initializing bot");
        anonymousService = new AnonymousService();
        registerCommands();
    }

    public static AnonymizerChat getInstance() {
        if (instance == null) {
            instance = new AnonymizerChat();
        }
        return instance;
    }

    private void registerCommands() {
        register(new StartCommand(anonymousService));
        register(new StopCommand(anonymousService));
        register(new GetRandomVoiceMessageCommand(anonymousService));
        register(new HelpCommand(this));
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUserName();
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        log.info("Processing non-command update");
        if (!update.hasMessage()) {
            throw new IllegalArgumentException("Update doesn't have a body");
        }

        if (!anonymousService.hasAnonymous(update.getMessage().getFrom())){
            SendMessage sendMessage = SendMessage.builder()
                    .text("Enter '/start' to join the chat!")
                    .chatId(update.getMessage().getChatId().toString())
                    .build();
            sendTextMessage(sendMessage, update.getMessage().getFrom());
            return;
        }

        if (!update.getMessage().hasVoice()) {
            SendMessage sendMessage = SendMessage.builder()
                    .text("Only voice messages!")
                    .chatId(update.getMessage().getChatId().toString())
                    .build();
            sendTextMessage(sendMessage, update.getMessage().getFrom());
            return;
        }

        User user = update.getMessage().getFrom();
        Voice voiceMessage = update.getMessage().getVoice();

        log.info("Adding voice message into storage");
        anonymousService.addVoiceMessage(user, voiceMessage);
        log.info("Setting into local storage");
        Path path = FileSystems.getDefault().getPath(String.format("./storage/%s.%s", UUID.randomUUID(), "oga"));
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(voiceMessage.getFileId());
            String filePath = execute(getFile).getFilePath();
            downloadFile(filePath, path.toFile());
        } catch (TelegramApiException  e){
            throw new IllegalArgumentException("aaa");
        }

        SendVoice broadcastVoiceMessage = new SendVoice();
        broadcastVoiceMessage.setVoice(new InputFile(voiceMessage.getFileId()));

        Stream<AnonymousUser> anonymousUserStream = anonymousService.getAnonymousesStream();
        anonymousUserStream.filter(a->!a.getUser().equals(user))
                .forEach(a -> {
                    broadcastVoiceMessage.setCaption(a.getDisplayedEmoji().getUnicode());
                    broadcastVoiceMessage.setChatId(a.getChat().getId().toString());
                    sendVoiceMessage(broadcastVoiceMessage, a.getUser(), user);
                });
    }


    private void sendVoiceMessage(SendVoice sendVoiceMessage, User receiver, User sender) {
        try {
            execute(sendVoiceMessage);
            log.info("Voice message was send to {} from {}", receiver.getId(), sender.getId());
        } catch (TelegramApiException e) {
            throw new IllegalArgumentException("Error in sending voice message from " +
                    sender.getId() + " to " + receiver.getId());
        }
    }

    private void sendTextMessage(SendMessage sendMessage, User receiver){
        try {
            execute(sendMessage);
            log.info("Text message was send to {}", receiver.getId());
        } catch (TelegramApiException e) {
            throw new IllegalArgumentException("Error in sending text message from " +
                     receiver.getId());
        }
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

}
