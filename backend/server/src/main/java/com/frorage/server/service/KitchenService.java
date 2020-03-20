package com.frorage.server.service;

import com.frorage.server.model.Kitchen;
import com.frorage.server.model.UsersGroup;
import com.frorage.server.payload.KitchenAdd;
import com.frorage.server.repository.KitchenRepository;
import com.frorage.server.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class KitchenService {

    private KitchenRepository kitchenRepository;
    private UsersGroupService usersGroupService;

    @Autowired
    public KitchenService(KitchenRepository kitchenRepository,  UsersGroupService usersGroupService){
        this.kitchenRepository = kitchenRepository;
        this.usersGroupService = usersGroupService;
    }

    public ResponseEntity<?> saveKitchen(KitchenAdd kitchen, UserPrincipal userPrincipal) {
        Kitchen k = new Kitchen(kitchen.getKitchenName(), kitchen.getKitchenPassword());
        Optional<Kitchen> kitchenOptional = this.getKitchenByNameAndPassword(k);
        if (kitchenOptional.isPresent()){
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Kitchen already exists"),HttpStatus.BAD_REQUEST);
        }
        kitchenRepository.save(k);
        kitchenOptional = this.getKitchenByNameAndPassword(k);
        if(kitchenOptional.isPresent()){
            Kitchen kitchenWithId = kitchenOptional.get();
            usersGroupService.addUsersGroup(userPrincipal.getId(), kitchenWithId.getKitchenId());
            return new ResponseEntity<>(kitchenWithId, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"Failed to add kitchen"),HttpStatus.BAD_REQUEST);
    }

    public Optional<Kitchen> getKitchenByNameAndPassword(Kitchen kitchen){
        return kitchenRepository.findByKitchenNameAndKitchenPassword(kitchen.getKitchenName(), kitchen.getKitchenPassword());
    }

    public List< Kitchen > listKitchen() {
        return kitchenRepository.findAll();
    }

    public ResponseEntity<?> getKitchen(int id, UserPrincipal userPrincipal) {
        Optional<Kitchen> kitchenOptional = kitchenRepository.findById(id);
        if (kitchenOptional.isPresent()){
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(id, userPrincipal.getId());
            if(usersGroupOptional.isPresent()){
                return new ResponseEntity<>(kitchenOptional.get(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"User is unauthorized"),HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"There is no such kitchen"),HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteKitchen(int id, UserPrincipal userPrincipal) {
        Optional<Kitchen> kitchenOptional = kitchenRepository.findById(id);
        if (kitchenOptional.isPresent()) {
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(id, userPrincipal.getId());
            if (usersGroupOptional.isPresent()) {
                kitchenRepository.deleteById(id);
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true,"OK"),HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, "User is unauthorized"), HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false,"There is no such kitchen"),HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getKitchensByUserId(UserPrincipal userPrincipal ){
        List<UsersGroup> usersGroupList = usersGroupService.getGroupsByUserId(userPrincipal.getId());

        List<Integer> kitchendIdList = usersGroupList.stream()
                .map(UsersGroup::getKitchenId)
                .collect(Collectors.toList());

        List<Kitchen> kitchenList = new ArrayList<>();
//        for (Integer kitchenId: kitchendIdList){
//            kitchenService.getKitchen(kitchenId).ifPresent(kitchenList::add);
//        }
        kitchendIdList.forEach(id -> kitchenRepository.findById(id).ifPresent(kitchenList::add));
        return new ResponseEntity<>(kitchenList, HttpStatus.OK);
    }

    public ResponseEntity<?> joinKitchen(KitchenAdd kitchen, UserPrincipal userPrincipal) {
        Kitchen k = new Kitchen(kitchen.getKitchenName(), kitchen.getKitchenPassword());
        Optional<Kitchen> kitchenOptional = this.getKitchenByNameAndPassword(k);
        if (kitchenOptional.isPresent()) {
            Optional<UsersGroup> usersGroupOptional = usersGroupService.getGroupByKitchenIdAndUserId(kitchenOptional.get().getKitchenId(), userPrincipal.getId());
            if (usersGroupOptional.isPresent()) {
                return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(true,"You already belong to this kitchen"),HttpStatus.OK);
            }else{
                usersGroupService.addUsersGroup(userPrincipal.getId(), kitchenOptional.get().getKitchenId());
                return new ResponseEntity<>(kitchenOptional.get(), HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>(new com.frorage.server.payload.ApiResponse(false, "Kitchen not found"), HttpStatus.FORBIDDEN);
        }
    }

}
