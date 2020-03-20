package com.frorage.functionaltests;

import com.google.gson.Gson;
import okhttp3.*;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final String username = "TestUser";
    private final String email = "sznycel100@o2.pl";
    private final String password = "TestPassword";


    private final int port = 8080;
    private final String uri = "http://localhost";
    private static String token;
    private static String accessToken;

    private final String testUserRegister = "{\n" +
            "\t\"username\" : \""+ username +"\",\n" +
            "\t\"email\" : \""+ email +"\",\n" +
            "\t\"password\" : \""+ password +"\"\n" +
            "}";

    private final String testUserLoginByUsername = "{\n" +
            "\t\"usernameOrEmail\" : \""+ username +"\",\n" +
            "\t\"password\" : \""+ password +"\"\n" +
            "}";

    private final String changeUserPassword = "{\n" +
            "\t\"oldPassword\" : \"TestPassword\",\n" +
            "\t\"newPassword\" : \"Robert1234\"\n" +
            "}\n";

    private final String resentEmail = "{\n" +
            "\t\"email\":\"" + email +"\"\n" +
            "}";

    private int userId;


    @Test
    @Order(1)
    void registerUser() {
        io.restassured.response.Response res =
                given()
                        .baseUri(uri)
                        .port(port)
                        .contentType("application/json")
                        .body(testUserRegister)
                        .when()
                        .post("/api/user/register");

        String location = res.header("location");
        userId = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));
        token = res.jsonPath().get("token").toString();
        // make valid JSON
        token = "{\n" +
                "\t\"token\":\"" + token +"\"\n" +
                "}";
        assertAll(
                () -> assertEquals(201, res.statusCode())
        );
    }

    @Test
    @Order(2)
    void resentUserEmailConfirmation(){
        io.restassured.response.Response res =
                given()
                        .baseUri(uri)
                        .port(port)
                        .contentType("application/json")
                        .body(resentEmail)
                        .when()
                        .post("/api/user/resend-email");


        assertAll(
                () -> assertEquals(200, res.statusCode())
        );
    }

    @Test
    @Order(3)
    void confirmUser(){
        io.restassured.response.Response res =
                given()
                        .baseUri(uri)
                        .port(port)
                        .contentType("application/json")
                        .body(token)
                        .when()
                        .post("/api/user/confirm-account");



        assertAll(
                () -> assertEquals(200, res.statusCode())
        );
    }


    @Test
    @Order(4)
    void loginUser(){
        io.restassured.response.Response res =
                given()
                        .baseUri(uri)
                        .port(port)
                        .contentType("application/json")
                        .body(testUserLoginByUsername)
                        .when()
                        .post("/api/user/login");

        accessToken = "Bearer "  + res.jsonPath().get("accessToken").toString();

        assertAll(
                () -> assertEquals(200, res.statusCode())
        );
    }

    @Test
    @Order(5)
    void updateUserPassword(){
        io.restassured.response.Response res =
                given()
                        .baseUri(uri)
                        .port(port)
                        .header("Authorization", accessToken)
                        .contentType("application/json")
                        .body(changeUserPassword)
                        .when()
                        .patch("/api/user");

        assertAll(
                () -> assertEquals(204, res.statusCode())
        );
    }

    @Test
    @Order(6)
    void forgotUserPassword(){
        io.restassured.response.Response res =
                given()
                        .baseUri(uri)
                        .port(port)
                        .header("Authorization", accessToken)
                        .when()
                        .post("/api/user/reset");

        assertAll(
                () -> assertEquals(200, res.statusCode())
        );
    }

    @Test
    @Order(7)
    void deleteUser(){
        io.restassured.response.Response res =
                given()
                        .baseUri(uri)
                        .port(port)
                        .header("Authorization", accessToken)
                        .when()
                        .delete("/api/user");

        assertAll(
                () -> assertEquals(200, res.statusCode())
        );
    }
}