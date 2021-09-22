package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.whatsapp.Helper.RecyclerItemClickListener;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.adapter.ContatosAdapter;
import com.example.whatsapp.adapter.GrupoSelecionadoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import configfirebase.ConfiguraçãoFirebase;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerMembrosSelecionados,recyclerMembros;
    private ContatosAdapter contatosAdapter;
    private List<Usuario> listaMembors = new ArrayList<>();
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference usuariosref;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    public void atualizarToolbar (){
        int selecionados = listaMembrosSelecionados.size();
        int total = listaMembors.size() + selecionados;
        toolbar.setSubtitle(selecionados + " de " + total + " selecionados");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        //configura toolbar
        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       //configurações iniciais
        recyclerMembros = findViewById(R.id.reccylcerMembros);
        fab = findViewById(R.id.fabGrupo);
        recyclerMembrosSelecionados = findViewById(R.id.reclycerMembrosSelecionados);
        usuariosref = ConfiguraçãoFirebase.getDatabaseReference().child("usuarios");

        //set ação para fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GrupoActivity.this,CadastroGrupoActivity2.class);
                i.putExtra("membros", (Serializable) listaMembrosSelecionados);
                startActivity(i);
            }
        });

        //configurar adpter de membros
            //obs: Utilizar mesmo adapter dos contatos
        contatosAdapter = new ContatosAdapter(listaMembors,getApplicationContext());

        //configurar recycler para membros ( contatos )
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(contatosAdapter);
        //adiciona evento de click para o recylcer dos membros , para que add o membro selecionado ao recylcer de membros selecionados
        recyclerMembros.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerMembros,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioselecionado = listaMembors.get(position);
                        listaMembrosSelecionados.add(usuarioselecionado);
                        grupoSelecionadoAdapter.notifyDataSetChanged();
                        listaMembors.remove(usuarioselecionado);
                        contatosAdapter.notifyDataSetChanged();
                        atualizarToolbar();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
        //configurar adapter de Membros selecionados
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados,getApplicationContext());
        //configura recycler para membros selecionados
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,
                false);
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        //adicona evento de click para o recycler dos mebors ja selecionados, para que possa ser retirado e adicionado aos recycler de membros
        recyclerMembrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerMembrosSelecionados, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioselecionado = listaMembrosSelecionados.get(position);
                listaMembrosSelecionados.remove(usuarioselecionado);
                grupoSelecionadoAdapter.notifyDataSetChanged();
                listaMembors.add(usuarioselecionado);
                contatosAdapter.notifyDataSetChanged();
                atualizarToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMembros();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuariosref.removeEventListener(valueEventListenerMembros);
    }

    public void recuperarMembros (){
        listaMembors.clear();
        valueEventListenerMembros = usuariosref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    String emailatual = UsuarioFirebase.getUsuarioatual().getEmail();
                    if (!emailatual.equals(usuario.getEmail()) ){
                        listaMembors.add(usuario);
                    }
                    contatosAdapter.notifyDataSetChanged();
                    atualizarToolbar();
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }

}