package com.example.topidana.models;

import java.util.Date;

public class Beasiswa {
    String flag;
    String id;
    String user_id;
    String nama_user;
    String keperluan;
    Float biaya;
    Float terkumpul;
    String deadline;
    String deskripsi;
    String donatur;
    String transkrip;
    String status;

    public Beasiswa(String flag, String id, String user_id, String nama_user, String keperluan, Float biaya, Float terkumpul, String deadline, String deskripsi, String donatur, String transkrip, String status) {
        this.flag = flag;
        this.id = id;
        this.user_id = user_id;
        this.nama_user = nama_user;
        this.keperluan = keperluan;
        this.biaya = biaya;
        this.terkumpul = terkumpul;
        this.deadline = deadline;
        this.deskripsi = deskripsi;
        this.donatur = donatur;
        this.transkrip = transkrip;
        this.status = status;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public String getKeperluan() {
        return keperluan;
    }

    public void setKeperluan(String keperluan) {
        this.keperluan = keperluan;
    }

    public Float getBiaya() {
        return biaya;
    }

    public void setBiaya(Float biaya) {
        this.biaya = biaya;
    }

    public Float getTerkumpul() {
        return terkumpul;
    }

    public void setTerkumpul(Float terkumpul) {
        this.terkumpul = terkumpul;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getDonatur() {
        return donatur;
    }

    public void setDonatur(String donatur) {
        this.donatur = donatur;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTranskrip() {
        return transkrip;
    }

    public void setTranskrip(String transkrip) {
        this.transkrip = transkrip;
    }
}
