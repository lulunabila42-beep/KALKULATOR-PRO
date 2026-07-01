package com.example

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "topK") val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    var customApiKey: String = ""

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    suspend fun solveWordProblem(prompt: String): String {
        val key = customApiKey.ifBlank { BuildConfig.GEMINI_API_KEY }
        if (key.isBlank() || key == "MY_GEMINI_API_KEY" || key == "GEMINI_API_KEY") {
            return "Waduh cuy, Gemini API Key lu belom diatur nih. Masukin dulu gih di kolom pengaturan di bawah atau pasang lewat panel Secrets AI Studio!"
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt)
                    )
                )
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.2f
            ),
            systemInstruction = GeminiContent(
                parts = listOf(
                    GeminiPart(text = "Lu adalah asisten mtk kalkulator yang super cerdas tapi asyik dan santai banget. Pecahin soal cerita/matematika berikut secara langkah-demi-langkah dengan cara yang gampang dimengerti, gaul, santai, dan gak kaku. Di akhir, tunjukin jawaban akhirnya dengan jelas biar langsung kelihatan, ya cuy!")
                )
            )
        )

        return try {
            val response = api.generateContent("gemini-1.5-flash", key, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Aduh bro, gak ada respon sama sekali nih dari model AI-nya. Coba lagi deh!"
        } catch (e: java.lang.Exception) {
            "Waduh, ada yang eror nih: ${e.message ?: "Koneksi internet lu lagi ampas kayaknya, coba cek deh"}"
        }
    }

    suspend fun validateApiKey(key: String): Boolean {
        if (key.isBlank()) return false
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = "test_key")
                    )
                )
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.1f
            )
        )
        return try {
            val response = api.generateContent("gemini-1.5-flash", key, request)
            response.candidates != null
        } catch (e: java.lang.Exception) {
            false
        }
    }
}
