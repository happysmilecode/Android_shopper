package c.offerak.speedshopper.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.BuyListResponse;
import c.offerak.speedshopper.utils.Utils;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {

    public static final String TAG = CouponAdapter.class.getSimpleName();
    Context context;
    List<BuyListResponse.DataBean> couponCodeList = new ArrayList<>();
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private Utils utils = new Utils();

    public CouponAdapter(Context context, List<BuyListResponse.DataBean> couponCodeList) {
        this.context = context;
        this.couponCodeList = couponCodeList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image, copy;
        public TextView ctv_store_name, ctv_product, ctv_price, ctv_coupon_code, ctv_website;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            ctv_store_name = itemView.findViewById(R.id.ctv_store_name);
            ctv_product = itemView.findViewById(R.id.ctv_product);
            ctv_price = itemView.findViewById(R.id.ctv_price);
            ctv_coupon_code = itemView.findViewById(R.id.ctv_coupon_code);
            ctv_website = itemView.findViewById(R.id.ctv_website);
            Linkify.addLinks(ctv_website, Linkify.ALL);
            copy = itemView.findViewById(R.id.copy);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_coupon_code, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(couponCodeList.get(position).getPath() + couponCodeList.get(position).getProduct_image()).into(holder.image);
        holder.ctv_store_name.setText(couponCodeList.get(position).getStore());
        holder.ctv_product.setText(couponCodeList.get(position).getProduct_name());
        holder.ctv_price.setText(couponCodeList.get(position).getPrice());
        holder.ctv_website.setText(couponCodeList.get(position).getWebsite());

        /*Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "SHOWG.TTF");
        holder.ctv_coupon_code.setTypeface(custom_font);*/
        holder.ctv_coupon_code.setText("coupon code - " + couponCodeList.get(position).getCoupon_code());
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                myClip = ClipData.newPlainText("Coupon", couponCodeList.get(position).getCoupon_code());
                myClipboard.setPrimaryClip(myClip);
                utils.showSnackBar(((Activity) context).getWindow().getDecorView().getRootView(),"Copied");
            }
        });
    }

    @Override
    public int getItemCount() {
        return couponCodeList.size();
    }
}
