package com.example.pathxplorer.ui.utils.customview

import com.example.pathxplorer.data.models.Question

val questionsList = listOf(
    listOf(
        "Menguji kualitas Komponen barang sebelum dikirim.",
        "Memasang batu bata atau ubin.",
        "Bekerja di pengeboran minyak lepas pantai.",
        "Merakit komponen elektronik.",
        "Mengoperasikan mesin penggiling di pabrik.",
        "Memperbaiki keran yang rusak.",
        "Merakit produk di pabrik.",
        "Memasang lantai di rumah."),

    listOf(
        "Mempelajari struktur tubuh manusia.",
        "Mempelajari perilaku hewan.",
        "Melakukan penelitian tentang tumbuhan atau hewan.",
        "Mengembangkan pengobatan atau prosedur medis baru.",
        "Melakukan penelitian biologi.",
        "Mempelajari paus dan jenis kehidupan laut lainnya.",
        "Bekerja di laboratorium biologi.",
        "Membuat peta dasar laut."),

    listOf(
        "Memimpin paduan suara musik.",
        "Mengarahkan sebuah drama.",
        "Mendesain karya seni untuk majalah.",
        "Menulis lagu.",
        "Menulis buku atau naskah drama.",
        "Bermain alat musik.",
        "Menjadi pemeran pengganti(melakukan aksi berbahaya) untuk film atau acara TV.",
        "Mendesain latar untuk drama."),

    listOf(
        "Memberikan bimbingan karir kepada orang lain.",
        "Melakukan pekerjaan sukarela di organisasi non-profit.",
        "Membantu orang yang memiliki masalah dengan narkoba atau alkohol.",
        "Mengajarkan rutinitas olahraga kepada individu.",
        "Membantu orang yang memiliki masalah keluarga.",
        "Mengawasi aktivitas anak-anak di perkemahan.",
        "Mengajarkan anak-anak cara membaca.",
        "Membantu orang lanjut usia dengan aktivitas harian mereka."),

    listOf(
        "Menjual waralaba restoran kepada individu.",
        "Menjual barang di toko serba ada.",
        "Mengelola operasional sebuah hotel.",
        "Mengoperasikan salon kecantikan atau pangkas rambut.",
        "Mengelola departemen dalam perusahaan besar.",
        "Mengelola toko pakaian.",
        "Menjual rumah.",
        "Mengelola toko mainan."),

    listOf(
        "Membuat cek gaji bulanan untuk sebuah kantor",
        "Mengelola persediaan barang menggunakan komputer.",
        "Menggunakan program komputer untuk membuat tagihan pelanggan.",
        "Mengelola catatan karyawan.",
        "Menghitung dan mencatat data statistik serta data numerik lainnya.",
        "Mengoperasikan kalkulator.",
        "Menangani transaksi perbankan pelanggan.",
        "Menyimpan catatan pengiriman dan penerimaan.")
)

fun getQuestion(): ArrayList<MutableList<Question>> {
    val dummyQuestion = ArrayList<MutableList<Question>>()
    if (dummyQuestion.isNotEmpty()) {
        return dummyQuestion
    }
    var numberQ = 1
    for (i in 1..6) {
        val questionList = mutableListOf<Question>()
        for (j in 1..8) {
            questionList.add(Question(i, questionsList[i-1][j-1], numberQ))
            numberQ++
        }
        dummyQuestion.add(questionList)
    }
    return dummyQuestion
}