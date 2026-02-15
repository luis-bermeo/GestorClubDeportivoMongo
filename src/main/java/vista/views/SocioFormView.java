package vista.views;

import controlador.LogicaMongo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import modelo.Socio;

public class SocioFormView extends GridPane {
    public SocioFormView(LogicaMongo logica, DashboardView dashboardView) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        TextField id = new TextField();
        TextField dni = new TextField();
        TextField nombre = new TextField();
        TextField apellidos = new TextField();
        TextField tel = new TextField();
        TextField email = new TextField();
        Button crear = new Button("Crear");

        addRow(0, new Label("idSocio*"), id);
        addRow(1, new Label("DNI"), dni);
        addRow(2, new Label("Nombre"), nombre);
        addRow(3, new Label("Apellidos"), apellidos);
        addRow(4, new Label("Teléfono"), tel);
        addRow(5, new Label("Email"), email);
        add(crear, 1, 6);

        crear.setOnAction(e -> {
            try {
                logica.altaSocio(new Socio(id.getText(), dni.getText(), nombre.getText(), apellidos.getText(), tel.getText(), email.getText()));
                showInfo("Socio insertado correctamente en MongoDB");
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