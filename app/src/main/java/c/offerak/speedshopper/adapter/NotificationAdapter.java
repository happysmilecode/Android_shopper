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
import c.offerak.speedshopper.modal.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    public static final String TAG=NotificationAdapter.class.getSimpleName();
    Context context;
    List<Notification> notificationList=new ArrayList<>();

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView ctv_title,ctv_date_time,ctv_type,ctv_message;

        public MyViewHolder(View itemView) {
            super(itemView);

            ctv_title=itemView.findViewById(R.id.ctv_title);
            ctv_date_time=itemView.findViewById(R.id.ctv_date_time);
            ctv_type=itemView.findViewById(R.id.ctv_type);
            ctv_message=itemView.findViewById(R.id.ctv_message);
            Linkify.addLinks(ctv_message, Linkify.ALL);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_notification,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.ctv_title.setText(notificationList.get(position).getTitle());
        holder.ctv_type.setText("Type: "+notificationList.get(position).getType());
        holder.ctv_message.setText(notificationList.get(position).getMsg());

        try {
            DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH); //HH for hour of the day (0 - 23)
            f1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            Date d = f1.parse(notificationList.get(position).getDate_time());
            Log.e(TAG, "Server: "+ notificationList.get(position).getDate_time());
            Log.e(TAG, "Formate: '"+d );

            DateFormat f2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:a", Locale.ENGLISH);
            f1.setTimeZone(TimeZone.getDefault());
            Log.e(TAG, "Current split_time: " + f2.format(d).toUpperCase());

            holder.ctv_date_time.setText(f2.format(d).toUpperCase());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
