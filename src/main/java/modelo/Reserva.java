package modelo;

import org.bson.codecs.pojo.annotations.BsonId;

public class Reserva {
    @BsonId
    private String idReserva;
    private String idSocio;
    private String idPista;
    private String fecha; // Cambiado a String
    private String horaInicio; // Cambiado a String
    private int duracionMin;
    private double precio;

    public Reserva() {}

    public Reserva(String idReserva, String idSocio, String idPista, String fecha, String horaInicio, int duracionMin, double precio) {
        this.idReserva = idReserva;
        this.idSocio = idSocio;
        this.idPista = idPista;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
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
    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }
    public int getDuracionMin() { return duracionMin; }
    public void setDuracionMin(int duracionMin) { this.duracionMin = duracionMin; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}