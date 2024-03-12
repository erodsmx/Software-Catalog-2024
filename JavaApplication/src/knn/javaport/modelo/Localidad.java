/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knn.javaport.modelo;

/**
 *
 * @author delta9
 */
public class Localidad {
    
    private float X;
    private float Y;
    private float distancia;
    private char nombre;
    private int cuadrante;
    private boolean PtoLimiteX;
    private boolean PtoLimiteY;

    //Constructor
    public Localidad(float X, float Y, char nom) {
        this.X = X;
        this.Y = Y;
        this.distancia=0;
        this.cuadrante=getQuadrant((int)X, (int) Y);
        this.nombre = nom;
        this.PtoLimiteX=false;
        this.PtoLimiteY=false;
    }
    // EL conjunto se divide en cuatro cuadrantes.
    private int getQuadrant(int x, int y) {
    if (x >= 50) {
        return y >= 50 ? 1 : 4;
    } else {
        return y >= 50 ? 2 : 3;
    }

}
    //Getters y Setters
    public float getX() {
        return X;
    }

    public void setX(float X) {
        this.X = X;
    }

    public float getY() {
        return Y;
    }
    public void setY(float Y) {
        this.Y = Y;
    }

    public char getNombre() {
        return nombre;
    }

    public void setNombre(char nombre) {
        this.nombre = nombre;
    }

    public int getCuadrante() {
        return cuadrante;
    }

    public void setCuadrante(int cuadrante) {
        this.cuadrante = cuadrante;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }
    public boolean isPtoLimiteX() {
        return PtoLimiteX;
    }

    public void setPtoLimiteX(boolean PtoLimite) {
        this.PtoLimiteX = PtoLimite;
    }
    public boolean isPtoLimiteY() {
        return PtoLimiteY;
    }

    public void setPtoLimiteY(boolean PtoLimite) {
        this.PtoLimiteY = PtoLimite;
    }

    @Override
    public String toString() {
        return "Loc{" + "X=" + X + ", Y=" + Y + ", distancia=" + distancia + ", nombre=" + nombre + ", cuadrante=" + cuadrante + ", PtoLimiteX=" + PtoLimiteX + ", PtoLimiteY=" + PtoLimiteY + '}';
    }
    


}
