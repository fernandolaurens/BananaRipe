package com.laurens.bananaripe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.laurens.adapter.ListPisangAdapter
import com.laurens.data.Banana
import com.laurens.detail.DetailActivity


class InformasiActivity : AppCompatActivity() {
    private lateinit var rvBanana: RecyclerView
    private val list = ArrayList<Banana>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi)

        rvBanana = findViewById(R.id.rv_Banana)
        rvBanana.setHasFixedSize(true)

        list.addAll(getListBanana())
        showRecyclerList()

        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_information

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_beranda -> {
                    startActivity(Intent(this@InformasiActivity, MainActivity::class.java))
                    true
                }
                R.id.bottom_detection -> {
                    startActivity(Intent(this@InformasiActivity, DeteksiActivity::class.java))
                    true
                }
                R.id.bottom_profile -> {
                    startActivity(Intent(this@InformasiActivity, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun getListBanana(): ArrayList<Banana> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescrption = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val dataDetail = resources.getStringArray(R.array.data_detail)
        val listBanana = ArrayList<Banana>()
        for (i in dataName.indices) {
            val banana = Banana(dataName[i], dataDescrption[i], dataPhoto.getResourceId(i, -1), dataDetail[i])
            listBanana.add(banana)
        }
        return listBanana
    }

    private fun showRecyclerList() {
        rvBanana.layoutManager = LinearLayoutManager(this)
        val listBananaAdapter2 = ListPisangAdapter(list)
        rvBanana.adapter = listBananaAdapter2

        listBananaAdapter2.setOnItemClickCallback(object : ListPisangAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Banana) {
                val pindahDataIntent = Intent(this@InformasiActivity, DetailActivity::class.java)
                pindahDataIntent.putExtra("Nama", data.name)
                pindahDataIntent.putExtra("Gambar", data.photo)
                pindahDataIntent.putExtra("Deskripsi", data.description)
                pindahDataIntent.putExtra("Detail", data.detail)
                startActivity(pindahDataIntent)
            }
        })
    }

}