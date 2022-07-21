package `in`.infoduniya.weatherupdate

import `in`.infoduniya.weatherupdate.databinding.ActivityMainBinding
import android.os.AsyncTask
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val CITY: String = "Haldia"
    val API: String = "441e9d684c6e8ea623b853e437b5ac4f"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        weatherTask().execute()

        binding.creatorTV.movementMethod = LinkMovementMethod.getInstance()
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            binding.loader.visibility = View.VISIBLE
            binding.mainContainer.visibility = View.GONE
            binding.errorText.visibility = View.GONE

        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Last Updated\n" + SimpleDateFormat(
                        "dd/MM/yyyy hh:mm a",
                        Locale.ENGLISH
                    ).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description").uppercase()

//                val address = jsonObj.getString("name") + ", " + sys.getString("country")
                val address = jsonObj.getString("name")

                /* Populating extracted data into our views */
                binding.address.setText(address.uppercase())
                binding.updatedAt.text = updatedAtText
                binding.status.text = weatherDescription
                if (weatherDescription.indexOf("CLOUD")!=-1) {
                    binding.weatherImg.setImageResource(R.drawable.cloudssplash)
                } else if (weatherDescription.indexOf("SNOW")!=-1) {
                    binding.weatherImg.setImageResource(R.drawable.snowy)
                } else if (weatherDescription.indexOf("STORM")!=-1 || weatherDescription.indexOf("TUNDER")!=-1) {
                    binding.weatherImg.setImageResource(R.drawable.thunderstorm)
                } else if (weatherDescription.indexOf("WIND")!=-1) {
                    binding.weatherImg.setImageResource(R.drawable.wind)
                } else if (weatherDescription.indexOf("RAIN")!=-1) {
                    binding.weatherImg.setImageResource(R.drawable.rainy)
                } else {
                    binding.weatherImg.setImageResource(R.drawable.sun)
                }
                binding.temp.text = temp
                binding.tempMin.text = tempMin
                binding.tempMax.text = tempMax
                binding.sunrise.text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                binding.sunset.text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                binding.wind.text = windSpeed
                binding.pressure.text = pressure
                binding.humidity.text = humidity

                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE

            } catch (e: Exception) {
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }

        }
    }
}

