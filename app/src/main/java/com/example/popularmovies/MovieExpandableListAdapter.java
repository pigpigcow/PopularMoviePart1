package com.example.popularmovies;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.popularmovies.model.JSONConvertible;

import java.util.HashMap;
import java.util.List;

public class MovieExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    public final static String REVIEWS_LABEL = "Review(s)";
    public final static String TRAILER_LABEL = "Trailer(s)";
    public final String expandableListTitle;
    private List<JSONConvertible> expandableListDetail;

    public MovieExpandableListAdapter(Context context, List<JSONConvertible> dataList, String title) {
        this.context = context;
        expandableListTitle = title;
        this.expandableListDetail = dataList;
    }

    public static HashMap<String, List<JSONConvertible>> makeHashMap(List<JSONConvertible> reviews, List<JSONConvertible> trailers) {
        HashMap<String, List<JSONConvertible>> expandableListDetail = new  HashMap<>();
        expandableListDetail.put(REVIEWS_LABEL, reviews);
        expandableListDetail.put(TRAILER_LABEL, trailers);
        return expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final JSONConvertible convertible = (JSONConvertible) getChild(listPosition, expandedListPosition);
        return convertible.updateUI(convertView, this.context, parent);
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, parent, false);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
