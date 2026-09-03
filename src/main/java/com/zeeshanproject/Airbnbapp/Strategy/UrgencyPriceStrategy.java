package com.zeeshanproject.Airbnbapp.Strategy;

import com.zeeshanproject.Airbnbapp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;


@RequiredArgsConstructor
public class UrgencyPriceStrategy implements PricingStrategy{

    private final PricingStrategy warpped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = warpped.calculatePrice(inventory);
        LocalDate today = LocalDate.now();
        if (!inventory.getDate().isBefore(today) && inventory.getDate().isBefore(today.plusDays(7))){
            price = price.multiply(BigDecimal.valueOf(1.15));
        }
        return price;
    }
}
