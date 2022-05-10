package api.software.salehunter.view;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import api.software.salehunter.R;

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

    private float scaleFactor;
    private float cornersRadius;
    private float threshHold;
    private int animationSpeed;


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

        this.scaleFactor = 0.08f;
        this.cornersRadius = 150f;
        this.animationSpeed = 400;
        this.threshHold = 0.35f;

        overLayView.post(()->{
            overLayView.setPivotX(overLayView.getWidth()/2f);
            overLayView.setPivotY(overLayView.getHeight()/2f);
        });

        overLayViewBackground = new GradientDrawable();
        overLayViewBackground.setColor(((ColorDrawable)overLayView.getBackground()).getColor());

        overLayView.setBackground(overLayViewBackground);

        menuView.setVisibility(View.INVISIBLE);

        if(menuButton!=null){
            menuButton.setOnClickListener(view -> {
                if(active) closeMenu();
                else openMenu();
            });
        }

    }

    public void detectTouch(MotionEvent event){
        screenWidth = activity.getWindow().getDecorView().getWidth();
        float touchX = event.getX();

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            inTouchRange = false;
            if(active && touchX < overLayView.getX()+(overLayView.getWidth()/4f) && touchX >= overLayView.getX()) inTouchRange = true;
            else if (!active && touchX < 100) inTouchRange = true;
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE && inTouchRange) {
            overLayView.setTranslationX(touchX);
            overLayView.setScaleX(1-(scaleFactor*touchX/screenWidth));
            overLayView.setScaleY(1-(scaleFactor*touchX/screenWidth));
            overLayViewBackground.setCornerRadius(cornersRadius*touchX/screenWidth);
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
        overLayView.animate().translationX(menuView.getRight()).setDuration(animationSpeed).setInterpolator(new DecelerateInterpolator()).start();
        active = true;
        overLayViewBackground.setCornerRadius(cornersRadius*menuView.getRight()/overLayView.getWidth());
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
