package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;
import ru.netology.data.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryTest {

    @BeforeEach
    public void setUp() {
        Configuration.baseUrl = "http://localhost:9999/";
        open("");
    }

    @Test
    public void shouldRescheduleMeetingSuccessfully() {
        UserInfo user = DataGenerator.generateValidUser();
        String firstDate = DataGenerator.generateDate(3);
        String secondDate = DataGenerator.generateDate(7);

        fillAndSubmitForm(user, firstDate);
        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + firstDate));

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(secondDate);
        $("button.button").click();

        $("[data-test-id=replan-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(5));
        $("[data-test-id=replan-notification] button")
                .click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + secondDate));
    }

    @Test
    public void shouldSubmitFormSuccessfully() {
        UserInfo user = DataGenerator.generateValidUser();
        String date = DataGenerator.generateDate(3);
        fillAndSubmitForm(user, date);

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + date));
    }

    @Test
    public void shouldSubmitFormWithFutureDateSuccessfully() {
        UserInfo user = DataGenerator.generateValidUser();
        String date = DataGenerator.generateDate(10);
        fillAndSubmitForm(user, date);

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + date));
    }

    @Test
    public void shouldSubmitFormWithNameContainingHyphen() {
        UserInfo user = new UserInfo(
                DataGenerator.generateCity(),
                "Иванов-Сидоров Алексей",
                DataGenerator.generatePhone()
        );
        String date = DataGenerator.generateDate(3);
        fillAndSubmitForm(user, date);

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    public void shouldShowErrorWhenCityIsEmpty() {
        UserInfo user = DataGenerator.generateValidUser();
        String date = DataGenerator.generateDate(3);

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldShowErrorWhenCityIsNotAdminCenter() {
        UserInfo user = new ru.netology.data.UserInfo(
                "Химки",
                DataGenerator.generateName(),
                DataGenerator.generatePhone()
        );
        fillAndSubmitForm(user, DataGenerator.generateDate(3));

        $("[data-test-id=city].input_invalid .input__sub")
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldShowErrorWhenDateIsTooEarly() {
        UserInfo user = DataGenerator.generateValidUser();
        fillAndSubmitForm(user, DataGenerator.generateDate(2));

        $("[data-test-id=date] .input_invalid .input__sub")
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldShowErrorWhenDateIsEmpty() {
        UserInfo user = DataGenerator.generateValidUser();

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=date] .input_invalid .input__sub")
                .shouldHave(Condition.text("Неверно введена дата"));
    }

    @Test
    public void shouldShowErrorWhenNameIsInLatin() {
        UserInfo user = new ru.netology.data.UserInfo(
                DataGenerator.generateCity(),
                "Ivan Ivanov",
                DataGenerator.generatePhone()
        );
        fillAndSubmitForm(user, DataGenerator.generateDate(3));

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(Condition.text(
                        "Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    public void shouldShowErrorWhenNameIsEmpty() {
        UserInfo user = DataGenerator.generateValidUser();
        String date = DataGenerator.generateDate(3);

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldShowErrorWhenPhoneHasNoPlus() {
        UserInfo user = new ru.netology.data.UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                "79001234567"
        );
        fillAndSubmitForm(user, DataGenerator.generateDate(3));

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text(
                        "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    public void shouldShowErrorWhenPhoneIsTooShort() {
        UserInfo user = new ru.netology.data.UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                "+7900123"
        );
        fillAndSubmitForm(user, DataGenerator.generateDate(3));

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text(
                        "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    public void shouldShowErrorWhenPhoneIsEmpty() {
        UserInfo user = DataGenerator.generateValidUser();
        String date = DataGenerator.generateDate(3);

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldShowErrorWhenCheckboxNotChecked() {
        UserInfo user = DataGenerator.generateValidUser();
        String date = DataGenerator.generateDate(3);

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("button.button").click();

        $("[data-test-id=agreement].input_invalid")
                .shouldBe(Condition.visible);
    }

    private void fillAndSubmitForm(UserInfo user, String date) {
        $("[data-test-id=city] input").setValue(user.getCity());

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);

        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();
    }
}