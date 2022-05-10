package api.software.salehunter.view.fragment.main;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import api.software.salehunter.R;
import api.software.salehunter.databinding.FragmentProductPageBinding;
import api.software.salehunter.model.BaseResponseModel;
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

    private LineChartView lineChartView;
    private CheckBox[] userRatingStars;
    private int userRatingValue;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setTitle("Product");

        viewModel = new ViewModelProvider(this).get(ProductPageViewModel.class);
        if (getArguments() != null) viewModel.setProductId(getArguments().getLong("productId"));

        if(isDarkModeEnabled()) vb.productPageStore.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        userRatingStars = new CheckBox[]{vb.productPageRateStar1, vb.productPageRateStar2, vb.productPageRateStar3, vb.productPageRateStar4, vb.productPageRateStar5};

        for(int i=0; i<userRatingStars.length; i++){
            final int index = i;
            userRatingStars[i].setOnClickListener( (star)->{
                for (int j = 0; j < index; j++) userRatingStars[j].setChecked(true);
                for (int j = index+1; j < userRatingStars.length; j++) userRatingStars[j].setChecked(false);
                userRatingValue = index+1;
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

        loadProductData();
    }

    public boolean isDarkModeEnabled() {
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
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
        vb.productPageRate.setText(productPageModel.getRating().getRating().substring(0,3));
        vb.productPageViews.setText(productPageModel.getViews().getCount()+"");
        vb.productPageFavourite.setChecked(productPageModel.isFavorite());

        //setUserRating(4);

        switch (productPageModel.getStore().getStoreName().toLowerCase()) {
            case "amazon":
                vb.productPageStore.setImageDrawable(getContext().getDrawable(R.drawable.amazon_logo_svg));
                break;

            case "jumia":
                vb.productPageStore.setImageDrawable(getContext().getDrawable(R.drawable.jumia_seeklogo_com_));
                break;

            default:
                Glide.with(getContext())
                        .load(productPageModel.getStore())
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(100))
                        .into(vb.productPageStore);
                break;
        }

        Glide.with(this).load(productPageModel.getImages().get(0).getImageUrl())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(vb.productPageImage);

        drawPriceTrackerChart(productPageModel.getPrices());

        vb.productPageOpenSourcePageButton.setOnClickListener( button ->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(productPageModel.getMainInfo().getUrl()));
            startActivity(intent);
        });

        vb.productPageShareProductButton.setOnClickListener( button ->{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, productPageModel.getMainInfo().getUrl());
            startActivity(Intent.createChooser(intent, productPageModel.getMainInfo().getName()));
        });

    }

    void setUserRating(int stars){
        userRatingStars[(int)stars-1].performClick();
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
        line.setPointRadius(8);
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

        //send chart data to view
        lineChartView.setLineChartData(chartData);
        //set chart view settings
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL);
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
                    Toast.makeText(getContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();
            });
        }
        else {
            viewModel.removeFavourite().observe(getViewLifecycleOwner(), response ->{
                if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED)
                    Toast.makeText(getContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();
            });
        }

    }
}