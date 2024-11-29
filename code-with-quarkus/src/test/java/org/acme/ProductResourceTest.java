package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ProductResourceTest {
    @Test
    void testProductEndpoint() {
        given()
          .when().get("/products")
          .then()
             .statusCode(200);
//             .body(is("Hello from Quarkus REST"));
    }

    @Test
    void testProductEndpoint2() {
        given()
                .when().get("/products/2")
                .then()
                .statusCode(200);
    }

    @Test
    void testProductEndpoint3() {
        given()
                .when().get("/products/98")
                .then()
                .statusCode(404);
    }

}