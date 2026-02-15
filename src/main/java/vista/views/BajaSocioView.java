package vista.views;

import controlador.LogicaMongo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import modelo.Socio;

public class BajaSocioView extends GridPane {
    public BajaSocioView(LogicaMongo logica, DashboardView dashboardView) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        ComboBox<Socio> id = new ComboBox<>();
        try {
            id.getItems().addAll(logica.getSocios());
            id.setConverter(new StringConverter<Socio>() {
                @Override
                public String toString(Socio socio) {
                    return socio != null ? socio.getNombre() + " (" + socio.getIdSocio() + ")" : "";
                }

                @Override
                public Socio fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("No se pudieron cargar los socios: " + e.getMessage());
        }

        Button baja = new Button("Dar de baja");

        addRow(0, new Label("Socio"), id);
        add(baja, 1, 1);

        baja.setOnAction(e -> {
            Socio socioSeleccionado = id.getValue();
            if (socioSeleccionado != null) {
                try {
                    logica.bajaSocio(socioSeleccionado.getIdSocio());
                    showInfo("Socio dado de baja correctamente en MongoDB");
                    dashboardView.refreshData();
                    id.getItems().clear();
                    id.getItems().addAll(logica.getSocios());
                } catch (Exception ex) {
                    showError("Error al dar de baja al socio: " + ex.getMessage());
                }
            } else {
                showError("Por favor, seleccione un socio.");
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