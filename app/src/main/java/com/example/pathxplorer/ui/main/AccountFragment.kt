package com.example.pathxplorer.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.example.pathxplorer.databinding.FragmentAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setupUserInfo()
        setupClickListeners()
    }

    private fun setupUserInfo() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                binding.apply {
                    tvUserName.text = user.name
                    tvUserEmail.text = user.email
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            userProfileCard.setOnClickListener {
                navigateToProfileSettings()
            }

            tentangAplikasiLayout.setOnClickListener {
                navigateToAboutApp()
            }

            syaratKetentuanLayout.setOnClickListener {
                navigateToTerms()
            }

            kebijakanPrivasiLayout.setOnClickListener {
                navigateToPrivacyPolicy()
            }

            settingLayout.setOnClickListener {
                navigateToSettings()
            }

            btnLogout.setOnClickListener {
                showLogoutConfirmation()
            }
        }
    }

    private fun navigateToProfileSettings() {
        startActivity(Intent(requireContext(), ProfileSettingsActivity::class.java))
    }

    private fun navigateToAboutApp() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tentang Aplikasi")
            .setMessage("""
                PathXplorer adalah aplikasi yang dirancang untuk membantu siswa menentukan jalur 
                pendidikan tinggi mereka. Dengan fitur pemetaan minat dan bakat, rekomendasi jurusan, 
                serta informasi kampus yang komprehensif, PathXplorer memudahkan siswa dalam membuat 
                keputusan untuk masa depan pendidikan mereka.
                
                Versi: 1.0.0
                Dikembangkan oleh: Tim C242-PS289
            """.trimIndent())
            .setPositiveButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToTerms() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Syarat dan Ketentuan")
            .setMessage("""
                1. Penggunaan Aplikasi
                - Pengguna harus berusia minimal 13 tahun
                - Informasi yang diberikan harus akurat
                - Dilarang menyalahgunakan layanan
                
                2. Akun Pengguna
                - Setiap pengguna bertanggung jawab atas keamanan akunnya
                - Informasi login bersifat rahasia
                - Satu email untuk satu akun
                
                3. Konten
                - Semua konten dilindungi hak cipta
                - Dilarang mendistribusikan ulang tanpa izin
                - Konten dapat diperbarui sewaktu-waktu
                
                4. Privasi
                - Data pengguna dilindungi
                - Tidak ada penyalahgunaan data
                - Kerahasiaan dijamin
                
                5. Perubahan Ketentuan
                - Dapat berubah sewaktu-waktu
                - Pengguna akan diberitahu
                - Penggunaan berkelanjutan berarti menyetujui perubahan
            """.trimIndent())
            .setPositiveButton("Saya Mengerti") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToPrivacyPolicy() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Kebijakan Privasi")
            .setMessage("""
                1. Informasi yang Kami Kumpulkan
                - Data profil (nama, email)
                - Data penggunaan aplikasi
                - Hasil tes dan preferensi
                
                2. Penggunaan Informasi
                - Personalisasi layanan
                - Peningkatan aplikasi
                - Komunikasi dengan pengguna
                
                3. Perlindungan Data
                - Enkripsi data
                - Akses terbatas
                - Monitoring keamanan
                
                4. Hak Pengguna
                - Akses data pribadi
                - Koreksi data
                - Hapus data
                
                5. Perubahan Kebijakan
                - Pemberitahuan perubahan
                - Evaluasi berkala
                - Transparansi
                
                6. Kontak
                Pertanyaan tentang privasi:
                privacy@pathxplorer.com
            """.trimIndent())
            .setPositiveButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToSettings() {
        showToast("Settings - Coming Soon")
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Keluar") { _, _ ->
                logout()
            }
            .show()
    }

    private fun logout() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.provider != "credential") {
                signOutGoogle()
            } else {
                viewModel.logout()
            }
        }
    }

    private fun signOutGoogle() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireActivity())
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            viewModel.logout()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}