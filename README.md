# RecipeApp

A simple Android app built with **Jetpack Compose** for browsing, viewing, and adding recipes. The app demonstrates navigation using `NavHostController`, route arguments, controlled backstack behavior, and state management with a ViewModel.

---

## Features

- **Home Screen**
  - Displays a list of recipe names using `LazyColumn`.
  - Tapping a recipe navigates to the detail screen.
  - Shows a message when no recipes have been added.

- **Recipe Detail Screen**
  - Displays full recipe information including title, ingredients, and steps.
  - Reads recipe ID from route arguments.
  - Includes a back button to navigate to the previous screen.

- **Add Recipe Screen**
  - Allows adding a new recipe with a title, ingredients, and steps.
  - Ingredients and steps are entered as multi-line text, one per line.
  - Buttons to add the recipe or cancel the action.
  - Automatically updates the Home screen upon adding a recipe.

- **Settings Screen**
  - Displays a top app bar and basic app information.

- **Bottom Navigation**
  - Navigate between **Home**, **Add**, and **Settings** screens.
  - Highlights the current selected screen dynamically.
  - Preserves state across navigation using `launchSingleTop` and `restoreState`.

- **Navigation & Backstack**
  - Uses `NavHost` and `NavHostController` for screen transitions.
  - Passes recipe IDs as route arguments to the detail screen.
  - Controlled backstack behavior when adding a recipe using `popUpTo`.

---

## How to Run the App

1. Clone this repository:
   ```bash
   git clone https://github.com/shanji361/RecipeApp.git
   ```
2. Open the project in Android Studio.

3. Run the app on an emulator or a physical Android device.   
---
## Reference
- AI assisted with navigation using sealed route classes, specifically in how to use createRoute() helper functions. This ensures consistency and prevents typos in route strings. It helped import libraries and clarified how to manage backstack behavior with popUpTo and launchSingleTop especially the placement of these. AI also helped with the logic for generating a new recipe ID in addRecipe function in MainViewModel.kt. Asked AI about the difference of usage between NavHostController and NavController.
- AI Misunderstandings: uses navController.popBackStack() instead of navController.navigateUp(). The latter handles difference between an in app task versus external deep link navigation.

