package edu.upc.dsa.models;

public class Usuario {
    private String nombre_usuario;
    private String contraseña;

    // Quitamos la lista de partidas del modelo para evitar problemas con el ORM
    // Las partidas se manejarán a través de la tabla Partida con relación a id_usuario

    public Usuario() {
        // Constructor vacío requerido para ORM
    }

    public Usuario(String nombre_usuario, String contraseña) {
        this.nombre_usuario = nombre_usuario;
        this.contraseña = contraseña;
    }

    public String getNombre_usuario() { return nombre_usuario; }
    public void setNombre_usuario(String nombre_usuario) { this.nombre_usuario = nombre_usuario; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    @Override
    public String toString() {
        return "Usuario [nombre_usuario=" + nombre_usuario + "]";
    }
}