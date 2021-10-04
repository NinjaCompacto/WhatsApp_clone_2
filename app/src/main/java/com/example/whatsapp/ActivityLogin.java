package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import configfirebase.ConfiguraçãoFirebase;

public class ActivityLogin extends AppCompatActivity {

    private TextInputEditText editEmailLogin,editSenhaLogin;
    private FirebaseAuth autenticacao = ConfiguraçãoFirebase.getAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //xml
        editEmailLogin = findViewById(R.id.editEmailLogin);
        editSenhaLogin = findViewById(R.id.editSenhaLogin);
        //autenticacao.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //verifica se o usuario esta logado
        if (autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }

    public void logarUsuario (Usuario usuario){

        //autentica usuario com email e senha recuperados
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    abrirTelaPrincipal();
                    Toast.makeText(ActivityLogin.this, "Sucesso ao Logar", Toast.LENGTH_LONG).show();
                }
                else {
                    String execao = "";
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                       execao = "Email e senha não correspondem!";
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        execao = "Usuário invalido!";
                    }
                    catch (Exception e) {
                        execao = "Erro ao Logar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(ActivityLogin.this,execao,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //classe atribuida ao botão "Logar" para autenticar usuario
    public void ValidarUsuario (View view){
        //recupera informações digitadas
        String email,senha;
        email = editEmailLogin.getText().toString();
        senha = editSenhaLogin.getText().toString();

        //verificação de campos
        if (!email.isEmpty()){
            if (!senha.isEmpty()) {
                    // configurando obejto usuario
                    Usuario usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);

                    // passando usuario como parametro
                    logarUsuario(usuario);


            }
            else {
             Toast.makeText(ActivityLogin.this,"Preencha o senha!",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(ActivityLogin.this,"Preencha o email!",Toast.LENGTH_LONG).show();
        }

    }

    public void abrirTelaCadastro (View view){
        Intent intent = new Intent(ActivityLogin.this,CadastroActivity.class);
        startActivity(intent);

    }

    public void abrirTelaPrincipal (){
        Intent intent = new Intent(ActivityLogin.this,MainActivity.class);
        startActivity(intent);
    }

}

