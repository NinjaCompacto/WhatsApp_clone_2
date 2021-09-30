package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.whatsapp.Fragments.ContatosFragment;
import com.example.whatsapp.Fragments.ConversasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import configfirebase.ConfiguraçãoFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao = ConfiguraçãoFirebase.getAuth();
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ToolBar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        //configurar abas
        FragmentPagerItemAdapter adapter = new  FragmentPagerItemAdapter (
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                        .add("Conversas", ConversasFragment.class)
                        .add("Contatos", ContatosFragment.class)
                        .create()
        );
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout smartTabLayout = findViewById(R.id.viewPagerTab);
        smartTabLayout.setViewPager(viewPager);

        //configurando searchview
        searchView = findViewById(R.id.materialSearchPrincipal);
        //listener para searchview
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
            ConversasFragment conversasFragment = (ConversasFragment) adapter.getPage(0);
            ContatosFragment contatosFragment = (ContatosFragment) adapter.getPage(1);
            conversasFragment.recarregarConversas();
            contatosFragment.recarregarContatos();
            }
        });
        //listener para caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //recuperando dados da fragment
                ConversasFragment conversasFragment = (ConversasFragment) adapter.getPage(0);
                ContatosFragment contatosFragment = (ContatosFragment) adapter.getPage(1);
                if ( newText != null && !newText.isEmpty()){
                    conversasFragment.PesquisarConversas(newText.toLowerCase());
                    contatosFragment.PesquisarContatos(newText.toLowerCase());
                }
                if (newText.isEmpty()){
                    conversasFragment.recarregarConversas();
                    contatosFragment.recarregarContatos();
                }
                return true;
            }
        });
    }


    //menu da toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflar o menu da toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //coonfigurando searchview
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       // caso de botão de sair selecionado
        switch (item.getItemId()){
           case R.id.menuSair:
               logOutUsuario();
               //retorna para tela de login ( tela principal )
               finish();
                 break;
           case R.id.menuConfiguracoes:
                openConfigScreen();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logOutUsuario (){
        //deloga o usuario da conta
        try {
            autenticacao.signOut();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openConfigScreen () {
        Intent intent = new Intent(MainActivity.this,ConfiguracoesActivity.class);
        startActivity(intent);
    }
}
