package c.offerak.speedshopper.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.ProductListResponse;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {

    public static final String TAG = ProductListAdapter.class.getSimpleName();
    private Context context;
    private List<ProductListResponse.DataBean> marketListResponses;

    public ProductListAdapter(Context context, List<ProductListResponse.DataBean> marketListResponses) {
        this.context = context;
        this.marketListResponses = marketListResponses;
    }

    @Override
    public int getItemCount() {
        return marketListResponses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;

        MyViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.productName);
            productPrice = view.findViewById(R.id.productPrice);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductListResponse.DataBean bean = marketListResponses.get(position);
        holder.productPrice.setText("Price - "+bean.getPrice()+" Tokens");
        holder.productName.setText(bean.getProduct_name());
    }
}

