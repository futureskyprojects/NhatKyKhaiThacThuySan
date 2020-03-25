package vn.vistark.nkktts.core.models.selected_job

import com.google.gson.annotations.SerializedName

class SelectedJob(
    @SerializedName("id") var id: Int = -1,
    @SerializedName("job_id") var jobId: Int = -1,
    @SerializedName("info_job_array") var jobInfoArray: List<Float> = listOf(-1F, -1F),
    @SerializedName("info_job") var jobInfo: String = "",
    @SerializedName("captain_id") var captainId: Int = -1
)