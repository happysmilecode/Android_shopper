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

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.modal.Images;
import c.offerak.speedshopper.modal.Message;
import c.offerak.speedshopper.response.StoreImageListResponse;
import c.offerak.speedshopper.rest.Constants;

public class StoreImageAdapter extends RecyclerView.Adapter<StoreImageAdapter.ViewHolder> {

    private Context mContext;

    List<Images> imageList = new ArrayList<>();
    public StoreImageAdapter(Context mContext, List<Images> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
//        TextView txtName;
        ImageView imgLogo;

        public ViewHolder(@NonNull View view) {
            super(view);
            imgLogo = (ImageView) view.findViewById(R.id.img_logo);
//            txtName = (TextView) view.findViewById(R.id.logo_name);
        }
    }
    @NonNull
    @Override
    public StoreImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.image_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StoreImageAdapter.ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(Constants.IMAGE_URL + imageList.get(position).getName())
                .into(holder.imgLogo);
//        int imageId = getResourseId(mContext, imageList.get(position).getName(), "drawable", mContext.getPackageName());
//        holder.imgLogo.setImageResource(imageId);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
    public static int getResourseId(Context context, String pVariableName, String pResourcename, String pPackageName) throws RuntimeException {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            throw new RuntimeException("Error getting Resource ID.", e);
        }
    }
}
