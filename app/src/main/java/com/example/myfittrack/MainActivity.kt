package com.example.myfittrack

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.myfittrack.model.FitTrackSection
import com.example.myfittrack.ui.ContentFragment
import com.example.myfittrack.ui.MenuFragment

class MainActivity : FragmentActivity(), MenuFragment.SectionSelectionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.menu_container, MenuFragment())
                .replace(R.id.content_container, ContentFragment())
                .commit()
        }
    }

    override fun onSectionSelected(section: FitTrackSection) {
        (supportFragmentManager.findFragmentById(R.id.content_container) as? ContentFragment)
            ?.show(section)
    }
}
