package com.haoduyoudu.DailyAccounts

data class WeatherData(val status:String, val data:List<Data>){
    data class Data(
        val ip: String,
        val nation: String,
        val province: String,
        val city: String,
        val weather: Weather
    )
    data class Weather(
        val temp: String,
        val weather: String,
        val weathercode: String
    )
    override fun toString(): String{
        return "WeatherData={status=$status, data=[" +
                data[0].let { "{ip=${it.ip}, nation=${it.nation}, " +
                        "province=${it.province}, city=${it.city}, weather={" +
                        "${it.weather.let { "temp=${it.temp}, weather=${it.weather}, " +
                                "weatherCode=${it.weathercode}" }}}}]}" }
        //屎一样 看到的别大惊小怪
        //作者退坑了
    }

}