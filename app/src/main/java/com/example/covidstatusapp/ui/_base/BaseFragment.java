package com.example.covidstatusapp.ui._base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.covidstatusapp.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends DaggerFragment {

    private static final String TAG = "BaseFragment";

    private BaseActivity mActivity;
    private View mRootView;
    private T mViewDataBinding;
    private V mViewModel;

    private NavController mNavController;

    private RxPermissions mPermission;

    public RxPermissions getMPermission() {
        return mPermission;
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mRootView = mViewDataBinding.getRoot();
        setHasOptionsMenu(true);
        mPermission = new RxPermissions(requireActivity());
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            mNavController = Navigation.findNavController(mRootView);
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = getViewModel();
        alertBuilder = new CFAlertDialog.Builder(getBaseActivity());
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
//            activity.onFragmentAttached();
        }
    }


    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    public void showKeyboard() {
        if (mActivity != null) {
            mActivity.showKeyboard();
        }
    }

    public boolean isNetworkConnected() {
        return mActivity != null && mActivity.isNetworkConnected();
    }

    protected void showLoading(String message) {
        if (mActivity != null) mActivity.showLoading(message);
    }

    protected void shareApp() {
        if (mActivity != null) mActivity.shareApp();
    }

    protected void hideLoading() {
        if (mActivity != null) mActivity.hideLoading();
    }

    protected <Z> Intent newIntent(Class<Z> clazz) {
        return new Intent(getBaseActivity(), clazz);
    }

    protected <Z> void newActivity(Class<Z> clazz) {
        startActivity(newIntent(clazz));
    }

    //    @Inject
    private CFAlertDialog.Builder alertBuilder;

    public CFAlertDialog.Builder showErrorDialog(String title, String message) {
        return alertBuilder.setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setDialogBackgroundColor(getResources().getColor(R.color.white))
                .setTextColor(getResources().getColor(R.color.sv_low_color))
                .setIcon(getResources().getDrawable(R.drawable.ic_warning_red_foreground))
                .setTitle(title)
                .setMessage(message)
                .setAutoDismissAfter(3000);
    }

    public CFAlertDialog.Builder showSuccessDialog(String title, String message) {
        return alertBuilder.setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setDialogBackgroundColor(getResources().getColor(R.color.green))
                .setTextColor(Color.LTGRAY)
                .setTitle(title)
                .setMessage(message)
                .setAutoDismissAfter(3000);
    }

    protected CFAlertDialog.Builder getAlertBuilder() {
        return new CFAlertDialog.Builder(getBaseActivity());
    }


    public void setupToolBar(Toolbar toolbar, String title) {
        getBaseActivity().setSupportActionBar(toolbar);
        if (getBaseActivity().getSupportActionBar() != null) {
            getBaseActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getBaseActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
            toolbar.setTitle(title);
        }
    }

    protected void navigateSafe(NavDirections navDirections){
        if(mNavController != null && navDirections != null){
            if(mNavController.getCurrentDestination() != null && mNavController.getCurrentDestination().getId() != navDirections.getActionId()){
                mNavController.navigate(navDirections);
            }
        }
    }

    public void navigateSafe(NavDirections navDirections, NavOptions navOptions){
        if(mNavController != null && navDirections != null){
            if(mNavController.getCurrentDestination() != null && mNavController.getCurrentDestination().getId() != navDirections.getActionId()){
                mNavController.navigate(navDirections,navOptions);
            }
        }
    }

    public void navigateSafe(NavDirections navDirections, Navigator.Extras navExtras){
        if(mNavController != null && navDirections != null){
            if(mNavController.getCurrentDestination() != null && mNavController.getCurrentDestination().getId() != navDirections.getActionId()){
                mNavController.navigate(navDirections,navExtras);
            }
        }
    }
}
