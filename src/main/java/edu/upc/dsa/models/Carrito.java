package edu.upc.dsa.models;

import java.util.ArrayList;
import java.util.List;

public class Carrito {

    private String id_usuario;
    private List<Objeto> objetos;

    public Carrito() {
        this.objetos = new ArrayList<>();
    }

    public Carrito(String id_usuario) {
        this();
        setId_usuario(id_usuario);
    }

    public String getId_usuario() { return id_usuario; }
    public void setId_usuario(String id_usuario) { this.id_usuario = id_usuario; }

    public List<Objeto> getObjetos() { return objetos; }
    public void setObjetos(List<Objeto> objetos) { this.objetos = objetos; }

    public void agregarObjeto(Objeto objeto) { this.objetos.add(objeto); }
    public void eliminarObjeto(String id_objeto) { this.objetos.removeIf(o -> o.getId_objeto().equals(id_objeto)); }

    public int getTotal() { return objetos.stream().mapToInt(Objeto::getPrecio).sum(); }

    public void vaciar() { this.objetos.clear(); }

    @Override
    public String toString() {
        return "Carrito [id_usuario=" + id_usuario + ", objetos=" + objetos + "]";
    }

}
