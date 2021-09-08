package com.example.whatsapp.Model;


import com.example.whatsapp.Helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import configfirebase.ConfiguraçãoFirebase;

public class Usuario implements Serializable {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;


    public String getNome() {
        return nome;
    }

    public void salvar(){
        DatabaseReference databaseReference = ConfiguraçãoFirebase.getDatabaseReference();
        DatabaseReference usuario = databaseReference.child("usuarios").child(getId());

        usuario.setValue(this);
    }

    public void atualizar () {

        String idusuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference database = ConfiguraçãoFirebase.getDatabaseReference();
        DatabaseReference usuarioref = database.child("usuarios").child(idusuario);
        //transfomando Usuario em Map
        Map<String,Object> valoresUsuario = converterParaMap();
        //Atualizando os childs
        usuarioref.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String,Object> converterParaMap () {
        HashMap<String, Object> usuariomap = new HashMap<>();
        usuariomap.put("email",getEmail());
        usuariomap.put("nome",getNome());
        usuariomap.put("foto",getFoto());
        return usuariomap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
