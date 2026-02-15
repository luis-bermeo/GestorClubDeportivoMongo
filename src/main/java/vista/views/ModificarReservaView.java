package vista.views;

import controlador.LogicaMongo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import modelo.Reserva;

public class ModificarReservaView extends GridPane {

    public ModificarReservaView(LogicaMongo logica, DashboardView dashboardView) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        // 1. Selector de Reserva
        ComboBox<Reserva> comboReservas = new ComboBox<>();
        // 2. Campo para nueva duración
        Spinner<Integer> spinnerDuracion = new Spinner<>(30, 300, 60, 30); // Min 30, Max 300, Inicio 60, Pasos 30
        Button btnModificar = new Button("Modificar Duración");

        // Cargar reservas
        cargarReservas(logica, comboReservas);

        // Layout
        addRow(0, new Label("Selecciona Reserva:"), comboReservas);
        addRow(1, new Label("Nueva Duración (min):"), spinnerDuracion);
        add(btnModificar, 1, 2);

        // Lógica del botón
        btnModificar.setOnAction(e -> {
            Reserva reservaSeleccionada = comboReservas.getValue();
            if (reservaSeleccionada != null) {
                try {
                    int nuevaDuracion = spinnerDuracion.getValue();
                    // Llamamos al método de la lógica que actualiza duración y precio
                    logica.modificarDuracionReserva(reservaSeleccionada.getIdReserva(), nuevaDuracion);

                    showInfo("Reserva modificada correctamente. Nuevo precio calculado.");
                    dashboardView.refreshData();
                    cargarReservas(logica, comboReservas); // Recargar lista
                } catch (Exception ex) {
                    showError("Error al modificar: " + ex.getMessage());
                }
            } else {
                showError("Por favor, selecciona una reserva.");
            }
        });
    }

    private void cargarReservas(LogicaMongo logica, ComboBox<Reserva> combo) {
        try {
            combo.getItems().clear();
            combo.getItems().addAll(logica.getReservas());
            combo.setConverter(new StringConverter<Reserva>() {
                @Override
                public String toString(Reserva r) {
                    return r != null ? "Reserva " + r.getIdReserva() + " (" + r.getDuracionMin() + " min)" : "";
                }
                @Override
                public Reserva fromString(String string) { return null; }
            });
        } catch (Exception e) {
            showError("Error al cargar reservas: " + e.getMessage());
        }
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