package com.company.vansales.app.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.activity.MaterialSelectionActivity
import com.company.vansales.app.datamodel.models.localmodels.MaterialsTruckItem
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.convertToEnglish
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.Constants.MATERIAL_LIST_FREE_TYPE
import com.company.vansales.app.utils.Constants.MATERIAL_LIST_ITEM_TYPE
import com.company.vansales.app.utils.extensions.displayToast
import com.company.vansales.app.view.adapter.MaterialsListAdapter
import com.company.vansales.app.viewmodel.MaterialsViewModel
import com.company.vansales.app.viewmodel.TransactionsHistoryViewModel
import com.company.vansales.databinding.AddMaterialsDialogBinding
import java.math.BigDecimal


class ShowAddDialog : DialogFragment() {

    lateinit var binding: AddMaterialsDialogBinding
    val materialsVM: MaterialsViewModel by lazy {
        MaterialSelectionActivity.instance?.materialsVM!!
    }
    val transactionHistoryViewModel: TransactionsHistoryViewModel by viewModels()
    var dialogMaterialsList = ArrayList<MaterialsTruckItem>()
    var filteredList: ArrayList<MaterialsTruckItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMaterialsDialogBinding.inflate(
            LayoutInflater.from(context),
            container, true
        )
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        isCancelable = false
        initView()
        callBacks()
        initButtons()
    }

    var returnsValue = "0.0".toBigDecimal()
    private fun callBacks() {
        materialsVM.showShowAddData.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.addMaterialDialogHeaderImageView.setImageResource(it.image)
                binding.addMaterialDialogHeaderTextView.text = it.header
                binding.addMaterialDialogFocTextView.text = it.FOCText

                binding.isFree = it.materialGroupFOCValue != null
                binding.addMaterialDialogFocValueTextView.text = "/${AppUtils.round(convertToEnglish(it.FOCValue)?.toDouble()?:0.0)}"
                binding.addGroupSelectedFocValueTextView.text = "/${AppUtils.round(it.materialGroupFOCValue?.let { it1 ->
                    convertToEnglish(
                        it1
                    )?.toDouble()
                } ?:0.0)}"
                binding.addGroupDialogFocValueTextView.text = "${AppUtils.round(it.materialGroupFOCTotal?.let { it1 ->
                    convertToEnglish(
                        it1
                    )?.toDouble()
                } ?:0.0)}"
                returnsValue = convertToEnglish(it.FOCValue)?.toBigDecimal()?: BigDecimal.ZERO

                if(materialsVM.isExchange) {
                    binding.isExchange = true
                    binding.addMaterialDialogFocValueTextView.visibility = View.GONE
                    binding.addGroupDialogFocValueTextView.visibility = View.VISIBLE
                    binding.addGroupDialogFocValueTextView.text = convertToEnglish(AppUtils.round(returnsValue).toString())
                }
                if(materialsVM.currentMaterialList == MATERIAL_LIST_FREE_TYPE){
                    binding.addGroupDialogFocValueTextView.visibility = View.VISIBLE
                }else if(materialsVM.currentMaterialList == MATERIAL_LIST_ITEM_TYPE)
                    binding.addGroupDialogFocValueTextView.visibility = View.INVISIBLE
              /*  if(binding.isFree != true && binding.isExchange != true)
                    binding.addGroupDialogFocValueTextView.visibility = View.INVISIBLE
            */}
        }
    }

    private fun initButtons() {
        val searchEditText = binding.addMaterialDialogSearchEditText
        /*binding.addMaterialDialogBarcodeImageView.setOnClickListener {
            val intent = Intent(requireContext(), ScanActivity::class.java)
            startActivityForResult(intent, Constants.camPermission)
        }*/

        binding.addMaterialDialogGroupSelectionTextView.setOnClickListener {
            val popupMenu =
                PopupMenu(requireContext(), binding.addMaterialDialogGroupSelectionTextView)
            popupMenu.menu.add(Menu.NONE, -1, Menu.NONE, resources.getString(R.string.all_groups))
            for (i in 0 until materialsVM.itemGroupList.size) {
                popupMenu.menu.add(Menu.NONE, i, Menu.NONE, materialsVM.itemGroupList[i])
            }
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                binding.addMaterialDialogGroupSelectionTextView.text = item.title
                if (item.title == resources.getString(R.string.all_groups)) {
                    filteredList = dialogMaterialsList
                    setMaterialsTruckItemListAdapter(
                        filteredList, binding.addMaterialsDialogItemsList,
                        binding.addMaterialDialogTotalPriceValueTextView ,
                        true
                    )
                    var value = 0.0
                    var selectedGroupValue = 0.0
                    materialsVM.listOfMaterialGroupItems.forEach {
                        value += it.value.selectedPrice
                        selectedGroupValue += it.value.totalPrice
                    }
                    binding.addGroupDialogFocValueTextView.text = AppUtils.round(value).toString()
                    binding.addGroupSelectedFocValueTextView.text = "/${AppUtils.round(selectedGroupValue)}"

                } else {
                    filteredList =
                        dialogMaterialsList.filter { it.materialGrp == item.title } as ArrayList

                    setMaterialsTruckItemListAdapter(
                        filteredList,
                        binding.addMaterialsDialogItemsList,
                        binding.addMaterialDialogTotalPriceValueTextView
                    )
                    var value = 0.0
                    var selectedGroupValue = 0.0
                    materialsVM.listOfMaterialGroupItems.forEach {
                        if (it.key == item.title) {
                            value = it.value.selectedPrice
                            selectedGroupValue = it.value.totalPrice
                            Log.d("dkjskjdl", " ${it.value.selectedPrice} " +
                                    "${it.value.materialNo}")
                        }
                    }
                    binding.addGroupDialogFocValueTextView.text = AppUtils.round(value).toString()
                    binding.addGroupSelectedFocValueTextView.text = "/${AppUtils.round(selectedGroupValue)}"
                }
                binding.addMaterialDialogSearchEditText.setText("")
                true
            }
        }

        binding.addMaterialDialogDoneButton.setOnClickListener {
            if (materialsVM.checkFOC()) {
//                if(materialsVM.checkGroupFOC()) {
                    materialsVM.finishMissingList()
                    dialog?.dismiss()
                    MaterialSelectionActivity.instance?.showClickToAddItem()
                    MaterialSelectionActivity.instance?.createTabs()
                    binding.addMaterialDialogTotalPriceValueTextView.text =
                        materialsVM.getTotalInvoicePrice()
                /*}else{
                    AppUtils.showMessage(
                        requireContext() ,
                        resources.getString(R.string.FOC_group_toast_message)
                    )
                }*/
            } else {
                AppUtils.showMessage(
                    requireContext(),
                    resources.getString(R.string.FOC_toast_message)
                )
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    val filteredSearchList = filteredList.filter { materialTruckItem ->
                        materialTruckItem.materialNo.contains(s, true)
                                || materialTruckItem.materialDescrption.contains(s,true)
                    } as ArrayList

               /*     filteredSearchList.addAll(filteredList.filter {
                        it.materialNo.contains(s, true)
                    } as ArrayList)*/
/*

                    filteredSearchList.addAll(filteredList.filter { item ->
                        item.barcodeList.any { it.contains(s, true) }
                    } as ArrayList)
*/

                    setMaterialsTruckItemListAdapter(
                        filteredSearchList,
                        binding.addMaterialsDialogItemsList,
                        binding.addMaterialDialogTotalPriceValueTextView
                    )
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        /*materialsVM.finishSelectedItemList()
        if(materialsVM.currentMaterialList == MATERIAL_LIST_ITEM_TYPE)
        materialsVM.applyDiscount()
        */
    }

    private fun initView() {
        if (materialsVM.currentMaterialList == MATERIAL_LIST_ITEM_TYPE) {
            binding.addMaterialDialogFocTextView.visibility = View.GONE
            binding.addMaterialDialogFocValueTextView.visibility = View.GONE
            dialogMaterialsList = materialsVM.onTruckMaterialsList
        } else if (materialsVM.currentMaterialList == MATERIAL_LIST_FREE_TYPE) {
            binding.addMaterialDialogFocTextView.visibility = View.VISIBLE
            binding.addMaterialDialogFocValueTextView.visibility = View.VISIBLE
            dialogMaterialsList = materialsVM.freeMaterialsList
        }
        if (materialsVM.isExchange) {
            binding.addMaterialDialogTotalPriceValueTextView.text = convertToEnglish(String.format(
                "%.3f",
                returnsValue - materialsVM.getTotalPriceDialog().toBigDecimal()
            ))
        } else {
            binding.addMaterialDialogTotalPriceValueTextView.text =
                convertToEnglish(String.format("%.3f", materialsVM.getTotalPriceDialog().toDouble()))
            binding.addGroupDialogFocValueTextView.text =
                convertToEnglish(AppUtils.round(materialsVM.getTotalMaterialGroupPrice()).toString())
        }
        setMaterialsTruckItemListAdapter(
            dialogMaterialsList, binding.addMaterialsDialogItemsList,
            binding.addMaterialDialogTotalPriceValueTextView
        )
        filteredList = dialogMaterialsList

        if (materialsVM.onTruckMaterialsList.isEmpty()) {
            materialsVM.getMaterialsList().observe(this, Observer
            {
                if (!it.isNullOrEmpty()) {
                    materialsVM.onTruckMaterialsList = it as ArrayList<MaterialsTruckItem>
                    if (!materialsVM.isExchange)
                        materialsVM.setFreeMaterialsList()
                    else
                        setFreeMaterialsList()

                    materialsVM.setGroupList()
                    binding.addMaterialsDialogItemsList.visibility = View.VISIBLE
                    binding.addMaterialsDialogProgressBar.visibility = View.GONE
                    if (dialogMaterialsList.isEmpty()) {
                        if (materialsVM.currentMaterialList == MATERIAL_LIST_ITEM_TYPE)
                            dialogMaterialsList = materialsVM.onTruckMaterialsList
                        else if (materialsVM.currentMaterialList == MATERIAL_LIST_FREE_TYPE)
                            dialogMaterialsList = materialsVM.freeMaterialsList

                        setMaterialsTruckItemListAdapter(
                            dialogMaterialsList, binding.addMaterialsDialogItemsList,
                            binding.addMaterialDialogTotalPriceValueTextView
                        )
                        filteredList = dialogMaterialsList
                    }
                } else {
                    binding.addMaterialsDialogItemsList.visibility = View.GONE
                    binding.addMaterialsDialogProgressBar.visibility = View.VISIBLE
                    AppUtils.showMessage(
                        requireContext(),
                        resources.getString(R.string.no_products)
                    )
                    requireActivity().finish()
                }
            })
        } else {
            binding.addMaterialsDialogItemsList.visibility = View.VISIBLE
            binding.addMaterialsDialogProgressBar.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       /* if (requestCode == Constants.camPermission && resultCode == Activity.RESULT_OK && data != null) {
            transactionHistoryViewModel.getMaterialByBarcode(
                data.getStringExtra("barcode").toString()
            )

            binding.addMaterialDialogSearchEditText.setText(
                transactionHistoryViewModel.getMaterialByBarcode(
                    data.getStringExtra("barcode").toString()
                ).materialDescrption
            )
        }*/
    }

    private fun setMaterialsTruckItemListAdapter(
        materialsList: ArrayList<MaterialsTruckItem>,
        rv: RecyclerView,
        priceTextView: TextView , allGroup : Boolean? = false
    ) {
        val adapter = MaterialsListAdapter(materialsList, materialsVM)
        rv.adapter = adapter
        rv.adapter?.notifyDataSetChanged()
        adapter.itemSelected = { materialTruckItem ->

            val material = materialsVM.listOfMaterialGroupItems[materialTruckItem.materialGrp]
            if (materialsVM.currentMaterialList == MATERIAL_LIST_ITEM_TYPE) {
                addItem(
                    materialTruckItem, priceTextView, null,
                    binding.addGroupDialogFocValueTextView
                )
            } else if (materialsVM.currentMaterialList == MATERIAL_LIST_FREE_TYPE && !materialsVM.isExchange && material != null) {
                if(allGroup != null && allGroup)
                addItem(
                            materialTruckItem,
                            priceTextView,
                            context?.getString(R.string.all_groups),
                            binding.addGroupDialogFocValueTextView
                        )
                else
                addItem(
                            materialTruckItem,
                            priceTextView,
                            materialTruckItem.materialGrp,
                            binding.addGroupDialogFocValueTextView
                        )
            } else if (materialsVM.currentMaterialList == MATERIAL_LIST_FREE_TYPE && !materialsVM.isExchange &&
                (materialTruckItem.selectedQuantity * materialTruckItem.unitPrice) <= materialsVM.totalFOC
            ) {
//                materialsVM.selectedMaterialsList.forEach {
                    addItem(
                        materialTruckItem,
                        priceTextView,
                        materialTruckItem.materialGrp,
                        binding.addGroupDialogFocValueTextView
                    )
//                }
            } else if (materialsVM.isExchange) {
                addItem(
                    materialTruckItem,
                    priceTextView,
                    null,
                    binding.addGroupSelectedFocValueTextView
                )
            } else {
                requireContext().displayToast(R.string.FOC_toast_message)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun addItem(
        materialsTruckItem: MaterialsTruckItem, priceTextView: TextView, materialGrp: String?,
        groupPriceTextView: TextView
    ) {
//        Log.d("asdlaskdasldAddTruckItem", "${materialsTruckItem.displayingValue}")
        Log.d("materialTruckItem99" ,
            " addItem : ${materialsTruckItem.displayingValue} " +
                    "${materialsTruckItem.unitQuantityList[materialsTruckItem.selectedUnit]} ")

        materialsVM.addSelectedMaterial(materialsTruckItem, materialGrp)
        if(materialsTruckItem.selectedQuantity != 0.0)
        Toast.makeText(requireContext(),"You have added ${materialsTruckItem.materialNo} " +
                "${materialsTruckItem.unitQuantityList[materialsTruckItem.selectedUnit]} " +
                "${materialsTruckItem.selectedUnit}" ,
        Toast.LENGTH_LONG).show()
        if (materialGrp != null && materialsVM.listOfMaterialGroupItems[materialGrp] != null &&
            (AppUtils.round(materialsVM.listOfMaterialGroupItems[materialGrp]?.selectedPrice ?: 0.0))
                    >= materialsVM.getMaterialTotalPrice(materialsTruckItem)) {
            if(materialGrp == context?.getString(R.string.all_groups)){
                var price = 0.0
                materialsVM.listOfMaterialGroupItems.forEach {
                    price += it.value.selectedPrice
                }
                groupPriceTextView.text = AppUtils.round(price).toString()
            }else{

                groupPriceTextView.text = AppUtils.round(materialsVM.listOfMaterialGroupItems[materialGrp]?.selectedPrice ?: 0.0).toString()
            }
            priceTextView.text = materialsVM.getTotalPriceDialog()
        } else {
            if (materialGrp == context?.getString(R.string.all_groups)) {
                var price = 0.0
                materialsVM.listOfMaterialGroupItems.forEach {
                    price += it.value.selectedPrice
                }
                groupPriceTextView.text = AppUtils.round(price).toString()
            } else {
                if (materialsVM.listOfMaterialGroupItems[materialGrp]?.selectedPrice == null)
                    groupPriceTextView.text = "0.0"
                else
                    groupPriceTextView.text =
                        AppUtils.round(materialsVM.getTotalMaterialGroupPrice()).toString()

                priceTextView.text = materialsVM.getTotalPriceDialog()
            }
        }
        if (materialsVM.isExchange) {

        } else if(materialsVM.currentMaterialList != MATERIAL_LIST_FREE_TYPE)
            MaterialSelectionActivity.instance?.binding?.totalPrice =
                materialsVM.getTotalPriceDialog()
    }

    private fun setFreeMaterialsList() {
        materialsVM.listFromMaterials = false
        materialsVM.getMaterialsList().observe(this) {
            if (!it.isNullOrEmpty()) {
                materialsVM.freeMaterialsList = it as ArrayList<MaterialsTruckItem>
            }
        }
    }

}