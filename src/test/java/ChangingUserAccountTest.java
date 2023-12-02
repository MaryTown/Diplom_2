import api.model.UserAuth;
import api.user.GettingParams;
import api.user.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import net.datafaker.Faker;

import static org.hamcrest.CoreMatchers.equalTo;
public class ChangingUserAccountTest extends BaseTest {
    private UserAuth userAuth;
    private User user;
    private String accessToken;
    private String newEmail;
    private String newName;
    static Faker faker = new Faker();

    @Before
    public void setUp() {
        userAuth = new UserAuth();
        user = GettingParams.getRandomUser();
        accessToken = userAuth.create(user).path("accessToken");

        newEmail = faker.name().username() + "@testtest.ru";
        newName = faker.name().firstName();

        user.setEmail(newEmail);
        user.setName(newName);
    }

    @Test
    @DisplayName("Обновить имя пользователя c авторизацией: 200")
    public void changeUserAccountWithLogin() {
        Response responseChange = userAuth.changeUser(accessToken, user);
        responseChange.then().assertThat().statusCode(200);
        responseChange.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(newEmail))
                .and().assertThat().body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Обновить данные о пользователе без авторизации: 401 Unauthorized")
    public void changeUserAccountWithoutLogin() {
        Response responseChange = userAuth.changeUser("", user);
        responseChange.then().assertThat().statusCode(401);
        responseChange.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("You should be authorised"));
    }
}