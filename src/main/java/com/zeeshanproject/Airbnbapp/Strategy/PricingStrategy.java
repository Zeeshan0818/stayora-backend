package com.zeeshanproject.Airbnbapp.Strategy;

import com.zeeshanproject.Airbnbapp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
