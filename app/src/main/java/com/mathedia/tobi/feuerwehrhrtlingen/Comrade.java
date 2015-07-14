package com.mathedia.tobi.feuerwehrhrtlingen;

/**
 * Created by Tobi on 11.06.2015.
 */
public class Comrade {

    private int id;
    private String vorname;
    private String nachname;
    private int status;
    private int admin;

    public Comrade() {
    }

    public Comrade(int id, String vorname, String nachname, int status, int admin) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.status = status;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAdmin() {
        return admin;
    }
}
