package c.offerak.speedshopper.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.MarketListResponse;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.MyViewHolder> {

    public static final String TAG = MarketListAdapter.class.getSimpleName();
    private Context context;
    private List<MarketListResponse.DataBean> marketListResponses;

    public MarketListAdapter(Context context, List<MarketListResponse.DataBean> marketListResponses) {
        this.context = context;
        this.marketListResponses = marketListResponses;
    }

    @Override
    public int getItemCount() {
        return marketListResponses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView storeName,storeAddress;
        ImageView image;

        MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            storeName = view.findViewById(R.id.shopName);
            storeAddress = view.findViewById(R.id.shopAddress);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.marker_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MarketListResponse.DataBean bean = marketListResponses.get(position);
        Glide.with(context).load(bean.getProfile_pic()).into(holder.image);
        holder.storeAddress.setText(bean.getAddress());
        holder.storeName.setText(bean.getStore_name());
    }
}

