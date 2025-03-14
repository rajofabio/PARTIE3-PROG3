package edu.restaurant.app.dao.operations;

import edu.restaurant.app.dao.DataSource;
import edu.restaurant.app.dao.entity.Dish;
import edu.restaurant.app.dao.entity.DishIngredient;
import edu.restaurant.app.dao.entity.Ingredient;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishCrudOperations implements CrudOperations<Dish> {
    private final DataSource dataSource = new DataSource();
    private final IngredientCrudOperations ingredientCrudOperations = new IngredientCrudOperations();

    @Override
    public List<Dish> getAll(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Dish findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select d.id, d.name, d.price from dish d where id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapFromResultSet(resultSet);
                }
            }
            throw new RuntimeException("Dish.id=" + id + " not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Dish mapFromResultSet(ResultSet resultSet) throws SQLException {
        Long idDish = resultSet.getLong("id");

        Dish dish = new Dish();
        dish.setId(idDish);
        dish.setName(resultSet.getString("name"));
        dish.setPrice(resultSet.getDouble("price"));
        List<DishIngredient> dishIngredients = ingredientCrudOperations.findByDishId(idDish);
        dish.setDishIngredients(dishIngredients);

        return dish;
    }

    @SneakyThrows
    @Override
    public List<Dish> saveAll(List<Dish> entities) {
        List<Dish> dishes = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into dish (id, name, price) values (?, ?, ?)"
                                 + " on conflict (id) do update set name=excluded.name, price=excluded.price"
                                 + " returning id, name, price")) {
                entities.forEach(entityToSave -> {
                    try {
                        statement.setLong(1, entityToSave.getId());
                        statement.setString(2, entityToSave.getName());
                        statement.setDouble(3, entityToSave.getPrice());
                        statement.addBatch(); // group by batch so executed as one query in database
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        dishes.add(mapFromResultSet(resultSet));
                    }
                }
                return dishes;
            }
        }
    }
}
