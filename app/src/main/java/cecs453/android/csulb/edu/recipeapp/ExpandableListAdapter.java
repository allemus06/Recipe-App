package cecs453.android.csulb.edu.recipeapp;

/**
 * Created by Aenah Ramones for FAQ page.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    public List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    public HashMap<String, List<String>> _listDataChild;

    private List<String> finalListDataHeader;
    private HashMap<String, List<String>> finalListChild;

    public void setFinalValues(){
        finalListDataHeader = _listDataHeader;
        finalListChild = _listDataChild;

    }

    public void refreshValues(){
        _listDataHeader = finalListDataHeader;
        _listDataChild = finalListChild;
        notifyDataSetChanged();
    }

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean filterData(String query){

        query = query.toLowerCase();

        List<String> newList = new ArrayList<String>();

        for(String compare : _listDataHeader){

            if(compare.toLowerCase().contains(query)){
                newList.add(compare);
            }
        }

        if(newList.size() == 0){
            return false;
        }
        else{
            _listDataHeader = newList;
            notifyDataSetChanged();
            return true;
        }



//        for (String continent : _listDataHeader) {
//
//            ArrayList<Country> countryList = continent.getCountryList();
//            ArrayList<Country> newList = new ArrayList<Country>();
//            for (Country country : countryList) {
//                if (country.getCode().toLowerCase().contains(query) ||
//                        country.getName().toLowerCase().contains(query)) {
//                    newList.add(country);
//                }
//            }
//            if (newList.size() > 0) {
//                Continent nContinent = new Continent(continent.getName(), newList);
//                continentList.add(nContinent);
//            }
//        }

    }
}