package coding.challenge;

import coding.challenge.domain.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TransactionControllerIntegrationTest {

    @Value("http://localhost:${local.server.port}")
    private String url;
    private Clock clock = Clock.systemDefaultZone();

    @Test
    @DirtiesContext
    public void add() throws Exception {
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(Clock.systemDefaultZone().millis());

        given()
                .contentType("application/json")
                .body(tx)
        .when()
                .post(url + "/transaction")
        .then()
                .statusCode(201);

    }

    @Test
    public void add_when_txTimestampIsMoreThan60Sec() throws Exception {
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(Clock.systemDefaultZone().instant().minusSeconds(70).toEpochMilli());

        given()
                .contentType("application/json")
                .body(tx)
        .when()
                .post(url + "/transaction")
        .then()
                .statusCode(204);

    }

    @Test
    @DirtiesContext
    public void statistics() throws Exception {
        // give a tx is added by API
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(Clock.systemDefaultZone().millis());

        given()
                .contentType("application/json")
                .body(tx)
        .when()
                .post(url + "/transaction")
        .then()
                .statusCode(201);

        // when I call statistics
        given()
        .when()
                .get(url + "/statistics")
        .then()
                .statusCode(200)
                .body("count", equalTo(1))
                .body("sum", is(10f))
                .body("min", is(10f))
                .body("max", is(10f))
                .body("avg", is(10f));
    }
}