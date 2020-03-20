package com.frorage.server.model;

import javax.persistence.*;

/**
 * Class representing table users_group in database
 */
@Entity
@Table(name = "users_group")
public class UsersGroup {

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "kitchen_id")
    private int kitchenId;

    public UsersGroup(Long userId, int kitchenId) {
        this.userId = userId;
        this.kitchenId = kitchenId;
    }
    public UsersGroup() {
    }
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getKitchenId() {
        return kitchenId;
    }

    public void setKitchenId(int kitchenId) {
        this.kitchenId = kitchenId;
    }
}
