/*
package com.example.covidstatusapp.ui.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;

import com.example.covidstatusapp.ui.models.CountryChartModel;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CountryChartRepository {
    private static CountryChartRepository countryChartRepository;
    private APIinterface apIinterface;

    private MediatorLiveData<Resource<List<CountryChartModel>>> data = new MediatorLiveData<>();


    private CountryChartRepository() {
        apIinterface = APIClient.getClient().create(APIinterface.class);
    }

    public static CountryChartRepository getInstance() {
        if (countryChartRepository == null) {
            countryChartRepository = new CountryChartRepository();
        }
        return countryChartRepository;
    }


    public MediatorLiveData<Resource<List<CountryChartModel>>> getCountryChartModel(String country) {

        data.setValue(Resource.loading(null));

        final LiveData<Resource<List<CountryChartModel>>> source = LiveDataReactiveStreams.fromPublisher(
                apIinterface.getCountryChartData(country)
                        .toFlowable()
                        .onErrorReturn(throwable -> {
                            CountryChartModel chartModel = new CountryChartModel();
                            chartModel.setActive(1);
                            ArrayList<CountryChartModel> list = new ArrayList<>();
                            list.add(chartModel);
                            return list;
                        })
                        .map((Function<List<CountryChartModel>, Resource<List<CountryChartModel>>>) chartModelList -> {
                            if (chartModelList.size() > 0) {
                                if (chartModelList.get(0).getActive() == 1) {
                                    return Resource.error("Something Went Wrong", null);
                                }
                            }
                            return Resource.success(chartModelList);
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        );

        data.addSource(source, liveCasesResource -> {
            data.setValue(liveCasesResource);
            data.removeSource(source);
        });

        return data;
    }
}
*/