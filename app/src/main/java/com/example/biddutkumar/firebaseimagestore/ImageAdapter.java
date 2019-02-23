package com.example.biddutkumar.firebaseimagestore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageAdapterHolder> {

    private Context context;
    private List<Upload> uploadList;
    private OnItemClickListener listener;

    public ImageAdapter(Context context, List<Upload> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
    }
    @NonNull
    @Override
    public ImageAdapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_layout,viewGroup,false);
        return new ImageAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapterHolder imageAdapterHolder, int i) {

        Upload upload=uploadList.get(i);
        imageAdapterHolder.textView.setText(upload.getImageName());
        Picasso.with(context)
                .load(upload.getImageUri())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerCrop()
                .into(imageAdapterHolder.imageView);

    }
    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class ImageAdapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener {
        TextView textView;
        ImageView imageView;

        public ImageAdapterHolder(@NonNull View itemView) {
            super(itemView);

            textView=itemView.findViewById(R.id.cardTVId);
            imageView=itemView.findViewById(R.id.cardImageVId);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {

            if(listener!=null)
            {
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION)
                {
                    listener.onItemClick(position);
                }
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Choose an action ");
            MenuItem doAnyTask=menu.add(Menu.NONE,1 ,1, "do Any Task");
            MenuItem delete =menu.add(Menu.NONE,2,2, "delete ");

            doAnyTask.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(listener!=null)
            {
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId())
                    {
                        case 1:
                            listener.onDoAnyTask(position);
                            return true;

                        case 2:
                            listener.onDelete(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }
    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onDoAnyTask(int position);
        void onDelete(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener=listener;

    }


}
