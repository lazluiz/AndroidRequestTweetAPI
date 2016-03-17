package com.zelius.requestapi.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zelius.requestapi.DetailsActivity;
import com.zelius.requestapi.model.TweetModel;

import java.util.List;

/**
 * Created by RequestTwitterAPI on 15/03/2016.
 */
public class TweetsListAdapter extends ArrayAdapter<TweetModel> {

    private Activity m_Activity;
    private int m_Layout;
    private List<TweetModel> m_Data;

    public TweetsListAdapter(Activity activity, int resource, List<TweetModel> data) {
        super(activity, resource, data);
        m_Activity = activity;
        m_Layout = resource;
        m_Data = data;
    }

    static class ViewHolder {
        TextView txt1;
        TextView txt2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        TweetModel dataItem = m_Data.get(position);

        if(convertView==null){
            convertView = m_Activity.getLayoutInflater().inflate(m_Layout, null);

            viewHolder = new ViewHolder();
            viewHolder.txt1 = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.txt2 = (TextView) convertView.findViewById(android.R.id.text2);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(dataItem != null) {
            viewHolder.txt1.setText(dataItem.getTweet().substring(0, Math.min(dataItem.getTweet().length(), 24)) + "...");
            viewHolder.txt2.setText(String.format("%s dias atrás", DetailsActivity.ReturnDateInDays(dataItem.getDateCreation())));
        }

        return convertView;
    }
    
}
