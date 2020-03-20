package com.frorage.server.controller;

import com.frorage.server.model.Kitchen;
import com.frorage.server.payload.KitchenAdd;
import com.frorage.server.security.CurrentUser;
import com.frorage.server.security.UserPrincipal;
import com.frorage.server.service.KitchenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "kitchen", tags = {"kitchen"})
@RestController
@RequestMapping("/api")
public class KitchenController {
     private KitchenService kitchenService;


    @Autowired
    public KitchenController(KitchenService kitchenService){
        this.kitchenService = kitchenService;
    }

    @ApiOperation(value = "add new kitchen")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Kitchen.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Adding failed", response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping("/kitchen")
    public ResponseEntity<?> addKitchen(@RequestBody KitchenAdd kitchen, @CurrentUser UserPrincipal userPrincipal){
        return kitchenService.saveKitchen(kitchen, userPrincipal);
    }

    @ApiOperation(value = "add user to kitchen")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Kitchen.class),
            @ApiResponse(code = 200, message = "OK", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 400, message = "Adding failed", response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping("/kitchen/join")
    public ResponseEntity<?> joinKitchen(@RequestBody KitchenAdd kitchen, @CurrentUser UserPrincipal userPrincipal) {
        return kitchenService.joinKitchen(kitchen, userPrincipal);
    }


    @GetMapping("/kitchen")
    public List<Kitchen> listKitchen(){
        return kitchenService.listKitchen();
    }

    @ApiOperation(value = "get kitchen by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Kitchen.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @GetMapping("/kitchen/{id}")
    public ResponseEntity<?> getKitchen(@PathVariable(value="id") int id, @CurrentUser UserPrincipal userPrincipal){
       return kitchenService.getKitchen(id, userPrincipal);
    }

    @ApiOperation(value = "delete kitchen by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 403, message = "Access forbidden", response = com.frorage.server.payload.ApiResponse.class)})
    @DeleteMapping("/kitchen/delete/{id}")
    public ResponseEntity<?> deleteKitchen(@PathVariable(value="id") int id, @CurrentUser UserPrincipal userPrincipal) {
        return kitchenService.deleteKitchen(id, userPrincipal);
    }
    @ApiOperation(value = "get kitchens for user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK",  response = Kitchen.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized")})
    @GetMapping("/kitchen/list")
    public ResponseEntity<?> getKitchensByUserId( @CurrentUser UserPrincipal userPrincipal ) {
        return kitchenService.getKitchensByUserId(userPrincipal);
    }
}
