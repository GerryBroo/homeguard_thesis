package hu.geri.homeguard.domain.face.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.geri.homeguard.domain.analyzer.model.SimilarityClassifier

//Save Faces to Shared Preferences.Conversion of Recognition objects to json string
fun insertToSP(
    context: Context,
    jsonMap: HashMap<String?, SimilarityClassifier.Recognition>
) {
    editJson(context, jsonMap)

    // Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show()
}

//Load Faces from Shared Preferences.Json String to Recognition object
fun readFromSP(context: Context): HashMap<String?, SimilarityClassifier.Recognition> {
    val outputSize = 192
    val retrievedMap = getJsonMap(context)

    for ((_, value) in retrievedMap) {
        val output = Array(1) {
            FloatArray(outputSize)
        }
        var arrayList = value.extra as ArrayList<*>
        arrayList = arrayList[0] as ArrayList<*>
        for (counter in arrayList.indices) {
            output[0][counter] = (arrayList[counter] as Double).toFloat()
        }
        value.extra = output
    }

    //Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show()
    return retrievedMap
}

fun deleteFromSP(context: Context, name: String) {
    val retrievedMap = getJsonMap(context)
    retrievedMap.remove(name)

    editJson(context, retrievedMap)

    // Toast.makeText(context, "Delete $name", Toast.LENGTH_SHORT).show()
}

private fun editJson(context: Context, map: HashMap<String?, SimilarityClassifier.Recognition>) {
    val jsonString = Gson().toJson(map)
    val sharedPreferences = context.getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("map", jsonString)
    editor.apply()
}

private fun getJsonMap(context: Context): HashMap<String?, SimilarityClassifier.Recognition> {
    val sharedPreferences = context.getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
    val defValue = Gson().toJson(HashMap<String, SimilarityClassifier.Recognition>())
    val json = sharedPreferences.getString("map", defValue)
    val token: TypeToken<HashMap<String, SimilarityClassifier.Recognition>> =
        object : TypeToken<HashMap<String, SimilarityClassifier.Recognition>>() {}

    return Gson().fromJson(json, token.type)
}