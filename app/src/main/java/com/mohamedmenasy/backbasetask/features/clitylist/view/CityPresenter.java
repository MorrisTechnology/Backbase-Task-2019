package com.mohamedmenasy.backbasetask.features.clitylist.view;

import android.content.Context;

import androidx.annotation.Nullable;

import com.mohamedmenasy.backbasetask.R;
import com.mohamedmenasy.backbasetask.core.data.City;
import com.mohamedmenasy.backbasetask.features.clitylist.data.LoadCitiesInteractor;
import com.mohamedmenasy.backbasetask.features.clitylist.data.LoadCityIdlingResource;
import com.mohamedmenasy.backbasetask.features.clitylist.data.SearchForCitiesInteractor;
import com.mohamedmenasy.backbasetask.core.model.trie.Trie;

import java.util.List;

public class CityPresenter {
    private CityView mainCityView;
    private LoadCitiesInteractor loadCitiesInteractor;
    private SearchForCitiesInteractor searchForCitiesInteractor;
    private Context context;
    private Trie<City> trie = new Trie<>();
    private boolean isDataLoaded = false;
    private LoadCityIdlingResource mIdlingResource;

    CityPresenter(Context context, CityView mainCityView,
                  LoadCitiesInteractor loadCitiesInteractor,
                  SearchForCitiesInteractor searchForCitiesInteractor,
                  @Nullable LoadCityIdlingResource idlingResource) {

        this.mainCityView = mainCityView;
        this.loadCitiesInteractor = loadCitiesInteractor;
        this.context = context;
        this.searchForCitiesInteractor = searchForCitiesInteractor;
        this.mIdlingResource = idlingResource;
    }

    void onResume() {
        if (!isDataLoaded) {
            if (mainCityView != null) {
                mainCityView.showProgress();
            }
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(false);
            }
            loadCitiesInteractor.loadItems(context.getResources().openRawResource(R.raw.cities), this::onFinished);
        }
    }

    void onDestroy() {
        mainCityView = null;
    }

    public void onFinished(List<City> items) {
        if (mainCityView != null) {

            mainCityView.setItems(items);
            for (City c : items) {
                trie.insert(c.getName(), c);
            }
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }

            mainCityView.hideProgress();

            isDataLoaded = true;
        }
    }

    public void onSearchFinished(List<City> items) {
        if (mainCityView != null) {
            mainCityView.setItems(items);
            mainCityView.hideProgress();
        }
    }

    public CityView getMainCityView() {
        return mainCityView;
    }

    public void search(String keyword) {
        searchForCitiesInteractor.findCities(trie, keyword, this::onSearchFinished);
    }
}
