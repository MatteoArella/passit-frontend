package it.uniroma1.macc.project.ui.screens.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import it.uniroma1.macc.project.R
import it.uniroma1.macc.project.databinding.ActivityMainBinding
import it.uniroma1.macc.project.ui.screens.chat.ChatFragment
import it.uniroma1.macc.project.ui.screens.list.ListFragment
import it.uniroma1.macc.project.ui.screens.profile.ProfileFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var active: String
    private lateinit var fragments: Map<String, Fragment>
    private val fm = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragments  = mapOf(
            LIST_FRAGMENT_TAG to ListFragment(),
            CHAT_FRAGMENT_TAG to ChatFragment(),
            PROFILE_FRAGMENT_TAG to ProfileFragment()
        )

        active = savedInstanceState?.getString(ACTIVE_FRAGMENT_KEY) ?: LIST_FRAGMENT_TAG

        fm.beginTransaction().add(
            R.id.main_container,
            fragments[PROFILE_FRAGMENT_TAG]!!,
            PROFILE_FRAGMENT_TAG
        ).apply {
            if (active != PROFILE_FRAGMENT_TAG) {
                hide(fragments[PROFILE_FRAGMENT_TAG]!!)
            }
        }.commit()

        fm.beginTransaction().add(
            R.id.main_container,
            fragments[CHAT_FRAGMENT_TAG]!!,
            CHAT_FRAGMENT_TAG
        ).apply {
            if (active != CHAT_FRAGMENT_TAG) {
                hide(fragments[CHAT_FRAGMENT_TAG]!!)
            }
        }.commit()
        fm.beginTransaction().add(
            R.id.main_container,
            fragments[LIST_FRAGMENT_TAG]!!,
            LIST_FRAGMENT_TAG
        ).apply {
            if (active != LIST_FRAGMENT_TAG) {
                hide(fragments[LIST_FRAGMENT_TAG]!!)
            }
        }.commit()

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_annunci -> {
                    fm.beginTransaction().hide(fragments[active]!!).show(
                            fragments[LIST_FRAGMENT_TAG]!!
                        ).commit()
                    active = LIST_FRAGMENT_TAG
                    true
                }
                R.id.page_chat -> {
                    fm.beginTransaction().hide(fragments[active]!!).show(
                            fragments[CHAT_FRAGMENT_TAG]!!
                        ).commit()
                    active = CHAT_FRAGMENT_TAG
                    true
                }
                R.id.page_profile -> {
                    fm.beginTransaction().hide(fragments[active]!!).show(
                            fragments[PROFILE_FRAGMENT_TAG]!!
                        ).commit()
                    active = PROFILE_FRAGMENT_TAG
                    true
                }
                else -> false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ACTIVE_FRAGMENT_KEY, active)
    }

    companion object {
        const val ACTIVE_FRAGMENT_KEY = "active"
        const val LIST_FRAGMENT_TAG = "ListFragment"
        const val CHAT_FRAGMENT_TAG = "ChatFragment"
        const val PROFILE_FRAGMENT_TAG = "ProfileFragment"
    }
}