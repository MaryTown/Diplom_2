import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matchers;

public class LoginUserTest {
    private User user;
    private UserAuth userAuth;
    private String accessToken;

    @Before
    public void setUp() {
        user = GettingParams.getRandomUser();
        userAuth = new UserAuth();
        userAuth.create(user);
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserAuth.delete(accessToken);
        }
    }

    @Test
    @DisplayName("логин под существующим пользователем: 200")
    public void positiveCreateAndLoginUserTest() {
        Response response = userAuth.login(UserCredentials.from(user));
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("accessToken", Matchers.notNullValue())
                .and().assertThat().body("refreshToken", Matchers.notNullValue());
        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("логин с неверным паролем: 401 Unauthorized")
    public void loginUserWithIncorrectPasswordTest() {
        Response response = userAuth.login(new UserCredentials(user.getEmail(),"asdfjhgasfd"));
        accessToken = userAuth.login(UserCredentials.from(user)).path("accessToken");
        response.then().assertThat().statusCode(401);
        response.then().assertThat().body("message", equalTo("email or password are incorrect"));
    }
}
