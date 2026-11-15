package com.econest.service;

import com.econest.model.Client;
import com.econest.model.DeliveryAgent;
import com.econest.model.Order;
import com.econest.model.OrderItem;

// Builds the text of the two notification emails
public class EmailMessages {

    private EmailMessages() {
    }

    // Requirement 8: tell the client their order is on the way
    public static String dispatchSubject(Order order) {
        return "Your EcoNest order " + order.getOrderNo() + " has been dispatched";
    }

    public static String dispatchBody(Order order, Client client, DeliveryAgent agent) {
        StringBuilder html = new StringBuilder(header());
        html.append("<p>Dear ").append(esc(client.getContactName())).append(",</p>");
        html.append("<p>Good news! Your order <b>").append(order.getOrderNo())
                .append("</b> has left our store and is on its way to you.</p>");
        html.append(itemTable(order));
        html.append("<p><b>Delivery address:</b> ").append(esc(order.getDeliveryAddress())).append("<br>");
        if (order.getDeliveryDate() != null) {
            html.append("<b>Expected delivery:</b> ").append(order.getDeliveryDate()).append("<br>");
        }
        if (agent != null) {
            html.append("<b>Delivered by:</b> ").append(esc(agent.getName())).append(" (").append(esc(agent.getPhone()))
                    .append("), ").append(esc(agent.getVehicleType())).append(" ").append(esc(agent.getVehicleNumber()));
        }
        html.append("</p><p>Thank you for choosing sustainable products.</p>");
        return html.append(footer()).toString();
    }

    // Requirement 9: tell the delivery agent about a new delivery
    public static String assignmentSubject(Order order) {
        return "New delivery assigned: " + order.getOrderNo();
    }

    public static String assignmentBody(Order order, Client client, DeliveryAgent agent) {
        StringBuilder html = new StringBuilder(header());
        html.append("<p>Hi ").append(esc(agent.getName())).append(",</p>");
        html.append("<p>A new delivery has been assigned to you.</p>");
        html.append("<p><b>Order:</b> ").append(order.getOrderNo()).append("<br>");
        html.append("<b>Delivery date:</b> ").append(order.getDeliveryDate()).append("<br>");
        html.append("<b>Client:</b> ").append(esc(client.getName())).append("<br>");
        html.append("<b>Contact:</b> ").append(esc(client.getContactName())).append(", ").append(esc(client.getPhone())).append("<br>");
        html.append("<b>Address:</b> ").append(esc(order.getDeliveryAddress())).append("</p>");
        html.append(itemTable(order));
        html.append("<p>Please collect the items from the store before leaving.</p>");
        return html.append(footer()).toString();
    }

    private static String itemTable(Order order) {
        StringBuilder html = new StringBuilder();
        html.append("<table cellpadding='6' cellspacing='0' style='border-collapse:collapse;font-size:14px'>");
        html.append("<tr style='background:#eef3ee'><th align='left'>Item</th><th align='right'>Qty</th><th align='right'>Amount (Rs.)</th></tr>");
        for (OrderItem item : order.getItems()) {
            html.append("<tr><td>").append(esc(item.getProductName())).append("</td><td align='right'>")
                    .append(item.getQuantity()).append("</td><td align='right'>")
                    .append(String.format("%,.2f", item.getSubtotal())).append("</td></tr>");
        }
        html.append("<tr><td colspan='2' align='right'><b>Total</b></td><td align='right'><b>")
                .append(String.format("%,.2f", order.getTotal())).append("</b></td></tr></table>");
        return html.toString();
    }

    private static String header() {
        return "<div style='font-family:Arial,sans-serif;font-size:14px;color:#22302a'>"
                + "<h2 style='color:#2e6b4e;margin-bottom:4px'>EcoNest</h2>";
    }

    private static String footer() {
        return "<p style='color:#7b867f;font-size:12px'>EcoNest Home &amp; Garden<br>"
                + "This is an automatic message from the EcoNest order system.</p></div>";
    }

    // stops characters like < or & in a name from breaking the html
    private static String esc(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
