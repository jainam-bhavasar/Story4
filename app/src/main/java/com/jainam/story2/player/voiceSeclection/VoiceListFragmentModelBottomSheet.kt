package com.jainam.story2.player.voiceSeclection

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jainam.story2.R
import kotlinx.android.synthetic.main.fragment_voice_list_model_bottom_sheet_list_dialog.*
import kotlinx.android.synthetic.main.fragment_voice_list_model_bottom_sheet_list_dialog_item.view.*

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    VoiceListFragmentModelBottomSheet.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class VoiceListFragmentModelBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.voice_selection_menu_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }


}
