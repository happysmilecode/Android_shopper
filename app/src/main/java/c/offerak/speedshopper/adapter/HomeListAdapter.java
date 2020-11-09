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
import c.offerak.speedshopper.modal.HomeListBean;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> implements Filterable {

    public static final String TAG = HomeListAdapter.class.getSimpleName();
    private Context context;
    private List<HomeListBean> homeListBeans;
    private List<HomeListBean> homeFilterList;

    public HomeListAdapter(Context context, List<HomeListBean> homeListBeans) {
        this.context = context;
        this.homeListBeans = homeListBeans;
        homeFilterList = homeListBeans;
    }

    public List<HomeListBean> getList(){
        return homeFilterList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView storeLogo;
        TextView storeAddress;

        MyViewHolder(View view) {
            super(view);
            storeLogo = view.findViewById(R.id.shopIcon);
            storeAddress = view.findViewById(R.id.shopAddress);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HomeListBean bean = homeFilterList.get(position);
        holder.storeAddress.setText(context.getResources().getString(R.string.address_txt) + " " + bean.getAddress());
        holder.storeLogo.setText(homeFilterList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return homeFilterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    homeFilterList = homeListBeans;
                } else {
                    ArrayList<HomeListBean> filteredList = new ArrayList<>();
                    for (HomeListBean row : homeListBeans) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    homeFilterList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = homeFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                homeFilterList = (ArrayList<HomeListBean>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
