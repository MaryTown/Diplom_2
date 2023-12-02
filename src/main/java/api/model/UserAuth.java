package api.model;

import api.user.User;
import api.user.UserCredentials;
import api.util.Spec;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class UserAuth {
    static final String USER_PATH = "/api/auth/";

    @Step("Создание пользователя")
    public static Response create(User user) {
        return given()
                .spec(Spec.getBaseSpec())
                .body(user)
                .when()
                .post(USER_PATH + "register");
    }

    @Step("Логин пользователя")
    public static Response login(UserCredentials credentials) {
        return given()
                .spec(Spec.getBaseSpec())
                .body(credentials)
                .when()
                .post(USER_PATH + "login");
    }

    @Step("Удаление пользователя")
    public static void delete(String accessToken) {
        given()
                .spec(Spec.getBaseSpec())
                .body(accessToken)
                .when()
                .delete(USER_PATH + "user");
    }

    @Step("Обновить данные о пользователе")
    public static Response changeUser(String refreshToken, User user) {
        return given()
                .spec(Spec.getBaseSpec())
                .header("Authorization", refreshToken)
                .body(user)
                .when()
                .patch(USER_PATH + "user");
    }

    /*
    @Step("Выйти из учётной записи пользователя")
    public static Response logout(String refreshToken) {
        return given()
                .spec(api.util.Spec.getBaseSpec())
                .body(refreshToken)
                .when()
                .post(USER_PATH + "token");
    }
     */
}
