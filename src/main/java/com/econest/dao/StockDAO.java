package com.econest.dao;

import com.econest.model.StockEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    // Saves the stock-in record and increases the quantity on hand together,
    // so we never end up with one without the other.
    public void receiveStock(StockEntry entry) throws SQLException {
        Connection con = DBConnection.getConnection();
        // Transaction: turn off auto-commit so nothing is saved until we call commit().
        // If any step fails, rollback() cancels all the steps together.
        con.setAutoCommit(false);
        try {
            String insert = "INSERT INTO stock_in (product_id, quantity, supplier, received_date, note) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insert)) {
                ps.setInt(1, entry.getProductId());
                ps.setInt(2, entry.getQuantity());
                ps.setString(3, entry.getSupplier());
                ps.setString(4, entry.getReceivedDate().toString());
                ps.setString(5, entry.getNote());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("UPDATE products SET quantity = quantity + ? WHERE id = ?")) {
                ps.setInt(1, entry.getQuantity());
                ps.setInt(2, entry.getProductId());
                ps.executeUpdate();
            }
            con.commit();           // everything worked -> save all the changes
        } catch (SQLException e) {
            con.rollback();         // something failed -> undo all the changes from this method
            throw e;                // and pass the error on so the screen can show a message
        } finally {
            con.setAutoCommit(true);    // always switch back to normal mode, even after an error
        }
    }

    public List<StockEntry> findRecent(int limit) throws SQLException {
        List<StockEntry> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name AS product_name FROM stock_in s "
                + "JOIN products p ON p.id = s.product_id ORDER BY s.received_date DESC, s.id DESC LIMIT " + limit;
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                StockEntry e = new StockEntry();
                e.setId(rs.getInt("id"));
                e.setProductId(rs.getInt("product_id"));
                e.setProductName(rs.getString("product_name"));
                e.setQuantity(rs.getInt("quantity"));
                e.setSupplier(rs.getString("supplier"));
                e.setReceivedDate(LocalDate.parse(rs.getString("received_date")));
                e.setNote(rs.getString("note"));
                list.add(e);
            }
        }
        return list;
    }
}
