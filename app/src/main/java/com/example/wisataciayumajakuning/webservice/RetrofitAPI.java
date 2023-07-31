package com.example.wisataciayumajakuning.webservice;

import com.example.wisataciayumajakuning.model.DirectionModel.DirectionResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {
    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);
}
