package controlador;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import modelo.Pista;
import modelo.Reserva;
import modelo.Socio;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class LogicaMongo {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Socio> sociosCol;
    private MongoCollection<Pista> pistasCol;
    private MongoCollection<Reserva> reservasCol;

    public LogicaMongo() {
        // Configuración de CodecRegistry para POJOs
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("dama_sports");

        sociosCol = database.getCollection("socios", Socio.class);
        pistasCol = database.getCollection("pistas", Pista.class);
        reservasCol = database.getCollection("reservas", Reserva.class);
    }

    public void cerrar() {
        if (mongoClient != null) mongoClient.close();
    }

    // ================= SOCIOS =================

    public List<Socio> getSocios() {
        return sociosCol.find().into(new ArrayList<>());
    }

    public void altaSocio(Socio socio) {
        if (sociosCol.find(Filters.eq("_id", socio.getIdSocio())).first() != null) {
            throw new IllegalArgumentException("El socio ya existe");
        }
        sociosCol.insertOne(socio);
    }

    public void bajaSocio(String idSocio) {
        // Comparamos fecha como String ISO (YYYY-MM-DD) que es como está en el JSON
        long reservasFuturas = reservasCol.countDocuments(Filters.and(
                Filters.eq("idSocio", idSocio),
                Filters.gt("fecha", LocalDate.now().toString())
        ));

        if (reservasFuturas > 0) {
            throw new IllegalArgumentException("El socio tiene reservas futuras y no se puede borrar.");
        }
        sociosCol.deleteOne(Filters.eq("_id", idSocio));
    }

    // ================= PISTAS =================

    public List<Pista> getPistas() {
        return pistasCol.find().into(new ArrayList<>());
    }

    public void altaPista(Pista pista) {
        if (pistasCol.find(Filters.eq("_id", pista.getIdPista())).first() != null) {
            throw new IllegalArgumentException("La pista ya existe");
        }
        pistasCol.insertOne(pista);
    }

    public void cambiarDisponibilidadPista(String idPista, boolean disponible) {
        pistasCol.updateOne(Filters.eq("_id", idPista), Updates.set("disponible", disponible));
    }

    // ================= RESERVAS =================

    public List<Reserva> getReservas() {
        return reservasCol.find().into(new ArrayList<>());
    }

    public List<Reserva> getReservasPorSocio(String idSocio) {
        return reservasCol.find(Filters.eq("idSocio", idSocio)).into(new ArrayList<>());
    }

    // Cambiado el parámetro fecha a String para que coincida con el JSON de Mongo
    public List<Reserva> getReservasPorPistaYFecha(String idPista, String fecha) {
        return reservasCol.find(Filters.and(
                Filters.eq("idPista", idPista),
                Filters.eq("fecha", fecha)
        )).into(new ArrayList<>());
    }

    public void crearReserva(Reserva reserva) {
        // Validar Socio
        if (sociosCol.find(Filters.eq("_id", reserva.getIdSocio())).first() == null) {
            throw new IllegalArgumentException("El socio no existe");
        }

        // Validar Pista operativa
        Pista pista = pistasCol.find(Filters.eq("_id", reserva.getIdPista())).first();
        if (pista == null || !pista.isDisponible()) {
            throw new IllegalArgumentException("La pista no existe o no está operativa");
        }

        // Lógica de Solapamientos
        List<Reserva> reservasDia = getReservasPorPistaYFecha(reserva.getIdPista(), reserva.getFecha());

        // Convertimos el String de la nueva reserva a LocalTime para operar
        LocalTime inicioNueva = LocalTime.parse(reserva.getHoraInicio());
        LocalTime finNueva = inicioNueva.plusMinutes(reserva.getDuracionMin());

        for (Reserva r : reservasDia) {
            LocalTime inicioExistente = LocalTime.parse(r.getHoraInicio());
            LocalTime finExistente = inicioExistente.plusMinutes(r.getDuracionMin());

            if (inicioNueva.isBefore(finExistente) && inicioExistente.isBefore(finNueva)) {
                throw new IllegalArgumentException("Solapamiento: La pista ya está ocupada en ese horario.");
            }
        }

        // Precio: 10€ la hora (0.166€ el minuto aprox)
        double precio = (reserva.getDuracionMin() / 60.0) * 10.0;
        reserva.setPrecio(Math.round(precio * 100.0) / 100.0); // Redondeo a 2 decimales

        reservasCol.insertOne(reserva);
    }

    public void modificarDuracionReserva(String idReserva, int nuevaDuracionMin) {
        double nuevoPrecio = (nuevaDuracionMin / 60.0) * 10.0;
        nuevoPrecio = Math.round(nuevoPrecio * 100.0) / 100.0;

        reservasCol.updateOne(
                Filters.eq("_id", idReserva),
                Updates.combine(
                        Updates.set("duracionMin", nuevaDuracionMin),
                        Updates.set("precio", nuevoPrecio)
                )
        );
    }

    public void cancelarReserva(String idReserva) {
        reservasCol.deleteOne(Filters.eq("_id", idReserva));
    }
}