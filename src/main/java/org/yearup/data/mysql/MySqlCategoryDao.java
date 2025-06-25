package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.yearup.data.mysql.MySqlProductDao.mapRow;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        // gets all categories
        String sql = "SELECT * FROM categories";
        List<Category> categories = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) // wjile loop to map each row into a category
            {
                categories.add(mapRow(resultSet));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving categories", e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        String sql = "SELECT * FROM categories WHERE category_id = ?";// gets category by specific id

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, categoryId);
            try (ResultSet result = statement.executeQuery())
            {
                if (result.next())
                {
                    return mapRow(result);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error getting category by ID", e);
        }
        return null;
    }
    @Override
    public Category create(Category category)
    {
        // create a new category
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        // makes new row

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                // return generated keys pulls id that the database assigned
        {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            int rows = statement.executeUpdate();

            if (rows > 0)
            {
                try (ResultSet keys = statement.getGeneratedKeys())
                {
                    if (keys.next())
                    {
                        int newId = keys.getInt(1);
                        category.setCategoryId(newId);
                        return category;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error inserting category", e);
        }

        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
        // updates row using provided ID
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String sql = "DELETE FROM categories WHERE category_id = ?";
        // deletes row from category ID

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");
        // pulls row from the database and builds a new category

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
