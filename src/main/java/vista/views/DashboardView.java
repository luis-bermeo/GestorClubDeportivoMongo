package vista.views;

import controlador.LogicaMongo;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import modelo.Pista;
import modelo.Reserva;
import modelo.Socio;

public class DashboardView extends BorderPane {
    private LogicaMongo logica;
    private TableView<Socio> tablaSocios;
    private TableView<Pista> tablaPistas;
    private TableView<Reserva> tablaReservas;

    public DashboardView(LogicaMongo logica) {
        this.logica = logica;
        setPadding(new Insets(10));
        Label title = new Label("Resumen MongoDB");
        setTop(title);

        // Tabla Socios
        tablaSocios = new TableView<>();
        TableColumn<Socio, String> c1 = new TableColumn<>("ID");
        c1.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdSocio()));
        TableColumn<Socio, String> c2 = new TableColumn<>("Nombre");
        c2.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<Socio, String> c3 = new TableColumn<>("Apellidos");
        c3.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getApellidos()));
        TableColumn<Socio, String> c4 = new TableColumn<>("Teléfono");
        c4.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTelefono()));
        TableColumn<Socio, String> c5 = new TableColumn<>("Email");
        c5.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getEmail()));
        tablaSocios.getColumns().addAll(c1, c2, c3, c4, c5);

        // Tabla Pistas
        tablaPistas = new TableView<>();
        TableColumn<Pista, String> p1 = new TableColumn<>("ID");
        p1.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdPista()));
        TableColumn<Pista, String> p2 = new TableColumn<>("Deporte");
        p2.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getDeporte()));
        TableColumn<Pista, String> p3 = new TableColumn<>("Disponible");
        p3.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(String.valueOf(p.getValue().isDisponible())));
        tablaPistas.getColumns().addAll(p1, p2, p3);

        // Tabla Reservas
        tablaReservas = new TableView<>();
        TableColumn<Reserva, String> r1 = new TableColumn<>("ID");
        r1.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdReserva()));
        TableColumn<Reserva, String> r2 = new TableColumn<>("Socio");
        r2.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdSocio()));
        TableColumn<Reserva, String> r3 = new TableColumn<>("Pista");
        r3.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdPista()));
        TableColumn<Reserva, String> r4 = new TableColumn<>("Fecha");
        r4.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getFecha()));
        TableColumn<Reserva, String> r5 = new TableColumn<>("Inicio");
        r5.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getHora_inicio()));
        TableColumn<Reserva, String> r6 = new TableColumn<>("Min");
        r6.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(String.valueOf(p.getValue().getDuracionMin())));
        TableColumn<Reserva, String> r7 = new TableColumn<>("Precio (€)");
        r7.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", p.getValue().getPrecio())));
        tablaReservas.getColumns().addAll(r1, r2, r3, r4, r5, r6, r7);

        refreshData();

        // Layout
        BorderPane center = new BorderPane();
        center.setTop(new Label("Socios"));
        center.setCenter(tablaSocios);

        BorderPane right = new BorderPane();
        right.setTop(new Label("Pistas"));
        right.setCenter(tablaPistas);

        BorderPane bottom = new BorderPane();
        bottom.setTop(new Label("Reservas"));
        bottom.setCenter(tablaReservas);

        setCenter(center);
        setRight(right);
        setBottom(bottom);
    }

    public void refreshData() {
        try {
            tablaSocios.getItems().clear();
            tablaSocios.getItems().addAll(logica.getSocios());
            tablaPistas.getItems().clear();
            tablaPistas.getItems().addAll(logica.getPistas());
            tablaReservas.getItems().clear();
            tablaReservas.getItems().addAll(logica.getReservas());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}