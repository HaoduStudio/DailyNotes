package com.haoduyoudu.DailyAccounts

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

const val NUM = 4
const val MOD_NUM = 1997

fun main(){
    println("Finding...")
    println("Start Time : ${getTime()}")
    val numMap = HashMap<Int, Int>()
    val stringBuilder = StringBuilder()
    while (true){
        stringBuilder.append(NUM)
        val modResult = stringBuilder.toString() % MOD_NUM
        if(modResult == 0){
            println("The result is : ${stringBuilder}")
            println("Length : ${stringBuilder.length}")
            break
        }
        println("${stringBuilder.length} : Result = ${modResult}")
        numMap[modResult] = (numMap[modResult]?:0) + 1
    }
    println("End Time : ${getTime()}")
    println()
    println()

    println("--------------Map--------------")
    for (i in numMap.keys.sorted())
        println("${i} Occurrence number : ${numMap[i]}")
    println(getRetryInterval(0))
}

@SuppressLint("SimpleDateFormat")
private fun getTime():String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

private operator fun String.rem(num: Int):Int{
    var result = 0
    for (i in 1..this.length){
        result = result*10+(this[i-1]-'0')
        if (result > num) result%=num
    }
    return result
}

private operator fun String.times(len: Long):String{
    val a = StringBuilder()
    for(i in 1..len){a.append(this)}
    return a.toString()
}
fun getRetryInterval(paramInteger: Int): Long {
    val d1 = Math.pow(5.0, paramInteger.toDouble())
    val d2 = Math.pow(5.0, (paramInteger + 1).toDouble())
    val l2 = (Random().nextDouble() * (d2 - d1) + d1).toLong()
    val l3 = 480L
    var l1 = l2
    if (l2 > l3) l1 = l3
    return l1
}
private fun tests(){

}