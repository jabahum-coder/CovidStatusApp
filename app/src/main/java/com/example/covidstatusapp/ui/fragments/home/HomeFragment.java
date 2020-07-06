package com.example.covidstatusapp.ui.fragments.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.covidstatusapp.R;
import com.example.covidstatusapp.ViewModelProviderFactory;
import com.example.covidstatusapp.databinding.HomeFragmentBinding;
import com.example.covidstatusapp.ui._base.BaseFragment;
import com.example.covidstatusapp.ui.dialogs.CleanHandsDialogFragment;
import com.example.covidstatusapp.ui.dialogs.CloseContactDialogFragment;
import com.example.covidstatusapp.ui.dialogs.FaceMaskDialogFragment;
import com.example.covidstatusapp.ui.models.Country;
import com.example.covidstatusapp.ui.models.SummaryResponse;
import com.example.covidstatusapp.ui.utils.CommonUtils;
import com.example.covidstatusapp.ui.utils.sharedPreferences.PreferenceManager;
import com.example.covidstatusapp.ui.viewModel.GlobalViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HomeFragment extends BaseFragment<HomeFragmentBinding, GlobalViewModel> {
    private static final String TAG = "HomeFragment";
    HomeFragmentBinding binding;

    private GlobalViewModel globalViewModel;

    int PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    String SELECTED_COUNTRY;


    @Inject
    PreferenceManager preferenceManager;

    @Inject
    ViewModelProviderFactory providerFactory;


    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment;
    }


    @Override
    public GlobalViewModel getViewModel() {
        globalViewModel = new ViewModelProvider(getBaseActivity(), providerFactory).get(GlobalViewModel.class);
        return globalViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = getViewDataBinding();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        subscribeObservers();
    }

    private void subscribeObservers() {
        globalViewModel.getGlobal().removeObservers(getViewLifecycleOwner());
        globalViewModel.getGlobal().observe(getViewLifecycleOwner(), summaryResponseResource -> {
            if (summaryResponseResource != null) {
                switch (summaryResponseResource.status) {
                    case ERROR:
                        hideLoading();
                        binding.setLoading(false);
                        Toast.makeText(getActivity(), "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        binding.setLoading(true);
                        showLoading("PLEASE WAIT ...");
                        break;
                    case SUCCESS:
                        if (summaryResponseResource.data != null) {
                            setDataGlobalDataPieChart(summaryResponseResource.data);
                            setDataCountryDataPieChart(summaryResponseResource.data.getCountries());
                            hideLoading();
                            binding.setLoading(false);
                        }
                        break;
                }
            }

        });

    }

    private void init() {

        //Close Contact
        binding.layoutCaution1.setOnClickListener(view -> {

            String passingContent = getResources().getString(R.string.social_distancing);
            String passingCitation = getResources().getString(R.string.social_distancing_citation);

            FragmentManager ft = getBaseActivity().getSupportFragmentManager();
            if (ft.findFragmentByTag(TAG) == null) {
                DialogFragment dialogFragment = new CloseContactDialogFragment(passingContent,passingCitation);
                dialogFragment.show(ft, TAG);
            }
        });

        //Clean Hands
        binding.layoutCaution2.setOnClickListener(view -> {
            String passingContent = getResources().getString(R.string.wash_hand);
            String passingCitation = getResources().getString(R.string.wash_hand_citation);

            FragmentManager ft = getBaseActivity().getSupportFragmentManager();
            if (ft.findFragmentByTag(TAG) == null) {
                DialogFragment dialogFragment = new CleanHandsDialogFragment(passingContent,passingCitation);
                dialogFragment.show(ft, TAG);
            }
        });

        //Face Mask Contact
        binding.layoutCaution3.setOnClickListener(view -> {

            String passingContent = getResources().getString(R.string.wear_mask);
            String passingCitation = getResources().getString(R.string.wear_mask_citation);

            FragmentManager ft = getBaseActivity().getSupportFragmentManager();
            if (ft.findFragmentByTag(TAG) == null) {
                DialogFragment dialogFragment = new FaceMaskDialogFragment(passingContent,passingCitation);
                dialogFragment.show(ft, TAG);
            }
        });

        //countryCode
        binding.spCountries.setOnCountryChangeListener(() -> {
            SELECTED_COUNTRY = binding.spCountries.getSelectedCountryName();
            preferenceManager.setSelectedCountry(SELECTED_COUNTRY);
            subscribeObservers();
        });

        //smsButton
        binding.btnSmsButton.setOnClickListener(view -> {
            Uri uri = Uri.parse("smsto:" + "+256800100066");
            Intent sms = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(sms);
        });

        //callButton
        binding.btnCallButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            } else if (!CommonUtils.isSimCardAvailable(requireContext())) {
                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();

            } else {
                String dial = "tel:" + "+256800100066";
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        });

        //Global Chart
        binding.homeGlobalDataPieChart.getDescription().setEnabled(false);
        binding.homeGlobalDataPieChart.setDragDecelerationFrictionCoef(0.95f);
