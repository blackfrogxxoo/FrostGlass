package me.wxc.frostglassdemo

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.wxc.frostglass.FrostGlass
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var frostGlass: FrostGlass? = null
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                executor.execute(Runnable { frostGlass!!.display() })
            }
        })
        frostGlass = FrostGlass(recyclerView!!, imageView)
    }

    private class Adapter : RecyclerView.Adapter<Holder>() {
        companion object {
            private val resIds = intArrayOf(
                    R.mipmap.bg1,
                    R.mipmap.bg2,
                    R.mipmap.bg3,
                    R.mipmap.bg4
            )
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main, null)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.image.setImageResource(resIds[position % resIds.size])
            holder.text1.text = "哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈$position"
            holder.text2.text = "垚垚垚垚垚垚垚垚垚垚$position"
        }

        override fun getItemCount(): Int {
            return 1000
        }
    }

    private class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        var text1: TextView = itemView.findViewById(R.id.text1)
        var text2: TextView = itemView.findViewById(R.id.text2)
    }
}