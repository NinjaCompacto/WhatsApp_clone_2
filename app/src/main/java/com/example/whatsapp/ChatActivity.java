package com.example.whatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Helper.Base64Custom;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Conversa;
import com.example.whatsapp.Model.Grupo;
import com.example.whatsapp.Model.Mensagem;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.adapter.ChatAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import configfirebase.ConfiguraçãoFirebase;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

        //constantes
        private static final int SELECAO_CAMERA = 100;

        //firebase
        private DatabaseReference firebasereference = ConfiguraçãoFirebase.getDatabaseReference();
        private StorageReference storageReference = ConfiguraçãoFirebase.getStorage();
        private DatabaseReference mensagensref;
        private ChildEventListener childEventListenerMensagens;
        private ValueEventListener valueEventListenerMensagens;
        //XML
        private CircleImageView circleImageChat;
        private TextView textNome;
        private Usuario usuariodestinatario;
        private EditText ediTextMessagem;
        private ImageView imageCamera;
        //id dos usuarios
        private String idusuarioremetente, idusuariodestinatario;
        private Grupo grupo;
       //RecyclerView
        private RecyclerView recyclerMensagens;
        private ChatAdapter chatAdapter;
        private List<Mensagem> mensagens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Configuração toolbar
        Toolbar toolbar = findViewById(R.id.toolbarChat);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configurações iniciais
        textNome = findViewById(R.id.textNomeChat);
        circleImageChat = findViewById(R.id.circleImageChat);
        ediTextMessagem = findViewById(R.id.editTextMessagem);
        idusuarioremetente = UsuarioFirebase.getIdUsuario();
        imageCamera = findViewById(R.id.imageCamera);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);

        //recuperando dados do usuariodestinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle !=  null) {

            if (bundle.containsKey("chatgrupo")) {

                grupo = (Grupo) bundle.getSerializable("chatgrupo");
                textNome.setText(grupo.getNome());
                idusuariodestinatario = grupo.getId();
                if (grupo.getFoto() != null){
                    Uri urlfoto = Uri.parse(grupo.getFoto());
                    Glide.with(ChatActivity.this).load(urlfoto).into(circleImageChat);
                }

            } else {
                usuariodestinatario = (Usuario) bundle.getSerializable("chatcontato");
                textNome.setText(usuariodestinatario.getNome());
                if (usuariodestinatario.getFoto() != null) {
                    Uri urlfoto = Uri.parse(usuariodestinatario.getFoto());
                    Glide.with(getApplication()).load(urlfoto).into(circleImageChat);
                }
                //recuperar id do usuario destinatario
                idusuariodestinatario = Base64Custom.codificarBase64(usuariodestinatario.getEmail());
            }
        }
        //atualizarMensagens();

        //configurando adapter
        chatAdapter = new ChatAdapter(mensagens,getApplicationContext());
        //configurando recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(chatAdapter);

        mensagensref = firebasereference.child("mensagem").child(idusuarioremetente).child(idusuariodestinatario);

        //evento de click Botão camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_CAMERA);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                Bitmap imagem = null;
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap)data.getExtras().get("data");
                        break;
                }
                if (imagem != null){
                    //recuperando dados da imagem
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70 ,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //nome da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //salvar imagem no firebase
                    final StorageReference  imagemref = storageReference.child("imagens").child("foto")
                            .child(idusuarioremetente)
                            .child(nomeImagem);


                    UploadTask uploadTask = imagemref.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.d("Erro","Erro ao fazer upload");
                            Toast.makeText(ChatActivity.this, "Erro ao fazer upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ChatActivity.this,"Sucesso ao fazer upload",Toast.LENGTH_SHORT).show();
                            imagemref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(Task<Uri> task) {
                                    Uri url = task.getResult();
                                    Mensagem mensagem = new Mensagem();
                                    mensagem.setIdUsuario(idusuarioremetente);
                                    mensagem.setMensagem("imagem.jpeg");
                                    mensagem.setImagem(url.toString());
                                    //salvar imagem
                                    salvarMensagem(idusuarioremetente,idusuariodestinatario,mensagem);
                                }
                                 });


                        }


                    });
                }

            }
            else {
                Toast.makeText(ChatActivity.this,"erro",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void enviar (View view) {
        String Textomensagem = ediTextMessagem.getText().toString();

        //verifica se a mensagem tem algum conteudo
        if (!Textomensagem.isEmpty()){
            Mensagem msg = new Mensagem();
            msg.setMensagem(Textomensagem);
            msg.setIdUsuario(idusuarioremetente);
            //salvando mensagem
            salvarMensagem(idusuarioremetente,idusuariodestinatario,msg);
            //salvando conversa
            salvarConversa(msg);
        }
        else{
            Toast.makeText(ChatActivity.this,"Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();;
        }


    }

    private void salvarMensagem (String idRemetente, String idDestinatario , Mensagem mensagem){
        mensagensref = firebasereference.child("mensagem");
        //salvar mensagem para remetente e destinatario
        mensagensref.child(idRemetente).child(idDestinatario).push().setValue(mensagem);
        mensagensref.child(idDestinatario).child(idRemetente).push().setValue(mensagem);
        ediTextMessagem.setText("");
    }

    private void salvarConversa (Mensagem mensagem){
            Conversa conversa = new Conversa();
            conversa.setIdRemetente(idusuarioremetente);
            conversa.setIdDestinatario(idusuariodestinatario);
            conversa.setUsuarioExibicao(usuariodestinatario);
            conversa.setUltimaMensagem(mensagem.getMensagem());
            conversa.salvar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensref.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens () {



        childEventListenerMensagens = mensagensref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot snapshot,  String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged( DataSnapshot snapshot,  String previousChildName) {

            }

            @Override
            public void onChildRemoved( DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved( DataSnapshot snapshot,  String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

}