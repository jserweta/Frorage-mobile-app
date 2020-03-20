package com.frorage.server.model;

import com.frorage.server.model.audit.DateAudit;
import org.agileware.test.PropertiesTester;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;


class ModelTests {

    @Test
    void testConfirmationToken() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.addMapping(LocalDateTime.class, LocalDateTime.now()).testAll(ConfirmationToken.class);
    }

    @Test
    void testEmail() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(Email.class);
    }

    @Test
    void testIngredient() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(Ingredient.class);
    }

    /*@Test // Todo: tak to ma byc?
    void testKitchen() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(Kitchen.class);
    }*/

    @Test
    void testProduct() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.addMapping(Date.class, new Date()).testAll(Product.class);
    }

    @Test
    void testRecipe() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(Recipe.class);
    }

    @Test
    void testToken() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(Token.class);
    }

    @Test
    void testUser() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(User.class);
    }

    @Test
    void testUsersGroup() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(UsersGroup.class);
    }

    @Test
    void testDateAudit() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.addMapping(Instant.class, Instant.now()).testAll(DateAudit.class);
    }

}