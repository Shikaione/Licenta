package com.mpetroiu.smc;

public class Place {
    private String nume;
    private int coperta;

    public Place(){
    }

    public Place(String nume, int coperta){
        this.nume = nume;
        this.coperta = coperta;
    }
    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public int getCoperta() {
        return coperta;
    }

    public void setCoperta(int coperta) {
        this.coperta = coperta;
    }
}
