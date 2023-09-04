package com.tomer.quotly.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import com.tomer.quotly.R
import com.tomer.quotly.databinding.ActivityMainBinding
import com.tomer.quotly.viewmodals.MainViewModal
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //region ::GLOBALS-->>

    private val b by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val vm: MainViewModal by viewModels()

    private val cateClick = View.OnClickListener {
        vm.onCateClick(it.tag as String)
    }

    //endregion ::GLOBALS-->>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(b.root)

        vm.currentQuote.observe(this) {
            b.tvQuote.text = it.content
            b.tvAuthor.text = it.author
            b.bgGrad.setColor(Color.YELLOW)
        }
        vm.currentCate.observe(this) { tag ->
            populateBottomCategory(tag)
        }
    }

    //region ::CATE INFLATER

    private fun populateBottomCategory(tag:String) {
        val categories = arrayOf(
            "random",
            "love",
            "science",
            "friendship",
            "business",
            "competition",
            "education",
            "film",
            "sports",
            "motivational",
            "inspirational",
            "life",
            "humorous",
            "technology",
            "happiness",
            "future"
        )

        b.llCatagory.removeAllViews()
        for (i in categories) {
            val tv = layoutInflater.inflate(R.layout.row_catagory, b.llCatagory, false) as TextView
            tv.tag = i
            tv.text = i.uppercase()
            tv.setOnClickListener(cateClick)
            if (i == tag) {
                tv.background = ContextCompat.getDrawable(this, R.drawable.selected_corner)
            }
            b.llCatagory.addView(tv)
        }
    }
    //endregion ::CATE INFLATER
}
