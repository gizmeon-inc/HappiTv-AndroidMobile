package com.happi.android;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.happi.android.adapters.PartnerVideoListAdapter;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.PartnerShowsModel;
import com.happi.android.models.PartnerVideoListResponseModel;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.AppUtils;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class PartnerVideosListingActivity extends BaseActivity {

    private TypefacedTextViewRegular tv_title;
    //private ImageView iv_menu;
    private ImageView iv_back;
    //private ImageView iv_logo_text;
    //private ImageView iv_search;

    private CompositeDisposable compositeDisposable;


    private LinearLayout ll_error;
    private TypefacedTextViewRegular tv_errormsg;
    private TypefacedTextViewRegular tv_error_video_list;
    private LinearLayout ll_image;
    private ImageView iv_partner_image;
    private LinearLayout ll_description;
    private ExpandableTextView ex_description;
    private TextView tv_more;
    private TextView tv_description;
    private NestedScrollView sv_scrollview_partner;
    private GridRecyclerView rv_partner_video_list;

    private ProgressDialog progressDialog;

    private int width = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_partner_videos_listing);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;

        int modifiedWidth = (int) ((21.46 * width)/ 100);

        float widthImage = AppUtils.convertDpToPx(this,modifiedWidth);
        float heightImage = (float) (widthImage * 1.34);


        tv_title = findViewById(R.id.tv_title);
        //iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        //iv_logo_text = findViewById(R.id.iv_logo_text);
       // iv_search = findViewById(R.id.iv_search);

        tv_title.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);

       // iv_menu.setVisibility(View.GONE);
       // iv_logo_text.setVisibility(View.GONE);
       // iv_search.setVisibility(View.GONE);


        ll_error = findViewById(R.id.ll_error);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        tv_error_video_list = findViewById(R.id.tv_error_video_list);
        ll_error.setVisibility(View.GONE);
        tv_error_video_list.setVisibility(View.GONE);

        ll_image = findViewById(R.id.ll_image);
        iv_partner_image = findViewById(R.id.iv_partner_image);

       /* RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int)widthImage, (int)heightImage);
        rl.setMargins(0,5,0,0);
        rl.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
        ll_image.setLayoutParams(rl);
        ll_image.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);

        int w = ll_image.getWidth();
        int h =  ll_image.getHeight();*/

        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams((int)widthImage, (int)heightImage);
        rl.gravity = Gravity.CENTER_HORIZONTAL;
        iv_partner_image.setLayoutParams(rl);

        ll_description = findViewById(R.id.ll_description);
        ex_description = findViewById(R.id.ex_description);
        tv_description = findViewById(R.id.tv_description);
        tv_more = findViewById(R.id.tv_more);
        tv_more.setVisibility(View.GONE);
        ex_description.setVisibility(View.GONE);
        sv_scrollview_partner = findViewById(R.id.sv_scrollview_partner);
        rv_partner_video_list = findViewById(R.id.rv_partner_video_list);

        compositeDisposable = new CompositeDisposable();

        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);


        String partnerId = "";
        String partner = "";
        if(getIntent() != null && getIntent().getStringExtra(ConstantUtils.PARTNER_DETAILS) != null){
            partner = getIntent().getStringExtra(ConstantUtils.PARTNER_DETAILS);
        }else{
            partner = SharedPreferenceUtility.getPartnerId();
        }


        if (partner != null && !partner.isEmpty()) {
            if(partner.contains(";")){
                String categoryid = partner.split(";")[0];
                partnerId = categoryid;
                String categorytitle = partner.split(";")[1];
                tv_title.setText(categorytitle);
                getVideosByPartner(partnerId);
            }

        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNavigation();
            }
        });

    }

    private void backNavigation(){
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }

    private void getVideosByPartner(String id){
        progressDialog.show();

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable showDisposable = usersService.getPartnerVideos(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(),
                id,SharedPreferenceUtility.getCountryCode(),SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(partnerVideoListResponseModel -> {

                    if(partnerVideoListResponseModel.getData() != null){
                        setUpPartnerDetails(partnerVideoListResponseModel.getData());
                    }else{
                        displayErrorLayout(getString(R.string.no_results_found));
                    }



                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                        displayErrorLayout(getString(R.string.error_message_loading_users));
                    }
                });

        compositeDisposable.add(showDisposable);
    }




    private void hideProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    private void displayErrorLayout(String errorMessage) {
        hideProgressDialog();

       ll_error.setVisibility(View.VISIBLE);
       tv_errormsg.setText(errorMessage);

       ll_image.setVisibility(View.GONE);
       sv_scrollview_partner.setVisibility(View.GONE);
    }

    private void setUpPartnerDetails(PartnerVideoListResponseModel.PartnerDataModel partnerDataModel) {
        ll_error.setVisibility(View.GONE);
        ll_image.setVisibility(View.VISIBLE);
        sv_scrollview_partner.setVisibility(View.VISIBLE);

        if (partnerDataModel != null) {

            if (partnerDataModel.getPartner_image() != null && !partnerDataModel.getPartner_image().isEmpty()) {

                Glide.with(this)
                        .load(ConstantUtils.THUMBNAIL_URL + partnerDataModel.getPartner_image())
                        .error(Glide.with(this)
                                .load(ContextCompat.getDrawable(this, R.drawable.ic_placeholder)))
                        .apply(placeholderOf(R.drawable.ic_placeholder))
                        .into(iv_partner_image);

                iv_partner_image.setVisibility(View.VISIBLE);
            } else {
                iv_partner_image.setVisibility(View.GONE);
            }

            sv_scrollview_partner.setScrollY(0);
            sv_scrollview_partner.setScrollX(0);

            if (partnerDataModel.getPartner_description() != null && !partnerDataModel.getPartner_description().isEmpty()) {
                String description = partnerDataModel.getPartner_description();
                if (description.contains("\r\n")) {
                    description = description.replace("\r\n", " ");
                }


                ll_description.setVisibility(View.VISIBLE);
                tv_description.setVisibility(View.VISIBLE);

//                ex_description.setVisibility(View.VISIBLE);
//                tv_more.setVisibility(View.VISIBLE);

                ex_description.setVisibility(View.GONE);
                tv_more.setVisibility(View.GONE);

                //for truncating description
                //ex_description.setText(description);
                // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
                //ex_description.setAnimationDuration(750L);

                // set interpolators for both expanding and collapsing animations
               // ex_description.setInterpolator(new OvershootInterpolator());

               // toggle the ExpandableTextView
               /* tv_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // buttonToggle.setText(expandableTextView.isExpanded() ? R.string.expand : R.string.collapse);
                        ex_description.toggle();
                        tv_more.setVisibility(View.GONE);
                    }
                });*/

                tv_description.setText(description);
                 truncateDescription(description);

            } else {

                ll_description.setVisibility(View.GONE);
            }

            if(partnerDataModel.getShows()!= null && partnerDataModel.getShows().size() > 0){
                sv_scrollview_partner.setVisibility(View.VISIBLE);
                loadPartnerVideoList(partnerDataModel.getShows());

            }else{
                rv_partner_video_list.setVisibility(View.GONE);
                tv_error_video_list.setVisibility(View.VISIBLE);
            }

        }

        hideProgressDialog();

    }
    private void truncateDescription(String fullDescription){
        if (tv_description.getTag() == null) {
            tv_description.setTag(tv_description.getText());
        }
        ViewTreeObserver vto = tv_description.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv_description.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);

                int lineCount = tv_description.getLayout().getLineCount();
                String more = "...More";
                if (lineCount > 5) {
                    int index = tv_description.getLayout().getLineEnd(4);
                    // String atIndex = tv_description.getText().
                    String truncated = tv_description.getText().subSequence(0, index - more.length() + 1) + " ";
                    tv_description.setText(Html.fromHtml(truncated + "<font color='#34A7CD'>...More</font>"));
                    tv_description.setVisibility(View.VISIBLE);
                    tv_description.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_description.setText(fullDescription);
                            tv_description.setVisibility(View.VISIBLE);
                        }
                    });


                }
            }
        });

    }
    private void loadPartnerVideoList(List<PartnerShowsModel> partnerShowsModels) {
        tv_error_video_list.setVisibility(View.GONE);
        rv_partner_video_list.setVisibility(View.VISIBLE);

        rv_partner_video_list.setHasFixedSize(true);
        PartnerVideoListAdapter adapter = new PartnerVideoListAdapter(partnerShowsModels, this, width);
        rv_partner_video_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_partner_video_list.setAdapter(adapter);

    }






    @Override
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(compositeDisposable);
    }

    @Override
    protected void onResume() {

        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        backNavigation();
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

}
