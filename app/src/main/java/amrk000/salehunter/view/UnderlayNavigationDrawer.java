package amrk000.salehunter.view;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import androidx.core.text.LocaleKt;

import java.util.Locale;

import amrk000.salehunter.R;

public class UnderlayNavigationDrawer {
    private View overLayView;
    private View overLayInnerView;
    private View menuView;
    private GradientDrawable overLayViewBackground;
    private ImageButton menuButton;
    private boolean active = false;
    private boolean inTouchRange = false;
    private Activity activity;
    private int screenWidth;

    private int menuHandleWidth;
    private float scaleFactor;
    private float cornersRadiusRatio;
    private float threshHold;
    private int animationSpeed;

    private boolean rtl;

    private MenuOpenCloseListener menuOpenCloseListener = null;

    public interface MenuOpenCloseListener {
        void onMenuOpened();
        void onMenuClosed();
    }

    public UnderlayNavigationDrawer(Activity activity, View overLayView, View overLayInnerView, View menuView, ImageButton menuButton){
        this.overLayView = overLayView;
        this.overLayInnerView = overLayInnerView;
        this.menuView = menuView;
        this.menuButton = menuButton;
        this.activity = activity;

        this.rtl = LocaleKt.getLayoutDirection(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;

        //Menu Configs:
        this.menuHandleWidth = 100;
        this.scaleFactor = 0.08f;
        this.cornersRadiusRatio = 150f;
        this.animationSpeed = 400;
        this.threshHold = 0.35f;

        this.overLayView.post(()->{
            this.overLayView.setPivotX(overLayView.getWidth()/2f);
            this.overLayView.setPivotY(overLayView.getHeight()/2f);
        });

        overLayViewBackground = new GradientDrawable();
        overLayViewBackground.setColor(((ColorDrawable)overLayView.getBackground()).getColor());

        this.overLayView.setBackground(overLayViewBackground);

        this.menuView.setVisibility(View.INVISIBLE);

        if(this.menuButton!=null){
            this.menuButton.setOnClickListener(view -> {
                if(active) closeMenu();
                else openMenu();
            });
        }

    }

    public void detectTouch(MotionEvent event){
        screenWidth = activity.getWindow().getDecorView().getWidth();

        float touchX;
        if(rtl) touchX = screenWidth - event.getX();
        else touchX = event.getX();

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            boolean closeHandleRange;
            if(rtl) closeHandleRange = touchX < screenWidth - (overLayView.getX()+(overLayView.getWidth()*80/100f)) && touchX >= screenWidth - (overLayView.getX()+overLayView.getWidth());
            else closeHandleRange = touchX < overLayView.getX()+(overLayView.getWidth()*20/100f) && touchX >= overLayView.getX();

            if(active && closeHandleRange) inTouchRange = true;
            else inTouchRange = !active && touchX < menuHandleWidth;
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE && inTouchRange) {
            if(rtl) overLayView.setTranslationX(-touchX);
            else overLayView.setTranslationX(touchX);
            overLayView.setScaleX(1-(scaleFactor*touchX/screenWidth));
            overLayView.setScaleY(1-(scaleFactor*touchX/screenWidth));
            overLayViewBackground.setCornerRadius(cornersRadiusRatio *touchX/screenWidth);
            overLayView.setBackground(overLayViewBackground);

            overLayInnerView.setScaleX(overLayView.getScaleX()*0.99f);
            overLayInnerView.setScaleY(overLayView.getScaleY()*0.99f);

            menuView.setVisibility(View.VISIBLE);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP && inTouchRange){
            try {
                if (touchX > screenWidth*threshHold) openMenu();
                else closeMenu();
            }
            catch (Exception e){e.printStackTrace();}
        }
    }

    public void openMenu(){
        overLayView.animate().scaleX(1-scaleFactor).scaleY(1-scaleFactor).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                menuView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(menuOpenCloseListener!=null) menuOpenCloseListener.onMenuOpened();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();

        int menuViewEnd;
        float cornerRadius;
        if(rtl){
            menuViewEnd = menuView.getLeft() - overLayView.getWidth();
            cornerRadius = -1*cornersRadiusRatio*menuViewEnd/overLayView.getWidth();
        }
        else {
            menuViewEnd = menuView.getRight();
            cornerRadius = cornersRadiusRatio*menuViewEnd/overLayView.getWidth();
        }

        overLayView.animate().translationX(menuViewEnd).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).start();
        active = true;
        overLayViewBackground.setCornerRadius(cornerRadius);
        overLayView.setBackground(overLayViewBackground);

        overLayInnerView.animate().scaleX((1-scaleFactor)*0.99f).scaleY((1-scaleFactor)*0.99f).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).start();

        if(menuButton!=null) {
            menuButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.menu_close, activity.getTheme()));
            menuButton.animate().rotation(-180).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).start();
        }

    }

    public void closeMenu(){
        overLayView.animate().translationX(0).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                menuView.setVisibility(View.GONE);
                if(menuOpenCloseListener!=null) menuOpenCloseListener.onMenuClosed();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();

        overLayView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).start();
        active = false;
        overLayViewBackground.setCornerRadius(0);
        overLayView.setBackground(overLayViewBackground);

        overLayInnerView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).start();

        if(menuButton!=null) {
            menuButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.menu_default, activity.getTheme()));
            menuButton.animate().rotation(0).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).start();
        }


    }

    public boolean isOpened(){return active;}

    public void setMenuOpenCloseListener(MenuOpenCloseListener menuOpenCloseListener){
        this.menuOpenCloseListener = menuOpenCloseListener;
    }

    public int getAnimationDuration() {return animationSpeed;}

}
