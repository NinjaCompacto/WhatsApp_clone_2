package com.example.whatsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.ChatActivity;
import com.example.whatsapp.Helper.RecyclerItemClickListener;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Conversa;
import com.example.whatsapp.Model.Mensagem;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.ConversasAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import configfirebase.ConfiguraçãoFirebase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConversasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConversasAdapter conversasAdapter;
    private List<Conversa> listConversas = new ArrayList<>();
    private String idusuario = UsuarioFirebase.getIdUsuario();
    private  ChildEventListener childEventListenerconversas;
    private DatabaseReference conversaref;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConversasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversasFragment newInstance(String param1, String param2) {
        ConversasFragment fragment = new ConversasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        conversaref.removeEventListener(childEventListenerconversas);
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_conversas, container, false);
        recyclerView =view1.findViewById(R.id.recyclerListaConversas);

        //configurando adpter
        conversasAdapter = new ConversasAdapter(listConversas,getActivity());
        //configurando recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(conversasAdapter);
        //configurando evento de click
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Conversa conversa = listConversas.get(position);
                        Usuario usuario = conversa.getUsuarioExibicao();
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra("chatcontato",usuario);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

        //configura conversasref
        String idusuario = UsuarioFirebase.getIdUsuario();
        conversaref = ConfiguraçãoFirebase.getDatabaseReference().child("conversas").child(idusuario);

        return view1;
    }


    //resolver problema de recuperação de dados para listagem
    public void recuperarConversas () {
        listConversas.clear();
        childEventListenerconversas =conversaref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot,String previousChildName) {
            //recuperar conversas
                Conversa conversa = snapshot.getValue(Conversa.class);
                listConversas.add(conversa);
                conversasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot,String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void PesquisarConversas (String texto){

        List<Conversa> listaConversasBusca = new ArrayList<>();
        for (Conversa conversa : listConversas){

            String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
            String ultimmsg = conversa.getUltimaMensagem().toLowerCase();

            if ( nome.contains(texto) || ultimmsg.contains(texto)){
                listaConversasBusca.add(conversa);
            }
        }
        conversasAdapter = new ConversasAdapter(listaConversasBusca,getActivity());
        recyclerView.setAdapter(conversasAdapter);
        conversasAdapter.notifyDataSetChanged();

    }

    public void recarregarConversas (){
        conversasAdapter = new ConversasAdapter(listConversas,getActivity());
        recyclerView.setAdapter(conversasAdapter);
        conversasAdapter.notifyDataSetChanged();
    }

}