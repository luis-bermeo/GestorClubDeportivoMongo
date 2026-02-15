package vista.views;

import controlador.LogicaMongo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import modelo.Reserva;

public class CancelarReservaView extends GridPane {
    public CancelarReservaView(LogicaMongo logica, DashboardView dashboardView) {
        setPadding(new Insets(12));
        setHgap(8); setVgap(8);

        ComboBox<Reserva> id = new ComboBox();
        try {
            id.getItems().addAll(logica.getReservas());
            id.setConverter(new StringConverter<Reserva>() {
                @Override
                public String toString(Reserva reserva) {
                    return reserva != null ? "Reserva " + reserva.getIdReserva() + " - Socio: " + reserva.getIdSocio() : "";
                }

                @Override
                public Reserva fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("No se pudieron cargar las reservas: " + e.getMessage());
        }

        Button cancelar = new Button("Cancelar reserva");

        addRow(0, new Label("Reserva"), id);
        add(cancelar, 1, 1);

        cancelar.setOnAction(e -> {
            Reserva reservaSeleccionada = id.getSelectionModel().getSelectedItem();
            if (reservaSeleccionada != null) {
                try {
                    logica.cancelarReserva(reservaSeleccionada.getIdReserva());
                    showInfo("Reserva cancelada correctamente en MongoDB");
                    dashboardView.refreshData();
                    id.getItems().clear();
                    id.getItems().addAll(logica.getReservas());
                } catch (Exception ex) {
                    showError("Error al cancelar la reserva: " + ex.getMessage());
                }
            } else {
                showError("Por favor, seleccione una reserva.");
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