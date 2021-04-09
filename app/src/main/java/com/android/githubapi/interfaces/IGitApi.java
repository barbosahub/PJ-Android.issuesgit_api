package com.android.githubapi.interfaces;
import com.android.githubapi.models.GithubApi;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface IGitApi {

    @Headers({"Content-type: application/json"})

    @GET("repos/JetBrains/kotlin/issues")
    Call<List<GithubApi>> findList();

}
