package com.frorage.server.service;

import com.frorage.server.model.Kitchen;
import com.frorage.server.model.UsersGroup;
import com.frorage.server.payload.ApiResponse;
import com.frorage.server.payload.KitchenAdd;
import com.frorage.server.repository.KitchenRepository;
import com.frorage.server.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KitchenServiceTest {

    @InjectMocks
    private KitchenService kitchenService;

    @Mock
    private UsersGroupService mockUserGroupService;

    @Mock
    private KitchenRepository mockKitchenRepository;

    private final String UNAUTHORIZED = "User is unauthorized";

    private final String NOKITCHEN = "There is no such kitchen";

    @Test
    void saveKitchen_KitchenNotExists_OkRequest(){
        KitchenAdd kitchenAdd = new KitchenAdd("KuchniaTest", "12345678");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        Kitchen kitchen = new Kitchen("KuchniaTest", "12345678");

        when(kitchenService.getKitchenByNameAndPassword(kitchen))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Kitchen(1,"KuchniaTest", "12345678")));

        ResponseEntity<?> responseEntity = kitchenService.saveKitchen(kitchenAdd, userPrincipal);
        Kitchen apiResponse = (Kitchen) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode()),
                () ->assertEquals(1,apiResponse.getKitchenId())
        );
    }

    @Test
    void saveKitchen_KitchenExists_BadRequest(){

        KitchenAdd kitchenAdd = new KitchenAdd("KuchniaTest", "12345678");
        Kitchen kitchen = new Kitchen("KuchniaTest", "12345678");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);

        when(kitchenService.getKitchenByNameAndPassword(kitchen)).thenReturn(Optional.of(new Kitchen("KuchniaTest", "12345678")));

        ResponseEntity<?> responseEntity = kitchenService.saveKitchen(kitchenAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Kitchen already exists",apiResponse.getMessage())
        );
    }

    @Test
    void getKitchen_KitchenExists_OkRequest(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);

        when(mockKitchenRepository.findById(1)).thenReturn(Optional.of(new Kitchen("KuchniaTest", "12345678")));
        when(mockUserGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup( userPrincipal.getId(), 1)));

        ResponseEntity<?> responseEntity = kitchenService.getKitchen(1, userPrincipal);
        Kitchen kitchen= (Kitchen) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () ->assertEquals(kitchen.getKitchenName(),"KuchniaTest"),
                () ->assertEquals(kitchen.getKitchenPassword(), DigestUtils.md5DigestAsHex("12345678".getBytes()))
        );
    }

    @Test
    void getKitchen_KitchenNotExists_NOT_FOUND(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);

        when(mockKitchenRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = kitchenService.getKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () -> assertEquals(NOKITCHEN,apiResponse.getMessage())
        );
    }
    @Test
    void getKitchen_KitchenExists_FORBIDDEN(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);

        when(mockKitchenRepository.findById(1)).thenReturn(Optional.of(new Kitchen("KuchniaTest", "12345678")));
        when(mockUserGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = kitchenService.getKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () -> assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }

    @Test
    void deleteKitchen_KitchenExists_OK_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        int idKitchen = 1;
        when(mockKitchenRepository.findById(idKitchen)).thenReturn(Optional.of(new Kitchen(1, "KuchniaTest", "12345678")));
        when(mockUserGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = kitchenService.deleteKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () -> assertEquals("OK",apiResponse.getMessage())
        );
    }

    @Test
    void deleteKitchen_KitchenNotExists_NotFound_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        int idKitchen = 1;
        when(mockKitchenRepository.findById(idKitchen)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = kitchenService.deleteKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode()),
                () -> assertEquals(NOKITCHEN,apiResponse.getMessage())
        );
    }
    @Test
    void deleteKitchen_KitchenExistsBadUser_FORBIDDEN_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        int idKitchen = 1;
        when(mockKitchenRepository.findById(idKitchen)).thenReturn(Optional.of(new Kitchen(1, "KuchniaTest", "12345678")));
        when(mockUserGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = kitchenService.deleteKitchen(1, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () -> assertEquals(UNAUTHORIZED,apiResponse.getMessage())
        );
    }
    @Test
    void  getKitchensByUserId_KitchenExists_OK_Test(){
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        List<UsersGroup> usersGroupList = new ArrayList<>();
        usersGroupList.add(new UsersGroup(userPrincipal.getId(), 1));
        usersGroupList.add(new UsersGroup(userPrincipal.getId(), 2));
        when(mockUserGroupService.getGroupsByUserId(anyLong())).thenReturn(usersGroupList);
        when(mockKitchenRepository.findById(1)).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia1", "Kuchnia123")));
        when(mockKitchenRepository.findById(2)).thenReturn(Optional.of(new Kitchen(2, "MojaKuchnia2", "Kuchnia123")));
        ResponseEntity<?> responseEntity = kitchenService.getKitchensByUserId(userPrincipal);
        List<Kitchen> apiResponse = (List<Kitchen>) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () -> assertEquals(2,apiResponse.size())
        );
    }

    @Test
    void joinKitchen_OK_Test(){
        KitchenAdd kitchenAdd = new KitchenAdd("KuchniaTest", "12345678");
        Kitchen kitchen = new Kitchen("KuchniaTest", "12345678");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(kitchenService.getKitchenByNameAndPassword(kitchen)).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUserGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.of(new UsersGroup(userPrincipal.getId(), 1)));
        ResponseEntity<?> responseEntity = kitchenService.joinKitchen(kitchenAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () -> assertEquals("You already belong to this kitchen",apiResponse.getMessage())
        );
    }

    @Test
    void joinKitchen_Created_Test(){
        KitchenAdd kitchenAdd = new KitchenAdd("KuchniaTest", "12345678");
        Kitchen kitchen = new Kitchen("KuchniaTest", "12345678");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(kitchenService.getKitchenByNameAndPassword(kitchen)).thenReturn(Optional.of(new Kitchen(1, "MojaKuchnia", "Kuchnia123")));
        when(mockUserGroupService.getGroupByKitchenIdAndUserId(1, userPrincipal.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = kitchenService.joinKitchen(kitchenAdd, userPrincipal);
        Kitchen apiResponse = (Kitchen) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode()),
                () -> assertEquals(1, apiResponse.getKitchenId()),
                () -> assertEquals("MojaKuchnia", apiResponse.getKitchenName()),
                () -> assertEquals(DigestUtils.md5DigestAsHex("Kuchnia123".getBytes()), apiResponse.getKitchenPassword())
        );
    }

    @Test
    void joinKitchen_Forbidden_Test(){
        KitchenAdd kitchenAdd = new KitchenAdd("KuchniaTest", "12345678");
        Kitchen kitchen = new Kitchen("KuchniaTest", "12345678");
        UserPrincipal userPrincipal = new UserPrincipal(15L, "test", "test@gmail.com", "test1234", true);
        when(kitchenService.getKitchenByNameAndPassword(kitchen)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = kitchenService.joinKitchen(kitchenAdd, userPrincipal);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertAll(
                () ->assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode()),
                () -> assertEquals("Kitchen not found",apiResponse.getMessage())
        );
    }

}
