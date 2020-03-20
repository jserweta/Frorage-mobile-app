package com.frorage.server.payload;

public class IngredientStatus {
    
    private int availableAmount;
    
    private int requiredAmount;

    public IngredientStatus(int availableAmount, int requiredAmount) {
        this.availableAmount = availableAmount;
        this.requiredAmount = requiredAmount;
    }

    public IngredientStatus() {
    }

    public int getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(int availableAmount) {
        this.availableAmount = availableAmount;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
}
