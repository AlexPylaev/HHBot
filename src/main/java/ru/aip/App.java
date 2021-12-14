package ru.aip;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class App {

    static class Job {
        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class HH {
        List<Job> items;

        public List<Job> getItems() {
            return items;
        }

        public void setItems(List<Job> items) {
            this.items = items;
        }

        HH() {}
    }

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
                                .uri(URI.create("https://api.hh.ru/vacancies?text=" +
                                        URLEncoder.encode(item.message().text(), StandardCharsets.UTF_8) +
                                        "&area=1"))
                                .build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    String body = response.body();
                    System.out.println(body);
                    HH hh = mapper.readValue(body, HH.class);
                    hh.items.subList(0,5).forEach(job -> {
                        bot.execute(new SendMessage(item.message().chat().id(),
                                "Вакансия: " + job.name +
                                     "\nСсылка: http://hh.ru/vacancy/" + job.id));
                        System.out.println(job.id + " " + job.name);
                    });
                    response.body();
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                }

            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
