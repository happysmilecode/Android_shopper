package c.offerak.speedshopper.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.TransactionHistoryActivity;
import c.offerak.speedshopper.response.TransactionHistoryResponse;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    public static final String TAG = TransactionHistoryActivity.class.getSimpleName();
    Context context;
    List<TransactionHistoryResponse.DataBean> transactionHistoryList = new ArrayList();

    public TransactionHistoryAdapter(Context context, List<TransactionHistoryResponse.DataBean> transactionHistoryList) {
        this.context = context;
        this.transactionHistoryList = transactionHistoryList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView ctv_description, ctv_date, ctv_time, ctv_quntity;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            ctv_description = itemView.findViewById(R.id.ctv_description);
            ctv_quntity = itemView.findViewById(R.id.ctv_quntity);
            ctv_date = itemView.findViewById(R.id.ctv_date);
            ctv_time = itemView.findViewById(R.id.ctv_time);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_transaction_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(transactionHistoryList.get(position).getType().equals("0")){
            holder.image.setImageResource(R.drawable.red);
        }else {
            holder.image.setImageResource(R.drawable.green);
        }
        holder.ctv_description.setText(transactionHistoryList.get(position).getDescription());
        holder.ctv_quntity.setText(transactionHistoryList.get(position).getQuantity());
        String[] str = transactionHistoryList.get(position).getCreated_at().split(" ");
        holder.ctv_date.setText(str[0]);
        holder.ctv_time.setText(str[1]);
    }

    @Override
    public int getItemCount() {
        return transactionHistoryList.size();
    }
}
