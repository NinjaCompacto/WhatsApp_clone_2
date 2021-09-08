package com.example.whatsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.ChatActivity;
import com.example.whatsapp.Helper.RecyclerItemClickListener;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.ContatosAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import configfirebase.ConfiguraçãoFirebase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContatosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listacontatos = new ArrayList<>();
    private DatabaseReference usuarioref;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioatual;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContatosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContatosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContatosFragment newInstance(String param1, String param2) {
        ContatosFragment fragment = new ContatosFragment();
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
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
         super.onStop();
         usuarioref.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        usuarioatual = UsuarioFirebase.getUsuarioatual();

        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_contatos, container, false);
        recyclerViewListaContatos = view.findViewById(R.id.recyclerListaContatos);
        usuarioref = ConfiguraçãoFirebase.getDatabaseReference().child("usuarios");

        //configurando adpter
        adapter = new ContatosAdapter(listacontatos,getActivity());
        //configurando recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter);
        //configurar evento de click
        recyclerViewListaContatos.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewListaContatos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listacontatos.get(position);
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra("chatcontato",usuarioSelecionado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

        return view;
    }

    public void recuperarContatos (){

        listacontatos.clear();
       valueEventListenerContatos = usuarioref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dados: snapshot.getChildren()) {
                    Usuario usuario = dados.getValue(Usuario.class);
                    String emailusueratual = usuarioatual.getEmail();
                    if (!emailusueratual.equals(usuario.getEmail())) {
                        listacontatos.add(usuario);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}