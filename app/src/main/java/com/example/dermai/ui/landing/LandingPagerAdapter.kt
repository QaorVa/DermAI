package com.example.dermai.ui.landing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dermai.R
import com.example.dermai.ui.login.LoginActivity
import com.example.dermai.ui.register.RegisterActivity

class LandingPagerAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layouts = listOf(R.layout.item_landing_page1, R.layout.item_landing_page2)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_landing_page1 -> ViewHolder1(view)
            R.layout.item_landing_page2 -> ViewHolder2(view)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder1 -> holder.bind()
            is ViewHolder2 -> holder.bind()
        }
    }

    override fun getItemCount() = layouts.size

    override fun getItemViewType(position: Int) = layouts[position]

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nextButton: Button = itemView.findViewById(R.id.nextButton)

        fun bind() {
            nextButton.setOnClickListener {
                // Swipe to the next page
                (context as LandingActivity).binding.viewPager.currentItem = 1
            }
        }
    }

    inner class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val signInButton: Button = itemView.findViewById(R.id.signInButton)
        private val registerButton: Button = itemView.findViewById(R.id.registerButton)

        fun bind() {
            signInButton.setOnClickListener {
                // Navigate to Sign In Activity
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
            registerButton.setOnClickListener {
                // Navigate to Register Activity
                // Assuming you have RegisterActivity
                context.startActivity(Intent(context, RegisterActivity::class.java))
            }
        }
    }
}
