package com.makeitez.acsalesapp.screens.products

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACActivity

class ProductsActivity : ACActivity(R.layout.activity_products) {

    private val navController: NavController by lazy {
        this.findNavController(R.id.productsNavHost)
    }

    override fun onConfirmedCreate(savedInstanceState: Bundle?) {
        super.onConfirmedCreate(savedInstanceState)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    companion object {
        fun show(activity: ACActivity) {
            val intent = Intent(activity, ProductsActivity::class.java)
            activity.startActivity(intent)
        }
    }
}