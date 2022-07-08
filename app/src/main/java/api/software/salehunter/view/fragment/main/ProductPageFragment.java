package api.software.salehunter.view.fragment.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import api.software.salehunter.R;
import api.software.salehunter.adapter.ImagesSliderViewPagerAdapter;
import api.software.salehunter.databinding.FragmentProductPageBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.model.ProductPageModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.viewmodel.fragment.main.ProductPageViewModel;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;


public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding vb;
    private ProductPageViewModel viewModel;
    private NavController navController;

    private ImagesSliderViewPagerAdapter imageSliderAdapter;
    private LineChartView lineChartView;
    private CheckBox[] userRatingStars;
    private int userRatingNewValue;

    private GoogleMap googleMap;

    public ProductPageFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentProductPageBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle("Product");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProductPageViewModel.class);
        if (getArguments() != null) viewModel.setProductId(getArguments().getLong("productId"));

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });

        imageSliderAdapter = new ImagesSliderViewPagerAdapter(getContext());
        vb.productPageImagesSlider.setAdapter(imageSliderAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(vb.productPageImagesSliderIndicator, vb.productPageImagesSlider,(tab, position) ->{ });
        tabLayoutMediator.attach();

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.product_page_map)).getMapAsync(map ->{
            this.googleMap = map;

            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        });

        vb.productPageSubmitRate.setVisibility(View.INVISIBLE);
        userRatingStars = new CheckBox[]{vb.productPageRateStar1, vb.productPageRateStar2, vb.productPageRateStar3, vb.productPageRateStar4, vb.productPageRateStar5};
        for(int i=0; i<userRatingStars.length; i++){
            final int index = i;
            userRatingStars[i].setOnClickListener( (star)->{
                for (int j = 0; j <= index; j++) userRatingStars[j].setChecked(true);
                for (int j = index+1; j < userRatingStars.length; j++) userRatingStars[j].setChecked(false);
                userRatingNewValue = index+1;
                showRatingSubmit(userRatingNewValue != viewModel.getProductPageModel().getUserRating());
            });
        }

        vb.productPageFavourite.setOnCheckedChangeListener((button,checked)->{
            if(checked) vb.productPageFavouriteText.setText("Remove");
            else vb.productPageFavouriteText.setText("Add");
        });

        vb.productPageFavourite.setOnClickListener( button ->{
           setFavourite(vb.productPageFavourite.isChecked());
        });

        vb.productPageFavouriteText.setOnClickListener( button ->{
            vb.productPageFavourite.performClick();
        });

        vb.productPageBack.setOnClickListener( button ->{
            getActivity().onBackPressed();
        });

        vb.productPageStore.setOnClickListener( image ->{
            Bundle bundle = new Bundle();
            bundle.putLong("storeId",viewModel.getProductPageModel().getStore().getStoreId());
            navController.navigate(R.id.action_productPageFragment_to_storePageFragment,bundle);
        });

        vb.productPageOpenSourcePageButton.setOnClickListener( button ->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(viewModel.getProductPageModel().getMainInfo().getSourceUrl()));
            startActivity(intent);
        });

        vb.productPageShareProductButton.setOnClickListener( button ->{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, viewModel.getProductPageModel().getMainInfo().getShareableUrl());
            startActivity(Intent.createChooser(intent, viewModel.getProductPageModel().getMainInfo().getName()));
        });

        vb.productPageNavigateButton.setOnClickListener( button ->{
            Uri uri = Uri.parse("google.navigation:q="+viewModel.getProductPageModel().getStore().getStoreLatitude()+","+viewModel.getProductPageModel().getStore().getStoreLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        });

        vb.productPageSubmitRate.setOnClickListener( button ->{
            rateProduct(userRatingNewValue);
            showRatingSubmit(false);
        });

        loadProductData();
    }

    void loadProductData(){
        vb.productPageLoadingPage.setVisibility(View.VISIBLE);

        viewModel.getProduct().observe(getViewLifecycleOwner(), response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if(response.body()!=null){
                        viewModel.setProductPageModel(response.body().getProduct());
                        renderProductData();
                        vb.productPageLoadingPage.setVisibility(View.GONE);
                        vb.getRoot().startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.lay_on));
                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog("Product Not Found !", "Product Not Found in Server.");
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: "+ response.code(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void renderProductData(){
        ProductPageModel productPageModel = viewModel.getProductPageModel();

        vb.productPageTitle.setText(productPageModel.getMainInfo().getName());
        vb.productPageBrand.setText(productPageModel.getMainInfo().getBrand());
        vb.productPagePrice.setText(productPageModel.getPrices().get(0).getPrice()+"LE");
        vb.productPageSalePercent.setText(productPageModel.getMainInfo().getSalePercent()+"% SALE");
        vb.productPageRate.setText(productPageModel.getProductRating().getRating().substring(0,3));
        vb.productPageViews.setText(productPageModel.getViews().getCount()+"");
        vb.productPageFavourite.setChecked(productPageModel.isFavorite());
        renderUserRating(productPageModel.getUserRating());

        Glide.with(this)
                .load(productPageModel.getStore().getStoreLogo())
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(vb.productPageStore);

        ArrayList<String> productImagesLinks = new ArrayList<>();
        for(ProductPageModel.ProductImage i : productPageModel.getImages())
            productImagesLinks.add(i.getImageUrl().replace("http://","https://"));
        imageSliderAdapter.addImages(productImagesLinks);
        if(productImagesLinks.size() == 1) vb.productPageImagesSliderIndicator.setVisibility(View.INVISIBLE);

        drawPriceTrackerChart(productPageModel.getPrices());

        if(productPageModel.getStore().getStoreType().equals(ProductModel.ONLINE_STORE)){
            vb.productPageMapSection.setVisibility(View.GONE);
            vb.productPageNavigateButton.setVisibility(View.GONE);
            vb.productPageDescription.setVisibility(View.GONE);
        }
        else {
            ProductPageModel.Store store = productPageModel.getStore();
            addProductOnMap(store.getStoreLatitude(),store.getStoreLongitude(),store.getStoreName());
            vb.productPageOpenSourcePageButton.setVisibility(View.GONE);

            String fullDescription = productPageModel.getMainInfo().getDescription();

            if(fullDescription.length()>120){
                String shortDescription = fullDescription.substring(0,110)+"... ";

                SpannableString readMore = new SpannableString("Read More");
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {

                        vb.productPageDescription.animate().alpha(0).setDuration(250).withEndAction(()->{
                            vb.productPageDescription.setText(fullDescription);
                            vb.productPageDescription.animate().alpha(1f).setDuration(250).start();
                        }).start();

                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };

                readMore.setSpan(clickableSpan,0,readMore.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                vb.productPageDescription.setText(shortDescription);
                vb.productPageDescription.append(readMore);
                vb.productPageDescription.setMovementMethod(LinkMovementMethod.getInstance());

            }
            else vb.productPageDescription.setText(fullDescription);

        }

        if(productPageModel.getMainInfo().getSalePercent() == 0) vb.productPageSalePercent.setVisibility(View.INVISIBLE);

    }

    void renderUserRating(int stars){
        if(stars>0) userRatingStars[stars-1].performClick();
    }

    void showRatingSubmit(boolean show){
        if(show == (vb.productPageSubmitRate.getVisibility()==View.VISIBLE)) return;

        if(show){
            vb.productPageSubmitRate.setVisibility(View.VISIBLE);
            vb.productPageSubmitRate.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in));
        } else {
            vb.productPageSubmitRate.setVisibility(View.INVISIBLE);
            vb.productPageSubmitRate.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.zoom_out));
        }
    }

    private void addProductOnMap(double lat, double lng, String storeName) {
        try {
            LatLng productLocation = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(productLocation).title(storeName).icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_store_mark))).showInfoWindow();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(productLocation)
                    .zoom(googleMap.getCameraPosition().zoom < 8 ? 8:googleMap.getCameraPosition().zoom)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);

        } catch (Exception e){
            Toast.makeText(getContext(), "Map Marker Error", Toast.LENGTH_SHORT).show();
        }

    }

    void drawPriceTrackerChart(ArrayList<ProductPageModel.ProductPrice> prices){
        lineChartView = vb.productPageChart;

        List<AxisValue> xValues = new ArrayList<>();
        List<PointValue> points = new ArrayList<>();

        xValues.add(new AxisValue(0).setLabel("untracked"));
        points.add(new PointValue(0,0));

        for (int i=1; i<=prices.size(); i++){
            xValues.add(new AxisValue(i).setLabel(dateTimeConvert(prices.get(i-1).getCreationDate())));
            points.add(new PointValue(i, (float) prices.get(i-1).getPrice()));
        }

        Line line = new Line();
        line.setValues(points);
        line.setColor(getResources().getColor(R.color.lightModeprimary));
        line.setStrokeWidth(3);
        line.setHasPoints(true);
        line.setShape(ValueShape.CIRCLE);
        line.setPointColor(getResources().getColor(R.color.lightModeprimary));
        line.setPointRadius(5);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setCubic(true);
        line.setFilled(true);
        line.setAreaTransparency(50);

        Axis axisX = new Axis();
        axisX.setValues(xValues);
        axisX.setName("Date");
        axisX.setLineColor(Color.GRAY);
        axisX.setTextColor(Color.GRAY);
        axisX.setTextSize(14);
        axisX.setTypeface(Typeface.DEFAULT);
        axisX.setHasLines(true);
        axisX.setMaxLabelChars(10);
        axisX.setHasTiltedLabels(true);

        Axis axisY = new Axis();
        axisY.setLineColor(Color.GRAY);
        axisY.setTextColor(Color.GRAY);
        axisY.setTextSize(10);
        axisY.setHasLines(true);

        //setting chart data
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData chartData = new LineChartData();
        chartData.setLines(lines);
        chartData.setAxisXBottom(axisX);
        chartData.setAxisYLeft(axisY);
        chartData.finish();

        //send chart data to view
        lineChartView.setLineChartData(chartData);
        //set chart view settings
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartView.setMaxZoom(5);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

    }

    public String dateTimeConvert(String dateTime){
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String outputPattern = "dd/MM/yyyy";

        try {
            SimpleDateFormat input = new SimpleDateFormat(inputPattern);
            input.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date parsed = input.parse(dateTime);

            SimpleDateFormat destFormat = new SimpleDateFormat(outputPattern);
            destFormat.setTimeZone(TimeZone.getDefault());

            return destFormat.format(parsed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-";
    }

    void setFavourite(boolean favourite){
        if(favourite){
            viewModel.addFavourite().observe(getViewLifecycleOwner(), response ->{
                if (response.code() != BaseResponseModel.SUCCESSFUL_CREATION)
                    Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_SHORT).show();
            });
        }
        else {
            viewModel.removeFavourite().observe(getViewLifecycleOwner(), response ->{
                if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED)
                    Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_SHORT).show();
            });
        }

    }

    void rateProduct(int rating){
        viewModel.rateProduct(rating).observe(getViewLifecycleOwner(), response ->{
            if (response.code() != BaseResponseModel.SUCCESSFUL_OPERATION)
                Toast.makeText(getContext(), "Error " + response.code(), Toast.LENGTH_SHORT).show();
            else viewModel.getProductPageModel().setUserRating(userRatingNewValue);
        });
    }

}