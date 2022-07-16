package com.haoduyoudu.DailyAccounts

fun getDate(ly: Int, lm: Int, ld: Int):Int{
    val ly2 = if(lm < 3) ly-1 else ly
    val c = ly2.toString().subSequence(0,2).toString().toInt()
    val y = ly2.toString().subSequence(2,4).toString().toInt()
    val m = if(lm < 3) 12+lm else lm
    val d = ld
    return (y+(y/4)+(c/4)-2*c+(26*(m+1)/10)+d-1)%7
}

fun main(){
    println("06".toInt())
}