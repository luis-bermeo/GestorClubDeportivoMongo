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

    // Métod para crear conexión
    public LogicaMongo() {
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(settings);

        // Conexión y colecciones
        database = mongoClient.getDatabase("dama_sports");

        // Obtener las colecciones tipadas (MongoCollection<POJO>)
        sociosCol = database.getCollection("socios", Socio.class);
        pistasCol = database.getCollection("pistas", Pista.class);
        reservasCol = database.getCollection("reservas", Reserva.class);
    }

    public void cerrar() {
        if (mongoClient != null) mongoClient.close();
    }

    // ================= SOCIOS =================

    // Obtener listado de socios
    public List<Socio> getSocios() {
        return sociosCol.find().into(new ArrayList<>());
    }

    // Insertar socio
    public void altaSocio(Socio socio) {
        if (sociosCol.find(Filters.eq("_id", socio.getIdSocio())).first() != null) {
            throw new IllegalArgumentException("El socio ya existe");
        }
        sociosCol.insertOne(socio);
    }

    // Eliminar socio (validando reglas)
    // No permitir eliminar un socio si tiene reservas futuras
    public void bajaSocio(String idSocio) {
        long reservasFuturas = reservasCol.countDocuments(Filters.and(
                Filters.eq("idSocio", idSocio),
                Filters.gt("fecha", LocalDate.now())
        ));

        if (reservasFuturas > 0) {
            throw new IllegalArgumentException("El socio tiene reservas futuras y no se puede borrar.");
        }
        sociosCol.deleteOne(Filters.eq("_id", idSocio));
    }

    // ================= PISTAS =================

    // Obtener listado de pistas
    public List<Pista> getPistas() {
        return pistasCol.find().into(new ArrayList<>());
    }

    // Insertar pista
    public void altaPista(Pista pista) {
        if (pistasCol.find(Filters.eq("_id", pista.getIdPista())).first() != null) {
            throw new IllegalArgumentException("La pista ya existe");
        }
        pistasCol.insertOne(pista);
    }

    // Modificar disponibilidad de pista
    // Uso de Updates.set(...)
    public void cambiarDisponibilidadPista(String idPista, boolean disponible) {
        pistasCol.updateOne(Filters.eq("_id", idPista), Updates.set("disponible", disponible));
    }

    // ================= RESERVAS =================

    public List<Reserva> getReservas() {
        return reservasCol.find().into(new ArrayList<>());
    }

    // Obtener reservas de un socio (por id_socio)
    // Uso de Filters.eq(...)
    public List<Reserva> getReservasPorSocio(String idSocio) {
        return reservasCol.find(Filters.eq("idSocio", idSocio)).into(new ArrayList<>());
    }

    // Obtener reservas de una pista por fecha
    public List<Reserva> getReservasPorPistaYFecha(String idPista, LocalDate fecha) {
        return reservasCol.find(Filters.and(
                Filters.eq("idPista", idPista),
                Filters.eq("fecha", fecha)
        )).into(new ArrayList<>());
    }

    // Insertar reserva
    public void crearReserva(Reserva reserva) {
        // Validar Socio
        if (sociosCol.find(Filters.eq("_id", reserva.getIdSocio())).first() == null) {
            throw new IllegalArgumentException("El socio no existe");
        }

        // No permitir reservas sobre pistas no disponibles
        Pista pista = pistasCol.find(Filters.eq("_id", reserva.getIdPista())).first();
        if (pista == null || !pista.isDisponible()) {
            throw new IllegalArgumentException("La pista no existe o no está disponible");
        }

        // Evitar solapamientos (misma pista, fecha y tramo)
        List<Reserva> reservasDia = getReservasPorPistaYFecha(reserva.getIdPista(), reserva.getFecha());

        for (Reserva r : reservasDia) {
            if (reserva.getHoraInicio().isBefore(r.getHoraInicio().plusMinutes(r.getDuracionMin())) &&
                    r.getHoraInicio().isBefore(reserva.getHoraInicio().plusMinutes(reserva.getDuracionMin()))) {
                throw new IllegalArgumentException("La pista ya está reservada en ese horario");
            }
        }

        // Calcular precio simple
        double precio = 10.0 * (reserva.getDuracionMin() / 60.0);
        reserva.setPrecio(precio);

        reservasCol.insertOne(reserva);
    }

    // Modificar cantidad/duración/precio o datos de una reserva
    public void modificarDuracionReserva(String idReserva, int nuevaDuracionMin) {
        // Recalculamos precio al cambiar duración
        double nuevoPrecio = 10.0 * (nuevaDuracionMin / 60.0);

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