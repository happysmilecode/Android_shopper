package c.offerak.speedshopper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.EZListsResponse;

public class EZListsAdapter extends RecyclerView.Adapter<EZListsAdapter.MyViewHolder> {

    public static final String TAG = EZListsAdapter.class.getSimpleName();
    private Context context;
    private List<EZListsResponse.DataBean> EZListResponses;

    public EZListsAdapter(Context context, List<EZListsResponse.DataBean> EZListResponses) {
        this.context = context;
        this.EZListResponses = EZListResponses;
    }

    @Override
    public int getItemCount() {
        return EZListResponses.size();
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
        EZListsResponse.DataBean bean = EZListResponses.get(position);
        Glide.with(context).load(bean.getImage()).into(holder.image);
        holder.storeAddress.setText(bean.getDescription());
        holder.storeName.setText(bean.getTitle());
    }
}

