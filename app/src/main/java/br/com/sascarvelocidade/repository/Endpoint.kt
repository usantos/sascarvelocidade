package br.com.sascarvelocidade.repository

import br.com.sascarvelocidade.entity.SpeedLimit
import retrofit2.Call
import retrofit2.http.GET

interface Endpoint {

    @GET("retriveAll")
    fun retrieveAll() : Call<List<SpeedLimit>>
}