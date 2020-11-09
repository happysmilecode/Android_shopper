package c.offerak.speedshopper.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.ItemEditActivity;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> implements Filterable {

    Context context;
    private List<String> aisleList;
    private List<String> aisleFilterList;
    private String pos;

    public LocationAdapter(Context context, List<String> aisleList, String pos) {
        this.context = context;
        this.aisleList = aisleList;
        this.pos = pos;
        aisleFilterList = aisleList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ctv_title_item;

        MyViewHolder(View itemView) {
            super(itemView);
            ctv_title_item = itemView.findViewById(R.id.title_item);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_location, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
//        Log.e(TAG, "onBindViewHolder: "+aisleList.get(position) );
       // Log.e(TAG, "aisleFilterList Size: "+aisleFilterList.size() );
        holder.ctv_title_item.setText(aisleFilterList.get(position));

        holder.ctv_title_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemEditActivity) context).locationChanged(holder.ctv_title_item.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return aisleFilterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    aisleFilterList = aisleList;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String row : aisleList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.toLowerCase().startsWith(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    aisleFilterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = aisleFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                aisleFilterList = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
