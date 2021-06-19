package com.example.main_project_five0605.showuserorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_project_five0605.MoneyFormat;
import com.example.main_project_five0605.R;
import com.example.main_project_five0605.settingmenu.ListViewAdapter_SettingMenu;

import java.util.ArrayList;

public class ListViewAdapter_ShowUserOrder extends RecyclerView.Adapter<ListViewAdapter_ShowUserOrder.ItemViewHolder> {

    Context context;
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem_ShowUserOrder> ShowUserOrderList = new ArrayList<ListViewItem_ShowUserOrder>();


    private OnItemClickListener listener = null; // recyclerView의 clickListener
    private OnItemCheckedListener listener2 = null; // recyclerView의 checkedListener

    // ListViewAdapter의 생성자
    public ListViewAdapter_ShowUserOrder( ) {

    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
        void onItemNumUpClick(View v, int position);
        void onItemNumDownClick(View v, int position);
        void destroyItemClick(View v, int position);

    }

    public interface OnItemCheckedListener {
        void onCheckedChanged(int position, boolean isChecked);

    }
    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setOnCheckedChangedListener(OnItemCheckedListener listener2){
        this.listener2 = listener2;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_suo, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(ShowUserOrderList.get(position));


        //recyclerView의 onClick, onChecked override
        holder.numup.setOnClickListener(new View.OnClickListener() {
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
        holder.numdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = v.getId();
                if(pos != RecyclerView.NO_POSITION){
                    if(listener != null){
                        listener.onItemNumDownClick(v, position);
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
        holder.setMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        return ShowUserOrderList.size();
    }

    public void addItem(ListViewItem_ShowUserOrder data) {
        ShowUserOrderList.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView order_ID, menu_name;
        TextView menu_price, num;

        Button numup, numdown, xbtn;

        Switch setMenu;
        TextView iscomplete;
        ImageView isCheckIMG;
        MoneyFormat mF;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            order_ID = (TextView)itemView.findViewById(R.id.order_ID);
            menu_name = (TextView) itemView.findViewById(R.id.menu_name);
            menu_price = (TextView) itemView.findViewById(R.id.menu_price);
            num = (TextView)itemView.findViewById(R.id.num);

            numup = (Button)itemView.findViewById(R.id.numup);
            numdown = (Button)itemView.findViewById(R.id.numdown);
            xbtn = (Button)itemView.findViewById(R.id.xbtn);


            setMenu = (Switch)itemView.findViewById(R.id.setMenu);
            iscomplete = (TextView)itemView.findViewById(R.id.iscomplete);
            isCheckIMG = (ImageView)itemView.findViewById(R.id.isCheckIMG);

            // moeny format
            mF = new MoneyFormat();




        }


        void onBind(ListViewItem_ShowUserOrder data) {

            //각 고객에 대한 주문내용을 recyclerView로 확인
            order_ID.setText(new String(data.get_order_ID()));
            menu_name.setText(new String(data.get_menu_name()));
            menu_price.setText(new String(mF.myFormatter.format(Integer.parseInt(data.get_menu_price())) + " 원"));
            num.setText(new String(data.get_menu_num()));


            //음식이 고객에게 할당(서비스) 되었는지 확인, 및 설정
            // 음식이 서비스 된경우, 별표!
            // 서비스 되지 않은 경우 "준비중"
            isCheckIMG.setVisibility( (data.get_ok().equals("1"))?View.VISIBLE:View.GONE);
            iscomplete.setVisibility( (data.get_ok().equals("1"))?View.GONE:View.VISIBLE );  ;
            setMenu.setChecked( (data.get_ok().equals("1"))?true:false);



        }

    }

    public ListViewItem_ShowUserOrder getItem(int pos) {
        return ShowUserOrderList.get(pos);
    }
}