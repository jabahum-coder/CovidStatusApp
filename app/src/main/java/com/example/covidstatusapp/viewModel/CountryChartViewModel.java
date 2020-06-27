package com.example.covidstatusapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.covidstatusapp.models.CountryChartModel;
import com.example.covidstatusapp.repositories.CountryChartRepository;
import com.example.covidstatusapp.utils.Resource;

import java.util.List;

public class CountryChartViewModel extends ViewModel {
    private MutableLiveData<Resource<List<CountryChartModel>>> mutableLiveData;
    private CountryChartRepository countryChartRepository;

    public void init(String country) {
        if (mutableLiveData != null) {
            return;
        }
        countryChartRepository = CountryChartRepository.getInstance();
        mutableLiveData = countryChartRepository.getCountryChartModel(country);

    }

    public LiveData<Resource<List<CountryChartModel>>> getCountryChartRepository() {
        return mutableLiveData;
    }
}