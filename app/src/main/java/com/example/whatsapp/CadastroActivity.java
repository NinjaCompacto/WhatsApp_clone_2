package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Helper.Base64Custom;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import configfirebase.ConfiguraçãoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editNome,editEmail,editSenha;
    private FirebaseAuth autenticacao=ConfiguraçãoFirebase.getAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmailLogin);
        editSenha = findViewById(R.id.editSenhaLogin);
        //autenticacao.signOut();

    }
    public void salvarUsuarioFirebase(Usuario usuario){
        autenticacao.createUserWithEmailAndPassword( usuario.getEmail() , usuario.getSenha() )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this,"Usuário cadastrado!",Toast.LENGTH_LONG).show();
                    finish();
                    salvarDados(usuario);
                    UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                }
                else {
                    String exececao ="";
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        exececao = "Digite uma senha mais forte!";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        exececao = "Digite um e-mail valido!";
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        exececao = "Este usuário já existe!";
                    }
                    catch (Exception e) {
                        exececao = "Erro ao criar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this,exececao,Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void validarCadastro (View view){
        //recupera dados digitados
        String textonome = editNome.getText().toString();
        String textoemail = editEmail.getText().toString();
        String textosenha = editSenha.getText().toString();

        //verificação de preenchimento de dados
        if (!textonome.isEmpty()){
            if (!textoemail.isEmpty()){
                if (!textosenha.isEmpty()){
                    // configurando usuario para passar para "salvarUsuarioFirebase"
                    Usuario usuario = new Usuario();
                    usuario.setNome(textonome);
                    usuario.setEmail(textoemail);
                    usuario.setSenha(textosenha);
                    salvarUsuarioFirebase(usuario);

                }
                else {
                    Toast.makeText(CadastroActivity.this,"Preencha a senha!",Toast.LENGTH_LONG).show();
                }
            }
            else {
            Toast.makeText(CadastroActivity.this,"Preencha o email!",Toast.LENGTH_LONG).show();
            }

        }
        else {
            Toast.makeText(CadastroActivity.this,"Preencha o nome!",Toast.LENGTH_LONG).show();
        }

    }

    public void salvarDados (Usuario usuario){
        try{
            //codifica o email do usuario para base64
            String identificadorusuario = Base64Custom.codificarBase64(usuario.getEmail());
            //atribui valor para o id
            usuario.setId(identificadorusuario);
            //salva os dados no firebase
            usuario.salvar();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}