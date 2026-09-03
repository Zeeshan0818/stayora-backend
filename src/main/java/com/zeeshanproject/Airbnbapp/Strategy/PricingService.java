package com.zeeshanproject.Airbnbapp.Strategy;

import com.zeeshanproject.Airbnbapp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public final BigDecimal calculateDynamicPricing(Inventory inventory){
        PricingStrategy pricingStrategy = new BasePriceStrategy();

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPriceStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPriceStartegy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

    public final BigDecimal calculateTotalPrice(List<Inventory> inventoryList){
        return inventoryList.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
