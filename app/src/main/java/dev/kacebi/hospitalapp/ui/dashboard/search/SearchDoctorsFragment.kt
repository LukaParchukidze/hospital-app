package dev.kacebi.hospitalapp.ui.dashboard.search

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.dashboard.DoctorOverviewModel
import dev.kacebi.hospitalapp.ui.dashboard.DoctorsOverviewsAdapter
import kotlinx.android.synthetic.main.fragment_search_doctors.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SearchDoctorsFragment : Fragment() {

    var itemView: View? = null
    private lateinit var adapter: DoctorsOverviewsAdapter
    private val doctorsOverviews = mutableListOf<DoctorOverviewModel>()

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
        adapter = DoctorsOverviewsAdapter(
            doctorsOverviews
        )
        searchDoctorsRecyclerView.adapter = adapter

        super.onViewCreated(view, savedInstanceState)
    }

    private fun getSearchDoctors(search: String) {
        if (search.isNotEmpty()) {
            searchDoctorsProgressBar.visibility = View.VISIBLE
            doctorsOverviews.clear()
            adapter.notifyDataSetChanged()

            CoroutineScope(Dispatchers.IO).launch {
                val querySnapshot = App.dbDoctors.get().await()
                for (document in querySnapshot.documents) {
                    if (document.get("full_name").toString().contains(search)) {
                        val doctor = document.toObject(DoctorOverviewModel::class.java)
                        val byteArray =
                            App.storage.child(document.id + ".png").getBytes(1024 * 1024L).await()
                        val bitmapDrawable = BitmapDrawable(
                            resources,
                            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        )
                        doctor!!.drawable = bitmapDrawable
                        doctorsOverviews.add(doctor)
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
            handler.postDelayed(workRunnable!!, 500)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
}