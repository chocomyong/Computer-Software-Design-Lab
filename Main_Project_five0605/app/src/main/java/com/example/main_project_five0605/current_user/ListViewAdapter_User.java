package com.example.main_project_five0605.current_user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.main_project_five0605.R;

import java.util.ArrayList;

public class ListViewAdapter_User extends RecyclerView.Adapter<ListViewAdapter_User.ItemViewHolder> {

        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<ListViewItem_User> listViewItemListUser = new ArrayList<ListViewItem_User>() ;
    private OnItemClickListener listener = null;
        public ListViewAdapter_User() {
        }
        public interface OnItemClickListener {
            void onItemClick(View v, int position);
        }
        public void setOnClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cur_user,parent,false);

            return new ItemViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            holder.onBind(listViewItemListUser.get(position));
        }
        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }
        @Override
        public int getItemCount() {
            return listViewItemListUser.size();
        }
        // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
        public void addItem(ListViewItem_User data) {
            listViewItemListUser.add(data);
        }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView order_ID ;
        TextView user_ID ;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            order_ID = (TextView) itemView.findViewById(R.id.order_ID) ;
            user_ID = (TextView) itemView.findViewById(R.id.user_ID) ;
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
        void onBind(ListViewItem_User data){
            order_ID.setText(data.get_order_ID());
            user_ID.setText(data.get_user_ID());


        }
    }
    public ListViewItem_User getItem(int pos){
        return listViewItemListUser.get(pos);
    }
}



