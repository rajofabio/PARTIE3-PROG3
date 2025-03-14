package edu.restaurant.app.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static java.time.LocalDate.now;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Price {
    private Long id;
    private Ingredient ingredient;
    private Double amount;
    private LocalDate dateValue;

    public Price(Double amount) {
        this.amount = amount;
        this.dateValue = now();
    }
}
