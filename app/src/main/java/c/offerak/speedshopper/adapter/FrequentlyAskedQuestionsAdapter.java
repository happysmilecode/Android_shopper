package c.offerak.speedshopper.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.FAQ_Details;
import c.offerak.speedshopper.response.FAQ_Response;

public class FrequentlyAskedQuestionsAdapter extends RecyclerView.Adapter<FrequentlyAskedQuestionsAdapter.MyViewHolder>{

    public static final String TAG = FrequentlyAskedQuestionsAdapter.class.getSimpleName();
    Context context;
    List<FAQ_Response.DataBean> dataBeans;

    public FrequentlyAskedQuestionsAdapter(Context context,List<FAQ_Response.DataBean> dataBeans) {
        this.context = context;
        this.dataBeans = dataBeans;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtQuestion;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.faq_items, parent, false));
    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtQuestion.setText(dataBeans.get(position).getQuestion());
        holder.txtQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FAQ_Details.class)
                        .putExtra("question", dataBeans.get(position).getQuestion())
                        .putExtra("answer", dataBeans.get(position).getAnswer())
                        .putExtra("image", dataBeans.get(position).getImage()));
            }
        });
    }
}
