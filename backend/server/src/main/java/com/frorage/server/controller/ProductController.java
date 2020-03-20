package com.frorage.server.controller;


import com.frorage.server.model.Product;
import com.frorage.server.payload.ProductAdd;
import com.frorage.server.payload.ProductFull;
import com.frorage.server.security.CurrentUser;
import com.frorage.server.security.UserPrincipal;
import com.frorage.server.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "product", tags = {"product"})
@RestController
@RequestMapping("/api")
public class ProductController {
    private ProductService productService;


    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @ApiOperation(value = "add product")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = String.class),
            @ApiResponse(code = 400, message = "Adding failed", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestBody ProductAdd p, @CurrentUser UserPrincipal userPrincipal){
        return productService.addProduct(p, userPrincipal);
    }
    @ApiOperation(value = "add full product")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = String.class),
            @ApiResponse(code = 400, message = "Adding failed", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping("/product_full")
    public ResponseEntity<?> addFullProduct(@RequestBody ProductFull p, @CurrentUser UserPrincipal userPrincipal){
        return productService.addFullProduct(p, userPrincipal);
    }
    @ApiOperation(value = "Update product")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 400, message = "Product does not exist", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @PatchMapping("/product/update")
    public ResponseEntity<?> updateProduct(@RequestBody Product p, @CurrentUser UserPrincipal userPrincipal){
        return productService.updateProduct(p, userPrincipal);
    }
    @ApiOperation(value = "get list of products")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = Product.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/product/list/{kitchen_id}")
    public ResponseEntity<?> getListOfProductsForKitchen(@PathVariable(value="kitchen_id") int kitchenId, @CurrentUser UserPrincipal userPrincipal){
        return productService.getListOfProductsForKitchen(kitchenId, userPrincipal);
    }

    @ApiOperation(value = "delete product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @DeleteMapping("/product/delete/{product_id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value="product_id") int productId, @CurrentUser UserPrincipal userPrincipal){
        return productService.deleteProduct(productId, userPrincipal);
    }

    @ApiOperation(value = "get list of products to buy")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = Product.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/shopping_list/{kitchen_id}")
    public ResponseEntity<?> getListOfProductsToBuy(@PathVariable(value="kitchen_id") int kitchenId, @CurrentUser UserPrincipal userPrincipal){
        return productService.getListOfProductsToBuy(kitchenId, userPrincipal);
    }

    @ApiOperation(value = "get list of favourite products")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = Product.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/favourite_products/{kitchen_id}")
    public ResponseEntity<?> getListOfFavoriteProducts(@PathVariable(value="kitchen_id") int kitchenId, @CurrentUser UserPrincipal userPrincipal){
        return productService.getListOfFavouriteProducts(kitchenId, userPrincipal);
    }

    @ApiOperation(value = "get list of running out products")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = Product.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/runningout_products/{kitchen_id}")
    public ResponseEntity<?> getListOfRunningOutProducts(@PathVariable(value="kitchen_id") int kitchenId, @CurrentUser UserPrincipal userPrincipal){
        return productService.getListOfRunningOutProducts(kitchenId, userPrincipal);
}
    @ApiOperation(value = "change the value of to buy")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized")})
    @PostMapping("/set_tobuy/{to_buy}")
    public ResponseEntity<?> setProductsToBuyValue(@RequestBody List<Integer> listToBuy,@PathVariable (value="to_buy") boolean toBuy, @CurrentUser UserPrincipal userPrincipal){
        return productService.setProductsToBuyValue(listToBuy, toBuy, userPrincipal);
    }
}
