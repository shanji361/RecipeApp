// MainActivity.kt
package com.example.dinnerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dinnerapp.ui.theme.DinnerAppTheme

// Sealed Routes Class
sealed class Routes(val route: String) {
    object Home : Routes("home")
    object AddRecipe : Routes("add_recipe")
    object Settings : Routes("settings")
    object RecipeDetail : Routes("recipe_detail/{id}") {
        fun createRoute(id: Int) = "recipe_detail/$id"
    }
}

// Bottom Navigation Item
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DinnerAppTheme {
                RecipeApp()
            }
        }
    }
}

@Composable
fun RecipeApp() {
    val navController = rememberNavController()
    val viewModel: RecipeViewModel = viewModel()

    val bottomNavItems = listOf(
        BottomNavItem(Routes.Home.route, Icons.Default.Home, "Home"),
        BottomNavItem(Routes.AddRecipe.route, Icons.Default.Add, "Add"),
        BottomNavItem(Routes.Settings.route, Icons.Default.Settings, "Settings")
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = bottomNavItems
            )
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onRecipeClick = { id ->
                        navController.navigate(Routes.RecipeDetail.createRoute(id)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = Routes.RecipeDetail.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getInt("id") ?: 0
                RecipeDetailScreen(
                    viewModel = viewModel,
                    recipeId = recipeId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(Routes.AddRecipe.route) {
                AddRecipeScreen(
                    onRecipeAdded = { title, ingredients, steps ->
                        viewModel.addRecipe(title, ingredients, steps)
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onCancelClick = { navController.navigateUp() }
                )
            }

            composable(Routes.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Routes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RecipeViewModel,
    onRecipeClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Recipes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (viewModel.recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No recipes yet. Add one to get started!",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(viewModel.recipes) { index, recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = recipe.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${recipe.ingredients.size} ingredients • ${recipe.steps.size} steps",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    viewModel: RecipeViewModel,
    recipeId: Int,
    onBackClick: () -> Unit
) {
    val recipe = viewModel.getRecipeById(recipeId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (recipe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Recipe not found", color = Color.Red)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = recipe.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ingredients",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            recipe.ingredients.forEach { ingredient ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text("• ", fontSize = 16.sp)
                                    Text(ingredient, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Steps",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            recipe.steps.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        "${index + 1}. ",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(step, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    onRecipeAdded: (String, List<String>, List<String>) -> Unit,
    onCancelClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var ingredientsText by remember { mutableStateOf("") }
    var stepsText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Recipe", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Recipe Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = ingredientsText,
                    onValueChange = { ingredientsText = it },
                    label = { Text("Ingredients (enter one item per line)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 8
                )
            }

            item {
                OutlinedTextField(
                    value = stepsText,
                    onValueChange = { stepsText = it },
                    label = { Text("Steps (enter one step per line)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val ingredients = ingredientsText
                                    .split("\n")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }
                                val steps = stepsText
                                    .split("\n")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                if (ingredients.isNotEmpty() && steps.isNotEmpty()) {
                                    onRecipeAdded(title, ingredients, steps)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotBlank() &&
                                ingredientsText.isNotBlank() &&
                                stepsText.isNotBlank()
                    ) {
                        Text("Add Recipe")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

}