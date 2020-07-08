package dev.kacebi.hospitalapp.ui.dashboard.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.extensions.autoScroll
import dev.kacebi.hospitalapp.ui.dashboard.PatientDashboardActivity
import dev.kacebi.hospitalapp.ui.dashboard.home.specialties.SpecialtyModel
import dev.kacebi.hospitalapp.ui.dashboard.home.specialties.SpecialtyOnClick
import dev.kacebi.hospitalapp.ui.dashboard.doctors.SpecialtiesAdapter
import dev.kacebi.hospitalapp.ui.dashboard.home.news.NewsActivity
import dev.kacebi.hospitalapp.ui.dashboard.home.news.NewsModel
import dev.kacebi.hospitalapp.ui.dashboard.home.news.NewsViewPagerAdapter
import dev.kacebi.hospitalapp.ui.dashboard.home.specialties.SpecialtiesWithIconsAdapter
import kotlinx.android.synthetic.main.activity_patient_dashboard.*
import kotlinx.android.synthetic.main.fragment_doctors.specialtiesRecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import me.relex.circleindicator.CircleIndicator
import java.util.ArrayList


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
            setUpNewsViePager(itemView!!)

        }
        return itemView
    }

    private fun setUpNewsViePager(itemView: View) {
        itemView.newsProgressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val newsQS = App.dbNews.get().await()
            for (item in newsQS) {
                val news = App.dbNews.document(item.id).get().await().toObject(
                    NewsModel::class.java
                )
                val byteArray = App.storage.child(news!!.image_uri).getBytes(1024 * 1024L).await()
                val bitmapDrawable = BitmapDrawable(
                    resources,
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                )
                news.drawable = bitmapDrawable
                newsModel.add(news)
            }

            d("newsModel", "$newsModel")
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
        d("position","Start position: $position")
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
                )
                val byteArray = App.storage.child(specialty!!.uri).getBytes(1024 * 1024L).await()
                val bitmapDrawable = BitmapDrawable(
                    resources,
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                )
                specialty.drawable = bitmapDrawable
                specialties.add(specialty)
            }
            adapter =
                SpecialtiesWithIconsAdapter(
                    specialties,
                    object :
                        SpecialtyOnClick {
                        override fun onClick(adapterPosition: Int) {
                            val activity = (activity as PatientDashboardActivity)
                            activity.goToFragment(
                                activity.doctorsFragment,
                                activity.homeFragment,
                                activity.searchDoctorsFragment
                            )
                            activity.doctorsFragment.getDoctorsIndex = adapterPosition
                            SpecialtiesAdapter.click = adapterPosition
                            activity.doctorsFragment.adapter.notifyDataSetChanged()
                            activity.doctorsFragment.setUpDoctorsRecyclerview(
                                activity.doctorsFragment.itemView!!,
                                specialties[adapterPosition].specialty
                            )
                            activity.doctorsFragment.specialtiesRecyclerView.scrollToPosition(
                                adapterPosition
                            )
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