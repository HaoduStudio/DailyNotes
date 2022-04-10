package com.haoduyoudu.DailyAccounts

fun main(){
    val string = "1 2 3 "
    val list = string.split(" ")
    for(i in list.filter { it!="" }.map { it.toInt() }) println("--$i--")
}