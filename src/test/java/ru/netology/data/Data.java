package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.util.Locale;

public class Data {
    private Data() {}

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static AuthInfo getOtherAuthInfo(AuthInfo original) {
        return new AuthInfo("petya", "123qwerty");
    }

    public static String getRandomPassword() {
        Faker faker = new Faker(new Locale("en"));
        String password = faker.internet().password();
        return password;
    }

    @SneakyThrows
    public static String getUserId(AuthInfo authInfo){
        var userIdSQL = "SELECT id FROM users WHERE login=?;";
        var runner = new QueryRunner();
        var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_db", "user", "pass");
        String userId = runner.query(conn, userIdSQL, new ScalarHandler<>(), authInfo.getLogin());
        return userId;
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    @SneakyThrows
    public static String getVerificationCode(AuthInfo authInfo){
        var codeSQL = "SELECT code FROM auth_codes WHERE user_id=? AND created > NOW() -INTERVAL 1 MINUTE;";
        var runner = new QueryRunner();
        var conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app_db", "user", "pass");
        var currentCode = runner.query(conn, codeSQL, new ScalarHandler<String>(), getUserId(authInfo));
        return currentCode;
    }

    @SneakyThrows
    public static String getUserStatus(AuthInfo authInfo){
        var userIdSQL = "SELECT status FROM users WHERE login=?;";
        var runner = new QueryRunner();
        var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_db", "user", "pass");
        String userStatus = runner.query(conn, userIdSQL, new ScalarHandler<>(), authInfo.getLogin());
        return userStatus;
    }

    @SneakyThrows
    public static void cleanDB(){
        var runner = new QueryRunner();
        var conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app_db", "user", "pass"
        );
        runner.update(conn, "DELETE FROM auth_codes");
        runner.update(conn, "DELETE FROM cards");
        runner.update(conn, "DELETE FROM users");
        runner.update(conn, "DELETE FROM card_transactions");
    }
}
