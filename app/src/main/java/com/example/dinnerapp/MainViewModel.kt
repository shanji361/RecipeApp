package com.example.dinnerapp

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

// Recipe data class
data class Recipe(
    val id: Int,
    val title: String,
    val ingredients: List<String>,
    val steps: List<String>
)


class RecipeViewModel : ViewModel() {
    private val _recipes = mutableStateListOf<Recipe>()
    val recipes: List<Recipe> = _recipes


    /**
     * Add new recipe to list
     */
    fun addRecipe(title: String, ingredients: List<String>, steps: List<String>) {
        val newId = (_recipes.maxOfOrNull { it.id } ?: -1) + 1
        _recipes.add(Recipe(newId, title, ingredients, steps))
    }

    /**
     * Get a recipe by ID; to access the right recipe data
     */
    fun getRecipeById(id: Int): Recipe? {
        return _recipes.find { it.id == id }
    }

}