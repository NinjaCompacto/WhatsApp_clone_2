package com.example.whatsapp.Model;

import com.example.whatsapp.Helper.Base64Custom;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import configfirebase.ConfiguraçãoFirebase;

public class Grupo implements Serializable {
    private String  id;
    private String  nome;
    private String  foto;
    private List<Usuario>   membros;

    public Grupo() {
        DatabaseReference database = ConfiguraçãoFirebase.getDatabaseReference();
        DatabaseReference gruporef = database.child("grupos");
        String idGrupoFirebase = gruporef.push().getKey();
        setId(idGrupoFirebase);
    }

    public void salvar (){
        DatabaseReference database = ConfiguraçãoFirebase.getDatabaseReference();
        DatabaseReference gruporef = database.child("grupos");
        gruporef.child(getId()).setValue(this);

        //salvando conversa
        for (Usuario membro : getMembros()){
            String idRemetente = Base64Custom.codificarBase64(membro.getEmail());
            String idDestinatario = getId();

            Conversa conversa = new Conversa();
            conversa.setIdRemetente(idRemetente);
            conversa.setIdDestinatario(idDestinatario);
            conversa.setUltimaMensagem("");
            conversa.setIsGrupo("true");
            conversa.setGrupo(this);
            conversa.salvar();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }
}
