import api.model.UserAuth;
import api.user.GettingParams;
import api.user.User;
import api.user.UserCredentials;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matchers;

public class CreatingUserTest extends BaseTest {
    private UserAuth userAuth;
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        userAuth = new UserAuth();
        user = GettingParams.getRandomUser();
    }

    @Test
    @DisplayName("создать уникального пользователя: 200, и залогиниться под ним: 200")
    public void positiveCreateAndLoginUserTest() {
        Response response = userAuth.create(user);
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("accessToken", Matchers.notNullValue())
                .and().assertThat().body("refreshToken", Matchers.notNullValue());
        accessToken = response.path("accessToken");

        Response responseLogin = userAuth.login(UserCredentials.from(user));
        responseLogin.then().assertThat().statusCode(200);
        responseLogin.then().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("создать пользователя, который уже зарегистрирован: 403 Forbidden")
    public void createTheSameUsersTest() {
        Response response = userAuth.create(user);
        accessToken = response.path("accessToken");
        Response responseDublicate = userAuth.create(user);
        responseDublicate.then().assertThat().statusCode(403);
        responseDublicate.then().assertThat().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить email: 403 Forbidden")
    public void createUserWithNullEmailTest() {
        user.setEmail(null);
        Response response = userAuth.create(user);
        response.then().assertThat().statusCode(403);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить password: 403 Forbidden")
    public void createUserWithNullPasswordTest() {
        user.setPassword(null);
        Response response = userAuth.create(user);
        response.then().assertThat().statusCode(403);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить name: 403 Forbidden")
    public void createUserWithNullNameTest() {
        user.setName(null);
        Response response = userAuth.create(user);
        response.then().assertThat().statusCode(403);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

}
