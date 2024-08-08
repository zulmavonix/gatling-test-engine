package VxTransaction;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.core.Simulation;

import java.util.Base64;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Buy extends Simulation {

    // Http Configuration
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.vonix.id")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static ChainBuilder auth = exec(session -> {
        // Base64 encode credentials if needed
        String email = "hariyanto.iyan@vonix.id";
        String password = "Vonix1234!";
        String credentials = email + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Set the encoded credentials in the session
        return session.set("encodedCredentials", encodedCredentials);
    }).exec(http("Login")
            .post("/v1/auth/login")
            .body(StringBody("{\n" +
                    "  \"email\" : \"hariyanto.iyan@vonix.id\",\n" +
                    "  \"password\" : \"Vonix1234!\"\n" +
                    "}"))
            .check(jmesPath("accessToken").saveAs("bearerToken"))
    ).exitHereIfFailed();

    private static ChainBuilder buyCoin = exec(http("Buy Coin")
            .post("/v1/transaction/trade/place/purchase")
            .header("Authorization", "Bearer #{bearerToken}")
            .body(ElFileBody("body/buyCoin.json")).asJson()
            .check(status().is(200))  // Check for 422 status to handle errors
            .check(bodyString().saveAs("responseBody"))  // Save response body for debugging
    ).exec(session -> {
        // Debug the response body for further inspection
        String responseBody = session.getString("responseBody");
        System.out.println("Response Body: " + responseBody);
        return session;
    });

    // Scenario
    private final ScenarioBuilder scn = scenario("Vonix.id")
            .exec(auth)    // Ensure auth is executed first
            .pause(2)
            .exec(buyCoin);

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}


