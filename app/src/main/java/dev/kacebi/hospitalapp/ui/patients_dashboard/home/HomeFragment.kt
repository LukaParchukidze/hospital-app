package dev.kacebi.hospitalapp.ui.patients_dashboard.home

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.extensions.autoScroll
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.patients_dashboard.PatientDashboardActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.news.NewsActivity
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.news.NewsModel
import dev.kacebi.hospitalapp.ui.patients_dashboard.home.news.NewsViewPagerAdapter
import dev.kacebi.hospitalapp.utils.Utils
import kotlinx.android.synthetic.main.activity_patient_dashboard.*
import kotlinx.android.synthetic.main.fragment_doctors.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import me.relex.circleindicator.CircleIndicator


class HomeFragment : Fragment() {

    private var itemView: View? = null
    private val specialties = mutableListOf<SpecialtyModel>()
    private lateinit var adapter: SpecialtiesWithIconsAdapter
    private lateinit var newsAdapter: NewsViewPagerAdapter
    private val newsModel = mutableListOf<NewsModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_home, container, false)
            setUpSpecialtiesRecyclerView(itemView!!)
            setUpNewsViewPager(itemView!!)

        }
        return itemView
    }

    private fun setUpNewsViewPager(itemView: View) {
        itemView.newsProgressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val newsQS = App.dbNews.get().await()
            for (item in newsQS) {
                val news = App.dbNews.document(item.id).get().await().toObject(
                    NewsModel::class.java
                )
                val byteArray = App.storage.child(news!!.image_uri).getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                val bitmap = Utils.byteArrayToBitmap(byteArray)
                news.bitmap = bitmap
                if (news != null) {
                    newsModel.add(news)
                }
            }

            newsAdapter =
                NewsViewPagerAdapter(
                    newsModel, this@HomeFragment
                )

            withContext(Dispatchers.Main) {
                newsViewPager.adapter = newsAdapter
                itemView.newsProgressBar.visibility = View.GONE

                newsViewPager.autoScroll(5000)

                val indicator = indicator as CircleIndicator
                indicator.setViewPager(newsViewPager)
            }

        }
    }

    fun startNewsActivity(position: Int) {
        val intent = Intent((activity as PatientDashboardActivity), NewsActivity::class.java)
        intent.putExtra("position",position)
        activity!!.startActivity(intent)
    }

    private fun setUpSpecialtiesRecyclerView(itemView: View) {
        itemView.specialtiesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.specialtiesProgressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val specialtiesQS = App.dbSpecialties.get().await()
            for (specialtyQDS in specialtiesQS) {
                val specialty = App.dbSpecialties.document(specialtyQDS.id).get().await().toObject(
                    SpecialtyModel::class.java
                )!!
                val byteArray = App.storage.child(specialty.uri).getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                val bitmap = Utils.byteArrayToBitmap(byteArray)
                specialty.bitmap = bitmap
                specialties.add(specialty)
            }
            adapter =
                SpecialtiesWithIconsAdapter(
                    specialties,
                    object :
                        ItemOnClickListener {
                        override fun onClick(adapterPosition: Int) {
                            val activity = (activity as PatientDashboardActivity)
                            activity.goToFragment(
                                activity.doctorsFragment,
                                activity.homeFragment,
                                activity.searchDoctorsFragment
                            )
                            activity.doctorsFragment.getDoctorsIndex = adapterPosition
                            activity.doctorsFragment.specialtiesTabLayout.getTabAt(adapterPosition)?.select()
                            activity.bottomNavigation.selectedItemId = R.id.miDoctors
                        }

                    }
                )
            withContext(Dispatchers.Main) {
                itemView.specialtiesRecyclerView.adapter = adapter
                itemView.specialtiesProgressBar.visibility = View.GONE
            }
        }
    }

}