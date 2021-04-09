package com.android.githubapi.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PullRequest {
    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("html_url")
    @Expose
    var htmlUrl: String? = null

    @SerializedName("diff_url")
    @Expose
    var diffUrl: String? = null

    @SerializedName("patch_url")
    @Expose
    var patchUrl: String? = null

}