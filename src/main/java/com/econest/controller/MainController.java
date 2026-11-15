package com.econest.controller;

import com.econest.dao.AgentDAO;
import com.econest.dao.ClientDAO;
import com.econest.dao.EmailLogDAO;
import com.econest.dao.OrderDAO;
import com.econest.dao.ProductDAO;
import com.econest.dao.ReportDAO;
import com.econest.dao.StockDAO;
import com.econest.service.EmailService;
import com.econest.view.AgentPanel;
import com.econest.view.ClientPanel;
import com.econest.view.DashboardPanel;
import com.econest.view.DeliveryPanel;
import com.econest.view.InventoryPanel;
import com.econest.view.MainFrame;
import com.econest.view.OrderPanel;
import com.econest.view.ProductPanel;
import com.econest.view.ReportPanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

// Creates every screen with its controller and handles the menu on the left
public class MainController {
    private final MainFrame frame;
    private final Map<String, ScreenController> controllers = new HashMap<>();

    public MainController(MainFrame frame) {
        this.frame = frame;

        ProductDAO productDAO = new ProductDAO();
        ClientDAO clientDAO = new ClientDAO();
        AgentDAO agentDAO = new AgentDAO();
        StockDAO stockDAO = new StockDAO();
        OrderDAO orderDAO = new OrderDAO();
        ReportDAO reportDAO = new ReportDAO();
        EmailLogDAO emailLogDAO = new EmailLogDAO();
        EmailService emailService = new EmailService();

        DashboardPanel dashboard = new DashboardPanel();
        ProductPanel products = new ProductPanel();
        ClientPanel clients = new ClientPanel();
        InventoryPanel inventory = new InventoryPanel();
        AgentPanel agents = new AgentPanel();
        OrderPanel orders = new OrderPanel();
        DeliveryPanel deliveries = new DeliveryPanel();
        ReportPanel reports = new ReportPanel();

        register("dashboard", dashboard.getRootPanel(), new DashboardController(dashboard, reportDAO, productDAO));
        register("products", products.getRootPanel(), new ProductController(products, productDAO));
        register("clients", clients.getRootPanel(), new ClientController(clients, clientDAO));
        register("inventory", inventory.getRootPanel(), new InventoryController(inventory, productDAO, stockDAO));
        register("agents", agents.getRootPanel(), new AgentController(agents, agentDAO));
        register("orders", orders.getRootPanel(), new OrderController(orders, clientDAO, productDAO, orderDAO));
        register("deliveries", deliveries.getRootPanel(),
                new DeliveryController(deliveries, orderDAO, agentDAO, clientDAO, emailLogDAO, emailService));
        register("reports", reports.getRootPanel(), new ReportController(reports, reportDAO, orderDAO, productDAO));

        // connect each menu button on the left to its screen
        // (the key is the screen name, the value is the button)
        for (Map.Entry<String, JButton> entry : frame.getNavButtons().entrySet()) {
            String screen = entry.getKey();
            entry.getValue().addActionListener(e -> open(screen));
        }
    }

    private void register(String name, JPanel screen, ScreenController controller) {
        frame.addScreen(name, screen);
        controllers.put(name, controller);
    }

    // data is reloaded each time a screen is opened so it is always up to date
    public void open(String name) {
        controllers.get(name).refresh();
        frame.showScreen(name);
    }
}
