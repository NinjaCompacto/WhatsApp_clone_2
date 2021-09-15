package com.example.whatsapp;

import android.os.Bundle;

import com.example.whatsapp.Fragments.ContatosFragment;
import com.example.whatsapp.Helper.RecyclerItemClickListener;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.adapter.ContatosAdapter;
import com.example.whatsapp.adapter.GrupoSelecionadoAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        //configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configurações iniciais
        recyclerMembros = findViewById(R.id.reccylcerMembros);
        recyclerMembrosSelecionados = findViewById(R.id.reclycerMembrosSelecionados);
        usuariosref = ConfiguraçãoFirebase.getDatabaseReference().child("usuarios");

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
                        listaMembors.remove(usuarioselecionado);
                        contatosAdapter.notifyDataSetChanged();
                        listaMembrosSelecionados.add(usuarioselecionado);
                        grupoSelecionadoAdapter.notifyDataSetChanged();
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
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

    }

}