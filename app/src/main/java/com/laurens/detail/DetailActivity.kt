package com.laurens.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.laurens.bananaripe.R

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private val EXTRA_PHOTO = "Gambar"
    private val EXTRA_NAME = "Nama"
    private val EXTRA_DESCRIPTION = "Deskripsi"
    private val EXTRA_DETAIL = "Detail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)

        val fotoPisang = intent.getIntExtra(EXTRA_PHOTO, 0)
        val namaPisang = intent.getStringExtra(EXTRA_NAME)
        val deskirpisiPisang = intent.getStringExtra(EXTRA_DESCRIPTION)
        val deskirpisiDetail = intent.getStringExtra(EXTRA_DETAIL)

        val IVfotoPahlawan = findViewById<ImageView>(R.id.img_item_photo)
        val TVnamaPahlawan = findViewById<TextView>(R.id.tv_item_name)
        val TVjudulDeskripsi = findViewById<TextView>(R.id.tv_item_description)
        val TVDetail= findViewById<TextView>(R.id.tv_Detail)

        IVfotoPahlawan.setImageResource(fotoPisang)
        TVnamaPahlawan.text = namaPisang
        TVjudulDeskripsi.text = deskirpisiPisang
        TVDetail.text = deskirpisiDetail


        val nama: TextView = findViewById(R.id.tv_item_name)
        nama.setOnClickListener(this)



    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}