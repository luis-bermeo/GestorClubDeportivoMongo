package vista.views;

import controlador.LogicaMongo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import modelo.Pista;

public class CambiarDisponibilidadView extends GridPane {
    public CambiarDisponibilidadView(LogicaMongo logica, DashboardView dashboardView) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        ComboBox<Pista> id = new ComboBox<>();
        try {
            id.getItems().addAll(logica.getPistas());
            id.setConverter(new StringConverter<Pista>() {
                @Override
                public String toString(Pista pista) {
                    return pista != null ? pista.getDescripcion() + " (" + pista.getIdPista() + ")" : "";
                }

                @Override
                public Pista fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("No se pudieron cargar las pistas: " + e.getMessage());
        }

        CheckBox disponible = new CheckBox("Disponible");
        Button cambiar = new Button("Aplicar");

        id.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                disponible.setSelected(newValue.isDisponible());
            }
        });

        addRow(0, new Label("idPista"), id);
        addRow(1, new Label("Estado"), disponible);
        add(cambiar, 1, 2);

        cambiar.setOnAction(e -> {
            Pista pistaSeleccionada = id.getValue();
            if (pistaSeleccionada != null) {
                try {
                    logica.cambiarDisponibilidadPista(pistaSeleccionada.getIdPista(), disponible.isSelected());
                    showInfo("Disponibilidad cambiada correctamente en MongoDB");
                    dashboardView.refreshData();
                } catch (Exception ex) {
                    showError("Error al cambiar la disponibilidad: " + ex.getMessage());
                }
            } else {
                showError("Por favor, seleccione una pista.");
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