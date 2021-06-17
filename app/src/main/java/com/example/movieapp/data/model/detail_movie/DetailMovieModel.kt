package com.example.movieapp.data.model.detail_movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailMovieModel(
    val adult : Boolean = false,
    @SerializedName("backdrop_path")
    val backdropPath : String = "",
    @SerializedName("belongs_to_collection")
    val belongsToCollection : BelongsToCollectionModel,
    val budget : Long = 0,
    val genres : ArrayList<GenresModel> = arrayListOf(),
    val homepage : String = "",
    val id: Int = 0,
    @SerializedName("imdb_id")
    val imdbId: String = "",
    @SerializedName("original_language")
    val originalLanguage: String = "",
    @SerializedName("original_title")
    val originalTitle: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    @SerializedName("poster_path")
    val posterPath: String = "",
    @SerializedName("production_companies")
    val productionCompanies: ArrayList<ProductionCompaniesModel> = arrayListOf(),
    @SerializedName("production_countries")
    val productionCountries: ArrayList<ProductionCountriesModel> = arrayListOf(),
    @SerializedName("release_date")
    val releaseDate: String = "",
    val revenue: Double = 0.0,
    val runtime: Int = 0,
    @SerializedName("spoken_languages")
    val spokenLanguages: ArrayList<SpokenLanguagesModel> = arrayListOf(),
    val status: String = "",
    val tagLine: String = "",
    val title: String = "",
    val video: Boolean = false,
    @SerializedName("vote_average")
    val voteAverage: String = "",
    @SerializedName("vote_count")
    val voteCount: String = "",
):Serializable
