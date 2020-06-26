package com.example.covidstatusapp.fragments.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.covidstatusapp.R;
import com.example.covidstatusapp.adapters.CustomPagerAdapter;
import com.example.covidstatusapp.common.FontUtils;
import com.example.covidstatusapp.models.Global;
import com.example.covidstatusapp.models.SummaryResponse;
import com.example.covidstatusapp.viewModel.GlobalViewModel;
import com.example.covidstatusapp.viewModel.MyCountryViewModel;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class StatisticsFragment extends Fragment {
    private SmartTabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView pageTitle;
    private TextView chartTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistics_fragment, container, false);    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout = view.findViewById(R.id.tl_main_statistics);
        mViewPager = view.findViewById(R.id.vp_region);
        pageTitle = view.findViewById(R.id.pagetitle);
        chartTitle = view.findViewById(R.id.chart_title);
        FontUtils.getFontUtils(getActivity()).setTextViewBoldFont(pageTitle);
        FontUtils.getFontUtils(getActivity()).setTextViewBoldFont(chartTitle);
        init();
    }
    private void init() {
        mViewPager.setAdapter(new CustomPagerAdapter(getChildFragmentManager(), getContext()));
        mTabLayout.setCustomTabView((container, position, adapter) -> {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View tab = inflater.inflate(R.layout.layout_custom_tab, container, false);
            TextView customText = tab.findViewById(R.id.textViewCustomTab);
            FontUtils.getFontUtils(getActivity()).setTextViewRegularFont(customText);
            switch (position) {
                case 0:
                case 1:
                    customText.setText(adapter.getPageTitle(position));
                    break;
                default:
                    throw new IllegalStateException("Invalid position: " + position);
            }
            return tab;
        });

        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
    }
}