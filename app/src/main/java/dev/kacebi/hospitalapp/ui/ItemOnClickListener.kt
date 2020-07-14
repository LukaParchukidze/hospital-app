package dev.kacebi.hospitalapp.ui

interface ItemOnClickListener {
    fun onClick(adapterPosition: Int) {}
    fun onClickChangeStatus(adapterPosition: Int, status: String) {}
}