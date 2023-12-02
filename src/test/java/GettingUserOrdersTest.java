import api.model.OrderClient;
import api.model.UserAuth;
import api.order.IngredientData;
import api.order.Order;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;

public class GettingUserOrdersTest extends BaseTest {
    private UserAuth userAuth;
    private OrderClient orderClient;
    private String accessToken;
    Random random = new Random();

    @Before
    public void setUp() {
        accessToken = createTestUser().path("accessToken");
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя: 200")
    public void orderListCanBeReturnedTest() {
        List<IngredientData> ingredientList = OrderClient.getIngredientList();
        List<String> ingredients = List.of(
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id());
        Response responseOrder = OrderClient.createOrder(accessToken, new Order(ingredients));
        responseOrder.then().assertThat().statusCode(200);

        Response responseOrderList = OrderClient.getUserOrderList(accessToken);
        responseOrderList.then().assertThat().statusCode(200);
        responseOrderList.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("orders", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказов не авторизованного пользователя: 401")
    public void orderListCantBeReturnedWithoutAuthTest() {
        Response response = OrderClient.getUserOrderList("");
        response.then().assertThat().statusCode(401);
        response.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("You should be authorised"));
    }
}