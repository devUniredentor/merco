package br.com.sistemas.soscidadao.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Denuncia implements Serializable {
    private String id;
    private String idUser ;
    private String problema;
    private String descricao;
    private boolean resolvido;
    private boolean reportado;
    private long dateCriacao, dataFinalizacao;
    private double latitude, longitude;


    public Denuncia(String id, String idUser, String problema, String descricao, boolean resolvido, boolean reportado, long dateCriacao, long dataFinalizacao, double latitude, double longitude) {
        this.id = id;
        this.idUser = idUser;
        this.problema = problema;
        this.descricao = descricao;
        this.resolvido = resolvido;
        this.reportado = reportado;
        this.dateCriacao = dateCriacao;
        this.dataFinalizacao = dataFinalizacao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Denuncia() {
        setDateCriacao(Calendar.getInstance().getTimeInMillis());
        setResolvido(false);
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }



    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isResolvido() {
        return resolvido;
    }

    public void setResolvido(boolean resolvido) {
        this.resolvido = resolvido;
    }

    public boolean isReportado() {
        return reportado;
    }

    public void setReportado(boolean reportado) {
        this.reportado = reportado;
    }

    public long getDateCriacao() {
        return dateCriacao;
    }

    public void setDateCriacao(long dateCriacao) {
        this.dateCriacao = dateCriacao;
    }

    public long getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(long dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
