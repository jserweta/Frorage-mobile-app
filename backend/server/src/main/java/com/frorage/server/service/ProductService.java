package com.frorage.server.service;

import com.frorage.server.model.Kitchen;
import com.frorage.server.model.Product;
import com.frorage.server.model.UsersGroup;
import com.frorage.server.payload.ProductAdd;
import com.frorage.server.payload.ProductFull;
import com.frorage.server.repository.KitchenRepository;
import com.frorage.server.repository.ProductRepository;
import com.frorage.server.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final String UNAUTHORIZED = "User is unauthorized";
    private final String NOTFOUND = "Kitchen not found";

    private ProductRepository productRepository;
    private UsersGroupService usersGroupService;
    private KitchenRepository kitchenRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,UsersGroupService usersGroupService, KitchenRepository kitchenRepository){
        this.productRepository = productRepository;
        this.usersGroupService = usersGroupService;
        this.kitchenRepository = kitchenRepository;
    }

    public ResponseEntity<?> addProduct(ProductAdd product, UserPrincipal userPrincipal){
        Product p = new Product(product.getKitchenId(), product.getProductName(), product.getAmount(), product.getUnit(), product.getExpirationDate());
        Optional<Product> productOptional;
        Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(p.getKitchenId(), userPrincipal.getId());
        if(usersGroupOptional.isPresent()){
            productRepository.save(p);
            productOptional = productRepository.findByProductNameAndKitchenId(p.getProductName(), p.getKitchenId());
            if( productOptional.isPresent()){
                return  new ResponseEntity<>(productOptional.get().getProductId(), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, "Failed"), HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<?> addFullProduct(ProductFull p, UserPrincipal userPrincipal){
        Product product = new Product(p.getKitchenId(), p.getProductName(), p.getAmount(), p.getUnit(), null, p.getRunningOut(), p.getToBuy(), p.getFavourite());
        Optional<Product> productOptional;
        Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(p.getKitchenId(), userPrincipal.getId());
        if(usersGroupOptional.isPresent()){

            if(p.getToBuy()){ // toBut = true
                productRepository.save(product);
                productOptional = productRepository.findByProductNameAndKitchenIdAndToBuyIsTrue(p.getProductName(), p.getKitchenId());
                if( productOptional.isPresent()){
                    return  new ResponseEntity<>(productOptional.get().getProductId(), HttpStatus.CREATED);
                }
            }else{ // toBut = true
                Optional<Product> productFoundOptional = productRepository.findByKitchenIdAndProductNameAndUnitAndToBuyIsFalse(product.getKitchenId(),product.getProductName(),product.getUnit());

                if(productFoundOptional.isPresent()){ // akutalizacja produktu
                    Product productFound = productFoundOptional.get();

                    if(productFound.getAmount() <= 0){ // aktualnie <0 wiec ustaw na wartosc dodawanego
                        productFound.setAmount(product.getAmount());
                    }else{
                        productFound.setAmount(productFound.getAmount() + product.getAmount()); // aktualnie >0 wiec dodaj wartosci amount obu produktow
                    }

                    productRepository.save(productFound);
                    return new ResponseEntity<>(productFound.getProductId(), HttpStatus.CREATED);

                }else{ // dodanie nowego produktu
                    productRepository.save(product);
                    productOptional = productRepository.findByProductNameAndKitchenId(product.getProductName(), product.getKitchenId());
                    if( productOptional.isPresent()){
                        return new ResponseEntity<>(productOptional.get().getProductId(), HttpStatus.CREATED);
                    }
                }
            }


            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, "Failed"), HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<?> updateProduct(Product p, UserPrincipal userPrincipal){
        Optional<Product> productOptional = productRepository.findByProductId(p.getProductId());
        if(productOptional.isPresent()){
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(p.getKitchenId(), userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                productRepository.save(p);
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true, "Ok"), HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Product does not exist"), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<?> getListOfProductsForKitchen(int kitchenId, UserPrincipal userPrincipal){
        Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(kitchenId, userPrincipal.getId());
        Optional<Kitchen> kitchenOptional = kitchenRepository.findById(kitchenId);
        if(kitchenOptional.isPresent()){
            if(usersGroupOptional.isPresent()){
                return new ResponseEntity<>(productRepository.findAllByKitchenIdAndToBuyIsFalseAndAmountGraterThan(kitchenId, 0F), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,NOTFOUND),HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteProduct(int productId, UserPrincipal userPrincipal){
        Optional<Product> productOptional = this.getProductById(productId);
        if(productOptional.isPresent()){
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(productOptional.get().getKitchenId(), userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                productOptional.get().setAmount(0F);
                productRepository.save(productOptional.get());
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true,"OK"),HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }
        else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"There is no such product"),HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<?> getListOfProductsToBuy(int kitchenId, UserPrincipal userPrincipal){
        Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(kitchenId, userPrincipal.getId());
        Optional<Kitchen> kitchenOptional = kitchenRepository.findById(kitchenId);
        if(kitchenOptional.isPresent()){
            if(usersGroupOptional.isPresent()){
                return new ResponseEntity<>(productRepository.findAllByKitchenIdAndToBuyIsTrue(kitchenId), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,NOTFOUND),HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getListOfFavouriteProducts(int kitchenId, UserPrincipal userPrincipal){
        Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(kitchenId, userPrincipal.getId());
        Optional<Kitchen> kitchenOptional = kitchenRepository.findById(kitchenId);
        if(kitchenOptional.isPresent()){
            if(usersGroupOptional.isPresent()){
                return new ResponseEntity<>(productRepository.findAllByKitchenIdAndFavouriteIsTrue(kitchenId), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,NOTFOUND),HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getListOfRunningOutProducts(int kitchenId, UserPrincipal userPrincipal){
        Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(kitchenId, userPrincipal.getId());
        Optional<Kitchen> kitchenOptional = kitchenRepository.findById(kitchenId);
        if(kitchenOptional.isPresent()){
            if(usersGroupOptional.isPresent()){
                return new ResponseEntity<>(productRepository.findAllByKitchenIdAndRunningOutIsTrue(kitchenId), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,UNAUTHORIZED),HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,NOTFOUND),HttpStatus.NOT_FOUND);
        }
    }
    public ResponseEntity<?> setProductsToBuyValue(List<Integer> listToBuy, boolean toBuy, UserPrincipal userPrincipal){
        Optional<Product> optionalProduct;
        Optional<UsersGroup> usersGroupOptional;

        for (Integer productId: listToBuy){
            optionalProduct = getProductById(productId); // Tobuy = true ma

            if(optionalProduct.isPresent()) {
                Product shoppingProduct = optionalProduct.get();
                Optional<Product> productFoundOptional = productRepository.findByKitchenIdAndProductNameAndUnitAndToBuyIsFalse(shoppingProduct.getKitchenId(), shoppingProduct.getProductName(), shoppingProduct.getUnit());

                if (productFoundOptional.isPresent()) { // akutalizacja produktu
                    Product productFound = productFoundOptional.get();

                    if (productFound.getAmount() <= 0) { // aktualnie <0 wiec ustaw na wartosc dodawanego
                        productFound.setAmount(shoppingProduct.getAmount());
                    } else {
                        productFound.setAmount(productFound.getAmount() + shoppingProduct.getAmount()); // aktualnie >0 wiec dodaj wartosci amount obu produktow
                    }

                    usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(shoppingProduct.getKitchenId(), userPrincipal.getId());
                    if (usersGroupOptional.isPresent()){
                        productRepository.delete(shoppingProduct);
                        productRepository.save(productFound);
                    }

                }else{ // tylko zmiana toBuy
                    usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(shoppingProduct.getKitchenId(), userPrincipal.getId());
                    if (usersGroupOptional.isPresent()){
                        shoppingProduct.setToBuy(toBuy);
                        productRepository.save(shoppingProduct);
                    }
                }
            }
        }
        return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true,"Ok"), HttpStatus.OK);
    }
    public Optional<Product> getProductById(int productId){
        return productRepository.findByProductId(productId);
    }
}
