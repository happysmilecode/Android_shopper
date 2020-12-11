package c.offerak.speedshopper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import c.offerak.speedshopper.R;

public class StoreImageAdapter extends RecyclerView.Adapter<StoreImageAdapter.ViewHolder> {

    private Context mContext;

    String[] imageCodeArray;
    public StoreImageAdapter(Context mContext, String[] imageCodeArray) {
        this.mContext = mContext;
        this.imageCodeArray = imageCodeArray;

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
        int imageId = getResourseId(mContext, imageCodeArray[position], "drawable", mContext.getPackageName());
        holder.imgLogo.setImageResource(imageId);
    }

    @Override
    public int getItemCount() {
        return imageCodeArray.length;
    }
    public static int getResourseId(Context context, String pVariableName, String pResourcename, String pPackageName) throws RuntimeException {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            throw new RuntimeException("Error getting Resource ID.", e);
        }
    }
}
