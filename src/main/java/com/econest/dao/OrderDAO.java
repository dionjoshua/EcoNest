package com.econest.dao;

import com.econest.model.Order;
import com.econest.model.OrderItem;
import com.econest.model.OrderStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private static final String SELECT = "SELECT o.*, c.name AS client_name, a.name AS agent_name FROM orders o "
            + "JOIN clients c ON c.id = o.client_id "
            + "LEFT JOIN delivery_agents a ON a.id = o.agent_id ";

    // Saves the order + its items and takes the items out of stock in one transaction.
    public void create(Order order) throws SQLException {
        Connection con = DBConnection.getConnection();
        // Transaction: turn off auto-commit so nothing is saved until we call commit().
        // If any step fails, rollback() cancels all the steps together.
        con.setAutoCommit(false);
        try {
            String sql = "INSERT INTO orders (client_id, order_date, status, delivery_address, total) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getClientId());
                ps.setString(2, order.getOrderDate().toString());
                ps.setString(3, order.getStatus().name());
                ps.setString(4, order.getDeliveryAddress());
                ps.setDouble(5, order.calculateTotal());
                ps.executeUpdate();
                // get the id that SQLite gave the new order (needed for the order items)
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    order.setId(keys.getInt(1));
                }
            }

            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            String stockSql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
            for (OrderItem item : order.getItems()) {
                try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                    ps.setInt(1, order.getId());
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getUnitPrice());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    // 0 rows changed means the "quantity >= ?" check failed,
                    // so we throw an error and the catch block below rolls the order back
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Not enough stock for " + item.getProductName());
                    }
                }
            }
            con.commit();
            order.setTotal(order.calculateTotal());
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public List<Order> findAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(SELECT + "ORDER BY o.id DESC")) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Order> findByMonth(int year, int month) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = SELECT + "WHERE o.order_date LIKE ? ORDER BY o.order_date";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, String.format("%04d-%02d-%%", year, month));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    // order with its items loaded
    public Order findById(int id) throws SQLException {
        Order order = null;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(SELECT + "WHERE o.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = map(rs);
                }
            }
        }
        if (order == null) {
            return null;
        }
        String sql = "SELECT i.*, p.name AS product_name FROM order_items i "
                + "JOIN products p ON p.id = i.product_id WHERE i.order_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    order.getItems().add(item);
                }
            }
        }
        return order;
    }

    public void assignAgent(int orderId, int agentId, LocalDate deliveryDate) throws SQLException {
        String sql = "UPDATE orders SET agent_id = ?, delivery_date = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, agentId);
            ps.setString(2, deliveryDate.toString());
            ps.setString(3, OrderStatus.ASSIGNED.name());
            ps.setInt(4, orderId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(int orderId, OrderStatus status) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("UPDATE orders SET status = ? WHERE id = ?")) {
            ps.setString(1, status.name());
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    // Cancelling puts the items back into stock
    public void cancel(int orderId) throws SQLException {
        Connection con = DBConnection.getConnection();
        // Transaction: turn off auto-commit so nothing is saved until we call commit().
        // If any step fails, rollback() cancels all the steps together.
        con.setAutoCommit(false);
        try {
            String restore = "UPDATE products SET quantity = quantity + "
                    + "(SELECT quantity FROM order_items WHERE order_id = ? AND product_id = products.id) "
                    + "WHERE id IN (SELECT product_id FROM order_items WHERE order_id = ?)";
            try (PreparedStatement ps = con.prepareStatement(restore)) {
                ps.setInt(1, orderId);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET status = ? WHERE id = ?")) {
                ps.setString(1, OrderStatus.CANCELLED.name());
                ps.setInt(2, orderId);
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

    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setClientId(rs.getInt("client_id"));
        o.setClientName(rs.getString("client_name"));
        o.setOrderDate(LocalDate.parse(rs.getString("order_date")));
        o.setStatus(OrderStatus.valueOf(rs.getString("status")));
        o.setDeliveryAddress(rs.getString("delivery_address"));
        o.setTotal(rs.getDouble("total"));
        int agentId = rs.getInt("agent_id");
        if (!rs.wasNull()) {
            o.setAgentId(agentId);
            o.setAgentName(rs.getString("agent_name"));
        }
        String deliveryDate = rs.getString("delivery_date");
        if (deliveryDate != null) {
            o.setDeliveryDate(LocalDate.parse(deliveryDate));
        }
        return o;
    }
}
