package com.example.main_project_two0523.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_project_two0523.MoneyFormat;
import com.example.main_project_two0523.R;

import java.util.ArrayList;

public class ListViewAdapter_Orders extends RecyclerView.Adapter<ListViewAdapter_Orders.ItemViewHolder> {

    int Number_Of_Menu = 0; // Dialog 처음 시작 시 입력되는 가격, menu 의 개수에 따라 first_menu_price의 배수만큼 증가.
    int cur_price = 0; // dialog 에서 사용하는 현재 가격
    int first_price = 0;
    Context context;
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem_Orders> listViewItemList = new ArrayList<ListViewItem_Orders>();


    private OnItemClickListener listener = null;


    // ListViewAdapter의 생성자
    public ListViewAdapter_Orders() {   }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
        void onItemNumUpClick(View v, int position);
        void onItemNumDownClick(View v, int position);
        void destroyItemClick(View v, int position);

    }
    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listViewItemList.get(position));
        holder.order_numdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = v.getId();
                //Toast.makeText(context, "RE : DOWN, "+ v.getId()+", "+ pos +", "+ position, Toast.LENGTH_SHORT).show();

                if(pos != RecyclerView.NO_POSITION){
                    if(listener != null){
                        listener.onItemNumDownClick(v, position);

                    }
                }
            }
        });
        holder.order_numup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = v.getId();
                if(pos != RecyclerView.NO_POSITION){
                    if(listener != null){
                        listener.onItemNumUpClick(v, position);

                    }
                }

            }
        });
        holder.xbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = v.getId();
                if(pos != RecyclerView.NO_POSITION){
                    if(listener != null){
                        listener.destroyItemClick(v, position);
                    }
                }
            }
        });


    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(ListViewItem_Orders data) {
        listViewItemList.add(data);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView order_menu_IMG;
        TextView order_menu_ID;
        TextView order_menu_name;
        TextView order_menu_price, order_num;
        Button order_numup, order_numdown;
        Button xbtn;
        MoneyFormat mF;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            order_menu_IMG = (ImageView) itemView.findViewById(R.id.order_img_menu_ID);
            order_menu_ID = (TextView) itemView.findViewById(R.id.order_menu_ID);
            order_menu_name = (TextView) itemView.findViewById(R.id.order_menu_name);
            order_menu_price = (TextView) itemView.findViewById(R.id.order_menu_price);

            order_num = (TextView) itemView.findViewById(R.id.order_num);
            order_numup = (Button) itemView.findViewById(R.id.order_numup);
            order_numdown = (Button)  itemView.findViewById(R.id.order_numdown);
            xbtn = (Button) itemView.findViewById(R.id.xbtn);
            mF = new MoneyFormat();




        }

        void onBind(ListViewItem_Orders data) {

            order_menu_ID.setText(data.get_menu_ID());
            order_menu_name.setText(data.get_menu_name());
            order_menu_price.setText(mF.myFormatter.format(Integer.parseInt(data.get_menu_price())) + " 원");
            order_num.setText(data.get_menu_number());

            first_price = Integer.parseInt(data.get_menu_price());
            cur_price = first_price;
            Number_Of_Menu = Integer.parseInt(data.get_menu_number());

            Glide.with(order_menu_IMG)
                    .load(data.get_IMG())
                    .into(order_menu_IMG);
        }

    }

    public ListViewItem_Orders getItem(int pos) {
        return listViewItemList.get(pos);
    }


}



