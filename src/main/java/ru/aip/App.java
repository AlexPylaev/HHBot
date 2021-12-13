package ru.aip;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {
    public static void main(String[] args) {
        final String HHBOT_TOKEN = System.getenv("HHBOT_TOKEN");
        if (HHBOT_TOKEN == null) {
            System.out.println("Environment variable HHBOT_TOKEN needed!");
            return;
        }
        System.out.println("BOT TOKEN: "+HHBOT_TOKEN);

        TelegramBot bot = new TelegramBot(HHBOT_TOKEN);
        bot.setUpdatesListener(element -> {
            System.out.println(element);

            element.forEach(item -> {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://vk.com//"))
                                .build();
                //HttpResponse<String> response = clinet.send(request, )

                bot.execute((new SendMessage(item.message().chat().id(), item.message().text())));
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
