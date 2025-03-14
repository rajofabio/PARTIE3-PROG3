package edu.restaurant.app.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DishIngredient {
    private Long id;
    private Ingredient ingredient;
    private Double requiredQuantity;
    private Unit unit;
}
