package com.example.main_project_five0605.showincome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_project_five0605.MoneyFormat;
import com.example.main_project_five0605.R;

import java.util.ArrayList;

public class ListViewAdapter_Income extends RecyclerView.Adapter<ListViewAdapter_Income.ItemViewHolder> {

        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<ListViewItem_Income> ListViewItem_Income = new ArrayList<ListViewItem_Income>() ;
        public ListViewAdapter_Income() {
        }

        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_income,parent,false);

            return new ItemViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            holder.onBind(ListViewItem_Income.get(position));
        }
        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }
        @Override
        public int getItemCount() {
            return ListViewItem_Income.size();
        }
        // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
        public void addItem(ListViewItem_Income data) {
            ListViewItem_Income.add(data);
        }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView income ;
        TextView incomeDay ;
        MoneyFormat mF;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            income = (TextView) itemView.findViewById(R.id.income) ;
            incomeDay = (TextView) itemView.findViewById(R.id.incomeDay) ;
            mF = new MoneyFormat();

        }
        void onBind(ListViewItem_Income data){
            income.setText(mF.myFormatter.format(Integer.parseInt( data.get_income() )) + " 원");
            incomeDay.setText(data.get_incomeDay());// Database 의 정확한 Column Name은 orderDay 이다. group by orderDay(= incomeDay )

        }
    }
    public ListViewItem_Income getItem(int pos){
        return ListViewItem_Income.get(pos);
    }
}



