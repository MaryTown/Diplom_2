import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreatingOrderTest {

    private User user;
    private UserAuth userAuth;
    private String accessToken;
    Random random = new Random();
    
    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserAuth.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа c авторизацией: 200")
    public void createNewOrderTest() {
        User user = GettingParams.getRandomUser();
        Response response = userAuth.create(user);
        accessToken = response.path("accessToken");
        response.then().assertThat().statusCode(200);

        List<IngredientData> ingredientList = OrderClient.getIngredientList();
        List<String> ingredients = List.of(
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id());
        Response responseOrder = OrderClient.createOrder(accessToken, new Order(ingredients));
        responseOrder.then().assertThat().statusCode(200);
        responseOrder.then().assertThat().body("name", Matchers.notNullValue())
                .and().assertThat().body("order.number", Matchers.notNullValue())
                .and().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        List<IngredientData> ingredientList = OrderClient.getIngredientList();
        List<String> ingredients = List.of(
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id(),
                ingredientList.get(random.nextInt(ingredientList.size())).get_id());
        Response responseOrder = OrderClient.createOrder("", new Order(ingredients));
        responseOrder.then().assertThat().statusCode(200);
        responseOrder.then().assertThat().body("name", Matchers.notNullValue())
                .and().assertThat().body("order.number", Matchers.notNullValue())
                .and().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов: 400 Ingredient ids must be provided")
    public void createOrderWithoutIngredients() {
        User user = GettingParams.getRandomUser();
        Response response = userAuth.create(user);
        accessToken = response.path("accessToken");
        response.then().assertThat().statusCode(200);

        Response responseOrder = OrderClient.createOrder(accessToken, new Order(null));
        responseOrder.then().assertThat().statusCode(400);
        responseOrder.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов: 500 Internal Server Error")
    public void createOrderWithIncorrectIngredient() {
        User user = GettingParams.getRandomUser();
        Response response = userAuth.create(user);
        accessToken = response.path("accessToken");
        response.then().assertThat().statusCode(200);

        Response responseOrder = OrderClient.createOrder("", new Order(Collections.singletonList("djsaf43334j23k34sfd")));
        responseOrder.then().assertThat().statusCode(500);
    }

}