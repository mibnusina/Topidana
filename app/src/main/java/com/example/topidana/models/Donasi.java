package com.example.topidana.models;

public class Donasi {
    String nama_user;
    String keperluan;
    String total_donasi;
    String tgl_donasi;
    String status;

    public Donasi(String nama_user, String keperluan, String total_donasi, String tgl_donasi, String status) {
        this.nama_user = nama_user;
        this.keperluan = keperluan;
        this.total_donasi = total_donasi;
        this.tgl_donasi = tgl_donasi;
        this.status = status;
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

    public String getTotal_donasi() {
        return total_donasi;
    }

    public void setTotal_donasi(String total_donasi) {
        this.total_donasi = total_donasi;
    }

    public String getTgl_donasi() {
        return tgl_donasi;
    }

    public void setTgl_donasi(String tgl_donasi) {
        this.tgl_donasi = tgl_donasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
