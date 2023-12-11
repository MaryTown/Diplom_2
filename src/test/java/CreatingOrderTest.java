import api.model.OrderClient;
import api.order.IngredientData;
import api.order.Order;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreatingOrderTest extends BaseTest {

    private static String accessToken;
    private List<IngredientData> ingredientList;
    private List<String> ingredients;
    Random random = new Random();

    @Before
    public void setUp() {
        accessToken = createTestUser().path("accessToken");
        ingredientList = OrderClient.getIngredientList();
        ingredients = List.of(
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id());
    }

    @Test
    @DisplayName("Создание заказа c авторизацией: 200")
    public void createNewOrderTest() {
        Response responseOrder = OrderClient.createOrder(accessToken, new Order(ingredients));
        responseOrder.then().assertThat().statusCode(200);
        responseOrder.then().assertThat().body("name", Matchers.notNullValue())
                .and().assertThat().body("order.number", Matchers.notNullValue())
                .and().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        Response responseOrder = OrderClient.createOrder("", new Order(ingredients));
        responseOrder.then().assertThat().statusCode(200);
        responseOrder.then().assertThat().body("name", Matchers.notNullValue())
                .and().assertThat().body("order.number", Matchers.notNullValue())
                .and().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов: 400 Ingredient ids must be provided")
    public void createOrderWithoutIngredients() {
        Response responseOrder = OrderClient.createOrder(accessToken, new Order(null));
        responseOrder.then().assertThat().statusCode(400);
        responseOrder.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов: 500 Internal Server Error")
    public void createOrderWithIncorrectIngredient() {
        Response responseOrder = OrderClient.createOrder("", new Order(Collections.singletonList("djsaf43334j23k34sfd")));
        responseOrder.then().assertThat().statusCode(500);
    }

}