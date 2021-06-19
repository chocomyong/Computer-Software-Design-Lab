package com.example.main_project_two0523.bill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_project_two0523.MoneyFormat;
import com.example.main_project_two0523.R;

import java.util.ArrayList;

public class ListViewAdapter_Bill extends RecyclerView.Adapter<ListViewAdapter_Bill.ItemViewHolder> {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<ListViewItem_Bill> listViewItemList = new ArrayList<ListViewItem_Bill>() ;
    private OnItemClickListener listener = null;
        // ListViewAdapter의 생성자
        public ListViewAdapter_Bill() {        }
        public interface OnItemClickListener {
            void onItemClick(View v, int position);
        }
        public void setOnClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bill,parent,false);
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
        public void addItem(ListViewItem_Bill data) {
            listViewItemList.add(data);
        }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView bill_menu_IMG ;
        TextView bill_menu_ID ;
        TextView bill_menu_name ;
        TextView bill_menu_price ;
        TextView order_num;
        MoneyFormat mF;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            bill_menu_IMG = (ImageView) itemView.findViewById(R.id.bill_img_menu_ID) ;
            bill_menu_ID = (TextView) itemView.findViewById(R.id.bill_menu_ID) ;
            bill_menu_name = (TextView) itemView.findViewById(R.id.bill_menu_name) ;
            bill_menu_price = (TextView) itemView.findViewById(R.id.bill_menu_price) ;
            order_num = (TextView) itemView.findViewById(R.id.bill_num);
            mF = new MoneyFormat();// Money Format에 대한 객체 생성 및, 초기화

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
        void onBind(ListViewItem_Bill data){
            bill_menu_ID.setText(data.get_menu_ID());
            bill_menu_name.setText(data.get_menu_name());
            bill_menu_price.setText(mF.myFormatter.format(Integer.parseInt(data.get_menu_price())) +" 원");
            order_num.setText(data.get_menu_number());
            Glide.with(bill_menu_IMG)
                    .load(data.get_IMG())
                    .into(bill_menu_IMG);
        }
    }
    public ListViewItem_Bill getItem(int pos){
        return listViewItemList.get(pos);
    }
}



