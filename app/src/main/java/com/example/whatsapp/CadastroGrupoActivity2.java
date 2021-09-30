package com.example.whatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Grupo;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.adapter.GrupoSelecionadoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import configfirebase.ConfiguraçãoFirebase;
import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity2 extends AppCompatActivity {

    private Grupo grupo;
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    //XML
    private Toolbar toolbar;
    private TextView textParticipantes;
    private RecyclerView recyclerViewMembrosGrupo;
    private EditText editTextNomeGrupo;
    private CircleImageView imagemGrupo;
    private FloatingActionButton fabSalvarGrupo;
    //Constantes
    private static final int SELECAO_GALEIRA = 200;
    //recyclerView Adapters
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    //Firebase
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Novo Grupo");
        toolbar.setSubtitle("Adicione nome");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configurações iniciais
        editTextNomeGrupo = findViewById(R.id.editTextNomeGrupo);
        textParticipantes = findViewById(R.id.textTotalParticipantes);
        recyclerViewMembrosGrupo = findViewById(R.id.recyclerMembrosGrupo);
        editTextNomeGrupo = findViewById(R.id.editTextNomeGrupo);
        imagemGrupo = findViewById(R.id.imagemGrupo);
        storageReference = ConfiguraçãoFirebase.getStorage();
        grupo = new Grupo();
        fabSalvarGrupo = findViewById(R.id.fabSalvarGrupo);

        //configura evento de click para imagem
        imagemGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_GALEIRA);
                }
            }
        });

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

        //adicona evento de click no fab para salvar o grupo
        fabSalvarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeGrupo = editTextNomeGrupo.getText().toString();
                grupo.setNome(nomeGrupo);
                listaMembrosSelecionados.add(UsuarioFirebase.getUsuarioLogado());
                grupo.setMembros(listaMembrosSelecionados);
                grupo.salvar();
                Intent i = new Intent(CadastroGrupoActivity2.this,ChatActivity.class);
                i.putExtra("chatgrupo", grupo);
                finish();
                startActivity(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //recupera imagem selecionada e seta a imagem do grupo
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                Uri imagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),imagemSelecionada);

                if (imagem != null){
                    imagemGrupo.setImageBitmap(imagem);

                    //recuperar dados da imagem para salvar no firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salva a imagem no firebase
                    StorageReference imagemref = storageReference
                            .child("imagens")
                            .child("grupos")
                            .child(grupo.getId()+".jpeg");

                    UploadTask uploadTask = imagemref.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(CadastroGrupoActivity2.this,"Erro ao fazer upload",Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CadastroGrupoActivity2.this,"Sucesso ao fazer Upload",Toast.LENGTH_LONG).show();
                            //recupera a url da imagem que foi upada para o Firebase
                            imagemref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(Task<Uri> task) {
                                    String url = task.getResult().toString();
                                    grupo.setFoto(url);
                                }
                            });
                        }
                    });
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}