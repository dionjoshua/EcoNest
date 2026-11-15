# EcoNest Management System

Java Swing desktop application for EcoNest (home & garden retail) - OOP group project.

## How to run (IntelliJ IDEA)

1. `File > Open` and choose this folder (the one with `pom.xml`). IntelliJ imports it as a Maven project
   and downloads the two libraries (SQLite driver and JavaMail) by itself. Needs JDK 11 or newer.
2. Open `src/main/java/com/econest/Main.java` and press the green run arrow.

The database file `econest.db` is created in the project folder the first time the program runs,
with some sample products, clients, agents and orders. Delete that file to start again with fresh data.

## Email setup

Open `config/email.properties` and fill in:

```
mail.username=youraddress@gmail.com
mail.password=your 16 letter app password
```

Gmail needs an *App password* (Google Account > Security > 2-Step Verification > App passwords),
the normal Gmail password does not work. No restart is needed after changing the file.

The sample clients and agents have made-up `example.com` addresses. To see an email arrive,
edit a client and an agent and put your own email address, then:

- Deliveries > select a pending order > choose agent + date > **Assign agent** -> email goes to the agent
- Deliveries > select the order > **Mark as dispatched** -> email goes to the client

Every email (sent or failed) is listed at the bottom of the Deliveries screen.

## Look and feel

All colours are in one place: `view/components/Theme.java`. The main colour is a deep wine red
(`#420217`) with an ivory background and a soft gold accent. Page titles and big numbers use a serif
font (Georgia on Windows/Mac), set by `Theme.applyHeadingFonts()` when each screen is added.

## Project structure (MVC)

```
src/main/java/com/econest
  Main.java              starts the program
  model/                 Product (Plant, Planter, GardeningKit), Client (HouseholdClient, OfficeClient),
                         DeliveryAgent, Order, OrderItem, StockEntry ...
  view/                  one .form + .java per screen (IntelliJ GUI Designer)
  view/components/       theme, rounded panel, chart and table renderers
  controller/            one controller per screen, connects the view to the data
  dao/                   database access (SQLite through JDBC)
  service/               EmailService and the email text
  util/                  validation and message boxes
config/email.properties  email account
database/econest_schema.sql   table definitions (for the ER diagram)
```

## Requirements covered

| # | Requirement | Where |
|---|-------------|-------|
| 1 | Manage product catalogue | Products screen - `ProductController`, `ProductDAO` |
| 2 | Manage clients | Clients screen - `ClientController`, `ClientDAO` |
| 3 | Manage stock / inventory | Inventory screen - `InventoryController`, `StockDAO` |
| 4 | Manage delivery agents | Delivery Agents screen - `AgentController`, `AgentDAO` |
| 5 | Process client orders | Orders screen - `OrderController`, `OrderDAO` |
| 6 | Assign delivery agents, track status | Deliveries screen - `DeliveryController` |
| 7 | Monthly sales & inventory reports | Reports screen + Dashboard - `ReportController`, `ReportDAO` |
| 8 | Email client when order is dispatched | `DeliveryController.markDispatched()` -> `EmailService` |
| 9 | Email agent when delivery is assigned | `DeliveryController.assignAgent()` -> `EmailService` |

## OOP concepts used

- **Inheritance / abstraction** - `Product` and `Client` are abstract classes with subclasses
- **Polymorphism** - `getCategory()`, `getSpecLabel()`, `getContactName()` behave differently per subclass
- **Interfaces** - `Notifiable` (anyone who can get an email), `CrudDAO<T>`, `ScreenController`
- **Encapsulation** - private fields with getters / setters in all model classes
- **Exception handling** - own `ValidationException` for wrong input, SQL errors handled in the controllers

## Notes

- The forms were made with the IntelliJ GUI Designer. If IntelliJ ever shows an error about
  `$$$setupUI$$$` when building, set `Settings > Editor > GUI Designer > Generate GUI into` to
  **Java source code** and rebuild.
- "Export to file" on the Reports screen writes a text report into the `reports` folder.
