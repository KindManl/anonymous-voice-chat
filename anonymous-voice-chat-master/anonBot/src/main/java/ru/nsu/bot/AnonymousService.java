package ru.nsu.bot;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class AnonymousService {
    private final Set<AnonymousUser> anonymousUserSet;
    private final Map<User, Voice> voiceMessages;
    private final List<Emoji> emojiCollection = EmojiManager.getAll().stream().toList();

    public AnonymousService() {
        this.anonymousUserSet = new HashSet<>();
        this.voiceMessages = new HashMap<>();
    }

    public boolean removeAnonymous(User user) {
        return anonymousUserSet.removeIf(a -> a.getUser().equals(user));
    }

    public boolean addAnonymous(AnonymousUser anonymous) {
        boolean isAdded = anonymousUserSet.add(anonymous);
        if (isAdded) {
            int randInd = ThreadLocalRandom.current().nextInt(0, 10000) % emojiCollection.size();
            anonymous.setDisplayedEmoji(emojiCollection.get(randInd));
        }
        return isAdded;
    }

    public boolean hasAnonymous(User user) {
        return anonymousUserSet.stream().anyMatch(a -> a.getUser().equals(user));
    }

    public Stream<AnonymousUser> getAnonymousesStream() {
        return anonymousUserSet.stream();
    }

    public void addVoiceMessage(User from, Voice voice) {
        voiceMessages.put(from, voice);
    }

    public Map.Entry<User, Voice> getRandomMessage(User from) {
        // TODO: here should get message from ANOTHER persons
        Optional<Map.Entry<User, Voice>>anotherUser = voiceMessages
                .entrySet()
                .stream()
                .findFirst();
        return anotherUser.orElse(null);
    }

    public Emoji getUserEmoji(User user){
        return anonymousUserSet.stream().filter(a-> a.getUser().getId().equals(user.getId())).findFirst().get().getDisplayedEmoji();
    }
}
