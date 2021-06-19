package com.example.main_project_five0605.settingmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.main_project_five0605.MoneyFormat;
import com.example.main_project_five0605.R;

import java.util.ArrayList;

public class ListViewAdapter_SettingMenu extends RecyclerView.Adapter<ListViewAdapter_SettingMenu.ItemViewHolder> {

    int Number_Of_Menu = 0; // Dialog 처음 시작 시 입력되는 가격, menu 의 개수에 따라 first_menu_price의 배수만큼 증가.
    int cur_price = 0; // dialog 에서 사용하는 현재 가격
    int first_price = 0;
    Context context;
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem_SettingMenu> listViewItemList = new ArrayList<ListViewItem_SettingMenu>();


//    private OnItemClickListener listener = null;
    private OnItemCheckedListener listener2 = null;

    // ListViewAdapter의 생성자
    public ListViewAdapter_SettingMenu() {   }


    public interface OnItemCheckedListener {
        void onCheckedChanged(int position, boolean isChecked);

    }

    public void setOnCheckedChangedListener(OnItemCheckedListener listener2){
        this.listener2 = listener2;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cur_menu, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listViewItemList.get(position));


//
//        holder.swMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int pos = v.getId();
//                if(pos != RecyclerView.NO_POSITION){
//                    if(listener != null){
//                        listener.onItemClick(v, position);
//                    }
//                }
//            }
//        });


        holder.swMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listener2 != null){
                    listener2.onCheckedChanged(position, isChecked  );
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
    public void addItem(ListViewItem_SettingMenu data) {
        listViewItemList.add(data);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView order_menu_IMG;
        TextView menu_ID, menu_name;
        TextView menu_price;

        Switch swMenu;
        MoneyFormat mF;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            menu_ID = (TextView)itemView.findViewById(R.id.menu_ID);
            menu_name = (TextView) itemView.findViewById(R.id.menu_name);
            menu_price = (TextView) itemView.findViewById(R.id.menu_price);
            swMenu = (Switch)itemView.findViewById(R.id.swMenu);


            mF = new MoneyFormat();




        }


        void onBind(ListViewItem_SettingMenu data) {

            menu_ID.setText(data.get_menu_ID());
            menu_name.setText(data.get_menu_name());
            menu_price.setText(mF.myFormatter.format(Integer.parseInt(data.get_menu_price())) + " 원");
            swMenu.setChecked( (data.get_cur().equals("1"))?true:false);

        }

    }

    public ListViewItem_SettingMenu getItem(int pos) {
        return listViewItemList.get(pos);
    }


}



