package com.graclyxz.kotomare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
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
       // val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        binding.webViewFLV.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null
            private var originalSystemUiVisibility: Int = 0

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    onHideCustomView()
                    return
                }

                customView = view
                originalSystemUiVisibility = requireActivity().window.decorView.systemUiVisibility
                customViewCallback = callback

                // Configurar vista en pantalla completa
                val decorView = requireActivity().window.decorView as FrameLayout
                decorView.addView(customView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                requireActivity().window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

                // Ocultar ActionBar en pantalla completa
                (requireActivity() as AppCompatActivity).supportActionBar?.hide()
            }

            override fun onHideCustomView() {
                val decorView = requireActivity().window.decorView as FrameLayout
                decorView.removeView(customView)
                customView = null
                requireActivity().window.decorView.systemUiVisibility = originalSystemUiVisibility

                // Mostrar ActionBar al salir de pantalla completa
                (requireActivity() as AppCompatActivity).supportActionBar?.show()

                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
            }
        }


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
                val adDomains = listOf("ak.zougreek.com", "ak.hesoorda.com", "s.optnx.com",
                    "continuedownloader.com", "my.mail.ru", "ig.caudataolibene.com",
                    "onclickperformance.com", "ak.omgothitar.com", "ig.caudataolibene.com",
                    "akutapro.com", "glersakr.com")

                // Si la URL contiene un dominio de anuncio, bloquea la solicitud
                if (adDomains.any { url.contains(it) }) {
                    // Devuelve un WebResourceResponse vacío para bloquear el anuncio
                    return WebResourceResponse("text/plain", "UTF-8", null)
                }

                // Si no es un anuncio, permite la carga normal
                return super.shouldInterceptRequest(view, request)
            }
        }

        // AnimeFLV settings
        binding.webViewFLV.settings.javaScriptEnabled = true
        binding.webViewFLV.settings.domStorageEnabled = true
        binding.webViewFLV.settings.mediaPlaybackRequiresUserGesture = false
        binding.webViewFLV.settings.loadWithOverviewMode = true
        binding.webViewFLV.settings.useWideViewPort = true

        // Load AnimeFLV URL
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