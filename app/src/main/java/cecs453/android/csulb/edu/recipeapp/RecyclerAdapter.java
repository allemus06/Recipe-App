package cecs453.android.csulb.edu.recipeapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alejandro Lemus
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.recyclerViewHolder>{

    // This is an inner class that defines the ViewHolder for each item in our RecyclerView
    public class recyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView card;
        TextView label;

        recyclerViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.card);
            label = (TextView) itemView.findViewById(R.id.label);
            mItemClickListener = listener;
            itemView.setOnClickListener(this);

        }

        // implementing an onClickListener for the ViewHolders
        @Override
        public void onClick(View v) {
            if(mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

    }

    // Declare an instance of an ArrayList that can contain our recipe search results
    private ArrayList<Recipe> recipeSearchResults;

    // This is a constructor for our adapter. We pass in the ArrayList created in MainActivity
    //    so that our adapter can use the results stored in there
    public RecyclerAdapter(ArrayList<Recipe> recipeList) {
        recipeSearchResults = recipeList;
    }

    // use this method to get the number of search results
    @Override
    public int getItemCount() {
        if (recipeSearchResults == null)
            return 0;
        else
            return recipeSearchResults.size();
    }

    // method for creating a view holder
    @Override
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recipe, parent, false);
        return new recyclerViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(recyclerViewHolder viewHolder, int position) {
        // extract a search result so that we can use the information stored in it for binding
        Recipe recipeItem = recipeSearchResults.get(position);

        // this uses the getLabel() method from Recipe.java to get the label (or name) of the recipe
        String recipeLabel = recipeItem.getLabel();

        // we set the text of the item in our recyclerView to whatever is stored in our label
        viewHolder.label.setText(recipeLabel);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // declare an instance of ItemClickListener
    private ItemClickListener mItemClickListener;

    // below is the interface used for checking item clicks
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener (ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
