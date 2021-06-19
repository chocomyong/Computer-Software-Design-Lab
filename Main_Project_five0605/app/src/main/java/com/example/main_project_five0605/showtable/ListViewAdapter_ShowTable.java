package com.example.main_project_five0605.showtable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_project_five0605.R;

import java.util.ArrayList;

import static com.example.main_project_five0605.R.drawable.ripple_button;
import static com.example.main_project_five0605.R.drawable.ripple_button_papaya;

public class ListViewAdapter_ShowTable extends RecyclerView.Adapter<ListViewAdapter_ShowTable.ItemViewHolder> {

    Context context;
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem_ShowTable> listViewItemList_ShowTable = new ArrayList<ListViewItem_ShowTable>();



    // ListViewAdapter의 생성자
    public ListViewAdapter_ShowTable() {   }



    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_setting_showtable, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listViewItemList_ShowTable.get(position));
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listViewItemList_ShowTable.size();
    }

    public void addItem(ListViewItem_ShowTable data) {
        listViewItemList_ShowTable.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView ID;
        Button btn_table;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ID = (TextView)itemView.findViewById(R.id.ID);
            btn_table = (Button)itemView.findViewById(R.id.btn_table);

        }


        void onBind(ListViewItem_ShowTable data) {
            ID.setText(data.get_ID());
            btn_table.setText(data.get_ID()+"번 테이블"); //ID gettext 해도 안됨
            if(data.get_occ().equals("1")){
//                btn_table.setBackgroundColor(shadow_red);
                btn_table.setTextColor(Color.WHITE);

                btn_table.setBackgroundResource(ripple_button_papaya);
            }
            else{
                btn_table.setBackgroundResource(ripple_button);

                btn_table.setTextColor(Color.WHITE);

              //  btn_table.setBackgroundResource(R.drawable.shadow );


            }


        }


    }

    public ListViewItem_ShowTable getItem(int pos) {
        return listViewItemList_ShowTable.get(pos);
    }
}