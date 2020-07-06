package dev.kacebi.hospitalapp.ui.dashboard

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var itemView: View? = null
    private val specialties = mutableListOf<SpecialtyModel>()
    private lateinit var adapter: SpecialtiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.fragment_home, container, false)
            setUpSpecialtiesRecyclerView(itemView!!)
        }
        return itemView
    }



    private fun setUpSpecialtiesRecyclerView(itemView: View) {
        itemView.specialtiesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.specialtiesProgressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val specialtiesQS = App.dbSpecialtiesRef.get().await()
            for (specialtyQDS in specialtiesQS) {
                val specialty = App.dbSpecialtiesRef.document(specialtyQDS.id).get().await().toObject(SpecialtyModel::class.java)
                val byteArray = App.storageRef.child(specialty!!.uri).getBytes(1024 * 1024L).await()
                val bitmapDrawable = BitmapDrawable(resources, BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                specialty.drawable = bitmapDrawable
                specialties.add(specialty)
            }
            adapter = SpecialtiesAdapter(specialties, R.layout.item_specialties_layout)
            withContext(Dispatchers.Main) {
                itemView.specialtiesRecyclerView.adapter = adapter
                itemView.specialtiesProgressBar.visibility = View.GONE
            }
        }
    }

}