package com.example.whatsapp;

import android.os.Bundle;

import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.adapter.GrupoSelecionadoAdapter;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity2 extends AppCompatActivity {

    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    //XML
    private Toolbar toolbar;
    private TextView textParticipantes;
    private RecyclerView recyclerViewMembrosGrupo;
    private EditText editTextNomeGrupo;
    private CircleImageView imagemGrupo;

    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Novo Grupo");
        toolbar.setSubtitle("Adicione nome");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //seta XML
        textParticipantes = findViewById(R.id.textTotalParticipantes);
        recyclerViewMembrosGrupo = findViewById(R.id.recyclerMembrosGrupo);
        editTextNomeGrupo = findViewById(R.id.editTextNomeGrupo);
        imagemGrupo = findViewById(R.id.imagemGrupo);

        //recebe lista de usuarios selecionados e seta o numero de participantes
        if (getIntent().getExtras() != null){
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listaMembrosSelecionados.addAll(membros);
            textParticipantes.setText("Participantes : " + listaMembrosSelecionados.size());
        }

        //configura adapter
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados,getApplicationContext());
        //configuração recycler
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerViewMembrosGrupo.setLayoutManager(layoutManagerHorizontal);
        recyclerViewMembrosGrupo.setHasFixedSize(true);
        recyclerViewMembrosGrupo.setAdapter(grupoSelecionadoAdapter);

    }
}