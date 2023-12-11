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

public class LoginUserTest extends BaseTest {
    private User user;
    private UserAuth userAuth;
    private String accessToken;

    @Before
    public void setUp() {
        userAuth = new UserAuth();
        user = GettingParams.getRandomUser();
        accessToken = userAuth.create(user).path("accessToken");
    }

    @Test
    @DisplayName("логин под существующим пользователем: 200")
    public void positiveCreateAndLoginUserTest() {
        Response response = userAuth.login(UserCredentials.from(user));
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("accessToken", Matchers.notNullValue())
                .and().assertThat().body("refreshToken", Matchers.notNullValue());
    }

    @Test
    @DisplayName("логин с неверным паролем: 401 Unauthorized")
    public void loginUserWithIncorrectPasswordTest() {
        Response response = userAuth.login(new UserCredentials(user.getEmail(),"asdfjhgasfd"));
        response.then().assertThat().statusCode(401);
        response.then().assertThat().body("message", equalTo("email or password are incorrect"));
    }
}
