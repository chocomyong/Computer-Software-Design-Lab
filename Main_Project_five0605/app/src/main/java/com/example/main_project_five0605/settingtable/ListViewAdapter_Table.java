package com.example.main_project_five0605.settingtable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_project_five0605.MoneyFormat;
import com.example.main_project_five0605.R;

import java.util.ArrayList;

import static com.example.main_project_five0605.R.drawable.*;

public class ListViewAdapter_Table extends RecyclerView.Adapter<ListViewAdapter_Table.ItemViewHolder> {

    Context context;
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem_Table> listViewItemList_Table = new ArrayList<ListViewItem_Table>();


    private OnItemClickListener listener = null;

    // ListViewAdapter의 생성자
    public ListViewAdapter_Table() {   }

    public interface OnItemClickListener {

        void onSettingTable(View v, int position);

    }


    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_setting_table, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listViewItemList_Table.get(position));


        holder.btn_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = v.getId();
                if(pos != RecyclerView.NO_POSITION){
                    if(listener != null){
                        listener.onSettingTable(v, position);
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
        return listViewItemList_Table.size();
    }

    public void addItem(ListViewItem_Table data) {
        listViewItemList_Table.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView ID;
        Button btn_table;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ID = (TextView)itemView.findViewById(R.id.ID);
            btn_table = (Button)itemView.findViewById(R.id.btn_table);
        }


        void onBind(ListViewItem_Table data) {
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


            }


        }


    }

    public ListViewItem_Table getItem(int pos) {
        return listViewItemList_Table.get(pos);
    }
}