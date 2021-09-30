package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Model.Conversa;
import com.example.whatsapp.Model.Grupo;
import com.example.whatsapp.Model.Mensagem;
import com.example.whatsapp.Model.Usuario;
import com.example.whatsapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> contatosConversa;
    private Context context;


    public ConversasAdapter (List<Conversa> contatos, Context c){
        this.contatosConversa = contatos;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemlista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos,parent,false);
        return new MyViewHolder(itemlista);
    }

    @Override
    public void onBindViewHolder(ConversasAdapter.MyViewHolder holder, int position) {
        Conversa conversa = contatosConversa.get(position);
        holder.textUltimaMsg.setText(conversa.getUltimaMensagem());

        //verifica se a conversa é de grupo
        if (conversa.getIsGrupo().equals("true")){
            Grupo grupo = conversa.getGrupo();
            holder.textNome.setText(grupo.getNome());

            if (grupo.getFoto() != null){
                Uri url =Uri.parse(grupo.getFoto());
                Glide.with(context).load(url).into(holder.circleImagePerfil);
            }
            else {
                holder.circleImagePerfil.setImageResource(R.drawable.padrao);
            }

        }
        else {
            Usuario usuario = conversa.getUsuarioExibicao();
            if (usuario != null) {
                holder.textNome.setText(usuario.getNome());
                holder.textUltimaMsg.setText(conversa.getUltimaMensagem());

                if (usuario.getFoto() != null) {
                    Uri url = Uri.parse(usuario.getFoto());
                    Glide.with(context).load(url).into(holder.circleImagePerfil);
                } else {
                    holder.circleImagePerfil.setImageResource(R.drawable.padrao);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return contatosConversa.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImagePerfil;
        private TextView textNome,textUltimaMsg;
        public MyViewHolder( View itemView) {
            super(itemView);
            circleImagePerfil = itemView.findViewById(R.id.imageViewContato);
            textNome = itemView.findViewById(R.id.textViewNomeContato);
            textUltimaMsg = itemView.findViewById(R.id.textViewEmailContato);
        }
    }
}
