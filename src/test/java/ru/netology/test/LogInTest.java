package ru.netology.test;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.data.Data;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;

import java.sql.DriverManager;

import static com.codeborne.selenide.Selenide.open;

public class LogInTest {

    @AfterAll
    @SneakyThrows
    static void cleanDB(){
        Data.cleanDB();
    }

    @Test
    public void shouldSLogInWithCorrectData(){
        open("http://localhost:9999/");
        var loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(Data.getAuthInfo());
        var dashboardPage = verificationPage.validVerify(Data.getAuthInfo());
    }

    @Test
    public void shouldShowAnErrorMessageAfterThreeTimeIncorrectPasswordEnter(){
        open("http://localhost:9999/");
        var loginPage = new LoginPage();
        loginPage.inValidLogin(Data.getAuthInfo());
        loginPage.clearFields();
        loginPage.inValidLogin(Data.getAuthInfo());
        loginPage.clearFields();
        loginPage.inValidLogin(Data.getAuthInfo());
        String expected = "blocked";
        String actual = Data.getUserStatus(Data.getAuthInfo());
        Assertions.assertEquals(expected, actual);
    }
}
