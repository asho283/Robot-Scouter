package com.supercilex.robotscouter.ui.scout.viewholder

import android.support.annotation.CallSuper
import android.view.View
import android.widget.CheckBox

open class CheckboxViewHolder(itemView: View) :
        ScoutViewHolderBase<Boolean, CheckBox>(itemView),
        View.OnClickListener {
    public override fun bind() {
        super.bind()
        name.isChecked = metric.value
        name.setOnClickListener(this)
    }

    @CallSuper
    override fun onClick(v: View) = updateMetricValue(name.isChecked)
}
