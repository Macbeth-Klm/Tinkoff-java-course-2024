package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;

public interface Command {
    String name();

    String description();

    SendMessage handle(long chatId, String text);
}
