package edu.upc.dsa.models;

import java.util.ArrayList;
import java.util.List;

public class Partida {

    private int id_partida;
    // id_usuario es el nombre del usuario
    private String id_usuario;
    private Integer vidas;
    private Integer monedas;
    private Integer puntuacion;
    private List<Objeto> inventario;

    public Partida() {
        // Inicializamos el inventario
        inventario = new ArrayList<>();
    }

    public Partida(int id_partida, String id_usuario, Integer vidas, Integer monedas, Integer puntuacion) {
        this(); // Llama al constructor sin parámetros
        this.id_partida = id_partida;
        setId_usuario(id_usuario);
        setVidas(vidas);
        setMonedas(monedas);
        setPuntuacion(puntuacion);
    }

    public int getId_partida() { return id_partida; }
    public void setId_partida(int id_partida) { this.id_partida = id_partida; }

    // Para compatibilidad con código existente - convertir String a int
    public void setId_partida(String id_partida) {
        try {
            this.id_partida = Integer.parseInt(id_partida);
        } catch (NumberFormatException e) {
            // Si no es un número válido, lo dejamos en 0
            this.id_partida = 0;
        }
    }

    public String getId_usuario() { return id_usuario; }
    public void setId_usuario(String id_usuario) { this.id_usuario = id_usuario; }

    public Integer getVidas() { return vidas; }
    public void setVidas(Integer vidas) { this.vidas = vidas; }

    public Integer getMonedas() { return monedas; }
    public void setMonedas(Integer monedas) { this.monedas = monedas; }

    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }

    public List<Objeto> getInventario() { return inventario; }
    public void setInventario(List<Objeto> inventario) { this.inventario = inventario; }

    @Override
    public String toString() {
        return "Partida [id_partida=" + id_partida + ", id_usuario=" + id_usuario + ", vidas=" + vidas +
                ", monedas=" + monedas + ", puntuacion=" + puntuacion + ", inventario=" + inventario + "]";
    }
}