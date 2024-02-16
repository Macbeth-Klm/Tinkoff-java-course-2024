package edu.java.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BotTest {
    private static final long FIRST_CHAT_ID = 5L;
    private static final long SECOND_CHAT_ID = 6L;
    private static final long THIRD_CHAT_ID = 7L;
    @Mock
    Update firstUpdate;
    @Mock
    Message firstMessage;
    @Mock
    Chat firstChat;
    @Mock
    Update secondUpdate;
    @Mock
    Message secondMessage;
    @Mock
    Chat secondChat;
    @Mock
    Update thirdUpdate;
    @Mock
    Message thirdMessage;
    @Mock
    Chat thirdChat;

    @Autowired
    Bot bot;

    private void setMocks() {
        Mockito.when(firstUpdate.message()).thenReturn(firstMessage);
        Mockito.when(firstMessage.chat()).thenReturn(firstChat);
        Mockito.when(firstUpdate.message().chat().id()).thenReturn(FIRST_CHAT_ID);
        Mockito.when(firstUpdate.message().text()).thenReturn("/start");

        Mockito.when(secondUpdate.message()).thenReturn(secondMessage);
        Mockito.when(secondMessage.chat()).thenReturn(secondChat);
        Mockito.when(secondUpdate.message().chat().id()).thenReturn(SECOND_CHAT_ID);
        Mockito.when(secondUpdate.message().text()).thenReturn("/start");

        Mockito.when(thirdUpdate.message()).thenReturn(thirdMessage);
        Mockito.when(thirdMessage.chat()).thenReturn(thirdChat);
        Mockito.when(thirdUpdate.message().chat().id()).thenReturn(THIRD_CHAT_ID);
        Mockito.when(thirdUpdate.message().text()).thenReturn("/start");
    }

    @Test
    public void shouldCorrectProcessAllUpdates() {
        List<Update> updates = List.of(firstUpdate, secondUpdate, thirdUpdate);
        setMocks();

        var result = bot.process(updates);

        Assertions.assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }
}
