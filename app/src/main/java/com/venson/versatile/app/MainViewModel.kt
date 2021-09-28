package com.venson.versatile.app

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venson.versatile.log.VLog
import com.venson.versatile.log.database.LogDatabase
import com.venson.versatile.log.database.LogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    val tag = MutableLiveData<String?>()
    val isIgnoreCase = MutableLiveData(true)
    val day = MutableLiveData(0)

    //level
    val isVerboseChecked = MutableLiveData(true)
    val isDebugChecked = MutableLiveData(true)
    val isInfoChecked = MutableLiveData(true)
    val isWarnChecked = MutableLiveData(true)
    val isErrorChecked = MutableLiveData(true)
    val isAssertChecked = MutableLiveData(true)

    //type
    val isJSONChecked = MutableLiveData(true)
    val isXMLChecked = MutableLiveData(true)
    val isOtherChecked = MutableLiveData(true)

    val data: MutableLiveData<List<LogEntity>?> = MutableLiveData()

    fun getData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            data.postValue(null)
            val level = StringBuilder().run {
                val isCheckedNone = isVerboseChecked.value != true
                        && isDebugChecked.value != true
                        && isInfoChecked.value != true
                        && isWarnChecked.value != true
                        && isErrorChecked.value != true
                        && isAssertChecked.value != true
                if (isCheckedNone || isVerboseChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append(VLog.V)
                }
                if (isCheckedNone || isDebugChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append(VLog.D)
                }
                if (isCheckedNone || isInfoChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append(VLog.I)
                }
                if (isCheckedNone || isWarnChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append(VLog.W)
                }
                if (isCheckedNone || isErrorChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append(VLog.E)
                }
                if (isCheckedNone || isAssertChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append(VLog.A)
                }
                toString()
            }
            val type = StringBuilder().run {
                val isCheckedNone = isJSONChecked.value != true
                        && isXMLChecked.value != true
                        && isOtherChecked.value != true
                if (isCheckedNone || isJSONChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append("json")
                }
                if (isCheckedNone || isXMLChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append("xml")
                }
                if (isCheckedNone || isOtherChecked.value == true) {
                    if (length > 0) {
                        append("|")
                    }
                    append("")
                }
                toString()
            }
            val time = (day.value ?: 0).let { day ->
                if (day == 0) {
                    0
                } else {
                    System.currentTimeMillis() - (day * 24 * 60 * 60 * 1000L)
                }
            }
            val logEntityList = LogDatabase.getInstance(context).logDao()
                .logList(tag.value, isIgnoreCase.value == true, level, type, time)
            if (logEntityList.isNullOrEmpty()) {
                data.postValue(emptyList())
            } else {
                data.postValue(logEntityList)
            }
        }
    }
}