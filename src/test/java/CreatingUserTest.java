import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matchers;

public class CreatingUserTest {
    private UserAuth userAuth;
    private String accessToken;

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
    @DisplayName("создать уникального пользователя: 200, и залогиниться под ним: 200")
    public void positiveCreateAndLoginUserTest() {
        User user = GettingParams.getRandomUser();
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
        User userDublicate = new User(GettingParams.getRandomUser().getEmail(),
                GettingParams.getRandomUser().getPassword(),
                GettingParams.getRandomUser().getName());
        userAuth.create(userDublicate);
        Response response = userAuth.create(userDublicate);
        response.then().assertThat().statusCode(403);
        response.then().assertThat().body("message", equalTo("User already exists"));
        Response responseLogin = userAuth.login(UserCredentials.from(userDublicate));
        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("создать пользователя и не заполнить email: 403 Forbidden")
    public void createUserWithNullEmailTest() {
        User userTest = new User(null,
                GettingParams.getRandomUser().getPassword(),
                GettingParams.getRandomUser().getName());
        Response response = userAuth.create(userTest);
        response.then().assertThat().statusCode(403);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить password: 403 Forbidden")
    public void createUserWithNullPasswordTest() {
        User userTest = new User(GettingParams.getRandomUser().getEmail(),
                null,
                GettingParams.getRandomUser().getName());
        Response response = userAuth.create(userTest);
        response.then().assertThat().statusCode(403);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить name: 403 Forbidden")
    public void createUserWithNullNameTest() {
        User userTest = new User(GettingParams.getRandomUser().getEmail(),
                GettingParams.getRandomUser().getPassword(),
                null);
        Response response = userAuth.create(userTest);
        response.then().assertThat().statusCode(403);
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

}
