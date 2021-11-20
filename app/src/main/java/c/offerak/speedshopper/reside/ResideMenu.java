package c.offerak.speedshopper.reside;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.fragment.CouponFragment;
import c.offerak.speedshopper.fragment.ShoppingListsFragment;
import c.offerak.speedshopper.adapter.ResideMenuAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.fragment.ContactUsFragment;
import c.offerak.speedshopper.fragment.FrequentlyAskedQuestionsFragment;
import c.offerak.speedshopper.fragment.NotificationFragment;
import c.offerak.speedshopper.fragment.ProfileFragment;
import c.offerak.speedshopper.fragment.SpeedShopperMarketListFragment;
import c.offerak.speedshopper.fragment.WalletFragment;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * User: special
 * Date: 13-12-10
 * Time: 下午10:44
 * Mail: specialcyci@gmail.com
 */
public class ResideMenu extends FrameLayout {

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    private static final int PRESSED_MOVE_HORIZONTAL = 2;
    private static final int PRESSED_DOWN = 3;
    private static final int PRESSED_DONE = 4;
    private static final int PRESSED_MOVE_VERTICAL = 5;
    private static final int ROTATE_Y_ANGLE = 10;
    private static final String TAG = ResideMenu.class.getSimpleName();
    public static TextView textUserName;
    public static ImageView imgUserImage;
    MySharedPreference mySharedPreference;
    UserBean bean;
    private ImageView imageViewShadow;
    private ImageView imageViewBackground;
    private ListView layoutLeftMenu;
    private LinearLayout layoutRightMenu;
    private View scrollViewLeftMenu;
    private View scrollViewRightMenu;
    private View scrollViewMenu;
    /**
     * Current attaching activity.
     */
    private Activity activity;
    /**
     * The DecorView of current activity.
     */
    private ViewGroup viewDecor;
    private TouchDisableView viewActivity;
    /**
     * The flag of menu opening status.
     */
    private boolean isOpened;
    private float shadowAdjustScaleX;
    private float shadowAdjustScaleY;
    /**
     * Views which need stop to intercept touch events.
     */
    private List<View> ignoredViews;
    private List<ResideMenuItem> leftMenuItems;
    private List<ResideMenuItem> rightMenuItems;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private OnMenuListener menuListener;
    private float lastRawX;
    private boolean isInIgnoredView = false;
    private int scaleDirection = DIRECTION_LEFT;
    private int pressedState = PRESSED_DOWN;
    private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
    // Valid scale factor is between 0.0f and 1.0f.
    private float mScaleValue = 0.5f;
    private boolean mUse3D;

