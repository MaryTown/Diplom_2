import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import net.datafaker.Faker;

import static org.hamcrest.CoreMatchers.equalTo;
public class ChangingUserAccountTest {
    private UserAuth userAuth;
    private String accessToken;
    static Faker faker = new Faker();

    @Before
    public void setUp() {
        userAuth = new UserAuth();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserAuth.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Обновить имя пользователя c авторизацией: 200")
    public void changeUserAccountWithLogin() {
        User user = GettingParams.getRandomUser();
        Response response = userAuth.create(user);
        accessToken = response.path("accessToken");

        String newEmail = faker.name().username() + "@testtest.ru";
        String newName = faker.name().firstName();

        user.setEmail(newEmail);
        user.setName(newName);

        Response responseChange = userAuth.changeUser(accessToken, user);
        responseChange.then().assertThat().statusCode(200);
        responseChange.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(newEmail))
                .and().assertThat().body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Обновить данные о пользователе без авторизации: 401 Unauthorized")
    public void changeUserAccountWithoutLogin() {
        User user = GettingParams.getRandomUser();
        Response response = userAuth.create(user);
        accessToken = response.path("accessToken");

        String newEmail = faker.name().username() + "@testtest.ru";
        String newName = faker.name().firstName();

        user.setEmail(newEmail);
        user.setName(newName);

        Response responseChange = userAuth.changeUser("", user);
        responseChange.then().assertThat().statusCode(401);
        responseChange.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("You should be authorised"));
    }
}