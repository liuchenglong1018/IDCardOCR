package com.jackie.ocr.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.jackie.ocr.bean.IDBean
import com.jackie.ocr.databinding.ItemMainBinding

class MainAdapter : BaseQuickAdapter<IDBean, MainAdapter.VH>() {

    // 自定义ViewHolder类
    class VH(
        parent: ViewGroup,
        val binding: ItemMainBinding =
            ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: IDBean?) {
        // 图片
        Glide.with(context)
            .load(item?.imgPath)
            .into(holder.binding.ivImage)

        // 出生年月
        holder.binding.llData.visibility = View.GONE
        // ID号
        holder.binding.tvNum.text = item?.id
        // 卡片类型
        if (item?.id_card_type == "2") {
            // Pan卡片
            holder.binding.tvType.text = "Pan"
            // 姓名
            holder.binding.tvNameTitle.text = "姓名(Full Name)："
            // 出生年月
            holder.binding.llData.visibility = View.GONE
            // ID号
            holder.binding.tvNumTitle.text = "证件号(Pan Number)："
        } else {
            // Aadhaar卡片
            holder.binding.tvType.text = "Aadhaar"
            // 姓名
            holder.binding.tvNameTitle.text = "姓名(Aadhaar Name)："
            // 出生年月
            holder.binding.llData.visibility = View.VISIBLE
            holder.binding.tvDate.text = item?.date
            // ID号
            holder.binding.tvNumTitle.text = "证件号(Aadhaar Number)："
        }

        // 性别
        holder.binding.tvGender.text = item?.gender
        // 姓名
        holder.binding.tvName.text = item?.name



    }

}