    private OnClickListener viewActivityOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isOpened()) closeMenu();
        }
    };

    private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (isOpened()) {
                showScrollViewMenu(scrollViewMenu);
                if (menuListener != null)
                    menuListener.openMenu();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            // reset the view;
            if (isOpened()) {
                viewActivity.setTouchDisable(true);
                viewActivity.setOnClickListener(viewActivityOnClickListener);
            } else {
                viewActivity.setTouchDisable(false);
                viewActivity.setOnClickListener(null);
                hideScrollViewMenu(scrollViewLeftMenu);
                hideScrollViewMenu(scrollViewRightMenu);
                if (menuListener != null)
                    menuListener.closeMenu();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private float lastActionDownX, lastActionDownY;
    private RelativeLayout sv_menu_logout;
    private ApiInterface apiService;
    private Utils utils = new Utils();

    public ResideMenu(Context context) {
        super(context);
        initViews(context, -1, -1);
    }

    /**
     * This constructor provides you to create menus with your own custom
     * layouts, but if you use custom menu then do not call addMenuItem because
     * it will not be able to find default views
     */
    public ResideMenu(Context context, int customLeftMenuId, int customRightMenuId) {
        super(context);
        initViews(context, customLeftMenuId, customRightMenuId);
    }

    private void initViews(final Context context, int customLeftMenuId, int customRightMenuId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.residemenu_custom, this);
        textUserName = findViewById(R.id.textUserName);
        imgUserImage = findViewById(R.id.imgUserImage);
        sv_menu_logout = findViewById(R.id.sv_menu_logout);

        mySharedPreference = new MySharedPreference(context);
        bean = mySharedPreference.getLoginDetails();
        textUserName.setText(bean.getUserName());

        Glide.with(this).load(bean.getUserImage()).into(imgUserImage);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        if (customLeftMenuId >= 0) {
            scrollViewLeftMenu = inflater.inflate(customLeftMenuId, this, false);
        } else {
            scrollViewLeftMenu = inflater.inflate(R.layout.residemenu_custom_left_scrollview, this, false);
            layoutLeftMenu = scrollViewLeftMenu.findViewById(R.id.sv_left_menu);
            List<String> strings = new ArrayList<>();
            strings.add("My Lists");
            strings.add("My Profile");
            strings.add("My Wallet");
            strings.add("SSTX Market");
            strings.add("Notifications");
            strings.add("My Codes");
            strings.add("FAQ");
            strings.add("Contact Us");

            ResideMenuAdapter resideMenuAdapter = new ResideMenuAdapter(context, strings);
            layoutLeftMenu.setAdapter(resideMenuAdapter);
            layoutLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    FragmentTransaction transaction;
                    FragmentManager fragmentManager;
                    Fragment fragment;
                    Log.e(TAG, "position: " + position);
                    switch (position) {

                        case 0:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.replace(R.id.homeFrame, new ShoppingListsFragment());
                            transaction.commit();
                            closeMenu();
                            break;

                        case 1:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.replace(R.id.homeFrame, new ProfileFragment(), "ProfileFragment");
                            transaction.commit();
                            closeMenu();
                            break;

                        case 2:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.homeFrame, new WalletFragment());
                            transaction.commit();
                            closeMenu();
                            break;

                        case 3:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.homeFrame, new SpeedShopperMarketListFragment());
                            transaction.commit();
                            closeMenu();
                            break;

                        case 4:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.homeFrame, new NotificationFragment());
                            transaction.commit();
                            closeMenu();
                            break;

                        case 5:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.homeFrame, new CouponFragment());
                            transaction.commit();
                            closeMenu();
                            break;

                        case 6:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.homeFrame, new FrequentlyAskedQuestionsFragment());
                            transaction.commit();
                            closeMenu();
                            break;

                        case 7:
                            fragmentManager = activity.getFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            fragmentManager.popBackStack();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.homeFrame, new ContactUsFragment());
                            transaction.commit();
                            closeMenu();
                            break;
                    }
                }
            });

            sv_menu_logout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (utils.isNetworkConnected(context)) {
                        String token = bean.getUserToken();
                        Call<GetResponse> call = apiService.logout(token);
                        call.enqueue(new Callback<GetResponse>() {
                            @Override
                            public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {

                                try {
                                    GetResponse tokenResponse = response.body();
                                    if (tokenResponse != null) {
                                        int status = tokenResponse.getStatus();
                                        String message = tokenResponse.getMessage();
                                        try {
                                            if (status == 200) {
                                                closeMenu();
                                                utils.showSnackBar(scrollViewLeftMenu, message);
                                                MySharedPreference mySharedPreference = new MySharedPreference(activity);
                                                mySharedPreference.clearPreference(context);
                                                mySharedPreference.setLoginDetails("", "", "", "", "","", "", "");
                                            } else {
                                                utils.showSnackBar(scrollViewLeftMenu, message);

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        activity.startActivity(new Intent(context, LoginActivity.class));
                                                        activity.finish();
                                                    }
                                                }, 2000);
                                            }

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    activity.startActivity(new Intent(context, LoginActivity.class));
                                                    activity.finish();
                                                }
                                            }, 2000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<GetResponse> call, Throwable t) {
                                //utils.hideDialog();
                                utils.showSnackBar(scrollViewLeftMenu, "Please check your internet connection!");
                            }
                        });
                    } else {
                        utils.showSnackBar(layoutLeftMenu, "You are not connected to internet!");
                    }
                }
            });
        }
        if (customRightMenuId >= 0) {
            scrollViewRightMenu = inflater.inflate(customRightMenuId, this, false);
        } else {
            scrollViewRightMenu = inflater.inflate(
                    R.layout.residemenu_custom_right_scrollview, this, false);
            layoutRightMenu = scrollViewRightMenu.findViewById(R.id.layout_right_menu);
        }
        imageViewShadow = findViewById(R.id.iv_shadow);
        imageViewBackground = findViewById(R.id.iv_background);
        RelativeLayout menuHolder = findViewById(R.id.sv_menu_holder);
        menuHolder.addView(scrollViewLeftMenu);
        menuHolder.addView(scrollViewRightMenu);
    }

    /**
     * Returns left menu view so you can findViews and do whatever you want with
     */
    public View getLeftMenuView() {
        return scrollViewLeftMenu;
    }

    /**
     * Returns right menu view so you can findViews and do whatever you want with
     */
    public View getRightMenuView() {
        return scrollViewRightMenu;
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        // Applies the content insets to the view's padding, consuming that
        // content (modifying the insets to be 0),
        // and returning true. This behavior is off by default and can be
        // enabled through setFitsSystemWindows(boolean)
        // in api14+ devices.

        // This is added to fix soft navigationBar's overlapping to content above LOLLIPOP
        int bottomPadding = viewActivity.getPaddingBottom() + insets.bottom;
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (!hasBackKey || !hasHomeKey) {//there's a navigation bar
            bottomPadding += getNavigationBarHeight();
        }

        this.setPadding(viewActivity.getPaddingLeft() + insets.left,
                viewActivity.getPaddingTop() + insets.top,
                viewActivity.getPaddingRight() + insets.right,
                bottomPadding);
        insets.left = insets.top = insets.right = insets.bottom = 0;
        return true;
    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Set up the activity;
     *
     * @param activity
     */
    public void attachToActivity(Activity activity) {
        initValue(activity);
        setShadowAdjustScaleXByOrientation();
        viewDecor.addView(this, 0);
    }

    private void initValue(Activity activity) {
        this.activity = activity;
        leftMenuItems = new ArrayList<ResideMenuItem>();
        rightMenuItems = new ArrayList<ResideMenuItem>();
        ignoredViews = new ArrayList<View>();
        viewDecor = (ViewGroup) activity.getWindow().getDecorView();
        viewActivity = new TouchDisableView(this.activity);

        View mContent = viewDecor.getChildAt(0);
        viewDecor.removeViewAt(0);
        viewActivity.setContent(mContent);
        addView(viewActivity);

        ViewGroup parent = (ViewGroup) scrollViewLeftMenu.getParent();
        parent.removeView(scrollViewLeftMenu);
        parent.removeView(scrollViewRightMenu);
    }

    private void setShadowAdjustScaleXByOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            shadowAdjustScaleX = 0.034f;
            shadowAdjustScaleY = 0.12f;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            shadowAdjustScaleX = 0.06f;
            shadowAdjustScaleY = 0.07f;
        }
    }

    /**
     * Set the background image of menu;
     *
     * @param imageResource
     */
    public void setBackground(int imageResource) {
        imageViewBackground.setImageResource(imageResource);
    }

    /**
     * The visibility of the shadow under the activity;
     *
     * @param isVisible
     */
    public void setShadowVisible(boolean isVisible) {
        if (isVisible) {
            imageViewShadow.setBackgroundResource(R.drawable.shadow);
        } else {
            imageViewShadow.setBackgroundResource(0);
        }
    }

    /**
     * Add a single item to the left menu;
     * <p/>
     * WARNING: It will be removed from v2.0.
     *
     * @param menuItem
     */
    @Deprecated
    public void addMenuItem(ResideMenuItem menuItem) {
        this.leftMenuItems.add(menuItem);
        //layoutLeftMenu.addView(menuItem);
    }

    /**
     * Add a single items;
     *
     * @param menuItem
     * @param direction
     */
    public void addMenuItem(ResideMenuItem menuItem, int direction) {
        if (direction == DIRECTION_LEFT) {
            this.leftMenuItems.add(menuItem);
            //layoutLeftMenu.addView(menuItem);
        } else {
            this.rightMenuItems.add(menuItem);
            layoutRightMenu.addView(menuItem);
        }
    }

    /**
     * Set menu items by a array;
     *
     * @param menuItems
     * @param direction
     */
    public void setMenuItems(List<ResideMenuItem> menuItems, int direction) {
        if (direction == DIRECTION_LEFT) {
            this.leftMenuItems = menuItems;
        } else {
            this.rightMenuItems = menuItems;
        }
        rebuildMenu();
    }

    private void rebuildMenu() {
        /*if (layoutLeftMenu != null) {
            layoutLeftMenu.removeAllViews();
            for (ResideMenuItem leftMenuItem : leftMenuItems)
                layoutLeftMenu.addView(leftMenuItem);
        }

        if (layoutRightMenu != null) {
            layoutRightMenu.removeAllViews();
            for (ResideMenuItem rightMenuItem : rightMenuItems)
                layoutRightMenu.addView(rightMenuItem);
        }*/
    }

    /**
     * WARNING: It will be removed from v2.0.
     *
     * @return
     */
    @Deprecated
    public List<ResideMenuItem> getMenuItems() {
        return leftMenuItems;
    }

    /**
     * WARNING: It will be removed from v2.0.
     *
     * @param menuItems
     */
    @Deprecated
    public void setMenuItems(List<ResideMenuItem> menuItems) {
        //this.leftMenuItems = menuItems;
        //rebuildMenu();
    }

    /**
     * Return instances of menu items;
     *
     * @return
     */
    public List<ResideMenuItem> getMenuItems(int direction) {
        if (direction == DIRECTION_LEFT)
            return leftMenuItems;
        else
            return rightMenuItems;
    }

    public OnMenuListener getMenuListener() {
        return menuListener;
    }

    /**
     * If you need to do something on closing or opening menu,
     * set a listener here.
     *
     * @return
     */
    public void setMenuListener(OnMenuListener menuListener) {
        this.menuListener = menuListener;
    }

    /**
     * Show the menu;
     */
    public void openMenu(int direction) {
        setScaleDirection(direction);

        isOpened = true;
        com.nineoldandroids.animation.AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity, mScaleValue, mScaleValue);
        com.nineoldandroids.animation.AnimatorSet scaleDown_shadow = buildScaleDownAnimation(imageViewShadow,
                mScaleValue + shadowAdjustScaleX, mScaleValue + shadowAdjustScaleY);
        com.nineoldandroids.animation.AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 1.0f);
        scaleDown_shadow.addListener(animationListener);
        scaleDown_activity.playTogether(scaleDown_shadow);
        scaleDown_activity.playTogether(alpha_menu);
        scaleDown_activity.start();
    }

    /**
     * Close the menu;
     */
    public void closeMenu() {
        isOpened = false;
        com.nineoldandroids.animation.AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity, 1.0f, 1.0f);
        com.nineoldandroids.animation.AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow, 1.0f, 1.0f);
        com.nineoldandroids.animation.AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 0.0f);
        scaleUp_activity.addListener(animationListener);
        scaleUp_activity.playTogether(scaleUp_shadow);
        scaleUp_activity.playTogether(alpha_menu);
        scaleUp_activity.start();
    }

    @Deprecated
    public void setDirectionDisable(int direction) {
        disabledSwipeDirection.add(direction);
    }

    public void setSwipeDirectionDisable(int direction) {
        disabledSwipeDirection.add(direction);
    }

    private boolean isInDisableDirection(int direction) {
        return disabledSwipeDirection.contains(direction);
    }

    private void setScaleDirection(int direction) {

        int screenWidth = getScreenWidth();
        float pivotX;
        float pivotY = getScreenHeight() * 0.5f;

        if (direction == DIRECTION_LEFT) {
            scrollViewMenu = scrollViewLeftMenu;
            pivotX = screenWidth * 1.5f;
        } else {
            scrollViewMenu = scrollViewRightMenu;
            pivotX = screenWidth * -0.5f;
        }
        ViewHelper.setPivotX(viewActivity, pivotX);
        ViewHelper.setPivotY(viewActivity, pivotY);
        ViewHelper.setPivotX(imageViewShadow, pivotX);
        ViewHelper.setPivotY(imageViewShadow, pivotY);
        scaleDirection = direction;
    }

    /**
     * return the flag of menu status;
     *
     * @return
     */
    public boolean isOpened() {
        return isOpened;
    }

    /**
     * A helper method to build scale down animation;
     *
     * @param target
     * @param targetScaleX
     * @param targetScaleY
     * @return
     */
    private com.nineoldandroids.animation.AnimatorSet buildScaleDownAnimation(View target, float targetScaleX, float targetScaleY) {

        com.nineoldandroids.animation.AnimatorSet scaleDown = new com.nineoldandroids.animation.AnimatorSet();
        scaleDown.playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
                ObjectAnimator.ofFloat(target, "scaleY", targetScaleY)
        );
        if (mUse3D) {
            int angle = scaleDirection == DIRECTION_LEFT ? -ROTATE_Y_ANGLE : ROTATE_Y_ANGLE;
            scaleDown.playTogether(ObjectAnimator.ofFloat(target, "rotationY", angle));
        }
        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity,
                android.R.anim.decelerate_interpolator));
        scaleDown.setDuration(250);
        return scaleDown;
    }

    /**
     * A helper method to build scale up animation;
     *
     * @param target
     * @param targetScaleX
     * @param targetScaleY
     * @return
     */
    private com.nineoldandroids.animation.AnimatorSet buildScaleUpAnimation(View target, float targetScaleX, float targetScaleY) {

        com.nineoldandroids.animation.AnimatorSet scaleUp = new com.nineoldandroids.animation.AnimatorSet();
        scaleUp.playTogether(ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
                ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));
        if (mUse3D) {
            scaleUp.playTogether(ObjectAnimator.ofFloat(target, "rotationY", 0));
        }
        scaleUp.setDuration(250);
        return scaleUp;
    }

    private com.nineoldandroids.animation.AnimatorSet buildMenuAnimation(View target, float alpha) {

        com.nineoldandroids.animation.AnimatorSet alphaAnimation = new com.nineoldandroids.animation.AnimatorSet();
        alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha", alpha));
        alphaAnimation.setDuration(250);
        return alphaAnimation;
    }

    /**
     * If there were some view you don't want reside menu
     * to intercept their touch event, you could add it to
     * ignored views.
     *
     * @param v
     */
    public void addIgnoredView(View v) {
        ignoredViews.add(v);
    }

    /**
     * Remove a view from ignored views;
     *
     * @param v
     */
    public void removeIgnoredView(View v) {
        ignoredViews.remove(v);
    }

    /**
     * Clear the ignored view list;
     */
    public void clearIgnoredViewList() {
        ignoredViews.clear();
    }

    /**
     * If the motion event was relative to the view
     * which in ignored view list,return true;
     *
     * @param ev
     * @return
     */
    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : ignoredViews) {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
        }
        return false;
    }

    private void setScaleDirectionByRawX(float currentRawX) {
        if (currentRawX < lastRawX)
            setScaleDirection(DIRECTION_RIGHT);
        else
            setScaleDirection(DIRECTION_LEFT);
    }

    private float getTargetScale(float currentRawX) {
        float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 0.75f;
        scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX : scaleFloatX;

        float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
        targetScale = targetScale > 1.0f ? 1.0f : targetScale;
        targetScale = targetScale < 0.5f ? 0.5f : targetScale;
        return targetScale;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);
        if (currentActivityScaleX == 1.0f)
            setScaleDirectionByRawX(ev.getRawX());

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastActionDownX = ev.getX();
                lastActionDownY = ev.getY();
                isInIgnoredView = isInIgnoredView(ev) && !isOpened();
                pressedState = PRESSED_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isInIgnoredView || isInDisableDirection(scaleDirection))
                    break;

                if (pressedState != PRESSED_DOWN &&
                        pressedState != PRESSED_MOVE_HORIZONTAL)
                    break;

                int xOffset = (int) (ev.getX() - lastActionDownX);
                int yOffset = (int) (ev.getY() - lastActionDownY);

                if (pressedState == PRESSED_DOWN) {
                    if (yOffset > 25 || yOffset < -25) {
                        pressedState = PRESSED_MOVE_VERTICAL;
                        break;
                    }
                    if (xOffset < -50 || xOffset > 50) {
                        pressedState = PRESSED_MOVE_HORIZONTAL;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else if (pressedState == PRESSED_MOVE_HORIZONTAL) {
                    if (currentActivityScaleX < 0.95)
                        showScrollViewMenu(scrollViewMenu);

                    float targetScale = getTargetScale(ev.getRawX());
                    if (mUse3D) {
                        int angle = scaleDirection == DIRECTION_LEFT ? -ROTATE_Y_ANGLE : ROTATE_Y_ANGLE;
                        angle *= (1 - targetScale) * 2;
                        ViewHelper.setRotationY(viewActivity, angle);

                        ViewHelper.setScaleX(imageViewShadow, targetScale - shadowAdjustScaleX);
                        ViewHelper.setScaleY(imageViewShadow, targetScale - shadowAdjustScaleY);
                    } else {
                        ViewHelper.setScaleX(imageViewShadow, targetScale + shadowAdjustScaleX);
                        ViewHelper.setScaleY(imageViewShadow, targetScale + shadowAdjustScaleY);
                    }
                    ViewHelper.setScaleX(viewActivity, targetScale);
                    ViewHelper.setScaleY(viewActivity, targetScale);
                    ViewHelper.setAlpha(scrollViewMenu, (1 - targetScale) * 2.0f);

                    lastRawX = ev.getRawX();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:

                if (isInIgnoredView) break;
                if (pressedState != PRESSED_MOVE_HORIZONTAL) break;

                pressedState = PRESSED_DONE;
                if (isOpened()) {
                    if (currentActivityScaleX > 0.56f)
                        closeMenu();
                    /*else
                        openMenu(scaleDirection);*/
                } else {
                    if (currentActivityScaleX < 0.94f) {
                        //openMenu(scaleDirection);
                    } else {
                        closeMenu();
                    }
                }
                break;
        }
        lastRawX = ev.getRawX();
        return super.dispatchTouchEvent(ev);
    }

    public int getScreenHeight() {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setScaleValue(float scaleValue) {
        this.mScaleValue = scaleValue;
    }

    public void setUse3D(boolean use3D) {
        mUse3D = use3D;
    }

    private void showScrollViewMenu(View scrollViewMenu) {
        if (scrollViewMenu != null && scrollViewMenu.getParent() == null) {
            addView(scrollViewMenu);
        }
    }

    private void hideScrollViewMenu(View scrollViewMenu) {
        if (scrollViewMenu != null && scrollViewMenu.getParent() != null) {
            removeView(scrollViewMenu);
        }
    }

    public interface OnMenuListener {

        /**
         * This method will be called at the finished time of opening menu animations.
         */
        void openMenu();

        /**
         * This method will be called at the finished time of closing menu animations.
         */
        void closeMenu();
    }
}
