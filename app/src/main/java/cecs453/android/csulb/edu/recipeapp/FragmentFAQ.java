package cecs453.android.csulb.edu.recipeapp;

/**
 * Created by Aenah Ramones with related XML.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class FragmentFAQ extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    EditText iSearch;
    String empty = "";
    int totalSize;

    public FragmentFAQ() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_faq, container, false);

        // get listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        // search feature
        iSearch = (EditText) rootView.findViewById(R.id.searchInput);

        // preparing list data
        initializeFAQ();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        listAdapter.setFinalValues();

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                if(listDataHeader.get(groupPosition).equals("1. Where can I go to find nutritional facts on specific ingredients?")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nutritionfacts.org"));
                    startActivity(browserIntent);
                }

                if(listDataHeader.get(groupPosition).equals("2. How do I report an issue within the app?")){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:202-555-0177"));
                    startActivity(intent);

                }

                if(listDataHeader.get(groupPosition).equals("3. Where do all these recipes come from?")){
                    Intent devIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.edamam.com"));
                    startActivity(devIntent);
                }

                if(listDataHeader.get(groupPosition).equals("8. Can I interchange grams for cups?")){
                    Intent convertIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.convertunits.com/from/grams/to/cups"));
                    startActivity(convertIntent);

                }
                return false;
            }
        });

        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    if(v.getText().toString().equals(empty)){
                        listAdapter.refreshValues();
                    }
                    else{
                        boolean qFound = listAdapter.filterData(v.getText().toString());

                        if(qFound == false) {
                            Toast.makeText(getActivity(), v.getText().toString() + " not found.", Toast.LENGTH_SHORT).show();
                        }


                    }

                    return true;
                }
                return false;
            }
        };

        iSearch.setOnEditorActionListener(searchListener);

        iSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iSearch.setText("");
                listAdapter.refreshValues();
            }
        });

        return rootView;
    }

    private void initializeFAQ() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding head data
        listDataHeader.add("1. Where can I go to find nutritional facts on specific ingredients?");
        listDataHeader.add("2. How do I report an issue within the app?");
        listDataHeader.add("3. Where do all these recipes come from?");
        listDataHeader.add("4. Can I add my own recipe?");
        listDataHeader.add("5. Are there organic/vegan/etc. recipes?");
        listDataHeader.add("6. When was this app created?");
        listDataHeader.add("7. Do you have any tips/footnotes on how to cook the food?");
        listDataHeader.add("8. Can I interchange grams for cups?");
        listDataHeader.add("9. Are recipes separated by genre?");
        listDataHeader.add("10. Who are the creators of Munchies?");
        listDataHeader.add("11. Can we share recipes with friends?");
        listDataHeader.add("12. Do I need to log in to access Munchies?");


        // Adding child data
        List<String> qOneAns = new ArrayList<String>();
        qOneAns.add("A: Unfortunately, we currently do not support nutritional facts but click this answer to learn more."); // https://nutritionfacts.org

        List<String> qTwoAns = new ArrayList<String>();
        qTwoAns.add("A: Contact us by clicking this answer, and you can call us regarding this issue so we can fix it.");

        List<String> qThreeAns = new ArrayList<String>();
        qThreeAns.add("Our recipes come from Edamam; click here to be taken to the developer site!");

        List<String> qFourAns = new ArrayList<String>();
        qFourAns.add("A: This feature is coming soon!");

        List<String> qFiveAns = new ArrayList<String>();
        qFiveAns.add("A: This feature is coming soon!");

        List<String> qSixAns = new ArrayList<String>();
        qSixAns.add("A: This application was created on December 8, 2017.");

        List<String> qSevenAns = new ArrayList<String>();
        qSevenAns.add("A: This feature is coming soon; we will be adding a notes section for those interested in writing down notes.");

        List<String> qEightAns = new ArrayList<String>();
        qEightAns.add("A: We do not support this feature for now, but please click here to taken to a conversion calculator."); //https://www.convertunits.com/from/grams/to/cups

        List<String> qNineAns = new ArrayList<String>();
        qNineAns.add("A: Unfortunately, we do not support this feature.");

        List<String> qTenAns = new ArrayList<String>();
        qTenAns.add("A: Alex, Chris, Mari and Aenah!");

        List<String> qElevenAns = new ArrayList<String>();
        qElevenAns.add("A: We currently do not support this feature but check back in a couple of months!");

        List<String> qTwelveAns = new ArrayList<String>();
        qTwelveAns.add("A: Yes, you must create an account before accessing Munchies.");


        listDataChild.put(listDataHeader.get(0), qOneAns);
        listDataChild.put(listDataHeader.get(1), qTwoAns);
        listDataChild.put(listDataHeader.get(2), qThreeAns);
        listDataChild.put(listDataHeader.get(3), qFourAns);
        listDataChild.put(listDataHeader.get(4), qFiveAns);
        listDataChild.put(listDataHeader.get(5), qSixAns);
        listDataChild.put(listDataHeader.get(6), qSevenAns);
        listDataChild.put(listDataHeader.get(7), qEightAns);
        listDataChild.put(listDataHeader.get(8), qNineAns);
        listDataChild.put(listDataHeader.get(9), qTenAns);
        listDataChild.put(listDataHeader.get(10), qElevenAns);
        listDataChild.put(listDataHeader.get(11), qTwelveAns);

        totalSize = listDataHeader.size();
    }

}