package dev.kacebi.hospitalapp.ui.patients_dashboard.search

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.full_information.DoctorInformationActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.PatientDashboardActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.DoctorOverviewModel
import dev.kacebi.hospitalapp.ui.patients_dashboard.doctors.list.adapters.DoctorsOverviewsAdapter
import dev.kacebi.hospitalapp.utils.Utils
import kotlinx.android.synthetic.main.fragment_search_doctors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Runnable


class SearchDoctorsFragment : Fragment() {

    var itemView: View? = null
    private lateinit var adapter: DoctorsOverviewsAdapter
    private val doctorsOverviews = mutableListOf<DoctorOverviewModel>()


    private var jobDoctors: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_search_doctors, container, false)
        }
        return itemView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchDoctorsEditText.addTextChangedListener(textWatcher)
        searchDoctorsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter =
            DoctorsOverviewsAdapter(
                doctorsOverviews,
                object : ItemOnClickListener {
                    override fun onClick(adapterPosition: Int) {
                        val intent = Intent(
                            (activity as PatientDashboardActivity),
                            DoctorInformationActivity::class.java
                        ).apply {
                            putExtra(
                                "doctorId",
                                doctorsOverviews[adapterPosition].doctorId
                            )
                            putExtra("lastName", doctorsOverviews[adapterPosition].last_name)
                        }
                        activity!!.startActivity(intent)
                    }

                }
            )
        searchDoctorsRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun getSearchDoctors(search: String) {
        if (search.isNotEmpty()) {
            searchDoctorsProgressBar.visibility = View.VISIBLE
            doctorsOverviews.clear()
            adapter.notifyDataSetChanged()

            if (jobDoctors != null)
                jobDoctors!!.cancel()

            jobDoctors = CoroutineScope(Dispatchers.IO).launch {
                val querySnapshot = App.dbDoctors.get().await()
                for (document in querySnapshot.documents) {
                    if (jobDoctors!!.isActive) {
                        if (document.get("full_name").toString().contains(search, true)) {
                            val doctorOverview =
                                DoctorOverviewModel(
                                    doctorId = document.id,
                                    full_name = document["full_name"] as String,
                                    last_name = document["last_name"] as String,
                                    specialty = document["specialty"] as String,
                                    working_experience = document["working_experience"] as Long
                                )
                            val byteArray =
                                App.storage.child("/doctor_photos/${document.id}.png")
                                    .getBytes(FileSizeConstants.THREE_MEGABYTES)
                                    .await()
                            val bitmap = Utils.byteArrayToBitmap(byteArray)
                            doctorOverview.bitmap = bitmap
                            doctorsOverviews.add(doctorOverview)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    searchDoctorsProgressBar.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        var handler = Handler(Looper.getMainLooper())
        var workRunnable: Runnable? = null
        override fun afterTextChanged(s: Editable) {
            if (workRunnable != null)
                handler.removeCallbacks(workRunnable!!)
            workRunnable = Runnable { getSearchDoctors(s.toString()) }
            handler.postDelayed(workRunnable!!, 1000)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
}