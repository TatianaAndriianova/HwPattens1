package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@UtilityClass
public class DataGenerator {

    private static final Faker faker = new Faker(new Locale("ru"));

    private static final List<String> ADMIN_CENTERS = List.of(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург",
            "Казань", "Нижний Новгород", "Красноярск", "Челябинск",
            "Самара", "Уфа", "Ростов-на-Дону", "Краснодар",
            "Воронеж", "Пермь", "Волгоград");

    /**
     * Генерирует дату в формате dd.MM.yyyy, отступая daysToAdd дней от сегодня.
     */
    public static String generateDate(int daysToAdd) {
        return LocalDate.now()
                .plusDays(daysToAdd)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * Возвращает случайный административный центр из предопределённого списка.
     */
    public static String generateCity() {
        return ADMIN_CENTERS.get(new Random().nextInt(ADMIN_CENTERS.size()));
    }

    /**
     * Генерирует русскоязычное ФИО (Фамилия Имя).
     * Faker для ru-локали возвращает имя в формате "Имя Отчество Фамилия",
     * поэтому берём только первые два токена и меняем порядок на Фамилия Имя.
     */
    public static String generateName() {
        String lastName = faker.name().lastName();
        String firstName = faker.name().firstName();
        return (lastName + " " + firstName)
                .replace("ё", "е")
                .replace("Ё", "Е");
    }

    /**
     * Генерирует российский номер телефона в формате +7XXXXXXXXXX (11 цифр).
     */
    public static String generatePhone() {
        StringBuilder sb = new StringBuilder("+7");
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Собирает объект UserInfo со случайными валидными данными.
     */
    public static UserInfo generateValidUser() {
        return new UserInfo(
                generateCity(),
                generateName(),
                generatePhone()
        );
    }
}