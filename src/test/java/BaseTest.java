import api.model.UserAuth;
import api.user.GettingParams;
import api.user.User;
import io.restassured.response.Response;
import org.junit.After;

public class BaseTest {
    private static UserAuth userAuth;
    private static String accessToken;
    private static User user;

    public Response createTestUser() {
        user = GettingParams.getRandomUser();
        userAuth = new UserAuth();
        return userAuth.create(user);
    }

    @After
    public void deleteTestUser() {
        if (accessToken != null) {
            UserAuth.delete(accessToken);
        }
    }
}