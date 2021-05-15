package com.makeitez.acsalesapp

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.makeitez.acsalesapp.services.*
import com.makeitez.acsalesapp.services.api.ACApi
import com.makeitez.acsalesapp.services.api.ACInterceptor
import com.makeitez.acsalesapp.utils.extensions.buildACMoshi
import com.makeitez.acsalesapp.utils.helper.AuthHelper
import com.makeitez.acsalesapp.utils.helper.ClientHelper
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ACService {

    lateinit var authHelper: AuthHelper

    private val acInterceptor: ACInterceptor by lazy {
        ACInterceptor(authHelper)
    }

    val acApi: ACApi by lazy {
        val moshi = buildACMoshi()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(ClientHelper.getNetworkClient(acInterceptor, BuildConfig.SSL_VERIFICATION_ON))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        return@lazy retrofit.create(ACApi::class.java)
    }

    val networkService: NetworkService by lazy {
        NetworkService(acApi)
    }

    val authService: AuthService by lazy {
        AuthService(authHelper, networkService)
    }

    val announcementService: AnnouncementService by lazy {
        AnnouncementService(networkService)
    }

    val customerService: CustomerService by lazy {
        CustomerService(networkService)
    }

    val generalService: GeneralService by lazy {
        GeneralService(networkService)
    }

    val productService: ProductService by lazy {
        ProductService(networkService)
    }

    val salesOrderService: SalesOrderService by lazy {
        SalesOrderService(networkService)
    }

    fun init(applicationContext: Application) {
        authHelper = AuthHelper(applicationContext.applicationContext)
    }

    fun relogin() {
        authService.clearUserData()
    }
}