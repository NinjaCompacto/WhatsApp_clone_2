package com.example.whatsapp.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Helper.UsuarioFirebase;
import com.example.whatsapp.Model.Mensagem;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.R;

import java.util.EventListener;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter <ChatAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE  = 0;
    private static final int TIPO_DESTINATARIO  = 1;


    public ChatAdapter(List<Mensagem> lista , Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //recebe a info se é remetente ou destinatario e infla o layout correspondente
        View item = null;
        if (viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente,parent,false);
        }else if (viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario,parent,false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.MyViewHolder holder, int position) {
        Mensagem mensagem = mensagens.get(position);
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();

        if (imagem != null){
            Uri url = Uri.parse(imagem);
            Glide.with(context).load(url).into(holder.imagem);
            holder.mensagem.setVisibility(View.GONE);
        }else{
            holder.mensagem.setText(msg);
            holder.imagem.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagens.get(position);
        String id = UsuarioFirebase.getIdUsuario();

        //verifica qual usuario deseja aacessar o chat
        if (id.equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        }
        else{
            return TIPO_DESTINATARIO;
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mensagem;
        ImageView imagem;
        public MyViewHolder(View itemView) {
            super(itemView);
            mensagem = itemView.findViewById(R.id.textMensagem);
            imagem = itemView.findViewById(R.id.imageMensagemFoto);
        }
    }
    }

