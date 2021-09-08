package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Helper.Permissao;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import configfirebase.ConfiguraçãoFirebase;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageView imageSalvarNome;
    private EditText editTextTextPersonName;
    private ImageButton imagebuttonGaleria,imagebuttonCamera;
    private CircleImageView circleImageView;
    private StorageReference storageReference;
    private String idusuairo;
    private Usuario usuariologado;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //configurações iniciais
        storageReference = ConfiguraçãoFirebase.getStorage();
        idusuairo = UsuarioFirebase.getIdUsuario();
        usuariologado = UsuarioFirebase.getUsuarioLogado();

        //setando ID
        imagebuttonCamera = findViewById(R.id.imageButtonCamera);
        imagebuttonGaleria = findViewById(R.id.imageButtonGaleria);
        circleImageView = findViewById(R.id.circleImageView);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        imageSalvarNome = findViewById(R.id.ImagemSalvarNome);

        //excultando permissões
        Permissao.validarPermissoes(permissoesNecessarias, this,1);

        //ToolBar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        //adiciona botão de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recuperando usuario atual
        FirebaseUser usuario = UsuarioFirebase.getUsuarioatual();
        Uri url = usuario.getPhotoUrl();

        //verificação se já tem imagem, se não houver estão sera configurada a imagem padrão
        if (url != null){
            //faz dowload e substitui a imagem
            Glide.with(ConfiguracoesActivity.this).load(url).into(circleImageView);
        }else {
            circleImageView.setImageResource(R.drawable.padrao);
        }
        String nome = usuario.getDisplayName();

        if (nome !=  null){
            editTextTextPersonName.setText(nome);
        }else{
            editTextTextPersonName.setText("Nome");
        }

        imagebuttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //verifica se ação foi resolvida com sucesso
                if (i.resolveActivity(getPackageManager())!= null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        imagebuttonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        Uri Imagemselecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),Imagemselecionada);
                        break;
                }

                if (imagem != null){
                    circleImageView.setImageBitmap(imagem);

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosimagem = baos.toByteArray();

                    //salvar imagem no firebase
                    final StorageReference  imagemref = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idusuairo)
                            .child("perfil.jpeg");

                    UploadTask uploadTask = imagemref.putBytes(dadosimagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this,"Erro ao fazer upload",Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this,"Sucesso ao fazer Upload",Toast.LENGTH_LONG).show();
                            //recupera a url da imagem que foi upada para o Firebase
                            imagemref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);
                                }
                            });
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizarFotoUsuario (Uri url){
        UsuarioFirebase.atualizaFotoUsuario(url);
        usuariologado.setFoto(url.toString());
        usuariologado.atualizar();
    }

    //atualiza nome de usuario no perfil do firebase
    public void atualizarNomeUsuario (View view){

        String nomeatual = editTextTextPersonName.getText().toString();
        boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nomeatual);
       if (!nomeatual.isEmpty()) {
           if (retorno == true) {
               Toast.makeText(ConfiguracoesActivity.this, "Sucesso ao atualizar nome !", Toast.LENGTH_SHORT).show();
               finish();
               usuariologado.setNome(nomeatual);
               usuariologado.atualizar();
               //atualizarNomeBanco(nomeatual);
           }
           else {
               Toast.makeText(ConfiguracoesActivity.this, "Erro ao atualizar nome !", Toast.LENGTH_SHORT).show();
           }
       }
       else{
           Toast.makeText(ConfiguracoesActivity.this, "Preencha o Nome !", Toast.LENGTH_SHORT).show();
       }


    }

    /*
    //atualiza o nome no banco de dados assim que é atualizado
    private void atualizarNomeBanco(String nome) {

        String nomeatual = nome;
        FirebaseUser user = UsuarioFirebase.getUsuarioatual();
        String email = user.getEmail();
        String emailcodificado = Base64Custom.codificarBase64(email);
        DatabaseReference databaseReference = ConfiguraçãoFirebase.getDatabaseReference();
        DatabaseReference usuario = databaseReference.child("usuarios").child(emailcodificado).child("nome");
        usuario.setValue(nomeatual).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (!task.isSuccessful()){
                    ToastMaker.makeToast(ConfiguracoesActivity.this,"Erro ao atualizar banco de dados");
                }
            }
        });

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoresultado: grantResults){
            if (permissaoresultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permissões Negadas");
            builder.setMessage("Para utilizar o APP é preciso aceitar todas as permissões");
            builder.setCancelable(false);
            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }


}