package com.econest.controller;

import com.econest.dao.OrderDAO;
import com.econest.dao.ProductDAO;
import com.econest.dao.ReportDAO;
import com.econest.model.Order;
import com.econest.model.OrderStatus;
import com.econest.model.Product;
import com.econest.util.Messages;
import com.econest.view.ReportPanel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Requirement 7: monthly sales report and inventory report with low-stock alerts
public class ReportController implements ScreenController {
    private final ReportPanel view;
    private final ReportDAO reportDAO;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    // what is on screen now, kept so it can be written to a file
    private YearMonth month;
    private double revenue;
    private int orderCount;
    private List<Object[]> productSales = new ArrayList<>();
    private List<Order> monthOrders = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    public ReportController(ReportPanel view, ReportDAO reportDAO, OrderDAO orderDAO, ProductDAO productDAO) {
        this.view = view;
        this.reportDAO = reportDAO;
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        view.getBtnGenerate().addActionListener(e -> refresh());
        view.getBtnExport().addActionListener(e -> export());
    }

    @Override
    public void refresh() {
        try {
            month = YearMonth.of(view.getSelectedYear(), view.getSelectedMonth());
            revenue = reportDAO.revenueForMonth(month);
            orderCount = reportDAO.orderCountForMonth(month);
            productSales = reportDAO.productSalesForMonth(month);
            monthOrders = orderDAO.findByMonth(month.getYear(), month.getMonthValue());
            products = productDAO.findAll();

            view.showSalesSummary(revenue, orderCount);
            view.showProductSales(productSales);
            view.showMonthOrders(monthOrders);
            view.showInventory(products);
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not generate the report.", e);
        }
    }

    // Writes both reports to a text file in the "reports" folder
    private void export() {
        refresh();
        File folder = new File("reports");
        folder.mkdirs();
        File file = new File(folder, "EcoNest-report-" + month + ".txt");
        String line = "--------------------------------------------------------------------------------";

        // try-with-resources closes the file for us when the block ends
        try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
            out.println("ECONEST - MONTHLY SALES REPORT");
            out.println(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")) + "   (generated " + LocalDate.now() + ")");
            out.println(line);
            out.printf("Revenue:              Rs. %,.2f%n", revenue);
            out.printf("Orders:               %d%n", orderCount);
            out.printf("Average order value:  Rs. %,.2f%n", orderCount == 0 ? 0 : revenue / orderCount);
            out.println();
            out.println("SALES BY PRODUCT");
            out.printf("%-34s %-14s %8s %16s%n", "Product", "Category", "Units", "Revenue (Rs.)");
            for (Object[] row : productSales) {
                out.printf("%-34s %-14s %8d %,16.2f%n", row[0], row[1], (Integer) row[2], (Double) row[3]);
            }
            out.println();
            out.println("ORDERS");
            out.printf("%-10s %-12s %-32s %-11s %14s%n", "Order no", "Date", "Client", "Status", "Total (Rs.)");
            for (Order o : monthOrders) {
                out.printf("%-10s %-12s %-32s %-11s %,14.2f%n", o.getOrderNo(), o.getOrderDate(), o.getClientName(),
                        o.getStatus(), o.getTotal());
            }
            // count the cancelled orders
            int cancelled = 0;
            for (Order o : monthOrders) {
                if (o.getStatus() == OrderStatus.CANCELLED) {
                    cancelled++;
                }
            }
            if (cancelled > 0) {
                out.println("(" + cancelled + " cancelled order(s) are listed but not counted in the revenue)");
            }

            out.println();
            out.println();
            out.println("ECONEST - INVENTORY REPORT");
            out.println("As at " + LocalDate.now());
            out.println(line);
            out.printf("%-34s %-14s %8s %8s  %-12s%n", "Product", "Category", "On hand", "Reorder", "Status");
            double value = 0;
            for (Product p : products) {
                value += p.getQuantity() * p.getPrice();
                out.printf("%-34s %-14s %8d %8d  %-12s%n", p.getName(), p.getCategory(), p.getQuantity(),
                        p.getReorderLevel(), p.getStockStatus());
            }
            out.printf("%nTotal stock value: Rs. %,.2f%n", value);
            out.println();
            out.println("LOW STOCK ALERTS");
            boolean any = false;
            for (Product p : products) {
                if (p.isLowStock()) {
                    any = true;
                    out.printf("  ! %-34s %d left (reorder level %d)%n", p.getName(), p.getQuantity(), p.getReorderLevel());
                }
            }
            if (!any) {
                out.println("  None. All products are above their reorder level.");
            }
        } catch (IOException e) {
            Messages.error(view.getRootPanel(), "Could not write the report file.", e);
            return;
        }
        Messages.info(view.getRootPanel(), "Report saved to:\n" + file.getAbsolutePath());
    }
}
