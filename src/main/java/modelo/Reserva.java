package modelo;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Reserva {
    @BsonId
    private String idReserva;
    @BsonProperty("id_socio")
    private String idSocio;
    @BsonProperty("id_pista")
    private String idPista;
    private String fecha; // Cambiado a String
    private String hora_inicio; // Cambiado a String
    @BsonProperty("duracion_min")
    private int duracionMin;
    private double precio;

    public Reserva() {}

    public Reserva(String idReserva, String idSocio, String idPista, String fecha, String hora_inicio, int duracionMin, double precio) {
        this.idReserva = idReserva;
        this.idSocio = idSocio;
        this.idPista = idPista;
        this.fecha = fecha;
        this.hora_inicio = hora_inicio;
        this.duracionMin = duracionMin;
        this.precio = precio;
    }

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }
    public String getIdSocio() { return idSocio; }
    public void setIdSocio(String idSocio) { this.idSocio = idSocio; }
    public String getIdPista() { return idPista; }
    public void setIdPista(String idPista) { this.idPista = idPista; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHora_inicio() { return hora_inicio; }
    public void setHora_inicio(String hora_inicio) { this.hora_inicio = hora_inicio; }
    public int getDuracionMin() { return duracionMin; }
    public void setDuracionMin(int duracionMin) { this.duracionMin = duracionMin; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}