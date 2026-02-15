package vista.views;

import controlador.LogicaMongo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import modelo.Pista;
import modelo.Reserva;
import modelo.Socio;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaFormView extends GridPane {
    public ReservaFormView(LogicaMongo logica, DashboardView dashboardView) {
        setPadding(new Insets(12));
        setHgap(8); setVgap(8);

        TextField id = new TextField();
        ComboBox<Socio> idSocio = new ComboBox();
        ComboBox<Pista> idPista = new ComboBox();

        try {
            idSocio.getItems().addAll(logica.getSocios());
            idSocio.setConverter(new StringConverter<Socio>() {
                @Override
                public String toString(Socio socio) {
                    return socio != null ? socio.getNombre() + " (" + socio.getIdSocio() + ")" : "";
                }
                @Override
                public Socio fromString(String string) { return null; }
            });

            idPista.getItems().addAll(logica.getPistas());
            idPista.setConverter(new StringConverter<Pista>() {
                @Override
                public String toString(Pista pista) {
                    return pista != null ? pista.getDescripcion() + " (" + pista.getIdPista() + ")" : "";
                }
                @Override
                public Pista fromString(String string) { return null; }
            });
        } catch (Exception e) {
            showError("No se pudieron cargar los datos: " + e.getMessage());
        }

        DatePicker fecha = new DatePicker(LocalDate.now());
        TextField hora = new TextField("10:00");
        Spinner<Integer> duracion = new Spinner<>(30, 300, 60, 30);
        Button crear = new Button("Reservar");

        addRow(0, new Label("idReserva*"), id);
        addRow(1, new Label("Socio*"), idSocio);
        addRow(2, new Label("Pista*"), idPista);
        addRow(3, new Label("Fecha*"), fecha);
        addRow(4, new Label("Hora inicio* (HH:mm)"), hora);
        addRow(5, new Label("Duración (min)"), duracion);
        add(crear, 1, 6);

        crear.setOnAction(e -> {
            try {
                if (idSocio.getValue() == null || idPista.getValue() == null) {
                    showError("Debe seleccionar un socio y una pista.");
                    return;
                }
                LocalTime t = LocalTime.parse(hora.getText());

                // En Mongo guardamos los IDs, no los objetos completos
                Reserva r = new Reserva(id.getText(),
                        idSocio.getValue().getIdSocio(),
                        idPista.getValue().getIdPista(),
                        fecha.getValue(), t, duracion.getValue(), 0);

                logica.crearReserva(r);
                showInfo("Reserva creada correctamente en MongoDB. Precio: " + r.getPrecio() + "€");
                dashboardView.refreshData();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }
    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}