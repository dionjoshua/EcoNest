package com.econest.dao;

import com.econest.model.Client;
import com.econest.model.ClientType;
import com.econest.model.OfficeClient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO implements CrudDAO<Client> {

    @Override
    public void add(Client c) throws SQLException {
        String sql = "INSERT INTO clients (name, type, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            fill(ps, c);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Client c) throws SQLException {
        String sql = "UPDATE clients SET name = ?, type = ?, contact_person = ?, phone = ?, email = ?, address = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            fill(ps, c);
            ps.setInt(7, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM clients WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Client> findAll() throws SQLException {
        List<Client> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM clients ORDER BY name")) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Client findById(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM clients WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private void fill(PreparedStatement ps, Client c) throws SQLException {
        ps.setString(1, c.getName());
        ps.setString(2, c.getType().name());
        // only office clients have a contact person
        if (c instanceof OfficeClient) {
            ps.setString(3, ((OfficeClient) c).getContactPerson());
        } else {
            ps.setString(3, null);
        }
        ps.setString(4, c.getPhone());
        ps.setString(5, c.getEmail());
        ps.setString(6, c.getAddress());
    }

    private Client map(ResultSet rs) throws SQLException {
        Client c = Client.create(ClientType.valueOf(rs.getString("type")));
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        if (c instanceof OfficeClient) {
            ((OfficeClient) c).setContactPerson(rs.getString("contact_person"));
        }
        return c;
    }
}
