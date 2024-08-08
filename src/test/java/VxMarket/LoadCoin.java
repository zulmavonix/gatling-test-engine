package VxMarket;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.core.Simulation;

public class LoadCoin extends Simulation {

    // Http Configuration
    private final HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl("https://api.vonix.id")
            .acceptHeader("application/json");

    // Scenario
    private final ScenarioBuilder scn = CoreDsl.scenario("Vonix.id")
            .exec(HttpDsl.http("Get Coin")
                    .get("/v2/master/coins?isDisabled=false&pageIndex=0&pageSize=1000"));

    // Load Simulation
    {
        setUp(
                scn.injectOpen(CoreDsl.atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
