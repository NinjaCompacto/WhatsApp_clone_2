package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.R;

import java.net.URI;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GrupoSelecionadoAdapter extends RecyclerView.Adapter<GrupoSelecionadoAdapter.MyViewHolder> {

    private List<Usuario> listausuarios;
    private Context context;

    public GrupoSelecionadoAdapter(List<Usuario> listausuariosselecionados,Context c) {
        this.listausuarios = listausuariosselecionados;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_grupo_selecioando,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GrupoSelecionadoAdapter.MyViewHolder holder, int position) {
            Usuario usuario = listausuarios.get(position);
            holder.textnome.setText(usuario.getNome());

            if (usuario.getFoto() != null){
                Uri uri = Uri.parse(usuario.getFoto());
                Glide.with(context).load(uri).into(holder.foto);
            }else {
                holder.foto.setImageResource(R.drawable.padrao);
            }
    }

    @Override
    public int getItemCount() {
        return listausuarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView foto;
        private TextView textnome;
        public MyViewHolder(View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imageViewMemborSelecionado);
            textnome = itemView.findViewById(R.id.textNomeMembroSelecionado);
        }
    }
}
