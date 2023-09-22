import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface GetUserPlanService {

    @GET("userPlans")
    @Headers("Accept: application/json", "Content-Type: application/json")
    fun getUserPlanList(): Call<List<UserPlan>>

    @GET("userPlans/{user_id}")
    fun getUserPlan(@Path("user_id") user_id: Int): Call<List<UserPlan>>

    @DELETE("deleteUserPlan/{user_id}")
    @Headers("Accept:application/json", "Content-Type:application/json",
        "Authorization: Bearer 73668350bdf06c66f3388408c1a712b378c3e25da599753b21b664a6261e246c")
    fun deleteUserPlanByUserId(@Path("user_id") user_id: Int): Call<UserPlan>
    @POST("inserUserPlan")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Authorization: Bearer 73668350bdf06c66f3388408c1a712b378c3e25da599753b21b664a6261e246c"
    )
    fun createUserPlan(@Body userPlan: UserPlan): Call<UserPlan>

    // Add other methods for user plans as needed
}