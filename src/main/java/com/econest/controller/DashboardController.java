package com.econest.controller;

import com.econest.dao.ProductDAO;
import com.econest.dao.ReportDAO;
import com.econest.model.Product;
import com.econest.util.Messages;
import com.econest.view.DashboardPanel;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardController implements ScreenController {
    private final DashboardPanel view;
    private final ReportDAO reportDAO;
    private final ProductDAO productDAO;

    public DashboardController(DashboardPanel view, ReportDAO reportDAO, ProductDAO productDAO) {
        this.view = view;
        this.reportDAO = reportDAO;
        this.productDAO = productDAO;
        view.getBtnRefresh().addActionListener(e -> refresh());
    }

    @Override
    public void refresh() {
        try {
            YearMonth thisMonth = YearMonth.now();
            List<Product> lowStock = productDAO.findLowStock();
            int waiting = reportDAO.countOrdersWithStatus("PENDING") + reportDAO.countOrdersWithStatus("ASSIGNED");

            view.setStats(reportDAO.revenueForMonth(thisMonth), reportDAO.orderCountForMonth(thisMonth),
                    waiting, lowStock.size(), reportDAO.countOrdersWithStatus("DISPATCHED"));
            view.showLowStock(lowStock);

            List<String> months = new ArrayList<>();
            List<Double> revenue = new ArrayList<>();
            for (Map.Entry<YearMonth, Double> entry : reportDAO.revenueForLastMonths(6).entrySet()) {
                months.add(entry.getKey().format(DateTimeFormatter.ofPattern("MMM")));
                revenue.add(entry.getValue());
            }
            view.setChartData(months, revenue);
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the dashboard.", e);
        }
    }
}
