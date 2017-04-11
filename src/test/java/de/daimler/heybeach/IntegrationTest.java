package de.daimler.heybeach;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import de.daimler.heybeach.auth.TokenService;
import de.daimler.heybeach.config.TestConfig;
import de.daimler.heybeach.model.User;
import de.daimler.heybeach.model.UserRole;
import de.daimler.heybeach.resources.UsersResource;
import de.daimler.heybeach.util.APIConstants;
import org.apache.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int localPort;

    @Autowired
    private TokenService tokenService;

    private String adminToken;
    private UUID adminUserId = UUID.fromString("1e2ed3a8-4623-4914-9e85-e74b9fdcd78a");
    private String sellerToken;
    private UUID sellerUserId = UUID.fromString("2e2ed3a8-4623-4914-9e85-e74b9fdcd78a");
    private String buyerToken;
    private UUID buyerUserId = UUID.fromString("3e2ed3a8-4623-4914-9e85-e74b9fdcd78a");

    @Before
    public void setUp() {
        RestAssured.port = localPort;
        adminToken = tokenService.generateNewToken();
        sellerToken = tokenService.generateNewToken();
        buyerToken = tokenService.generateNewToken();

        tokenService.store(adminToken, new TestingAuthenticationToken(adminUserId, nullValue(), UserRole.admin.name()));
        tokenService.store(sellerToken, new TestingAuthenticationToken(sellerUserId, nullValue(), UserRole.seller.name()));
        tokenService.store(buyerToken, new TestingAuthenticationToken(buyerUserId, nullValue(), UserRole.buyer.name()));
    }

    @Test
    public void testAuthenticationSuccess() {
        given()
                .headers("X-Auth-Username", "admin@heybeach.com")
                .headers("X-Auth-Password", "pass")
                .when()
                .post(APIConstants.AUTHENTICATE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", is(not(isEmptyOrNullString())));
    }

    @Test
    public void testAuthenticationFailed() {
        given()
                .headers("X-Auth-Username", "unknown@heybeach.com")
                .headers("X-Auth-Password", "pass")
                .when()
                .post(APIConstants.AUTHENTICATE_URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setRole(UserRole.seller);
        user.setPassword("password");
        user.setEmail("email@heybeach.com");
        Response response = given()
                .body(user)
                .contentType(JSON)
                .when()
                .post(UsersResource.PATH)
                .andReturn();
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, is(not(isEmptyOrNullString())));

        String userURI = response.getHeader(HttpHeaders.LOCATION);

        given()
                .header("X-Auth-Token", adminToken)
                .when()
                .get(userURI)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("email", is(equalTo("email@heybeach.com")))
                .body("$", not(hasKey("password")))
                .body("role", is(equalTo(UserRole.seller.name())));
    }

    @Test
    public void testUnauthorizedAccess() {
        when()
                .get(UsersResource.PATH + "/{id}", sellerUserId)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }


}
