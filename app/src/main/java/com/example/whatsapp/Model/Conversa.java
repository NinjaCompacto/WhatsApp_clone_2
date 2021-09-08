package com.example.whatsapp.Model;

import com.example.whatsapp.ConfiguracoesActivity;
import com.google.firebase.database.DatabaseReference;

import configfirebase.ConfiguraçãoFirebase;

public class Conversa {

    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;
    private String imagem;


    public Conversa() {
    }

    public void salvar () {
        DatabaseReference databaseReference = ConfiguraçãoFirebase.getDatabaseReference();
        DatabaseReference conversaref = databaseReference.child("conversas");
        conversaref.child(this.getIdRemetente()).child(this.idDestinatario).setValue(this);
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
