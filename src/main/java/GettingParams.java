import net.datafaker.Faker;

public class GettingParams {
    static Faker faker = new Faker();

    public static User getRandomUser() {
        String email = faker.name().username() + "@testtest.ru";
        String password = String.valueOf(faker.password());
        String name = faker.name().firstName();
        return new User(email, password, name);
    }
}
