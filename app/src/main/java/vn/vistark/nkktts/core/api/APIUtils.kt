package vn.vistark.nkktts.core.api

class APIUtils {
    companion object {
        var mAPIServices: APIServices? = getAPIServices()
        private fun getAPIServices(): APIServices? {
            return RetrofitClient.getClient()?.create(APIServices::class.java)
        }

        fun replaceAPIServices() {
            mAPIServices = getAPIServices()
        }
    }
}