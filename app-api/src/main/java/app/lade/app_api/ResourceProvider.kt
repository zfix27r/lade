package app.lade.app_api

interface ResourceProvider {
   fun getString(resId: Int): String

    fun getString(resId: Int, vararg args: Any): String

    fun getColor(resId: Int): Int

    fun getDimension(resId: Int): Float
}