package com.example.aleem.routefinder;

import com.example.aleem.routefinder.Model.Results;
import com.example.aleem.routefinder.Remote.IGoogleAPIService;
import com.example.aleem.routefinder.Remote.RetrofitClient;
import com.example.aleem.routefinder.Remote.RetrofitScalarsClient;

public class Common {

    public static Results currentResult;
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleApiService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

    public static IGoogleAPIService getGoogleApiServiceScalars(){
        return RetrofitScalarsClient.getScalarClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
