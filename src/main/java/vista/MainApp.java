package vista;

import controlador.LogicaMongo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vista.views.*;

public class MainApp extends Application {

    private LogicaMongo logica;
    private BorderPane root;
    private Label status;
    private DashboardView dashboardView;

    @Override
    public void start(Stage stage)  {

        try {
            // Inicializamos la conexión a Mongo
            logica = new LogicaMongo();
            showInfo("Conectado a MongoDB correctamente");
        } catch (Exception e) {
            showError("Error al conectar a MongoDB: " + e.getMessage());
            return;
        }

        root = new BorderPane();
        root.setTop(buildMenuBar());
        status = new Label("Listo");
        status.setPadding(new Insets(4));
        root.setBottom(status);

        // Vista por defecto
        dashboardView = new DashboardView(logica);
        root.setCenter(dashboardView);

        Scene scene = new Scene(root, 960, 640);
        stage.setTitle("Club DAMA Sports - MongoDB Edition");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar buildMenuBar() {
        MenuBar mb = new MenuBar();

        // --- MENÚ SOCIOS ---
        Menu socios = new Menu("Socios");
        MenuItem altaSocio = new MenuItem("Alta socio");
        altaSocio.setOnAction(e -> root.setCenter(new SocioFormView(logica, dashboardView)));
        MenuItem bajaSocio = new MenuItem("Baja socio");
        bajaSocio.setOnAction(e -> root.setCenter(new BajaSocioView(logica, dashboardView)));
        socios.getItems().addAll(altaSocio, bajaSocio);

        // --- MENÚ PISTAS ---
        Menu pistas = new Menu("Pistas");
        MenuItem altaPista = new MenuItem("Alta pista");
        altaPista.setOnAction(e -> root.setCenter(new PistaFormView(logica, dashboardView)));
        MenuItem cambiarDisp = new MenuItem("Cambiar disponibilidad");
        cambiarDisp.setOnAction(e -> root.setCenter(new CambiarDisponibilidadView(logica, dashboardView)));
        pistas.getItems().addAll(altaPista, cambiarDisp);

        // --- MENÚ RESERVAS ---
        Menu reservas = new Menu("Reservas");

        MenuItem crearReserva = new MenuItem("Crear reserva");
        crearReserva.setOnAction(e -> root.setCenter(new ReservaFormView(logica, dashboardView)));

        MenuItem modReserva = new MenuItem("Modificar reserva");
        modReserva.setOnAction(e -> root.setCenter(new ModificarReservaView(logica, dashboardView)));

        MenuItem cancelarReserva = new MenuItem("Cancelar reserva");
        cancelarReserva.setOnAction(e -> root.setCenter(new CancelarReservaView(logica, dashboardView)));

        reservas.getItems().addAll(crearReserva, modReserva, cancelarReserva);

        // --- MENÚ VER ---
        Menu ver = new Menu("Ver");
        MenuItem dashboard = new MenuItem("Dashboard");
        dashboard.setOnAction(e -> root.setCenter(dashboardView));
        ver.getItems().addAll(dashboard);

        // --- MENÚ ARCHIVO ---
        Menu archivo = new Menu("Archivo");
        MenuItem salir = new MenuItem("Salir");
        salir.setOnAction(e -> {
            if (logica != null) logica.cerrar();
            Platform.exit();
        });
        archivo.getItems().addAll(salir);

        mb.getMenus().addAll(archivo, socios, pistas, reservas, ver);
        return mb;
    }

    public void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        if (logica != null) logica.cerrar();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
