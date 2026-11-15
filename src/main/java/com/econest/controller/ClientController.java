package com.econest.controller;

import com.econest.dao.ClientDAO;
import com.econest.model.Client;
import com.econest.model.OfficeClient;
import com.econest.util.Messages;
import com.econest.util.SearchListener;
import com.econest.util.ValidationException;
import com.econest.util.Validator;
import com.econest.view.ClientPanel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Requirement 2: manage household and office clients
public class ClientController implements ScreenController {
    private final ClientPanel view;
    private final ClientDAO clientDAO;
    private List<Client> allClients = new ArrayList<>();

    public ClientController(ClientPanel view, ClientDAO clientDAO) {
        this.view = view;
        this.clientDAO = clientDAO;

        view.getBtnAdd().addActionListener(e -> addClient());
        view.getBtnUpdate().addActionListener(e -> updateClient());
        view.getBtnRemove().addActionListener(e -> removeClient());
        view.getBtnClear().addActionListener(e -> view.clearForm());
        view.getTxtSearch().getDocument().addDocumentListener(new SearchListener(this::applyFilter));
        view.getTblClients().getSelectionModel().addListSelectionListener(e -> {
            Client selected = view.getSelectedClient();
            if (!e.getValueIsAdjusting() && selected != null) {
                view.fillForm(selected);
            }
        });
    }

    @Override
    public void refresh() {
        try {
            allClients = clientDAO.findAll();
            applyFilter();
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the clients.", e);
        }
    }

    private void applyFilter() {
        String search = view.getTxtSearch().getText().trim().toLowerCase();
        List<Client> shown = new ArrayList<>();
        for (Client c : allClients) {
            if (c.getName().toLowerCase().contains(search) || c.getPhone().contains(search)
                    || c.getEmail().toLowerCase().contains(search)) {
                shown.add(c);
            }
        }
        view.showClients(shown);
    }

    private void addClient() {
        try {
            Client client = readForm();
            clientDAO.add(client);
            view.clearForm();
            refresh();
            Messages.info(view.getRootPanel(), client.getName() + " was added.");
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not save the client.", e);
        }
    }

    private void updateClient() {
        Client selected = view.getSelectedClient();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select a client from the table first.");
            return;
        }
        try {
            Client client = readForm();
            client.setId(selected.getId());
            clientDAO.update(client);
            view.clearForm();
            refresh();
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not update the client.", e);
        }
    }

    private void removeClient() {
        Client selected = view.getSelectedClient();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select a client from the table first.");
            return;
        }
        if (!Messages.confirm(view.getRootPanel(), "Remove " + selected.getName() + "?")) {
            return;
        }
        try {
            clientDAO.delete(selected.getId());
            view.clearForm();
            refresh();
        } catch (SQLException e) {
            Messages.warn(view.getRootPanel(), selected.getName() + " has orders in the system, so they cannot be removed.");
        }
    }

    private Client readForm() throws ValidationException {
        Client client = Client.create(view.getClientType());
        client.setName(Validator.required(view.getTxtName().getText(), "Name"));
        if (client instanceof OfficeClient) {
            String contact = Validator.required(view.getTxtContactPerson().getText(), "Contact person");
            ((OfficeClient) client).setContactPerson(contact);
        }
        client.setPhone(Validator.phone(view.getTxtPhone().getText()));
        client.setEmail(Validator.email(view.getTxtEmail().getText()));
        client.setAddress(Validator.required(view.getTxtAddress().getText(), "Address"));
        return client;
    }
}
