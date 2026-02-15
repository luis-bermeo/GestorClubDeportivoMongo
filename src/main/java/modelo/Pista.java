package modelo;

import org.bson.codecs.pojo.annotations.BsonId;

public class Pista {
    @BsonId
    private String idPista;
    private String deporte;
    private String descripcion;
    private boolean disponible;

    public Pista() {}

    public Pista(String idPista, String deporte, String descripcion, boolean disponible) {
        this.idPista = idPista;
        this.deporte = deporte;
        this.descripcion = descripcion;
        this.disponible = disponible;
    }

    public String getIdPista() { return idPista; }
    public void setIdPista(String idPista) { this.idPista = idPista; }
    public String getDeporte() { return deporte; }
    public void setDeporte(String deporte) { this.deporte = deporte; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
