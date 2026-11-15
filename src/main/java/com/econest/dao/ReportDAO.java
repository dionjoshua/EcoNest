package com.econest.dao;

import com.econest.model.ProductCategory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Queries used by the dashboard and the reports screen.
// Cancelled orders are never counted as revenue.
public class ReportDAO {

    private static final String NOT_CANCELLED = "o.status <> 'CANCELLED'";

    public double revenueForMonth(YearMonth month) throws SQLException {
        String sql = "SELECT COALESCE(SUM(o.total), 0) FROM orders o WHERE o.order_date LIKE ? AND " + NOT_CANCELLED;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, month + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    public int orderCountForMonth(YearMonth month) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders o WHERE o.order_date LIKE ? AND " + NOT_CANCELLED;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, month + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int countOrdersWithStatus(String status) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM orders WHERE status = ?")) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // revenue for the last few months, oldest first (for the dashboard chart)
    public Map<YearMonth, Double> revenueForLastMonths(int months) throws SQLException {
        Map<YearMonth, Double> result = new LinkedHashMap<>();
        YearMonth current = YearMonth.now();
        for (int i = months - 1; i >= 0; i--) {
            YearMonth m = current.minusMonths(i);
            result.put(m, revenueForMonth(m));
        }
        return result;
    }

    // rows of: product name, category label, units sold, revenue
    public List<Object[]> productSalesForMonth(YearMonth month) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT p.name, p.category, SUM(i.quantity) AS units, SUM(i.quantity * i.unit_price) AS revenue "
                + "FROM order_items i JOIN orders o ON o.id = i.order_id JOIN products p ON p.id = i.product_id "
                + "WHERE o.order_date LIKE ? AND " + NOT_CANCELLED + " "
                + "GROUP BY p.id ORDER BY revenue DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, month + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String category = ProductCategory.valueOf(rs.getString("category")).getLabel();
                    rows.add(new Object[]{rs.getString("name"), category,
                            rs.getInt("units"), rs.getDouble("revenue")});
                }
            }
        }
        return rows;
    }
}
