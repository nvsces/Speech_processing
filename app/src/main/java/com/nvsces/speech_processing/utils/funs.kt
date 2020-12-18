package com.nvsces.speech_processing.utils

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.speech_processing.MainActivity
import com.nvsces.speech_processing.R
import kotlin.collections.ArrayList
import kotlin.math.abs


fun doubleListToArrayListFloat(x:List<Double>):ArrayList<Float>{
    val ans=ArrayList<Float>()

    for (i in 0 until x.size){
        ans.add(x[i].toFloat())
    }
    return ans
}

fun doubleArrayToFloatArray(x:List<Double>):FloatArray{
    val ans=FloatArray(x.size)

    for (i in 0 until x.size){
        ans[i]=x[i].toFloat()
    }
    return ans
}

fun doubleArrayToFloatArray(x:DoubleArray):FloatArray{
    val ans=FloatArray(x.size)

    for (i in 0 until x.size){
        ans[i]=x[i].toFloat()
    }
    return ans
}

fun overlapWindowDataBuffer(listBuffer: ArrayList<FloatArray>): ArrayList<FloatArray> {
    val bufferSize=listBuffer[0].size
    val outList=ArrayList<FloatArray>()
    outList.add(listBuffer[0])

    for (i in 1 until listBuffer.size-1){
        val tempArray=FloatArray(bufferSize)
        System.arraycopy(listBuffer[i],0,tempArray,0,bufferSize/2)
        System.arraycopy(listBuffer[i+1],0,tempArray,bufferSize/2,bufferSize/2)
        outList.add(tempArray)
    }
    outList.add(listBuffer[listBuffer.size-1])
    return outList
}

fun overlapWindowDataBufferShort(listBuffer: ArrayList<ShortArray>): ArrayList<ShortArray> {
    val bufferSize=listBuffer[0].size
    val outList=ArrayList<ShortArray>()
    outList.add(listBuffer[0])

    for (i in 1 until listBuffer.size-1){
        val tempArray=ShortArray(bufferSize)
        System.arraycopy(listBuffer[i],0,tempArray,0,bufferSize/2)
        System.arraycopy(listBuffer[i+1],0,tempArray,bufferSize/2,bufferSize/2)
        outList.add(tempArray)
    }
    outList.add(listBuffer[listBuffer.size-1])
    return outList
}

fun magnitudeSpec(re: FloatArray, im: FloatArray): FloatArray {
    val magnitude=FloatArray(re.size)
    for(i in 0 until re.size){
        magnitude[i]=re[i]*re[i]+im[i]*im[i]
    }
    return magnitude
}


fun shortArrayListToDoubleArrayList(shortList: ArrayList<ShortArray>): ArrayList<DoubleArray> {
    val floatList=ArrayList<DoubleArray>()
    var doubleArray=DoubleArray(shortList[0].size)

    for (i in 0 until shortList.size){
        doubleArray= short2DoubleArray(shortList[i])
        floatList.add(doubleArray)
    }
    return floatList
}

fun splitDataWavChannelsToShort(sampleBuffer:IntArray):ArrayList<ShortArray>{
    val splitList=ArrayList<ShortArray>()
    val arrayOneChannels=ShortArray(sampleBuffer.size/2)
    val arrayTwoChannels=ShortArray(sampleBuffer.size/2)
    var onek=0
    var twok=0

    for (i in 0 until sampleBuffer.size){
        if (i%2!=0) {
            arrayOneChannels[onek]=sampleBuffer[i].toShort()
            onek++
        }else{
            arrayTwoChannels[twok]=sampleBuffer[i].toShort()
            twok++
        }
    }
    splitList.add(arrayTwoChannels)
    splitList.add(arrayOneChannels)


    return splitList
}


fun splitDataWavChannelsToFloat(sampleBuffer:DoubleArray):ArrayList<DoubleArray>{
    val splitList=ArrayList<DoubleArray>()
    val arrayOneChannels=DoubleArray(sampleBuffer.size/2)
    val arrayTwoChannels=DoubleArray(sampleBuffer.size/2)
    var onek=0
    var twok=0

    for (i in 0 until sampleBuffer.size){
        if (i%2!=0) {
            arrayOneChannels[onek]=sampleBuffer[i]
            onek++
        }else{
            arrayTwoChannels[twok]=sampleBuffer[i]
            twok++
        }
    }
    splitList.add(arrayTwoChannels)
    splitList.add(arrayOneChannels)


    return splitList
}

fun deleteFrameIsMFCC(mfcc: Array<FloatArray>): FloatArray {
    return mfcc[0]
}

fun deleteFrameListIsMFCC(listMFCC:ArrayList<Array<FloatArray>>):ArrayList<FloatArray>{
    val outArray=ArrayList<FloatArray>()
    for ( i in 0 until listMFCC.size){
        val array:FloatArray=listMFCC[i][0]
        outArray.add(array)
    }
    return outArray
}


fun short2FloatArray(shortArray: ShortArray):FloatArray{
    val floatArray=FloatArray(shortArray.size)
    for (i in shortArray.indices){
        floatArray[i]=shortArray[i].toFloat()
    }
    return floatArray
}

fun short2DoubleArrayNorm(shortArray: ShortArray):DoubleArray{
    val maxAr= maxValueAbs(shortArray)
    val doubleArray=DoubleArray(shortArray.size)
    for (i in shortArray.indices){
        doubleArray[i]=shortArray[i]/maxAr
    }
    return doubleArray
}


fun short2DoubleArray(shortArray: ShortArray):DoubleArray{
    val doubleArray=DoubleArray(shortArray.size)
    for (i in shortArray.indices){
        doubleArray[i]=shortArray[i].toDouble()
    }
    return doubleArray
}

fun maxValueAbs(array: ShortArray): Double {
    var max = abs(array[0].toDouble())
    for (i in 0 until array.size) {
        if (abs(array[i].toDouble()) > max)
            max = abs(array[i].toDouble())
    }
    return max
}


fun maxValueAbs(array: List<Double>): Float {
    var max = abs(array[0]).toFloat()
    for (i in 0 until array.size) {
        if (abs(array[i]) > max)
            max = abs(array[i]).toFloat()
    }
    return max
}



fun maxValue(array: ArrayList<Float>): Float {
    var max = 0f
    for (i in 0 until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}

fun searchMaxDefIndex(array: ArrayList<Float>,Index:Int): Float {
    var max = 0f
    for (i in Index until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}

fun searchMaxDefIndex(array: FloatArray,Index:Int): Float {
    var max = 0f
    for (i in Index until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}

fun searchMaxAbs(array: FloatArray): Float {
    var max = 0f
    for (i in 4 until array.size) {
        if (array[i]<0) array[i]=-array[i]
        if (array[i] > max)
            max = array[i]
    }
    return max
}


fun searchMax(array: FloatArray): Float {
    var max = 0f
    for (i in 4 until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}
fun searchMax(array: ArrayList<Float>): Float {
    var max = 0f
    for (i in 4 until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}
fun searchMax(array: ShortArray): Float {
    var max = 0f
    for (i in 0 until array.size) {
        if (array[i] > max)
            max = array[i].toFloat()
    }
    return max
}


fun restartActivity(){
    val intent= Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack:Boolean=true){
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }else{
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }
}

fun showToast(message: String){
    Toast.makeText(APP_ACTIVITY,message, Toast.LENGTH_SHORT).show()
}