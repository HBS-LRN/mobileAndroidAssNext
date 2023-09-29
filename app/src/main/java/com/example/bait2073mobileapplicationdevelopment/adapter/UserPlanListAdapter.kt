import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.interfaces.GetUserPLanListService
import com.example.bait2073mobileapplicationdevelopment.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserPlanListAdapter(private val context: Context, private val listener: UserPlanClickListener) :
    RecyclerView.Adapter<UserPlanListAdapter.UserPlanListViewHolder>() {

    private var ctx: Context? = null
    var UserPlan = mutableListOf<UserPlan>()
    var UserPlanfullList = mutableListOf<UserPlan>()
    var UserPlanList = mutableListOf<UserPlanList>()
    var userPlanWorkoutfullList = mutableListOf<UserPlanList>()

    fun setData(newData:  List<UserPlan>) {
        UserPlan = newData as ArrayList<UserPlan>
//        UserPlan.clear()
//        UserPlan.addAll(newData)
//        notifyDataSetChanged()
    }
    fun setUserPlanData(newData:  List<UserPlanList>) {
        UserPlanList.clear()
        UserPlanList.addAll(newData)
        notifyDataSetChanged()
    }
    inner class UserPlanListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userPlan_layout = itemView.findViewById<CardView>(R.id.userPlan_layout)
        val planName = itemView.findViewById<TextView>(R.id.planName)
        val totalExcise = itemView.findViewById<TextView>(R.id.qtyPlantxt)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPlanListViewHolder {
        // Inflate your item layout and create a ViewHolder
        ctx = parent.context
        return UserPlanListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycleview_userplanlist, parent,false)
        )
    }

    override fun getItemCount(): Int {

        return UserPlan.size
    }

    override fun onBindViewHolder(holder: UserPlanListViewHolder, position: Int) {
        val currentUserPlan = UserPlan[position]

        val service = RetrofitClientInstance.retrofitInstance!!.create(GetUserPLanListService::class.java)
        val call = service.getUserPlanListByUserPlanId(currentUserPlan.id)

            call.enqueue(object : Callback<List<UserPlanList>> {
                override fun onResponse(call: Call<List<UserPlanList>>, response: Response<List<UserPlanList>>) {
                    if (response.isSuccessful) {
                        val userPlanList = response.body()
                        val totalExcise = userPlanList?.size ?: 0 // Get the size of the fetched list

                        holder.planName.text = currentUserPlan.plan_name
                        holder.totalExcise.text = totalExcise.toString()

                        holder.userPlan_layout.setOnClickListener {
                            listener.onItemClicked(UserPlan[holder.adapterPosition])
                        }

                        holder.userPlan_layout.setOnLongClickListener {
                            listener.OnLongItemClicked(UserPlan[holder.adapterPosition], holder.userPlan_layout)
                            true
                        }
                    } else {
                        // Handle the case where the API request was not successful
                    }
                }

                override fun onFailure(call: Call<List<UserPlanList>>, t: Throwable) {
                    // Handle the failure of the API request
                }
            })

    }
    fun updateUserPlanList(newList : List<UserPlan>){

        Log.e("userplanlist","$newList")
        UserPlanfullList.clear()
        UserPlanfullList.addAll(newList)
        UserPlan.clear()
        UserPlan.addAll(UserPlanfullList)
//        Log.e("gg","$UserPlan")
        notifyDataSetChanged()
    }

    fun filerList(search: String) {
        Log.e("TestSearch","filerList")
        UserPlan.clear()

        for (item in UserPlanfullList) {
            Log.e("TestSearch","${item.plan_name}")
            if (item.plan_name?.lowercase()?.contains(search.lowercase()) == true) {
                UserPlan.add(item)
                Log.e("TestSearchNAme","${item.plan_name}")
            }
        }

        notifyDataSetChanged()
    }


    interface UserPlanClickListener {
        fun onItemClicked(userPlan: UserPlan)
        fun OnLongItemClicked(userPlan: UserPlan,cardView: CardView)
    }
}

