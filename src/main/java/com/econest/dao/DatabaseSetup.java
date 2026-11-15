package com.econest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

// Creates the tables the first time the program runs and adds some sample data
// so the screens are not empty. Delete econest.db to start again from scratch.
public class DatabaseSetup {

    public static void initialise() throws SQLException {
        Connection con = DBConnection.getConnection();
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS products ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "category TEXT NOT NULL, "
                    + "price REAL NOT NULL, "
                    + "eco_rating INTEGER NOT NULL, "
                    + "spec TEXT, "
                    + "description TEXT, "
                    + "quantity INTEGER NOT NULL DEFAULT 0, "
                    + "reorder_level INTEGER NOT NULL DEFAULT 5)");

            st.execute("CREATE TABLE IF NOT EXISTS clients ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "type TEXT NOT NULL, "
                    + "contact_person TEXT, "
                    + "phone TEXT NOT NULL, "
                    + "email TEXT NOT NULL, "
                    + "address TEXT NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS delivery_agents ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "phone TEXT NOT NULL, "
                    + "email TEXT NOT NULL, "
                    + "vehicle_type TEXT NOT NULL, "
                    + "vehicle_number TEXT NOT NULL, "
                    + "available INTEGER NOT NULL DEFAULT 1)");

            st.execute("CREATE TABLE IF NOT EXISTS stock_in ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "product_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "supplier TEXT NOT NULL, "
                    + "received_date TEXT NOT NULL, "
                    + "note TEXT, "
                    + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS orders ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "client_id INTEGER NOT NULL, "
                    + "order_date TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "delivery_address TEXT, "
                    + "total REAL NOT NULL, "
                    + "agent_id INTEGER, "
                    + "delivery_date TEXT, "
                    + "FOREIGN KEY (client_id) REFERENCES clients(id), "
                    + "FOREIGN KEY (agent_id) REFERENCES delivery_agents(id))");

            st.execute("CREATE TABLE IF NOT EXISTS order_items ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "order_id INTEGER NOT NULL, "
                    + "product_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "unit_price REAL NOT NULL, "
                    + "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (product_id) REFERENCES products(id))");

