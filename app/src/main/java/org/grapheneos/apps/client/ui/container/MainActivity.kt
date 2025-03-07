package org.grapheneos.apps.client.ui.container

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.grapheneos.apps.client.App
import org.grapheneos.apps.client.R
import org.grapheneos.apps.client.databinding.ActivityMainBinding
import org.grapheneos.apps.client.item.InstallCallBack
import org.grapheneos.apps.client.service.SeamlessUpdaterJob
import org.grapheneos.apps.client.utils.isInstallBlockedByAdmin

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var views: ActivityMainBinding
    private val navCtrl by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(views.container.id) as NavHostFragment

        navHostFragment.navController
    }
    private val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(setOf(R.id.mainScreen, R.id.updatesScreen))
            .build()
    }

    private val obs = Observer<Int> { updatableCount ->
        if (updatableCount == 0) {
            views.bottomNavView.removeBadge(R.id.updatesScreen)
        } else {
            views.bottomNavView.getOrCreateBadge(R.id.updatesScreen).number = updatableCount
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)
        setSupportActionBar(views.toolbar)
        window.setDecorFitsSystemWindows(false)

        ViewCompat.setOnApplyWindowInsetsListener(views.root) { v, insets ->
            val paddingInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = paddingInsets.left
                rightMargin = paddingInsets.right
            }
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            views.toolbar
        ) { v, insets ->

            val paddingInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = paddingInsets.top
            }
            insets
        }

        if (SeamlessUpdaterJob.NOTIFICATION_ACTION == intent.action) {
            (getSystemService(NotificationManager::class.java)).cancel(
                SeamlessUpdaterJob.NOTIFICATION_ID
            )
        }
        NavigationUI.setupWithNavController(views.bottomNavView, navCtrl)
        setupActionBarWithNavController(navCtrl, appBarConfiguration)

        navCtrl.addOnDestinationChangedListener { _, destination, _ ->
            views.bottomNavView.isGone =
                !appBarConfiguration.topLevelDestinations.contains(destination.id)
        }
        (applicationContext as App).updateCount.observe(this, obs)
    }

    override fun onPostResume() {
        super.onPostResume()
        if (isInstallBlockedByAdmin()) {
            navCtrl.navigate(R.id.installRestrictionScreen)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navCtrl.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun navigateToErrorScreen(status: InstallCallBack) {
        navCtrl.navigate(R.id.installErrorScreen, Bundle().apply {
            putParcelable("error", status)
        })
    }

}
