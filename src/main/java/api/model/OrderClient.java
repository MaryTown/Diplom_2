package api.model;

import api.order.IngredientData;
import api.order.IngredientList;
import api.order.Order;
import api.util.Spec;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.*;

import static io.restassured.RestAssured.given;

public class OrderClient {

    static final String ORDER_PATH ="/api/";

    @Step("Создание заказа")
    public static Response createOrder(String accessToken, Order order) {
        return given()
                .spec(Spec.getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH + "orders");
    }

    @Step("Получение списка заказов")
        public static Response getUserOrderList(String accessToken) {
        return given()
                .spec(Spec.getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH + "orders");
    }

    @Step("Получение списка ингредиентов")
    public static List<IngredientData> getIngredientList() {
        return given()
                .spec(Spec.getBaseSpec())
                .get(ORDER_PATH + "ingredients")
                .body().as(IngredientList.class).getData();
    }
}