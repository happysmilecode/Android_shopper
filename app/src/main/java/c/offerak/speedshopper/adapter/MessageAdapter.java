package c.offerak.speedshopper.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.modal.Message;
import c.offerak.speedshopper.modal.Notification;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    public static final String TAG = MessageAdapter.class.getSimpleName();
    Context context;
    List<Message> messageList = new ArrayList<>();

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView msg_title,msg_date_time,msg_message;

        public MyViewHolder(View itemView) {
            super(itemView);

            msg_title=itemView.findViewById(R.id.msg_title);
            msg_date_time=itemView.findViewById(R.id.msg_date_time);
            msg_message=itemView.findViewById(R.id.msg_message);
            Linkify.addLinks(msg_message, Linkify.ALL);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_message,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.msg_title.setText(messageList.get(position).getTitle());
        holder.msg_message.setText(messageList.get(position).getMsg());

        try {
            DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH); //HH for hour of the day (0 - 23)
            f1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            Date d = f1.parse(messageList.get(position).getDate_time());
            Log.e(TAG, "Server: "+ messageList.get(position).getDate_time());
            Log.e(TAG, "Formate: '"+d );

            DateFormat f2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:a", Locale.ENGLISH);
            f1.setTimeZone(TimeZone.getDefault());
            Log.e(TAG, "Current split_time: " + f2.format(d).toUpperCase());

            holder.msg_date_time.setText(f2.format(d).toUpperCase());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
