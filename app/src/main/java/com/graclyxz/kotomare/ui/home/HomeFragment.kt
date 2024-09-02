package com.graclyxz.kotomare.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.graclyxz.kotomare.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        binding.webViewFLV.webChromeClient = WebChromeClient()

        binding.webViewFLV.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Permitir solo URLs de animeflv.net
                if (url != null && url.contains("animeflv.net")) {
                    view?.loadUrl(url)
                }
                return true // Impedir que el WebView intente abrir la URL en el navegador
            }
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val url = request?.url.toString()

                // Lista de dominios conocidos de anuncios
                val adDomains = listOf("ak.zougreek.com", "ak.hesoorda.com")

                // Si la URL contiene un dominio de anuncio, bloquea la solicitud
                if (adDomains.any { url.contains(it) }) {
                    // Devuelve un WebResourceResponse vacío para bloquear el anuncio
                    return WebResourceResponse("text/plain", "UTF-8", null)
                }

                // Si no es un anuncio, permite la carga normal
                return super.shouldInterceptRequest(view, request)
            }
        }
        binding.webViewFLV.settings.javaScriptEnabled = true
        binding.webViewFLV.settings.domStorageEnabled = true
        binding.webViewFLV.settings.mediaPlaybackRequiresUserGesture = false
        binding.webViewFLV.settings.loadWithOverviewMode = true
        binding.webViewFLV.settings.useWideViewPort = true

        binding.webViewFLV.loadUrl("https://www3.animeflv.net")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Manejar el botón "Atrás" en el fragmento
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webViewFLV.canGoBack()) {
                        binding.webViewFLV.goBack()
                    } else {
                        // Si no puede retroceder en el WebView, sale del fragmento o la actividad
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }

}