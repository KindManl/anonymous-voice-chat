package ru.nsu.bot;

import com.vdurmont.emoji.Emoji;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

@Slf4j
@Data
public class AnonymousUser {
    private final User user;
    private final Chat chat;
    private Emoji displayedEmoji;

    public AnonymousUser(User user, Chat chat) {
        if (user == null || chat == null){
            log.error("User or chat cannot be null!");
            throw new IllegalArgumentException("User or chat cannot be null!");
        }
        this.user = user;
        this.chat = chat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnonymousUser that = (AnonymousUser) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    public void setDisplayedEmoji(Emoji displayedEmoji) {
        this.displayedEmoji = displayedEmoji;
    }
}
