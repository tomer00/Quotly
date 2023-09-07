package com.tomer.quotly.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import com.tomer.quotly.R
import com.tomer.quotly.databinding.ActivityMainBinding
import com.tomer.quotly.viewmodals.MainViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs
import kotlin.random.Random


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

    private var initialX = 0f
    private val colors = intArrayOf(
        R.color.green, R.color.blue, R.color.dark_blue, R.color.sea_green, R.color.magenta,
        R.color.yellow, R.color.voilet, R.color.peach, R.color.primary_dark
    )

    //endregion ::GLOBALS-->>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(b.root)

        val statusHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        if (statusHeight > 0) {
            val p = b.topBar.layoutParams as ConstraintLayout.LayoutParams
            p.setMargins(0, statusHeight, 0, 0)
            b.topBar.layoutParams = p
        }

        //region OBSERVERS

        vm.currentQuote.observe(this) { quote ->
            b.tvQuote.text = quote.content
            "~ ${quote.author}".also { b.tvAuthor.text = it }
            val color = ContextCompat.getColor(this, colors[Random.nextInt(colors.size)])
            b.bgGrad.setColor(color)
            b.blurCard.setColor(color)
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
                        duration = 400
                        start()
                    }
                }
            } else {
                b.apply {
                    favView.animate().apply {
                        x(b.root.width.toFloat())
                        duration = 320
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

                    "~ ${i.author}".also { author -> tvAuthor.text = author }
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

        //endregion OBSERVERS

        //region CLICK LISTENERS


        b.btFavQuotes.setOnClickListener {
            vm.onFavClick()
            it.performHaptic()
        }

        b.btAddFav.setOnClickListener {
            vm.onFavAdd()
            b.blurCard.animateFavParticles(true)
            it.performHaptic()
        }
        b.btShare.setOnClickListener {
            b.blurCard.animateFavParticles(false)
            shareIntent()
            it.performHaptic()
        }
        b.noInternet.setOnClickListener {
            vm.isConn()
        }

        b.bgGrad.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                initialX = event.x
            else if (event.action == MotionEvent.ACTION_UP) {
                val diff = initialX - event.x
                if (abs(diff) > 80f) vm.swipe(diff > 0f)
            }


            return@setOnTouchListener true
        }

        //endregion CLICK LISTENERS
    }

    private fun View.performHaptic() {
        this.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING)
    }

    private fun shareIntent() {
        val sb = StringBuilder("Quote of the Day !!!\n\n")
        sb.append(b.tvQuote.text)
        sb.append('\n')
        sb.append(b.tvAuthor.text)
        sb.append("\n\nGet Quotly app from here\n👇👇👇👇👇👇👇👇\n")
        sb.append("https://github.com/tomer00/CODSOFT-Quotly")
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        b.root.postDelayed({ startActivity(shareIntent) }, 1200)
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


    //region FULLSCREEN

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    //endregion FULLSCREEN
}
