package com.example.main_project_two0523.restaurants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_project_two0523.R;
import com.example.main_project_two0523.restaurants.ListViewItem_Restaurants;

import java.util.ArrayList;

public class ListViewAdapter_Restaurants  extends RecyclerView.Adapter<ListViewAdapter_Restaurants.ItemViewHolder> {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem_Restaurants> listViewItemList = new ArrayList<ListViewItem_Restaurants>() ;
    private OnItemClickListener listener = null;
    // ListViewAdapter의 생성자
    public ListViewAdapter_Restaurants() {
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_restaurant,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listViewItemList.get(position));
    }



    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }



    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }


    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(ListViewItem_Restaurants data) {

        listViewItemList.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView res_IMG ;
        TextView res_ID ;
        TextView res_name ;
        TextView res_rating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            res_IMG = (ImageView) itemView.findViewById(R.id.img_res_ID) ;
            res_ID = (TextView) itemView.findViewById(R.id.id) ;
            res_name = (TextView) itemView.findViewById(R.id.name) ;
            res_rating = (TextView) itemView.findViewById(R.id.res_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(listener != null){
                            listener.onItemClick(v, pos);
                        }

                    }
                }
            });

        }

        void onBind(ListViewItem_Restaurants data){

            res_ID.setText(data.get_res_ID());
            res_name.setText(data.get_res_name());
            res_rating.setText(data.get_res_rating()+"");

           // res_IMG.setImageBitmap(data.getIcon());
            Glide.with(res_IMG)
                    .load(data.get_IMG())
                    .into(res_IMG);
        }

    }
    public ListViewItem_Restaurants getItem(int pos){
        return listViewItemList.get(pos);
    }


}



