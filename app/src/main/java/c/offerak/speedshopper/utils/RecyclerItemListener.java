package c.offerak.speedshopper.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Francesco on 05/09/2016.
 */
public class RecyclerItemListener implements RecyclerView.OnItemTouchListener  {

    private RecyclerTouchListener listener;
    private GestureDetector gd;

    public interface RecyclerTouchListener {
        public void onClickItem(View v, int position) ;
        public void onLongClickItem(View v, int position);
    }

    public RecyclerItemListener(Context ctx, final RecyclerView rv, final RecyclerTouchListener listener) {
        this.listener = listener;
        gd = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                // We find the view
                View v = rv.findChildViewUnder(e.getX(), e.getY());
                // Notify the even
                listener.onLongClickItem(v, rv.getChildAdapterPosition(v));
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View v = rv.findChildViewUnder(e.getX(), e.getY());
                // Notify the even
                listener.onClickItem(v, rv.getChildAdapterPosition(v));
                return true;
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        return ( child != null && gd.onTouchEvent(e));
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