            st.execute("CREATE TABLE IF NOT EXISTS email_log ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "sent_at TEXT NOT NULL, "
                    + "recipient TEXT NOT NULL, "
                    + "subject TEXT NOT NULL, "
                    + "status TEXT NOT NULL)");

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertSampleData(con);
                }
            }
        }
    }

    private static void insertSampleData(Connection con) throws SQLException {
        // all the sample rows are added in one transaction (faster, and all-or-nothing)
        con.setAutoCommit(false);
        try {
            // name, category, price, eco rating, spec, description, quantity, reorder level
            product(con, "Snake Plant", "PLANT", 1850, 5, "Low light", "Hardy air purifying plant, 6 inch pot", 34, 10);
            product(con, "Peace Lily", "PLANT", 2200, 4, "Medium, indirect", "Flowering indoor plant", 18, 8);
            product(con, "Money Plant (Pothos)", "PLANT", 950, 5, "Low to medium", "Trailing plant, good for desks", 6, 12);
            product(con, "ZZ Plant", "PLANT", 3400, 4, "Low light", "Needs watering only twice a month", 11, 5);
            product(con, "Areca Palm", "PLANT", 4500, 4, "Bright, indirect", "Large floor plant for offices", 4, 6);
            product(con, "Aloe Vera", "PLANT", 780, 5, "Bright light", "Small succulent", 40, 10);
            product(con, "Coir Fibre Pot 8 inch", "PLANTER", 450, 5, "Coconut coir", "Biodegradable pot made locally", 60, 20);
            product(con, "Terracotta Planter Medium", "PLANTER", 1250, 4, "Clay", "Hand made, with drainage plate", 22, 10);
            product(con, "Recycled Plastic Planter", "PLANTER", 890, 3, "Recycled plastic", "Lightweight, 10 inch", 3, 8);
            product(con, "Bamboo Hanging Planter", "PLANTER", 1650, 5, "Bamboo", "Comes with jute rope", 14, 6);
            product(con, "Balcony Herb Kit", "KIT", 3200, 4, "3 pots, soil, basil/mint/coriander seeds", "Starter kit for small balconies", 9, 5);
            product(con, "Desk Succulent Kit", "KIT", 2750, 4, "4 succulents, tray, pebbles", "Popular with office clients", 0, 4);
            product(con, "Kitchen Compost Kit", "KIT", 5400, 5, "Bin, compost starter, guide", "Turns kitchen waste into compost", 7, 3);

            // name, type, contact person, phone, email, address
            client(con, "Nimali Perera", "HOUSEHOLD", null, "0771234567", "nimali.perera@example.com", "24/1 Temple Road, Nugegoda");
            client(con, "Kasun Fernando", "HOUSEHOLD", null, "0719876543", "kasun.f@example.com", "112 Lake Drive, Rajagiriya");
            client(con, "Tharushi Jayasinghe", "HOUSEHOLD", null, "0764455667", "tharushi.j@example.com", "8 Hill Street, Kandy");
            client(con, "BlueWave Software (Pvt) Ltd", "OFFICE", "Dilshan Silva", "0112345678", "admin@bluewave.example.com", "Level 4, 56 Galle Road, Colombo 03");
            client(con, "Serendib Co-Working", "OFFICE", "Amaya Wijesekara", "0114567890", "hello@serendibcowork.example.com", "17 Park Street, Colombo 02");
            client(con, "Green Leaf Cafe", "OFFICE", "Ruwan Bandara", "0812233445", "greenleaf@example.com", "3 Peradeniya Road, Kandy");

            agent(con, "Chaminda Rathnayake", "0755512345", "chaminda.r@example.com", "Van", "PH-4521", 1);
            agent(con, "Sanduni Karunaratne", "0723344556", "sanduni.k@example.com", "Three-wheeler", "ABK-7788", 1);
            agent(con, "Mohamed Rizwan", "0778899001", "rizwan.m@example.com", "Motorbike", "BFG-2040", 1);
            agent(con, "Pradeep Kumara", "0701122334", "pradeep.k@example.com", "Lorry", "LK-9034", 0);

            LocalDate today = LocalDate.now();
            stockIn(con, 1, 30, "Lanka Nurseries", today.minusDays(40), "Opening stock");
            stockIn(con, 7, 60, "CocoGreen Exports", today.minusDays(33), "");
            stockIn(con, 11, 12, "UrbanGrow Supplies", today.minusDays(20), "Second batch");
            stockIn(con, 6, 25, "Lanka Nurseries", today.minusDays(6), "");

            // some past orders so the reports have something to show
            // client, days ago, status, agent (0 = none), {product, qty} pairs
            order(con, 4, 150, "DELIVERED", 1, 5, 4, 8, 4);
            order(con, 1, 141, "DELIVERED", 2, 1, 2, 7, 2);
            order(con, 5, 122, "DELIVERED", 1, 12, 6, 2, 3);
            order(con, 2, 110, "DELIVERED", 3, 11, 1);
            order(con, 6, 95, "DELIVERED", 1, 2, 4, 10, 4, 6, 6);
            order(con, 3, 88, "DELIVERED", 2, 13, 1, 3, 2);
            order(con, 4, 70, "DELIVERED", 1, 4, 5, 8, 5);
            order(con, 1, 63, "CANCELLED", 0, 9, 2);
            order(con, 5, 52, "DELIVERED", 1, 5, 2, 12, 8);
            order(con, 2, 45, "DELIVERED", 3, 6, 3, 7, 3);
            order(con, 6, 31, "DELIVERED", 2, 11, 2, 1, 3);
            order(con, 3, 24, "DELIVERED", 3, 2, 1, 8, 1);
            order(con, 4, 12, "DELIVERED", 1, 1, 6, 4, 2, 10, 3);
            order(con, 1, 5, "DISPATCHED", 2, 13, 1);
            order(con, 5, 3, "ASSIGNED", 1, 5, 1, 2, 2);
            order(con, 2, 1, "PENDING", 0, 3, 2, 7, 2);
            order(con, 6, 0, "PENDING", 0, 11, 1);
            con.commit();           // everything worked -> save all the changes
        } catch (SQLException e) {
            con.rollback();         // something failed -> undo all the changes from this method
            throw e;                // and pass the error on so the screen can show a message
        } finally {
            con.setAutoCommit(true);    // always switch back to normal mode, even after an error
        }
    }

    private static void product(Connection con, String name, String category, double price, int eco,
                                String spec, String description, int qty, int reorder) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, eco_rating, spec, description, quantity, reorder_level) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setInt(4, eco);
            ps.setString(5, spec);
            ps.setString(6, description);
            ps.setInt(7, qty);
            ps.setInt(8, reorder);
            ps.executeUpdate();
        }
    }

    private static void client(Connection con, String name, String type, String contact, String phone,
                               String email, String address) throws SQLException {
        String sql = "INSERT INTO clients (name, type, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, contact);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, address);
            ps.executeUpdate();
        }
    }

    private static void agent(Connection con, String name, String phone, String email, String vehicleType,
                              String vehicleNo, int available) throws SQLException {
        String sql = "INSERT INTO delivery_agents (name, phone, email, vehicle_type, vehicle_number, available) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setString(4, vehicleType);
            ps.setString(5, vehicleNo);
            ps.setInt(6, available);
            ps.executeUpdate();
        }
    }

    private static void stockIn(Connection con, int productId, int qty, String supplier, LocalDate date,
                                String note) throws SQLException {
        String sql = "INSERT INTO stock_in (product_id, quantity, supplier, received_date, note) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, qty);
            ps.setString(3, supplier);
            ps.setString(4, date.toString());
            ps.setString(5, note);
            ps.executeUpdate();
        }
    }

    // productAndQty = productId, quantity, productId, quantity ...
    private static void order(Connection con, int clientId, int daysAgo, String status, int agentId,
                              int... productAndQty) throws SQLException {
        LocalDate date = LocalDate.now().minusDays(daysAgo);
        int orderId;
        String sql = "INSERT INTO orders (client_id, order_date, status, delivery_address, total, agent_id, delivery_date) "
                + "VALUES (?, ?, ?, (SELECT address FROM clients WHERE id = ?), 0, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, clientId);
            ps.setString(2, date.toString());
            ps.setString(3, status);
            ps.setInt(4, clientId);
            if (agentId == 0) {
                ps.setNull(5, java.sql.Types.INTEGER);
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setInt(5, agentId);
                ps.setString(6, date.plusDays(2).toString());
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                orderId = keys.getInt(1);
            }
        }
        for (int i = 0; i < productAndQty.length; i += 2) {
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) "
                    + "VALUES (?, ?, ?, (SELECT price FROM products WHERE id = ?))";
            try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, productAndQty[i]);
                ps.setInt(3, productAndQty[i + 1]);
                ps.setInt(4, productAndQty[i]);
                ps.executeUpdate();
            }
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET total = "
                + "(SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = ?) WHERE id = ?")) {
            ps.setInt(1, orderId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }
}
