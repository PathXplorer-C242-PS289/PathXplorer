package com.example.pathxplorer.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.local.entity.DailyQuestEntity
import com.example.pathxplorer.databinding.FragmentAccountBinding
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Date

class AccountFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    // Animation properties
    private lateinit var fadeInAnimation: Animation
    private lateinit var slideUpAnimation: Animation
    private lateinit var slideRightAnimation: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        initializeAnimations()
        setupUserInfo()
        setupClickListeners()
        startEntryAnimations()
    }

    private fun initializeAnimations() {
        fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        slideRightAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_right)
    }

    private fun startEntryAnimations() {
        binding.apply {
            // Initial setup
            userProfileCard.alpha = 0f
            userProfileCard.translationY = 100f

            // Animate profile card entry
            userProfileCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun setupUserInfo() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.apply {
                // Animate user info updates
                val fadeAnimation = AlphaAnimation(0f, 1f).apply {
                    duration = 300
                    interpolator = DecelerateInterpolator()
                }

                tvUserName.startAnimation(fadeAnimation)
                tvUserEmail.startAnimation(fadeAnimation)

                viewModel.getDailyQuestById(user.userId)
                viewModel.dailyQuest.observe(viewLifecycleOwner) { dailyQuest ->
                    if (dailyQuest == null) {
                        val dailyQuestEntity = DailyQuestEntity(
                            idUser = user.userId,
                            emailUser = user.email,
                            lastCheck = Date().toString(),
                            dailyQuestCount = 1,
                            score = 0,
                        )
                        viewModel.insertDaily(dailyQuestEntity)
                    } else {
                        dailyQuest.dailyQuestCount.toString().also { dailyQuestValue.text = it }
                        dailyQuest.score.toString().also { scoreValue.text = it }
                        levelValue.text = when (dailyQuest.score) {
                            null, 0 -> "Pemula"
                            in 1..30 -> "Junior"
                            in 31..60 -> "Intermediate"
                            in 61..90 -> "Advanced"
                            else -> "Expert"
                        }
                    }
                }

                viewModel.getTestResults().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showError(result.error.toString())
                        }
                        is Result.Success -> {
                            showLoading(false)
                            testsValue.text = result.data.data.testResults.size.toString()
                        }
                    }
                }

                tvUserName.text = user.name
                tvUserEmail.text = user.email

                val statsLayout = userProfileCard.findViewById<LinearLayout>(
                    LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 16, 0, 0)
                            Log.d("AccountFragment", "ID: ${user.token}")
                        }
                    }.id
                )

                statsLayout?.let { layout ->
                    // Animate each stat with sequential delay
                    layout.children.forEachIndexed { index, view ->
                        view.alpha = 0f
                        view.animate()
                            .alpha(1f)
                            .setStartDelay((index * 100).toLong())
                            .setDuration(500)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }

                    // Update stats with animations

                    layout.getChildAt(2)?.findViewById<TextView>(R.id.daily_quest_value)?.apply {
                        animateTextChange((user.dailyQuestCount ?: 0).toString())
                    }
                    layout.getChildAt(3)?.findViewById<TextView>(R.id.score_value)?.apply {
                        animateTextChange((user.score ?: 0).toString())
                    }
                }
            }
        }
    }

    private fun TextView.animateTextChange(newText: String) {
        animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                text = newText
                animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun setupClickListeners() {
        binding.apply {
            // Profile card click animation
            userProfileCard.setOnClickListener { view ->
                view.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction {
                                navigateToProfileSettings()
                            }
                            .start()
                    }
                    .start()
            }

            // Menu items animations
            val menuItems = listOf(
                tentangAplikasiLayout,
                syaratKetentuanLayout,
                kebijakanPrivasiLayout,
                settingLayout
            )

            menuItems.forEach { layout ->
                layout.setOnClickListener { view ->
                    view.startAnimation(slideRightAnimation)
                    when (view) {
                        tentangAplikasiLayout -> showAboutApp()
                        syaratKetentuanLayout -> showTermsAndConditions()
                        kebijakanPrivasiLayout -> showPrivacyPolicy()
                        settingLayout -> showToast("Settings - Coming Soon")
                    }
                }
            }

            // Logout button animation
            btnLogout.setOnClickListener { view ->
                view.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction {
                                showLogoutConfirmation()
                            }
                            .start()
                    }
                    .start()
            }
        }
    }

    private fun showAboutApp() {
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
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showTermsAndConditions() {
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
            .setPositiveButton("Saya Mengerti") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showPrivacyPolicy() {
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
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun navigateToProfileSettings() {
        startActivity(Intent(requireContext(), ProfileSettingsActivity::class.java))
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Keluar") { _, _ -> logout() }
            .show()
    }

    private fun logout() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.provider != "credentials") {
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

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            userProfileCard.animate()
                .alpha(if (isLoading) 0.5f else 1f)
                .setDuration(300)
                .start()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}