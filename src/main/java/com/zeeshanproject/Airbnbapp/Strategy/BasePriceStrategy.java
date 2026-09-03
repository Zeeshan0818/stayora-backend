package com.zeeshanproject.Airbnbapp.Strategy;

import com.zeeshanproject.Airbnbapp.entity.Inventory;

import java.math.BigDecimal;

public class BasePriceStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
