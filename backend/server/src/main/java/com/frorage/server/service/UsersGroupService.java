package com.frorage.server.service;

import com.frorage.server.model.UsersGroup;
import com.frorage.server.repository.UsersGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersGroupService {

    private UsersGroupRepository usersGroupRepository;


    @Autowired
    public UsersGroupService(UsersGroupRepository usersGroupRepository) {
        this.usersGroupRepository = usersGroupRepository;

    }

    public List<UsersGroup> getGroupsByUserId(Long userId){
        return usersGroupRepository.findAllByUserId(userId);
    }

    public void addUsersGroup(Long userId, int kitchenId){
        UsersGroup usersGroup = new UsersGroup(userId, kitchenId);
        usersGroupRepository.save(usersGroup);

    }

    public Optional<UsersGroup> getGroupByKitchenIdAndUserId(int kitchenId, Long userId){
        return usersGroupRepository.findByKitchenIdAndUserId(kitchenId, userId);
    }


}
