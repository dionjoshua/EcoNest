package com.econest.dao;

import com.econest.model.Product;
import com.econest.model.ProductCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements CrudDAO<Product> {

    @Override
    public void add(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, eco_rating, spec, description, quantity, reorder_level) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory().name());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getEcoRating());
            ps.setString(5, p.getSpec());
            ps.setString(6, p.getDescription());
            ps.setInt(7, p.getQuantity());
            ps.setInt(8, p.getReorderLevel());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                }
            }
        }
    }

    // quantity is not changed here, stock only moves through stock-in and orders
    @Override
    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, eco_rating = ?, spec = ?, "
                + "description = ?, reorder_level = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory().name());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getEcoRating());
            ps.setString(5, p.getSpec());
            ps.setString(6, p.getDescription());
            ps.setInt(7, p.getReorderLevel());
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM products ORDER BY name")) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Product findById(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM products WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Product> findLowStock() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= reorder_level ORDER BY quantity";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public void updateReorderLevel(int productId, int level) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("UPDATE products SET reorder_level = ? WHERE id = ?")) {
            ps.setInt(1, level);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = Product.create(ProductCategory.valueOf(rs.getString("category")));
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setEcoRating(rs.getInt("eco_rating"));
        p.setSpec(rs.getString("spec"));
        p.setDescription(rs.getString("description"));
        p.setQuantity(rs.getInt("quantity"));
        p.setReorderLevel(rs.getInt("reorder_level"));
        return p;
    }
}
