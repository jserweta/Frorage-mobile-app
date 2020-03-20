package com.frorage.server.service;

import com.frorage.server.model.Kitchen;
import com.frorage.server.model.Product;
import com.frorage.server.model.UsersGroup;
import com.frorage.server.payload.ApiResponse;
import com.frorage.server.payload.ProductAdd;
import com.frorage.server.payload.ProductFull;
import com.frorage.server.repository.KitchenRepository;
import com.frorage.server.repository.ProductRepository;
import com.frorage.server.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private UsersGroupService mockUsersGroupService;

    @Mock
    private ProductRepository mockProductRepository;

    @Mock
    private KitchenRepository mockKitchenRepository;

    private final String UNAUTHORIZED = "User is unauthorized";

    @Test
    void addProduct_Created_Test(){
        ProductAdd productAdd = new ProductAdd(1, "produkt1", 1F, "szt", null);
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByProductNameAndKitchenId(anyString(), anyInt())).thenReturn(Optional.of(new Product()));
        ResponseEntity<?> responseEntity = productService.addProduct(productAdd, userPrincipal);

        assertAll(
                () ->assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode())
        );
    }

    @Test
    void addProduct_Forbidden_Test(){
        ProductAdd productAdd = new ProductAdd(1, "produkt1", 1F, "szt", null);
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.addProduct(productAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }
    @Test
    void addProduct_BadRequest_Test(){
        ProductAdd productAdd = new ProductAdd(1, "produkt1", 1F, "szt", null);
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByProductNameAndKitchenId(anyString(), anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.addProduct(productAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () ->assertEquals("Failed",apiResponse.getMessage())
        );
    }

    @Test
    void addFullProduct_Created_ToBuyTrue_Test(){
        ProductFull productFull = new ProductFull(1, "produkt1", 1F, "szt", null, true, true, true);
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByProductNameAndKitchenIdAndToBuyIsTrue(anyString(), anyInt())).thenReturn(Optional.of(new Product()));
        ResponseEntity<?> responseEntity = productService.addFullProduct(productFull, userPrincipal);

        assertAll(
                () ->assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode())
        );
    }

    @Test
    void addFullProduct_Created_ToBuyFalse_Test(){
        ProductFull productFull = new ProductFull(1, "produkt1", 1F, "szt", null, true, false, false);
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByKitchenIdAndProductNameAndUnitAndToBuyIsFalse(anyInt(), anyString(), anyString())).thenReturn(Optional.of(new Product(1, "produkt1", 1F, "szt", null, false, false, false)));
        ResponseEntity<?> responseEntity = productService.addFullProduct(productFull, userPrincipal);

        assertAll(
                () ->assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode())
        );
    }

    @Test
    void addFullProduct_Forbidden_Test(){
        ProductFull productFull = new ProductFull(1, "produkt1", 1F, "szt", null, true, true, true);        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.addFullProduct(productFull, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }
    @Test
    void addFullProduct_BadRequest_Test(){
        ProductFull productFull = new ProductFull(1, "produkt1", 1F, "szt", null, true, true, true);        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockProductRepository.findByProductNameAndKitchenIdAndToBuyIsTrue(anyString(), anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.addFullProduct(productFull, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () ->assertEquals("Failed",apiResponse.getMessage())
        );
    }

    @Test
    void getListOfProductsForKitchen_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        when(mockKitchenRepository.findById(1)).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1, "produkt1", 1F, "szt", null));
        productList.add(new Product(1, "produkt2", 2F, "szt", null));
        when(mockProductRepository.findAllByKitchenIdAndToBuyIsFalseAndAmountGraterThan(1, 0F)).thenReturn(productList);
        ResponseEntity<?> responseEntity = productService.getListOfProductsForKitchen(1, userPrincipal);
        List<Product> apiResponse = (List<Product>) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(2,apiResponse.size())
        );
    }

    @Test
    void getListOfProductsForKitchen_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        when(mockKitchenRepository.findById(1)).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        ResponseEntity<?> responseEntity = productService.getListOfProductsForKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void getListOfProductsForKitchen_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.getListOfProductsForKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("Kitchen not found",apiResponse.getMessage())
        );
    }

    @Test
    void deleteProduct_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(productService.getProductById(anyInt())).thenReturn(Optional.of(new Product(1, "produkt1", 1F, "szt", null)));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = productService.deleteProduct(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals("OK",apiResponse.getMessage())
        );
    }

    @Test
    void deleteProduct_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(productService.getProductById(anyInt())).thenReturn(Optional.of(new Product(1, "produkt1", 1F, "szt", null)));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.deleteProduct(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void deleteProduct_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(productService.getProductById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.deleteProduct(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("There is no such product",apiResponse.getMessage())
        );
    }

    @Test
    void getListOfProductsToBuy_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        List<Product> productList = new ArrayList<>();
        Product prod1 = new Product(1, "produkt1", 1F, "szt", null);
        prod1.setToBuy(true);
        Product prod2 = new Product(1, "produkt2", 2F, "szt", null);
        prod2.setToBuy(true);
        productList.add(prod1);
        productList.add(prod2);
        when(mockProductRepository.findAllByKitchenIdAndToBuyIsTrue(1)).thenReturn(productList);
        ResponseEntity<?> responseEntity = productService.getListOfProductsToBuy(1, userPrincipal);
        List<Product> apiResponse = (List<Product>) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(2,apiResponse.size())
        );
    }

    @Test
    void getListOfProductsToBuy_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.getListOfProductsToBuy(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void getListOfProductsToBuy_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.getListOfProductsToBuy(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("Kitchen not found",apiResponse.getMessage())
        );
    }

    @Test
    void getListOfRunningOutProducts_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        List<Product> productList = new ArrayList<>();
        Product prod1 = new Product(1, "produkt1", 1F, "szt", null);
        prod1.setRunningOut(true);
        Product prod2 = new Product(1, "produkt2", 2F, "szt", null);
        prod2.setRunningOut(true);
        productList.add(prod1);
        productList.add(prod2);
        when(mockProductRepository.findAllByKitchenIdAndRunningOutIsTrue(1)).thenReturn(productList);
        ResponseEntity<?> responseEntity = productService.getListOfRunningOutProducts(1, userPrincipal);
        List<Product> apiResponse = (List<Product>) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(2,apiResponse.size())
        );
    }

    @Test
    void getListOfRunningOutProducts_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.getListOfRunningOutProducts(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void getListOfRunningOutProducts_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.getListOfRunningOutProducts(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("Kitchen not found",apiResponse.getMessage())
        );
    }

    @Test
    void getListOfFavouriteProducts_Ok_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        List<Product> productList = new ArrayList<>();
        Product prod1 = new Product(1, "produkt1", 1F, "szt", null);
        prod1.setFavourite(true);
        Product prod2 = new Product(1, "produkt2", 2F, "szt", null);
        prod2.setFavourite(true);
        productList.add(prod1);
        productList.add(prod2);
        when(mockProductRepository.findAllByKitchenIdAndFavouriteIsTrue(1)).thenReturn(productList);
        ResponseEntity<?> responseEntity = productService.getListOfFavouriteProducts(1, userPrincipal);
        List<Product> apiResponse = (List<Product>) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(2,apiResponse.size())
        );
    }

    @Test
    void getListOfFavouriteProducts_Forbidden_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.getListOfFavouriteProducts(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () ->assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void getListOfFavouriteProducts_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(mockKitchenRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(mockUsersGroupService.getGroupByKitchenIdAndUserId(anyInt(), anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.getListOfFavouriteProducts(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () ->assertEquals("Kitchen not found",apiResponse.getMessage())
        );
    }
}
