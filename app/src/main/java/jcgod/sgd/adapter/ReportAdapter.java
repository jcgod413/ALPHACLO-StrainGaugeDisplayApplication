package jcgod.sgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jcgod.sgd.R;
import jcgod.sgd.item.ReportItem;

/**
 * Created by Jaecheol on 16. 8. 13..
 */
public class ReportAdapter extends BaseAdapter  {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ReportItem> listViewItemList = new ArrayList<ReportItem>();

    // ListViewAdapter의 생성자
    public ReportAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_report, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView dateText = (TextView) convertView.findViewById(R.id.item_date);
        TextView timeText = (TextView) convertView.findViewById(R.id.item_time);
        TextView distanceText = (TextView) convertView.findViewById(R.id.item_distance);
        TextView caloryText = (TextView) convertView.findViewById(R.id.item_calory);
        TextView lrText = (TextView) convertView.findViewById(R.id.item_lr);
        TextView qhText = (TextView) convertView.findViewById(R.id.item_qh);
        TextView lrqhText = (TextView) convertView.findViewById(R.id.item_lrqh);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ReportItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        dateText.setText(listViewItem.getDate());
        timeText.setText(listViewItem.getTime());
        distanceText.setText(listViewItem.getDistance());
        caloryText.setText(listViewItem.getCalory());
        lrText.setText(listViewItem.getLR());
        qhText.setText(listViewItem.getQH());
        lrqhText.setText(listViewItem.getLRQH());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(String date, String time, String distance, String calory, String lr, String qh, String lrqh) {
        ReportItem item = new ReportItem();

        item.setDate(date);
        item.setTime(time);
        item.setDistance(distance);
        item.setCalory(calory);
        item.setLR(lr);
        item.setQH(qh);
        item.setLRQH(lrqh);

        listViewItemList.add(item);
    }
}
