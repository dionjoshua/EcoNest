package com.econest.controller;

import com.econest.dao.AgentDAO;
import com.econest.dao.ClientDAO;
import com.econest.dao.EmailLogDAO;
import com.econest.dao.OrderDAO;
import com.econest.model.Client;
import com.econest.model.DeliveryAgent;
import com.econest.model.Order;
import com.econest.model.OrderStatus;
import com.econest.service.EmailMessages;
import com.econest.service.EmailService;
import com.econest.util.Messages;
import com.econest.util.ValidationException;
import com.econest.util.Validator;
import com.econest.view.DeliveryPanel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Requirement 6: assign agents and track the delivery status
// Requirements 8 and 9: the emails are sent from here when the status changes
public class DeliveryController implements ScreenController {
    private final DeliveryPanel view;
    private final OrderDAO orderDAO;
    private final AgentDAO agentDAO;
    private final ClientDAO clientDAO;
    private final EmailLogDAO emailLogDAO;
    private final EmailService emailService;

    public DeliveryController(DeliveryPanel view, OrderDAO orderDAO, AgentDAO agentDAO, ClientDAO clientDAO,
                              EmailLogDAO emailLogDAO, EmailService emailService) {
        this.view = view;
        this.orderDAO = orderDAO;
        this.agentDAO = agentDAO;
        this.clientDAO = clientDAO;
        this.emailLogDAO = emailLogDAO;
        this.emailService = emailService;

        view.getBtnAssign().addActionListener(e -> assignAgent());
        view.getBtnDispatch().addActionListener(e -> markDispatched());
        view.getBtnDelivered().addActionListener(e -> markDelivered());
        view.getChkActiveOnly().addActionListener(e -> refresh());
        view.getTblDeliveries().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                orderSelected();
            }
        });
    }

    @Override
    public void refresh() {
        try {
            Order selected = view.getSelectedOrder();

            // only agents marked as available can be given a delivery
            List<DeliveryAgent> available = new ArrayList<>();
            for (DeliveryAgent agent : agentDAO.findAll()) {
                if (agent.isAvailable()) {
                    available.add(agent);
                }
            }
            view.setAgents(available);

            boolean activeOnly = view.getChkActiveOnly().isSelected();
            List<Order> shown = new ArrayList<>();
            for (Order order : orderDAO.findAll()) {
                boolean finished = order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED;
                if (!activeOnly || !finished) {
                    shown.add(order);
                }
            }
            view.showOrders(shown);
            view.showEmailLog(emailLogDAO.findRecent(30));
            if (selected != null) {
                view.selectOrder(selected.getId());
            } else {
                view.showSelected(null);
            }
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the deliveries.", e);
        }
    }

    private void orderSelected() {
        Order order = view.getSelectedOrder();
        view.showSelected(order);
        // show the agent that is already on the order
        if (order != null && order.getAgentId() != null) {
            for (int i = 0; i < view.getCmbAgent().getItemCount(); i++) {
                if (((DeliveryAgent) view.getCmbAgent().getItemAt(i)).getId() == order.getAgentId()) {
                    view.getCmbAgent().setSelectedIndex(i);
                }
            }
        }
    }

    private void assignAgent() {
        Order selected = view.getSelectedOrder();
        DeliveryAgent agent = view.getSelectedAgent();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select an order from the table first.");
            return;
        }
        if (agent == null) {
            Messages.warn(view.getRootPanel(), "There is no available delivery agent. Add one on the Delivery Agents screen.");
            return;
        }
        try {
            LocalDate date = Validator.futureDate(view.getTxtDeliveryDate().getText(), "Delivery date");
            orderDAO.assignAgent(selected.getId(), agent.getId(), date);

            // requirement 9: let the agent know
            Order order = orderDAO.findById(selected.getId());
            Client client = clientDAO.findById(order.getClientId());
            view.setEmailStatus("Sending email to " + agent.getName() + "...", false);
            emailService.sendInBackground(agent, EmailMessages.assignmentSubject(order),
                    EmailMessages.assignmentBody(order, client, agent), this::emailFinished);
            refresh();
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not assign the agent.", e);
        }
    }

    private void markDispatched() {
        Order selected = view.getSelectedOrder();
        if (selected == null || selected.getStatus() != OrderStatus.ASSIGNED) {
            Messages.warn(view.getRootPanel(), "Select an order that has an agent assigned.");
            return;
        }
        try {
            orderDAO.updateStatus(selected.getId(), OrderStatus.DISPATCHED);

            // requirement 8: let the client know
            Order order = orderDAO.findById(selected.getId());
            Client client = clientDAO.findById(order.getClientId());
            DeliveryAgent agent = agentDAO.findById(order.getAgentId());
            view.setEmailStatus("Sending email to " + client.getName() + "...", false);
            emailService.sendInBackground(client, EmailMessages.dispatchSubject(order),
                    EmailMessages.dispatchBody(order, client, agent), this::emailFinished);
            refresh();
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not update the order.", e);
        }
    }

    private void markDelivered() {
        Order selected = view.getSelectedOrder();
        if (selected == null || selected.getStatus() != OrderStatus.DISPATCHED) {
            Messages.warn(view.getRootPanel(), "Select an order that has been dispatched.");
            return;
        }
        try {
            orderDAO.updateStatus(selected.getId(), OrderStatus.DELIVERED);
            view.setEmailStatus(selected.getOrderNo() + " marked as delivered.", false);
            refresh();
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not update the order.", e);
        }
    }

    // runs when the background email is done
    private void emailFinished(boolean sent, String message) {
        view.setEmailStatus(message, !sent);
        try {
            view.showEmailLog(emailLogDAO.findRecent(30));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
