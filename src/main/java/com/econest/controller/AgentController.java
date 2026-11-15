package com.econest.controller;

import com.econest.dao.AgentDAO;
import com.econest.model.DeliveryAgent;
import com.econest.util.Messages;
import com.econest.util.SearchListener;
import com.econest.util.ValidationException;
import com.econest.util.Validator;
import com.econest.view.AgentPanel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Requirement 4: manage delivery agents and their vehicles
public class AgentController implements ScreenController {
    private final AgentPanel view;
    private final AgentDAO agentDAO;
    private List<DeliveryAgent> allAgents = new ArrayList<>();

    public AgentController(AgentPanel view, AgentDAO agentDAO) {
        this.view = view;
        this.agentDAO = agentDAO;

        view.getBtnAdd().addActionListener(e -> addAgent());
        view.getBtnUpdate().addActionListener(e -> updateAgent());
        view.getBtnRemove().addActionListener(e -> removeAgent());
        view.getBtnClear().addActionListener(e -> view.clearForm());
        view.getTxtSearch().getDocument().addDocumentListener(new SearchListener(this::applyFilter));
        view.getTblAgents().getSelectionModel().addListSelectionListener(e -> {
            DeliveryAgent selected = view.getSelectedAgent();
            if (!e.getValueIsAdjusting() && selected != null) {
                view.fillForm(selected);
            }
        });
    }

    @Override
    public void refresh() {
        try {
            allAgents = agentDAO.findAll();
            applyFilter();
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the delivery agents.", e);
        }
    }

    private void applyFilter() {
        String search = view.getTxtSearch().getText().trim().toLowerCase();
        List<DeliveryAgent> shown = new ArrayList<>();
        for (DeliveryAgent a : allAgents) {
            if (a.getName().toLowerCase().contains(search) || a.getVehicleNumber().toLowerCase().contains(search)
                    || a.getVehicleType().toLowerCase().contains(search)) {
                shown.add(a);
            }
        }
        view.showAgents(shown);
    }

    private void addAgent() {
        try {
            DeliveryAgent agent = readForm();
            agentDAO.add(agent);
            view.clearForm();
            refresh();
            Messages.info(view.getRootPanel(), agent.getName() + " was added.");
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not save the agent.", e);
        }
    }

    private void updateAgent() {
        DeliveryAgent selected = view.getSelectedAgent();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select an agent from the table first.");
            return;
        }
        try {
            DeliveryAgent agent = readForm();
            agent.setId(selected.getId());
            agentDAO.update(agent);
            view.clearForm();
            refresh();
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not update the agent.", e);
        }
    }

    private void removeAgent() {
        DeliveryAgent selected = view.getSelectedAgent();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select an agent from the table first.");
            return;
        }
        if (!Messages.confirm(view.getRootPanel(), "Remove " + selected.getName() + "?")) {
            return;
        }
        try {
            agentDAO.delete(selected.getId());
            view.clearForm();
            refresh();
        } catch (SQLException e) {
            Messages.warn(view.getRootPanel(), selected.getName() + " has deliveries in the system, so they cannot be removed.\n"
                    + "Untick \"Available for deliveries\" instead.");
        }
    }

    private DeliveryAgent readForm() throws ValidationException {
        DeliveryAgent agent = new DeliveryAgent();
        agent.setName(Validator.required(view.getTxtName().getText(), "Name"));
        agent.setPhone(Validator.phone(view.getTxtPhone().getText()));
        agent.setEmail(Validator.email(view.getTxtEmail().getText()));
        agent.setVehicleType((String) view.getCmbVehicleType().getSelectedItem());
        agent.setVehicleNumber(Validator.required(view.getTxtVehicleNumber().getText(), "Vehicle number").toUpperCase());
        agent.setAvailable(view.getChkAvailable().isSelected());
        return agent;
    }
}
