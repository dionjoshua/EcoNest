package com.econest.dao;

import com.econest.model.EmailLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmailLogDAO {

    public void save(String recipient, String subject, String status) throws SQLException {
        String sql = "INSERT INTO email_log (sent_at, recipient, subject, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ps.setString(2, recipient);
            ps.setString(3, subject);
            ps.setString(4, status);
            ps.executeUpdate();
        }
    }

    public List<EmailLog> findRecent(int limit) throws SQLException {
        List<EmailLog> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM email_log ORDER BY id DESC LIMIT " + limit)) {
            while (rs.next()) {
                list.add(new EmailLog(rs.getString("sent_at"), rs.getString("recipient"),
                        rs.getString("subject"), rs.getString("status")));
            }
        }
        return list;
    }
}
