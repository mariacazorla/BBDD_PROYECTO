package edu.upc.dsa.models;

public class Objeto {
    private String id_objeto;
    private String objeto;  // Nombre del objeto
    private int precio;
    private int cantidad;   // Para inventario
    private String imagen;  // Ruta de imagen, pensando en BBDD
    private String descripcion;
    private String categoria;

    public Objeto() {
        // Constructor vacío para ORM
    }

    public Objeto(String objeto, int cantidad) {
        this.objeto = objeto;
        this.cantidad = cantidad;
    }

    public Objeto(String id_objeto, String objeto, int precio, String imagen, String descripcion, String categoria) {
        this.id_objeto = id_objeto;
        this.objeto = objeto;
        this.precio = precio;
        this.imagen = imagen;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public String getId_objeto() { return id_objeto; }
    public void setId_objeto(String id_objeto) { this.id_objeto = id_objeto; }

    public String getObjeto() { return objeto; }
    public void setObjeto(String objeto) { this.objeto = objeto; }

    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // Para compatibilidad con el enum CategoriaObjeto
    public void setCategoria(CategoriaObjeto categoria) {
        if (categoria != null) {
            this.categoria = categoria.toString();
        }
    }

    @Override
    public String toString() {
        if (cantidad > 0) {
            return "Objeto [objeto=" + objeto + ", cantidad=" + cantidad + "]";
        } else {
            return "Objeto [id_objeto=" + id_objeto + ", objeto=" + objeto + ", precio=" + precio +
                    ", imagen=" + imagen + ", descripcion=" + descripcion + ", categoria=" + categoria + "]";
        }
    }
}