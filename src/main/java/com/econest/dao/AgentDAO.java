package com.econest.dao;

import com.econest.model.DeliveryAgent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AgentDAO implements CrudDAO<DeliveryAgent> {

    @Override
    public void add(DeliveryAgent a) throws SQLException {
        String sql = "INSERT INTO delivery_agents (name, phone, email, vehicle_type, vehicle_number, available) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            fill(ps, a);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(DeliveryAgent a) throws SQLException {
        String sql = "UPDATE delivery_agents SET name = ?, phone = ?, email = ?, vehicle_type = ?, vehicle_number = ?, available = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            fill(ps, a);
            ps.setInt(7, a.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM delivery_agents WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<DeliveryAgent> findAll() throws SQLException {
        List<DeliveryAgent> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM delivery_agents ORDER BY name")) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public DeliveryAgent findById(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM delivery_agents WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private void fill(PreparedStatement ps, DeliveryAgent a) throws SQLException {
        ps.setString(1, a.getName());
        ps.setString(2, a.getPhone());
        ps.setString(3, a.getEmail());
        ps.setString(4, a.getVehicleType());
        ps.setString(5, a.getVehicleNumber());
        ps.setInt(6, a.isAvailable() ? 1 : 0);
    }

    private DeliveryAgent map(ResultSet rs) throws SQLException {
        DeliveryAgent a = new DeliveryAgent();
        a.setId(rs.getInt("id"));
        a.setName(rs.getString("name"));
        a.setPhone(rs.getString("phone"));
        a.setEmail(rs.getString("email"));
        a.setVehicleType(rs.getString("vehicle_type"));
        a.setVehicleNumber(rs.getString("vehicle_number"));
        a.setAvailable(rs.getInt("available") == 1);
        return a;
    }
}
