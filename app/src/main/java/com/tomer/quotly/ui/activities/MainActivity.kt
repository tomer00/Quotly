package com.tomer.quotly.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.lifecycle.withCreated
import com.tomer.quotly.R
import com.tomer.quotly.databinding.ActivityMainBinding
import com.tomer.quotly.viewmodals.MainViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //region ::GLOBALS-->>

    private val b by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val vm: MainViewModal by viewModels()

    private val cateClick = View.OnClickListener {
        vm.onCateClick(it.tag as String)
    }
    private val favClick = View.OnClickListener {
        vm.favSel(it.tag as String)
    }
    private val favLongClick = View.OnLongClickListener {
        isDel = true
        vm.onFavDel(it.tag as String)
        b.llFav.removeView(it)
        return@OnLongClickListener true
    }

    private var isDel = false

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
        vm.isOpen.observe(this) {
            if (it) {
                b.apply {
                    favView.visibility = View.VISIBLE
                    favView.x = b.root.width.toFloat()
                    favView.animate().apply {
                        x(0f)
                        interpolator = OvershootInterpolator(1.1f)
                        duration = 160
                        start()
                    }
                }
            } else {
                b.apply {
                    favView.animate().apply {
                        x(b.root.width.toFloat())
                        duration = 240
                        this.withEndAction {
                            favView.visibility = View.GONE
                        }
                        start()
                    }
                }
            }
        }
        vm.favList.observe(this) {
            if (b.llFav.childCount == 0) {
                for (i in it) {
                    val view = layoutInflater.inflate(R.layout.fav_row, b.llFav, false)
                    view.tag = i._id


                    view.setOnClickListener(favClick)
                    view.setOnLongClickListener(favLongClick)

                    val tvQuote = view.findViewById(R.id.quote) as TextView
                    val tvAuthor = view.findViewById(R.id.tvAuthor) as TextView

                    tvAuthor.text = i.author
                    tvQuote.text = i.content

                    b.llFav.addView(view)
                }
                return@observe
            }
            // adding the newly added quote to our fav List
            if (isDel) {
                isDel = false
                return@observe
            }

            val i = it.last()
            val view = layoutInflater.inflate(R.layout.fav_row, b.llFav, false)
            view.tag = i._id


            view.setOnClickListener(favClick)
            view.setOnLongClickListener(favLongClick)

            val tvQuote = view.findViewById(R.id.quote) as TextView
            val tvAuthor = view.findViewById(R.id.tvAuthor) as TextView

            tvAuthor.text = i.author
            tvQuote.text = i.content

            b.llFav.addView(view)
            b.favView.smoothScrollTo(b.llFav[b.llFav.childCount - 1].right, 0)

        }

        vm.isConn.observe(this) {
            b.noInternet.visibility = if (it) View.VISIBLE else View.GONE
        }

        b.btFavQuotes.setOnClickListener {
            vm.onFavClick()
        }

        b.btAddFav.setOnClickListener {
            vm.onFavAdd()
        }
        b.btShare.setOnClickListener {
            //todo sharing feature of quote to different apps
        }
        b.noInternet.setOnClickListener {
            vm.isConn()
        }

    }

    //region ::CATE INFLATER

    private fun populateBottomCategory(tag: String) {
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
        if (b.llCatagory.childCount != 0) {
            val ite = b.llCatagory.children.iterator()
            while (ite.hasNext()) {
                val v = ite.next() as TextView
                if (tag == v.tag as String) {
                    v.background = ContextCompat.getDrawable(this, R.drawable.selected_corner)
                } else v.background = ContextCompat.getDrawable(this, R.drawable.corner_bg)
            }
            return
        }
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