//        mGlobalDataPieChart.setDrawHoleEnabled(true);

        binding.homeGlobalDataPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        binding.homeGlobalDataPieChart.setRotationEnabled(true);
        binding.homeGlobalDataPieChart.setHighlightPerTapEnabled(true);

        binding.homeGlobalDataPieChart.animateY(1500, Easing.EaseInOutQuad);
        binding.homeGlobalDataPieChart.getLegend().setEnabled(false);
        // entry label styling


        //Country Chart

        binding.homeCountryDataPieChart.getDescription().setEnabled(false);
        binding.homeCountryDataPieChart.setDragDecelerationFrictionCoef(0.95f);
//        mGlobalDataPieChart.setDrawHoleEnabled(true);

        binding.homeCountryDataPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        binding.homeCountryDataPieChart.setRotationEnabled(true);
        binding.homeCountryDataPieChart.setHighlightPerTapEnabled(true);

        binding.homeCountryDataPieChart.animateY(1500, Easing.EaseInOutQuad);
        binding.homeCountryDataPieChart.getLegend().setEnabled(false);
        // entry label styling

    }


    @Override
    public void onResume() {
        subscribeObservers();
        super.onResume();
    }

    @SuppressLint("ResourceType")
    private void setDataGlobalDataPieChart(SummaryResponse summaryResponse) {

        int confirmed = summaryResponse.getGlobal().getTotalConfirmed();
        int death = summaryResponse.getGlobal().getTotalDeaths();
        int recovered = summaryResponse.getGlobal().getTotalRecovered();

        binding.setGlobalConfirmed(CommonUtils.numberSeparator(confirmed));
        binding.setGlobalRecovered(CommonUtils.numberSeparator(death));
        binding.setGlobalDeaths(CommonUtils.numberSeparator(recovered));

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(confirmed, 1));
        entries.add(new PieEntry(death, 2));
        entries.add(new PieEntry(recovered, 3));

        PieDataSet dataSet = new PieDataSet(entries, "Global Data");

        dataSet.setDrawIcons(false);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.parseColor(getResources().getString(R.color.colorAccent)));
        colors.add(Color.parseColor(getResources().getString(R.color.themeRed)));
        colors.add(Color.parseColor(getResources().getString(R.color.themeOrange)));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        binding.homeGlobalDataPieChart.setData(data);

        // undo all highlights
        binding.homeGlobalDataPieChart.highlightValues(null);
        binding.homeGlobalDataPieChart.setCenterText("Update:\n" + summaryResponse.getDate()
                .replace("T", "\n")
                .replaceAll("Z", ""));
        binding.homeGlobalDataPieChart.invalidate();

    }
    @SuppressLint("ResourceType")
    private void setDataCountryDataPieChart(List<Country> countryList) {
        if (countryList != null) {
            for (Country country : countryList) {
                if (country.getCountry().equals(preferenceManager.getSelectedCountry())) {


                    int confirmed = country.getTotalConfirmed();
                    int death = country.getTotalDeaths();
                    int recovered = country.getTotalRecovered();

                    binding.setCountryConfirmed(CommonUtils.numberSeparator(confirmed));
                    binding.setCountryRecovered(CommonUtils.numberSeparator(death));
                    binding.setCountryDeaths(CommonUtils.numberSeparator(recovered));

                    ArrayList<PieEntry> entries = new ArrayList<>();

                    entries.add(new PieEntry(confirmed, 1));
                    entries.add(new PieEntry(death, 2));
                    entries.add(new PieEntry(recovered, 3));

                    PieDataSet dataSet = new PieDataSet(entries, "Country Data");

                    dataSet.setDrawIcons(false);
                    dataSet.setDrawValues(false);
                    dataSet.setSliceSpace(3f);
                    dataSet.setIconsOffset(new MPPointF(0, 40));
                    dataSet.setSelectionShift(5f);

                    // add a lot of colors

                    ArrayList<Integer> colors = new ArrayList<>();

                    colors.add(Color.parseColor(getResources().getString(R.color.colorAccent)));
                    colors.add(Color.parseColor(getResources().getString(R.color.themeRed)));
                    colors.add(Color.parseColor(getResources().getString(R.color.themeOrange)));

                    dataSet.setColors(colors);

                    PieData data = new PieData(dataSet);
                    binding.homeCountryDataPieChart.setData(data);


                    // undo all highlights
                    binding.homeCountryDataPieChart.highlightValues(null);
                    binding.homeCountryDataPieChart.setCenterText("Update:\n" + country.getDate()
                            .replace("T", "\n")
                            .replace("Z", ""));

                    binding.homeCountryDataPieChart.invalidate();

                }

            }

        }
    }



}
