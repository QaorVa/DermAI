package com.example.dermai.ui.details

import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.dermai.R
import com.example.dermai.databinding.ActivityDetailsBinding
import com.example.dermai.ui.base.BaseActivity

class DetailsActivity : BaseActivity<ActivityDetailsBinding>() {

    override fun getViewBinding(): ActivityDetailsBinding {
        return ActivityDetailsBinding.inflate(layoutInflater)
    }

    override fun setUI() {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.chevron_left)
        }

        when(intent.getStringExtra(EXTRA_SELECT)) {
            "skin_type" -> {
                binding.apply {
                    openCloseCard(hiddenViewSkintype, baseCardViewSkintype, btArrowSkintype)
                }
            }
            "acne_level" -> {
                binding.apply {
                    openCloseCard(hiddenViewAcnelevel, baseCardViewAcnelevel, btArrowAcnelevel)
                }
            }
            "skin_tone" -> {
                binding.apply {
                    openCloseCard(hiddenViewSkintone, baseCardViewSkintone, btArrowSkintone)
                }
            }
        }


    }

    override fun setActions() {
        binding.apply {
            btArrowSkintype.setOnClickListener {
                openCloseCard(hiddenViewSkintype, baseCardViewSkintype, btArrowSkintype)
            }
            btArrowAcnelevel.setOnClickListener {
                openCloseCard(hiddenViewAcnelevel, baseCardViewAcnelevel, btArrowAcnelevel)
            }
            btArrowSkintone.setOnClickListener {
                openCloseCard(hiddenViewSkintone, baseCardViewSkintone, btArrowSkintone)
            }
        }
    }

    override fun setProcess() {

    }

    override fun setObservers() {

    }

    private fun openCloseCard(hiddenView: View, baseCardView: CardView, btArrow: ImageButton) {
        if (hiddenView.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(baseCardView, AutoTransition())
            hiddenView.visibility = View.GONE
            btArrow.setImageResource(R.drawable.expand_more)
        } else {
            TransitionManager.beginDelayedTransition(baseCardView, AutoTransition())
            hiddenView.visibility = View.VISIBLE
            btArrow.setImageResource(R.drawable.expand_less)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_SELECT = "extra_select"
    }
}