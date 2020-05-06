import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2020 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class UpdateProfileData (

	@SerializedName("id") val id : Int,
	@SerializedName("shipowner") val shipowner : String,
	@SerializedName("captain") val captain : String,
	@SerializedName("ship_number") val ship_number : String,
	@SerializedName("length_ship") val length_ship : Int,
	@SerializedName("power") val power : Int,
	@SerializedName("fishing_license") val fishing_license : Int,
	@SerializedName("duration") val duration : String,
	@SerializedName("second_job") val second_job : String,
	@SerializedName("phone") val phone : String,
	@SerializedName("image") val image : String,
	@SerializedName("status") val status : Int,
	@SerializedName("created_at") val created_at : String,
	@SerializedName("updated_at") val updated_at : String
)