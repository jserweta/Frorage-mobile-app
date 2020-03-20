package com.frorage.server.controller;

import com.frorage.server.model.UsersGroup;
import com.frorage.server.service.UsersGroupService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(value = "usersGroup", tags = {"usersGroup"})
@RestController
@RequestMapping("/api")
public class UsersGroupController {

    private UsersGroupService usersGroupService;

    @Autowired
    public UsersGroupController(UsersGroupService usersGroupService){
        this.usersGroupService = usersGroupService;
    }

    @GetMapping("/kitchen/kitchenlist/{user_id}")
    public List<UsersGroup> getGroupsByUserId(@PathVariable(value="user_id") Long userId) {
        return usersGroupService.getGroupsByUserId(userId);
    }

}
