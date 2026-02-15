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

public class ReservaFormView extends GridPane {

    public ReservaFormView(LogicaMongo logica, DashboardView dashboardView) {
        // Configuración básica del panel
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        // Componentes del formulario
        TextField id = new TextField();
        ComboBox<Socio> idSocio = new ComboBox<>();
        ComboBox<Pista> idPista = new ComboBox<>();
        DatePicker fecha = new DatePicker(LocalDate.now());
        TextField hora = new TextField("10:00");
        Spinner<Integer> duracion = new Spinner<>(30, 300, 60, 30);
        Button crear = new Button("Realizar Reserva");

        // Carga de datos iniciales
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
            showError("Error al cargar datos desde MongoDB: " + e.getMessage());
        }

        // Layout del formulario
        addRow(0, new Label("idReserva*"), id);
        addRow(1, new Label("Socio*"), idSocio);
        addRow(2, new Label("Pista*"), idPista);
        addRow(3, new Label("Fecha*"), fecha);
        addRow(4, new Label("Hora inicio* (HH:mm)"), hora);
        addRow(5, new Label("Duración (min)"), duracion);
        add(crear, 1, 6);

        // Acción del botón Crear
        crear.setOnAction(e -> {
            try {
                // Validaciones básicas de la vista
                if (id.getText().isEmpty() || idSocio.getValue() == null || idPista.getValue() == null) {
                    showError("Por favor, rellena todos los campos obligatorios.");
                    return;
                }

                // Creamos el objeto Reserva pasando fechas y horas como String
                Reserva nuevaReserva = new Reserva(
                        id.getText(),
                        idSocio.getValue().getIdSocio(),
                        idPista.getValue().getIdPista(),
                        fecha.getValue().toString(), // LocalDate a String
                        hora.getText(),              // Hora como String
                        duracion.getValue(),
                        0.0                          // El precio lo calcula LogicaMongo
                );

                // Llamada a la lógica de persistencia
                logica.crearReserva(nuevaReserva);

                showInfo("Reserva guardada correctamente. ID: " + nuevaReserva.getIdReserva());

                // Actualizamos la tabla principal
                dashboardView.refreshData();

            } catch (IllegalArgumentException ex) {
                // Captura reglas de negocio (solapes, pista no disponible)
                showError(ex.getMessage());
            } catch (Exception ex) {
                showError("Error inesperado: " + ex.getMessage());
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error de validación");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText("Operación exitosa");
        a.showAndWait();
    }
}